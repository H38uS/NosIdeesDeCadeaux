package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifRecurentIdeaUnbook;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.IdeaLogic;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaModification;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/protected/je_le_veux_encore")
public class JeLaVeuxEncore extends AbstractIdea<IdeaModification> {

    private static final long serialVersionUID = 5633779078170135048L;
    public static final String IDEA_ID_PARAM = "idee";

    public JeLaVeuxEncore() {
        super(new IdeaModification(IDEA_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        Idee idea = policy.getIdea();

        // On notifie toujours ceux qui ont réservé
        Set<User> toBeNotified = new HashSet<>(idea.getBookers());

        // Puis si l'anniversaire est proche, tous les amis !
        User current = thisOne;
        if (IdeaLogic.isBirthdayClose(current)) {
            toBeNotified.addAll(UserRelationsRepository.getAllUsersInRelation(current));
        }

        // Notification
        for (User user : toBeNotified) {
            NotificationsRepository.addNotification(user.id, new NotifRecurentIdeaUnbook(current, idea));
        }

        // On supprime les réservations
        IdeesRepository.toutDereserver(idea.getId());

        request.setAttribute("from", getFrom(request, AjouterIdee.PROTECTED_AJOUTER_IDEE).substring(1));
        RootingsUtils.rootToPage("/protected/je_le_veux_encore.jsp", request, response);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
        RootingsUtils.redirectToPage(AjouterIdee.PROTECTED_AJOUTER_IDEE, request, response);
    }

}
