package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/protected/mes_reservations")
public class MesReservations extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    public static final String VIEW_PAGE_URL = "/protected/mes_reservations.jsp";

    /**
     * Class constructor.
     */
    public MesReservations() {
        // No security : we will see only our content.
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse resp) {
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, resp);
    }
}
