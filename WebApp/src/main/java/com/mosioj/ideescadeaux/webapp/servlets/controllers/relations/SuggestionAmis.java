package com.mosioj.ideescadeaux.webapp.servlets.controllers.relations;

import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNewRelationSuggestion;
import com.mosioj.ideescadeaux.core.model.repositories.*;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/protected/suggestion_amis")
public class SuggestionAmis extends IdeesCadeauxGetAndPostServlet<AllAccessToPostAndGet> {

    private static final long serialVersionUID = -8566629037022016825L;
    private static final String DISPATCH_URL = "suggestion_amis.jsp";
    private static final Logger logger = LogManager.getLogger(SuggestionAmis.class);

    public SuggestionAmis() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
        req.setAttribute("suggestions", UserRelationsSuggestionRepository.getUserSuggestions(thisOne));
        RootingsUtils.rootToPage(DISPATCH_URL, req, resp);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        int userId = thisOne.id;

        Map<String, String[]> params = request.getParameterMap();

        List<Integer> toBeAsked = getSelectedChoices(params, "selected_");
        List<Integer> toIgnore = getSelectedChoices(params, "reject_");

        List<String> errors = new ArrayList<>();

        for (int userToAsk : toBeAsked) {
            UsersRepository.getUser(userToAsk).ifPresent(u -> {
                try {
                    UserRelationsSuggestionRepository.removeIfExists(userId, userToAsk);
                    if (u.id == userId || UserRelationsRepository.associationExists(u.id, userId)) {
                        errors.add(MessageFormat.format("{0} fait déjà parti de votre réseau.", u.getName()));
                        return;
                    }
                    if (UserRelationRequestsRepository.associationExists(userId, u.id)) {
                        errors.add(MessageFormat.format("Vous avez déjà envoyé une demande à {0}.", u.getName()));
                        return;
                    }
                    // On ajoute l'association
                    UserRelationRequestsRepository.insert(thisOne, u);
                } catch (SQLException e) {
                    logger.error(e);
                    e.printStackTrace();
                }
            });
        }

        for (int ignore : toIgnore) {
            UserRelationsSuggestionRepository.removeIfExists(userId, ignore);
        }

        List<AbstractNotification> notifications = NotificationsRepository.getUserNotifications(thisOne);
        for (AbstractNotification n : notifications) {
            if (n instanceof NotifNewRelationSuggestion) {
                NotifNewRelationSuggestion notification = (NotifNewRelationSuggestion) n;
                if (!UserRelationsSuggestionRepository.hasReceivedSuggestionFrom(userId,
                                                                                 notification.getUserIdParam())) {
                    NotificationsRepository.remove(notification);
                }
            }
        }

        request.setAttribute("suggestions", UserRelationsSuggestionRepository.getUserSuggestions(thisOne));
        request.setAttribute("error_messages", errors);
        RootingsUtils.rootToPage(DISPATCH_URL, request, response);
    }

}
