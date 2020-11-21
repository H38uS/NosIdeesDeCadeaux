package com.mosioj.ideescadeaux.webapp.servlets.error;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/public/NotFound")
public class NotFound extends IdeesCadeauxGetAndPostServlet<AllAccessToPostAndGet> {

    private static final String VIEW_PUBLIC_URL = "/public/NotFound.jsp";

    public NotFound() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) {
        RootingsUtils.rootToPage(VIEW_PUBLIC_URL, request, response);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) {
        RootingsUtils.rootToPage(VIEW_PUBLIC_URL, request, response);
    }

}
