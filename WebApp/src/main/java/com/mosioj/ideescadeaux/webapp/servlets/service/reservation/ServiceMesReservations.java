package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppIdea;
import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.Page;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.PagedResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.viewhelper.ListResultWithPagesHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/protected/service/mes_reservations")
public class ServiceMesReservations extends ServiceGet<AllAccessToPostAndGet> {

    /** The page helper. */
    private static final ListResultWithPagesHelper PAGES_HELPER = ListResultWithPagesHelper.withDefaultMax();

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(ServiceMesReservations.class);

    /**
     * Class constructor.
     */
    public ServiceMesReservations() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        // All ideas for which we do participate
        logger.trace("[Perf] Récupération des idées où je participe...");
        Set<Idee> idees = IdeesRepository.getIdeasWhereIDoParticipateIn(thisOne);

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

        logger.trace("[Perf] OK ! Génération des pages...");
        int firstRow = PAGES_HELPER.getFirstRow(request);
        List<OwnerIdeas> pagedOwners = ownerIdeas.stream()
                                                 .skip(firstRow)
                                                 .limit(PAGES_HELPER.getMaxNumberOfResults())
                                                 .collect(Collectors.toList());

        // Wrapping them with the page mechanism
        final List<Page> pages = PAGES_HELPER.getPages(request, pagedOwners.size(), (request1) -> ownerIdeas.size());

        // Writing answer
        logger.trace("[Perf] OK ! Envoie de la réponse...");
        buildResponse(response, ServiceResponse.ok(PagedResponse.from(pages, pagedOwners), thisOne));
    }
}
