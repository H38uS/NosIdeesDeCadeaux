package com.mosioj.ideescadeaux.webapp.servlets.error;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/public/NotFound")
public class NotFound extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    private static final long serialVersionUID = 936404523785343564L;
    private static final String VIEW_PUBLIC_URL = "/public/NotFound.jsp";

    public NotFound() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) {
        RootingsUtils.rootToPage(VIEW_PUBLIC_URL, request, response);
    }

}
