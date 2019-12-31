package com.mosioj.ideescadeaux.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.entities.Share;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.NotifGroupEvolution;
import com.mosioj.ideescadeaux.notifications.instance.NotifGroupSuggestion;
import com.mosioj.ideescadeaux.servlets.controllers.idees.AbstractIdea;
import com.mosioj.ideescadeaux.servlets.controllers.idees.VoirListe;
import com.mosioj.ideescadeaux.servlets.securitypolicy.BookingGroupInteraction;
import com.mosioj.ideescadeaux.utils.ParametersUtils;
import com.mosioj.ideescadeaux.utils.RootingsUtils;
import com.mosioj.ideescadeaux.utils.validators.ParameterValidator;
import com.mosioj.ideescadeaux.utils.validators.ValidatorFactory;

@WebServlet("/protected/detail_du_groupe")
public class GroupIdeaDetails extends AbstractIdea<BookingGroupInteraction> {

    private static final long serialVersionUID = -2188278918134412556L;
    private static final Logger logger = LogManager.getLogger(GroupIdeaDetails.class);

    public static final String GROUP_ID_PARAM = "groupid";

    public static final String VIEW_PAGE_URL = "/protected/detail_du_groupe.jsp";
    public static final String GET_PAGE_WITH_GROUP_ID = "/protected/detail_du_groupe?groupid=";

    /**
     * Class constructor.
     */
    public GroupIdeaDetails() {
        super(new BookingGroupInteraction(GROUP_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        int groupId = policy.getGroupId();

        logger.debug("Getting details for idea group " + groupId + "...");
        IdeaGroup group = model.groupForIdea.getGroupDetails(groupId).orElse(new IdeaGroup(-1, 0));
        double currentTotal = 0;
        for (Share share : group.getShares()) {
            currentTotal += share.getAmount();
        }
        double remaining = group.getTotal() - currentTotal;

        Object errors = request.getSession().getAttribute("errors");
        if (errors != null) {
            request.setAttribute("errors", errors);
            request.getSession().removeAttribute("errors");
        }

        Idee idee = model.idees.getIdeaWithoutEnrichmentFromGroup(groupId);
        User user = thisOne;
        model.idees.fillAUserIdea(user, idee, device);

        // Suppression des notif's si y'en a
        model.notif.getNotifications(user.id,
                                     NotificationType.GROUP_IDEA_SUGGESTION,
                                     ParameterName.GROUP_ID,
                                     groupId).forEach(n -> model.notif.remove(n.id));

        request.setAttribute("idee", idee);
        request.setAttribute("is_in_group", group.contains(thisOne));
        request.setAttribute("group", group);
        request.setAttribute("currentTotal", currentTotal);
        request.setAttribute("remaining", String.format("%1$,.2f", remaining));
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);

    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        User thisUser = thisOne;
        int groupId = policy.getGroupId();
        String amount = ParametersUtils.readIt(request, "amount").replaceAll(",", ".");

        if ("annulation".equals(amount)) {

            User owner = model.idees.getIdeaOwnerFromGroup(groupId);
            boolean isThereSomeoneRemaning = model.groupForIdea.removeUserFromGroup(thisUser, groupId);
            List<AbstractNotification> notifications = model.notif.getNotification(ParameterName.GROUP_ID, groupId);
            notifications.parallelStream()
                         .filter(n -> n.getType().equals(NotificationType.GROUP_IDEA_SUGGESTION.name()))
                         .forEach(n -> {
                             model.notif.remove(n.id);
                             logger.debug(MessageFormat.format("Notification {0} (type: {1}) dropped !",
                                                               n.id,
                                                               n.getType()));
                         });

            if (isThereSomeoneRemaning) {
                Optional<IdeaGroup> group = model.groupForIdea.getGroupDetails(groupId);
                if (group.isPresent()) {

                    // On supprime les notifications précédentes de cette personne si y'en a
                    model.notif.removeAllType(NotificationType.GROUP_EVOLUTION,
                                              ParameterName.GROUP_ID,
                                              groupId,
                                              ParameterName.USER_ID,
                                              thisUser);

                    final Idee idee = model.idees.getIdeaWithoutEnrichmentFromGroup(groupId);
                    group.get().getShares().parallelStream().forEach(s -> {
                        try {
                            model.notif.addNotification(s.getUser().id,
                                                        new NotifGroupEvolution(thisUser, groupId, idee, false));
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("Fail to send group evolution notification => " + e.getMessage());
                        }
                    });
                }
                RootingsUtils.redirectToPage(GET_PAGE_WITH_GROUP_ID + groupId, request, response);
            } else {
                RootingsUtils.redirectToPage(VoirListe.PROTECTED_VOIR_LIST +
                                             "?" +
                                             VoirListe.USER_ID_PARAM +
                                             "=" +
                                             owner.id,
                                             request,
                                             response);
            }

            return;
        }

        ParameterValidator val = ValidatorFactory.getMascValidator(amount, "montant");
        val.checkEmpty();
        val.checkIfAmount();
        val.checkIntegerGreaterThan(1);
        List<String> errorsAmount = val.getErrors();

        if (!errorsAmount.isEmpty()) {
            request.getSession().setAttribute("errors", errorsAmount);
        } else {
            // Modification de la participation
            boolean newMember = model.groupForIdea.updateAmount(groupId, thisUser, Double.parseDouble(amount));
            if (newMember) {
                List<AbstractNotification> notifications = model.notif.getNotification(ParameterName.GROUP_ID, groupId);
                for (AbstractNotification notification : notifications) {
                    if (notification instanceof NotifGroupSuggestion && notification.owner == thisUser.id) {
                        model.notif.remove(notification.id);
                    }
                }
                Optional<IdeaGroup> group = model.groupForIdea.getGroupDetails(groupId);
                if (group.isPresent()) {

                    // On supprime les notifications précédentes de cette personne si y'en a
                    model.notif.removeAllType(NotificationType.GROUP_EVOLUTION,
                                              ParameterName.GROUP_ID,
                                              groupId,
                                              ParameterName.USER_ID,
                                              thisUser);

                    final Idee idee = model.idees.getIdeaWithoutEnrichmentFromGroup(groupId);
                    group.get().getShares().parallelStream().filter(s -> !s.getUser().equals(thisUser)).forEach(s -> {
                        try {
                            model.notif.addNotification(s.getUser().id,
                                                        new NotifGroupEvolution(thisUser, groupId, idee, true));
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("Fail to send group evolution notification => " + e.getMessage());
                        }
                    });
                }
            }
        }

        RootingsUtils.redirectToPage(GET_PAGE_WITH_GROUP_ID + groupId, request, response);
    }

}
