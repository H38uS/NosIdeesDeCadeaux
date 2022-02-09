package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.Share;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.Notification;
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
            buildResponse(response, ServiceResponse.ko(errorsAmount, thisOne));
            return;
        }

        // Modification de la participation
        boolean newMember = !group.contains(thisOne);
        if (newMember) {

            GroupIdeaRepository.addNewAmount(Double.parseDouble(amount), thisOne.id, group.getId());

            // On participe, aka plus de suggestion de participation...
            NotificationsRepository.terminator()
                                   .whereOwner(thisOne)
                                   .whereType(NType.GROUP_IDEA_SUGGESTION)
                                   .whereGroupIdea(group)
                                   .terminates();

            // On supprime les notifications précédentes de cette personne si y'en a
            NotificationsRepository.terminator()
                                   .whereType(NType.JOIN_GROUP)
                                   .whereUser(thisOne)
                                   .terminates();
            NotificationsRepository.terminator()
                                   .whereType(NType.LEAVE_GROUP)
                                   .whereUser(thisOne)
                                   .terminates();

            // On a forcément une idée pour un groupe... Sinon grosse erreur !!
            Idee idee = IdeesRepository.getIdeaFromGroup(group.getId()).orElseThrow(SQLException::new);

            final Notification groupEvolution = NType.JOIN_GROUP.with(thisOne, idee, group);
            group.getShares()
                 .parallelStream()
                 .map(Share::getUser)
                 .filter(u -> !u.equals(thisOne))
                 .forEach(groupEvolution::sendItTo);

            logger.info("{} vient de participer au groupe {} ({}).", thisOne, group, amount);
        } else {
            GroupIdeaRepository.updateAmount(group.getId(), thisOne, Double.parseDouble(amount));
            logger.info("{} vient de mettre à jour sa participation au groupe {} ({}).", thisOne, group, amount);
        }

        buildResponse(response, ServiceResponse.ok(thisOne));
    }
}
