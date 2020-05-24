package com.mosioj.ideescadeaux.webapp.servlets.logichelpers;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifIdeaModifiedWhenBirthdayIsSoon;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.utils.validators.ParameterValidator;
import com.mosioj.ideescadeaux.webapp.utils.validators.ValidatorFactory;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

public class IdeaLogic {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(IdeaLogic.class);

    private IdeaLogic() {
        // Forbidden - helper
    }

    /**
     * @param user The user.
     * @return True if and only if the birthday of this user is set up, and will come in less than
     * NotifIdeaModifiedWhenBirthdayIsSoon.NB_DAYS_BEFORE_BIRTHDAY.
     */
    public static boolean isBirthdayClose(User user) {

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
     * @param user The user.
     * @param idea The idea.
     * @return True if it already has and if no error occured.
     */
    private static boolean hasIdeaModifiedNotifForThis(User user, Idee idea) {
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
     * Checks idea parameters in this request and returns the error list if any.
     *
     * @param parameters The request parameters.
     * @return The list of error in the parameters found.
     */
    public static List<String> fillIdeaOrErrors(Map<String, String> parameters) {

        String text = parameters.get("text");
        String type = parameters.get("type");
        int priority = Integer.parseInt(parameters.get("priority"));

        if (text.isEmpty() && type.isEmpty() && priority == -1) {
            logger.debug("All parameters are empty.");
            return Collections.singletonList("Il semblerait que tous les paramètres soient vides...");
        }

        ParameterValidator valText = ValidatorFactory.getMascValidator(text, "text");
        valText.checkEmpty();

        ParameterValidator valPrio = ValidatorFactory.getFemValidator(priority + "", "priorité");
        valPrio.checkEmpty();
        valPrio.checkIfInteger();

        List<String> errors = new ArrayList<>();
        errors.addAll(valText.getErrors());
        errors.addAll(valPrio.getErrors());
        return errors;
    }

    /**
     * Ajoute une notification au amis de la personne si son anniversaire approche.
     *
     * @param user  The user.
     * @param idea  The idea.
     * @param isNew Whether this is a new idea or not.
     */
    public static void addModificationNotification(User user, Idee idea, boolean isNew) {
        if (IdeaLogic.isBirthdayClose(user)) {
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
     * Deletes old pictures from the server.
     *
     * @param path  The picture path.
     * @param image The picture name.
     */
    public static void removeUploadedImage(File path, String image) {
        if (image != null && !image.isEmpty()) {
            image = StringEscapeUtils.unescapeHtml4(image); // FIXME : est-ce nécessaire ?
            String imageName = path.toString();
            try {
                imageName = path.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
                logger.warn(e.getMessage());
            }
            logger.debug(MessageFormat.format("Deleting pictures ({1}) in {0} folder...", imageName, image));
            File small = new File(path, "small/" + image);
            if (!small.delete()) {
                logger.error("Fail to delete {} file...", small);
            }
            File large = new File(path, "large/" + image);
            if (!large.delete()) {
                logger.error("Fail to delete {} file...", large);
            }
        }
    }
}
