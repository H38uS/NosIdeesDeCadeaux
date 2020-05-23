package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppIdea;
import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/protected/service/mes_reservations")
public class ServiceMesReservations extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    private static final long serialVersionUID = 2763424501732173771L;

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(ServiceMesReservations.class);

    /**
     * Class constructor.
     */
    public ServiceMesReservations() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {

        // All ideas for which we do participate
        logger.trace("[Perf] Récupération des idées où je participe...");
        List<Idee> idees = IdeesRepository.getIdeasWhereIDoParticipateIn(thisOne);

        // Grouped by owners
        logger.trace("[Perf] OK ! Cela concerne {} idées. Groupage par owner...", idees.size());
        Map<User, List<DecoratedWebAppIdea>> userToIdeas = idees.parallelStream()
                                                                .filter(i -> !thisOne.equals(i.getOwner()))
                                                                .map(i -> new DecoratedWebAppIdea(i, thisOne, device))
                                                                .collect(Collectors.groupingBy(DecoratedWebAppIdea::getIdeaOwner));

        logger.trace("[Perf] OK ! Ajout des idées par owner...");
        List<OwnerIdeas> ownerIdeas = new ArrayList<>();
        userToIdeas.forEach((u, ideas) -> ownerIdeas.add(OwnerIdeas.from(u, ideas)));

        // Sorting according to owners
        logger.trace("[Perf] OK ! Tri par user...");
        ownerIdeas.sort(Comparator.comparing(OwnerIdeas::getOwner));

        // Writing answer
        logger.trace("[Perf] OK ! Envoie de la réponse...");
        buildResponse(response, ServiceResponse.ok(ownerIdeas, isAdmin(request), thisOne));
    }
}
