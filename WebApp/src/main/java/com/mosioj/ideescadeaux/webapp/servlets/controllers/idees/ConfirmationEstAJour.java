package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.Notification;
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
import java.util.Optional;

@WebServlet("/protected/confirmation_est_a_jour")
public class ConfirmationEstAJour extends IdeesCadeauxGetServlet<IdeaModification> {

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

        // Gets all previous notifications asking if this idea is up to date
        final List<Notification> asked = NotificationsRepository.fetcher()
                                                                .whereIdea(idea)
                                                                .whereType(NType.IS_IDEA_UP_TO_DATE)
                                                                .fetch();

        // Deletes all of them
        asked.forEach(NotificationsRepository::remove);

        // Creating new confirmation notification
        final Notification confirmedUpToDate = NType.CONFIRMED_UP_TO_DATE.with(thisOne, idea);
        asked.stream()
             .map(Notification::getUserParameter)
             .filter(Optional::isPresent)
             .map(Optional::get)
             .map(confirmedUpToDate::setOwner)
             .forEach(Notification::send);

        RootingsUtils.rootToPage(MesNotifications.URL, request, response);
    }

}
