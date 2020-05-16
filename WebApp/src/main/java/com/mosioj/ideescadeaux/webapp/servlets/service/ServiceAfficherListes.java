package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AfficherListes;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/service/afficher_listes")
public class ServiceAfficherListes extends AfficherListes {

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {
        int firstRow = getFirstRow(request);
        buildResponse(response, ServiceResponse.ok(getDisplayedEntities(firstRow, request), isAdmin(request), thisOne));
    }
}
