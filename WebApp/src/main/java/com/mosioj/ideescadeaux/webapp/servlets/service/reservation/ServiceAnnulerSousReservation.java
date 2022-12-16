package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.repositories.booking.SousReservationRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/protected/service/annuler_sous_reservation")
public class ServiceAnnulerSousReservation extends ServicePost<AllAccessToPostAndGet> {

    private static final Logger logger = LogManager.getLogger(ServiceAnnulerSousReservation.class);

    /**
     * Class constructor.
     */
    public ServiceAnnulerSousReservation() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {
        ParametersUtils.readInt(request, "id").ifPresent(i -> {
            logger.debug("Suppression de la sous réservation {} par {}", i, thisOne);
            SousReservationRepository.remove(i);
        });
        buildResponse(response, ServiceResponse.ok(thisOne));
    }
}
