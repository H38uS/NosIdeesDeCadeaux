package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifIdeaModifiedWhenBirthdayIsSoon;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.SousReservationRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import com.mosioj.ideescadeaux.webapp.utils.validators.ParameterValidator;
import com.mosioj.ideescadeaux.webapp.utils.validators.ValidatorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class AbstractIdea<P extends SecurityPolicy> extends IdeesCadeauxGetAndPostServlet<P> {

    private static final long serialVersionUID = -1774633803227715931L;
    private static final Logger logger = LogManager.getLogger(AbstractIdea.class);

    private static final String FROM_URL = "from";

    protected List<String> errors = new ArrayList<>();

    /**
     * @param policy The security policy defining whether we can interact with the parameters, etc.
     */
    public AbstractIdea(P policy) {
        super(policy);
    }

    /**
     * Tries to read a from request parameters or from parameters map. If it looks coming from the web site, returns it.
     * Otherwise, returns the default value.
     *
     * @param request      Current request being processed.
     * @param defaultValue Default value for the next redirection.
     * @return The next page to be redirected to.
     */
    protected String getFrom(HttpServletRequest request, String defaultValue) {

        String from = ParametersUtils.readIt(request, FROM_URL);
        logger.debug(MessageFormat.format("Resolving request from: {0}", from));

        if (from.trim().isEmpty()) {

            if (parameters == null) {
                return defaultValue;
            }

            // Trying to resolve it from parameters
            from = parameters.get(FROM_URL);
            if (from == null || from.trim().isEmpty()) {
                return defaultValue;
            }
        }

        if (!from.startsWith("/")) {
            // Looks like it is not coming from the website...
            return defaultValue;
        }

        return from;
    }

    protected void fillIdeaOrErrors(HttpServletRequest request,
                                    HttpServletResponse response) throws ServletException, SQLException, IOException {

        errors.clear();

        // Reading parameters

        // Parse the request to get file items.
        readMultiFormParameters(request, ParametersUtils.getIdeaPicturePath());

        String text = parameters.get("text");
        String type = parameters.get("type");
        int priority = Integer.parseInt(parameters.get("priority"));

        if (text.isEmpty() && type.isEmpty() && priority == -1) {
            logger.debug("All parameters are empty.");
            // We can assume we wanted to do a get
            ideesKDoGET(request, response);
            return;
        }

        ParameterValidator valText = ValidatorFactory.getMascValidator(text, "text");
        valText.checkEmpty();

        ParameterValidator valPrio = ValidatorFactory.getFemValidator(priority + "", "priorité");
        valPrio.checkEmpty();
        valPrio.checkIfInteger();

        errors.addAll(valText.getErrors());
        errors.addAll(valPrio.getErrors());
    }

    /**
     * @param user The user.
     * @return True if and only if the birthday of this user is set up, and will come in less than
     * NotifIdeaModifiedWhenBirthdayIsSoon.NB_DAYS_BEFORE_BIRTHDAY.
     */
    protected boolean isBirthdayClose(User user) {

        if (user.birthday == null) {
            return false;
        }

        Calendar birthday = Calendar.getInstance();
        birthday.setTime(new Date(user.birthday.getTime()));

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        birthday.set(Calendar.YEAR, today.get(Calendar.YEAR));
        if (birthday.before(today)) {
            birthday.add(Calendar.YEAR, 1);
        }
        today.add(Calendar.DAY_OF_YEAR, NotifIdeaModifiedWhenBirthdayIsSoon.NB_DAYS_BEFORE_BIRTHDAY);
        logger.debug(MessageFormat.format("Resovled birthday: {0}", birthday));

        return birthday.before(today);
    }

    /**
     * Ajoute une notification au amis de la personne si son anniversaire approche.
     *
     * @param user  The user.
     * @param idea  The idea.
     * @param isNew Whether this is a new idea or not.
     */
    protected void addModificationNotification(User user, Idee idea, boolean isNew) {
        if (isBirthdayClose(user)) {
            // Send a notification for each user that has no such modification notification yet
            final List<User> users = UserRelationsRepository.getAllUsersInRelation(user);
            users.parallelStream()
                 .filter(u -> hasIdeaModifiedNotifForThis(u, idea))
                 .forEach(u -> NotificationsRepository.addNotification(u.id,
                                                                       new NotifIdeaModifiedWhenBirthdayIsSoon(user,
                                                                                                               idea,
                                                                                                               isNew))
                 );
        }
    }

    /**
     * @param user The user.
     * @param idea The idea.
     * @return True if it already has and if no error occured.
     */
    private boolean hasIdeaModifiedNotifForThis(User user, Idee idea) {
        try {
            return NotificationsRepository.getNotifications(user.id,
                                                            NotificationType.IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON,
                                                            ParameterName.IDEA_ID,
                                                            idea.getId()).size() == 0;
        } catch (SQLException e) {
            logger.error("Fail to add a notification.", e);
            return false;
        }
    }

    /**
     * @param request    The http request.
     * @param response   The http response.
     * @param user       The user.
     * @param idea       The idea.
     * @param landingURL The url.
     * @return True in case of success, false otherwise.
     */
    protected boolean sousReserver(HttpServletRequest request,
                                   HttpServletResponse response,
                                   User user,
                                   Idee idea,
                                   String landingURL) throws SQLException, ServletException {

        List<String> errors = new ArrayList<>();
        String comment = ParametersUtils.readAndEscape(request, "comment");
        if (comment == null || comment.isEmpty()) {
            errors.add("Le commentaire ne peut pas être vide !");
        }

        if (!IdeesRepository.canSubBook(idea.getId(), user.id)) {
            errors.add("L'idée a déjà été réservée, ou vous en avez déjà réservé une sous partie.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            RootingsUtils.rootToPage(landingURL, request, response);
            return false;
        }

        IdeesRepository.sousReserver(idea.getId());
        SousReservationRepository.sousReserver(idea.getId(), user.id, comment);
        return true;
    }

}
