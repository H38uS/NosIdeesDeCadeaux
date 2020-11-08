package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.RestoreIdea;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/service/idee/restore")
public class ServiceRestoreIdea extends ServicePost<RestoreIdea> {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(ServiceRestoreIdea.class);

    /** The idea identifier parameter. */
    public static final String IDEE_ID_PARAM = "idee";

    /** The parameter telling whether to restore the booking information of not. */
    public static final String RESTORE_BOOKING = "restoreBooking";

    /** Class constructor. */
    public ServiceRestoreIdea() {
        super(new RestoreIdea(IDEE_ID_PARAM));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        // Parameters
        boolean restoreBooking = "true".equalsIgnoreCase(ParametersUtils.readAndEscape(request, RESTORE_BOOKING));
        Idee idee = policy.getIdea();

        // Restoring the idea
        logger.debug("Restoration de l'idée {}, avec réservation ? => {}", idee, restoreBooking);
        IdeesRepository.restoreIdea(idee, restoreBooking);

        // FIXME : envoyer des notifications - quand l'anniversaire est proche + ceux qui ont réservé (si on les garde)
        // FIXME faire un test de restoration ! Et une méthode pour vraiment tout supprimer
        // FIXME : ALTER TABLE `IDEES_HIST` ADD `idee_id` INT(11) NOT NULL AFTER `id`; -- avant déploiement
        // FIXME : puis update IDEES_HIST set idee_id = id
        // FIXME : ALTER TABLE `IDEES_HIST` ADD PRIMARY KEY(`id`); -- faudra supprimer les doublons...
        // select id, count(*) from IDEES_HIST group by id having(count(*) > 1)
        // FIXME : ALTER TABLE `IDEES_HIST` CHANGE `id` `id` INT(11) NOT NULL AUTO_INCREMENT;

        // Sending back the OK response
        buildResponse(response, ServiceResponse.ok(isAdmin(request), thisOne));
    }
}
