package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.RechercherPersonne;

import javax.servlet.annotation.WebServlet;

@WebServlet("/protected/service/rechercher_personne")
public class ServiceRechercherPersonne extends RechercherPersonne {

    private static final long serialVersionUID = 9147880158497428623L;
    public static final String FORM_URL_SERVICE = "/protected/service/rechercher_personne.jsp";

    public ServiceRechercherPersonne() {
        super(FORM_URL_SERVICE);
    }
}
