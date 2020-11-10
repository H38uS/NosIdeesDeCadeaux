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

        // FIXME ajouter le statut => ALTER TABLE `IDEES` ADD `status` VARCHAR(50) NOT NULL AFTER `cree_le`;

        // FIXME mater les doublons au cas où dans IDEES_HIST => select id, count(*) from IDEES_HIST group by id having(count(*) > 1)

        // FIXME insérer les ancienns =>
        // insert into IDEES (`owner`, `idee`, `reserve`, `type`, `groupe_kdo_id`, `priorite`, `surprise_par`, `image`, `reserve_le`, `modification_date`, `a_sous_reservation`, `cree_par`, `cree_le`, `status`)
        // select `owner`, `idee`, `reserve`, `type`, `groupe_kdo_id`, `priorite`, `surprise_par`, `image`, `reserve_le`, `modification_date`, `a_sous_reservation`, `cree_par`, `cree_le`, 'DELETED' as `status`
        // from IDEES_HIST

        // FIXME droper la table => drop table IDEES_HIST;

        // Sending back the OK response
        buildResponse(response, ServiceResponse.ok(isAdmin(request), thisOne));
    }
}
