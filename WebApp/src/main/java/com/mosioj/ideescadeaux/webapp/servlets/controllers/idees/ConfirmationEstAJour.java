package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifConfirmedUpToDate;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.compte.MesNotifications;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaModification;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/protected/confirmation_est_a_jour")
public class ConfirmationEstAJour extends AbstractIdea<IdeaModification> {

    private static final long serialVersionUID = -6645017540948612364L;
    public static final String IDEE_FIELD_PARAMETER = "idee";

    public ConfirmationEstAJour() {
        super(new IdeaModification(IDEE_FIELD_PARAMETER));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        Idee idea = policy.getIdea();
        IdeesRepository.touch(idea.getId());
        int userId = thisOne.id;
        IsUpToDateQuestionsRepository.deleteAssociation(idea.getId(), userId);

        List<AbstractNotification> notifications = NotificationsRepository.getNotification(ParameterName.IDEA_ID,
                                                                                           idea.getId());
        Set<Integer> ids = new HashSet<>();
        for (AbstractNotification notification : notifications) {
            if (notification instanceof NotifAskIfIsUpToDate) {
                NotifAskIfIsUpToDate isUpToDate = (NotifAskIfIsUpToDate) notification;
                NotificationsRepository.addNotification(isUpToDate.getUserIdParam(),
                                                        new NotifConfirmedUpToDate(thisOne, idea));
                NotificationsRepository.remove(notification.id);
                ids.add(isUpToDate.getUserIdParam());
            }
        }
        for (AbstractNotification notification : notifications) {
            // Deletes old confirmation notifications
            if (notification instanceof NotifConfirmedUpToDate) {
                NotifConfirmedUpToDate confirmed = (NotifConfirmedUpToDate) notification;
                if (ids.contains(confirmed.owner)) {
                    NotificationsRepository.remove(notification.id);
                }
            }
        }

        RootingsUtils.rootToPage(MesNotifications.URL, request, response);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
        RootingsUtils.redirectToPage(MesNotifications.URL, request, response);
    }

}
