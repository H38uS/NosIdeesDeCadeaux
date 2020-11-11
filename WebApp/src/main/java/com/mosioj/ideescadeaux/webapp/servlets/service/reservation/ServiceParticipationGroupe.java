package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifGroupEvolution;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifGroupSuggestion;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.BookingGroupInteraction;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.validators.ValidatorBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/protected/service/group/participation")
public class ServiceParticipationGroupe extends ServicePost<BookingGroupInteraction> {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(ServiceParticipationGroupe.class);

    /** Group parameter */
    public static final String GROUP_ID_PARAM = "groupid";

    /**
     * Class constructor.
     */
    public ServiceParticipationGroupe() {
        super(new BookingGroupInteraction(GROUP_ID_PARAM));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        IdeaGroup group = policy.getGroupId();
        String amount = ParametersUtils.readIt(request, "amount").replaceAll(",", ".");

        List<String> errorsAmount = ValidatorBuilder.getMascValidator(amount, "montant")
                                                    .checkEmpty()
                                                    .checkIfAmount()
                                                    .checkDoubleAmount(1.0, Double.MAX_VALUE)
                                                    .build()
                                                    .getErrors();

        if (!errorsAmount.isEmpty()) {
            buildResponse(response, ServiceResponse.ko(errorsAmount, isAdmin(request), thisOne));
            return;
        }

        // Modification de la participation
        boolean newMember = !group.contains(thisOne);
        if (newMember) {

            GroupIdeaRepository.addNewAmount(Double.parseDouble(amount), thisOne.id, group.getId());
            List<AbstractNotification> notifications = NotificationsRepository.getNotification(ParameterName.GROUP_ID,
                                                                                               group.getId());
            notifications.stream()
                         .filter(n -> n instanceof NotifGroupSuggestion && thisOne.equals(n.getOwner()))
                         .forEach(NotificationsRepository::remove);

            // On supprime les notifications précédentes de cette personne si y'en a
            NotificationsRepository.removeAllType(NotificationType.GROUP_EVOLUTION,
                                                  ParameterName.GROUP_ID,
                                                  group.getId(),
                                                  ParameterName.USER_ID,
                                                  thisOne.id);

            // On a forcément une idée pour un groupe... Sinon grosse erreur !!
            Idee idee = IdeesRepository.getIdeaFromGroup(group.getId()).orElseThrow(SQLException::new);

            final NotifGroupEvolution groupEvolution = new NotifGroupEvolution(thisOne, group.getId(), idee, true);
            group.getShares()
                 .parallelStream()
                 .filter(s -> !s.getUser().equals(thisOne))
                 .forEach(s -> NotificationsRepository.addNotification(s.getUser().id, groupEvolution));

            logger.info("{} vient de participer au groupe {} ({}).", thisOne, group, amount);
        } else {
            GroupIdeaRepository.updateAmount(group.getId(), thisOne, Double.parseDouble(amount));
            logger.info("{} vient de mettre à jour sa participation au groupe {} ({}).", thisOne, group, amount);
        }

        buildResponse(response, ServiceResponse.ok(isAdmin(request), thisOne));
    }
}
