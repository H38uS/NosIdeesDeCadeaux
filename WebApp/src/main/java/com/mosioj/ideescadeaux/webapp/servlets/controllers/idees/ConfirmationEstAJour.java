package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifConfirmedUpToDate;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.compte.MesNotifications;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaModification;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.mosioj.ideescadeaux.core.model.notifications.ParameterName.IDEA_ID;

@WebServlet("/protected/confirmation_est_a_jour")
public class ConfirmationEstAJour extends IdeesCadeauxGetServlet<IdeaModification> {

    private static final long serialVersionUID = -6645017540948612364L;
    public static final String IDEE_FIELD_PARAMETER = "idee";

    public ConfirmationEstAJour() {
        super(new IdeaModification(IDEE_FIELD_PARAMETER));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {

        Idee idea = policy.getIdea();
        IdeesRepository.touch(idea.getId());
        IsUpToDateQuestionsRepository.deleteAssociations(idea.getId());

        // Gets all notification on this idea
        List<AbstractNotification> notifications = NotificationsRepository.getNotification(IDEA_ID, idea.getId());

        // Gets all previous notifications asking if this idea is up to date
        final List<NotifAskIfIsUpToDate> asked = notifications.stream()
                                                              .filter(n -> n instanceof NotifAskIfIsUpToDate)
                                                              .map(n -> (NotifAskIfIsUpToDate) n)
                                                              .collect(Collectors.toList());

        // Deletes all of them
        asked.forEach(NotificationsRepository::remove);

        // Creating new confirmation notification
        asked.forEach(a -> NotificationsRepository.addNotification(a.getUserIdParam(),
                                                                   new NotifConfirmedUpToDate(thisOne, idea)));

        RootingsUtils.rootToPage(MesNotifications.URL, request, response);
    }

}
