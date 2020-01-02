package com.mosioj.ideescadeaux.webapp.servlets.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;

@WebServlet("/protected/service/mes_reservations")
public class ServiceMesReservations extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    private static final long serialVersionUID = 2763424501732173771L;

    /**
     * Class constructor.
     */
    public ServiceMesReservations() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {

        // All ideas for which we do participate
        List<Idee> idees = IdeesRepository.getIdeasWhereIDoParticipateIn(thisOne);

        // Grouped by owners
        Map<User, List<Idee>> userToIdeas = idees.stream()
                                                 .filter(i -> !thisOne.equals(i.getOwner()))
                                                 .collect(Collectors.groupingBy(Idee::getOwner));
        List<OwnerIdeas> ownerIdeas = new ArrayList<>();
        userToIdeas.forEach((u, ideas) -> ownerIdeas.add(OwnerIdeas.from(u, ideas)));

        // Sorting according to owners
        ownerIdeas.sort(Comparator.comparing(OwnerIdeas::getOwner));

        // Writing answer
        buildResponse(response, ServiceResponse.ok(ownerIdeas, isAdmin(request)));
    }
}