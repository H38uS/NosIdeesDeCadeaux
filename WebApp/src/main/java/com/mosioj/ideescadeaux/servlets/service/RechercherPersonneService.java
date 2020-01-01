package com.mosioj.ideescadeaux.servlets.service;

import javax.servlet.annotation.WebServlet;

import com.mosioj.ideescadeaux.servlets.controllers.relations.RechercherPersonne;

@WebServlet("/protected/service/rechercher_personne")
public class RechercherPersonneService extends RechercherPersonne {

    private static final long serialVersionUID = 9147880158497428623L;
    public static final String FORM_URL_SERVICE = "/protected/service/rechercher_personne.jsp";

    public RechercherPersonneService() {
        super(FORM_URL_SERVICE);
    }
}
