package com.mosioj.ideescadeaux.webapp.servlets.logichelpers;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppIdea;
import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.utils.validators.ValidatorBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IdeaLogic {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(IdeaLogic.class);

    private IdeaLogic() {
        // Forbidden - helper
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

        // Text validation
        List<String> errors = ValidatorBuilder.getMascValidator(text, "text").checkEmpty().build().getErrors();

        // Priority validation
        errors.addAll(ValidatorBuilder.getFemValidator(priority + "", "priorité")
                                      .checkEmpty()
                                      .checkIfInteger()
                                      .build().getErrors());

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
        if (user.getNbDaysBeforeBirthday() < User.NB_DAYS_BEFORE_BIRTHDAY) {
            // Send a notification for each user that has no such modification notification yet
            final List<User> users = UserRelationsRepository.getAllUsersInRelation(user);
            users.parallelStream()
                 .map(u -> isNew ?
                         NType.NEW_IDEA_BIRTHDAY_SOON.with(user, idea).setOwner(u) :
                         NType.MODIFIED_IDEA_BIRTHDAY_SOON.with(user, idea).setOwner(u))
                 .filter(n -> NotificationsRepository.findNotificationsMatching(n).isEmpty())
                 .forEach(Notification::send);
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
            String imageName = path.toString();
            try {
                imageName = path.getCanonicalPath();
            } catch (IOException e) {
                logger.warn(e);
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

    /**
     * Fills in the user's ideas.
     *
     * @param connectedUser The user currently connected for this request.
     * @param users         We want the ideas of those user list.
     * @param device        The device used by the connected user.
     * @return Those users as their ideas in a single list.
     */
    public static List<OwnerIdeas> getPersonsIdeasFromUsers(User connectedUser, List<User> users, Device device) {
        return users.stream()
                    .map(u -> getPersonIdeasFromUser(connectedUser, device, u))
                    .toList();
    }

    /**
     * Fills in the user ideas.
     *
     * @param connectedUser The user currently connected for this request.
     * @param user          We want the ideas of this user.
     * @param device        The device used by the connected user.
     * @return Those users as their ideas in a single list.
     */
    private static OwnerIdeas getPersonIdeasFromUser(User connectedUser, Device device, User user) {
        Stream<DecoratedWebAppIdea> ideas = IdeesRepository.getIdeasOf(user)
                                                           .parallelStream()
                                                           .map(i -> new DecoratedWebAppIdea(i,
                                                                                             connectedUser,
                                                                                             device));
        if (connectedUser.equals(user)) {
            // Filter out the surprise of the connected user
            ideas = ideas.filter(i -> !i.getIdee().isASurprise());
        }
        return OwnerIdeas.from(user, ideas.collect(Collectors.toList()));
    }
}
