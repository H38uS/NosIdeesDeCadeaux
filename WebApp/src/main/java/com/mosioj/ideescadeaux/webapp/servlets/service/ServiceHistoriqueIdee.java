package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.repositories.IdeasWithInfoRepository;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.Page;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.PagedResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.viewhelper.ListResultWithPagesHelper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/protected/service/idee/historique")
public class ServiceHistoriqueIdee extends ServiceGet<AllAccessToPostAndGet> {

    /** The page helper. */
    private static final ListResultWithPagesHelper PAGES_HELPER = ListResultWithPagesHelper.with(10);

    /** Class constructor. */
    public ServiceHistoriqueIdee() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) {

        // Getting parameters
        int firstRow = PAGES_HELPER.getFirstRow(request);

        // Building the result
        final var allDeletedIdeas = IdeasWithInfoRepository.getDeletedIdeasOf(thisOne);
        final var ideas = allDeletedIdeas.stream()
                                         .map(this::toDecoratedIdea)
                                         .skip(firstRow)
                                         .limit(PAGES_HELPER.getMaxNumberOfResults())
                                         .collect(Collectors.toList());
        final List<OwnerIdeas> ownerIdeas = Collections.singletonList(OwnerIdeas.from(thisOne, ideas));

        // Only one page : only fetching the list of one user...
        final List<Page> pages = PAGES_HELPER.getPages(request, ideas.size(), (r) -> allDeletedIdeas.size());

        // Sending the response
        buildResponse(response, ServiceResponse.ok(PagedResponse.from(pages, ownerIdeas), thisOne));
    }

}
