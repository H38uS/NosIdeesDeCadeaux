package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.VoirListe;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/protected/service/voir_liste")
public class ServiceVoirListe extends VoirListe {

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {
        int firstRow = helper.getFirstRow(request);
        buildResponse(response, ServiceResponse.ok(getDisplayedEntities(firstRow, request), isAdmin(request), thisOne));
    }

    /**
     * @param response The http response.
     * @param ans      This specific service answer, as a JSon string.
     */
    // FIXME : a supprimer quand déplacé dans la hiérarchie ServiceGetAndPost
    protected void buildResponse(HttpServletResponse response, ServiceResponse<?> ans) {
        try {
            response.getOutputStream().print(ans.asJSon(response));
        } catch (IOException ignored) {
        }
    }
}
