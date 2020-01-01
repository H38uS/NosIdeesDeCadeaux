package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.SurpriseModification;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AbstractIdea;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.MesListes;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

@WebServlet("/protected/supprimer_surprise")
public class SupprimerSurprise extends AbstractIdea<SurpriseModification> {

    private static final Logger logger = LogManager.getLogger(SupprimerSurprise.class);
    private static final long serialVersionUID = -8244829899125982644L;
    private static final String IDEA_ID_PARAM = "idee";

    /**
     * Class constructor
     */
    public SupprimerSurprise() {
        super(new SurpriseModification(IDEA_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse resp) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        logger.debug(MessageFormat.format("Suppression de la surprise {0} par {1}.", idea.getId(), thisOne));
        IdeesRepository.remove(idea.getId());
        RootingsUtils.redirectToPage(getFrom(request, MesListes.PROTECTED_MES_LISTES), request, resp);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
    }

}
