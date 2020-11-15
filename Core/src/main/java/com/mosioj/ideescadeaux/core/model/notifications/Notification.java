package com.mosioj.ideescadeaux.core.model.notifications;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.utils.EmailSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.Properties;

public class Notification {

    /** Class Logger */
    private static final Logger logger = LogManager.getLogger(Notification.class);

    // FIXME : migration
    // Supprimer les notifications => DELETE from NOTIFICATIONS where type in ('GROUP_EVOLUTION', 'NEW_QUESTION_ON_IDEA', 'IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON');
    // Ajout colonnes => ALTER TABLE `NOTIFICATIONS` ADD `user_id_param` INT NULL AFTER `read_on`, ADD `idea_id_param` INT NULL AFTER `user_id_param`, ADD `group_id_param` INT NULL AFTER `idea_id_param`;
    // Migration user id => update NOTIFICATIONS n set user_id_param = (select np.parameter_value from NOTIFICATION_PARAMETERS np where np.notification_id = n.id and np.parameter_name = 'USER_ID')
    // Migration idea id => update NOTIFICATIONS n set n.idea_id_param = (select np.parameter_value from NOTIFICATION_PARAMETERS np where np.notification_id = n.id and np.parameter_name = 'IDEA_ID')
    // Migration group id => update NOTIFICATIONS n set n.group_id_param = (select np.parameter_value from NOTIFICATION_PARAMETERS np where np.notification_id = n.id and np.parameter_name = 'GROUP_ID_PARAM')
    // ALTER TABLE `NOTIFICATIONS` DROP `text`;
    // drop table NOTIFICATION_PARAMETERS;

    /** The notification type, useful for database insertion. Cannot be null. */
    private final NType type;

    private final Properties p; // FIXME faire un truc statique

    /** The notification's unique identifier. */
    public Long id;

    /** The notification's owner. */
    private User owner;

    // Paramètres

    /** The optional user parameter for this notification. */
    private User userParameter;

    /** The optional idea parameter for this notification. */
    private Idee ideaParameter;

    /** The optional group parameter for this notification. */
    private IdeaGroup groupParameter;

    /**
     * Default constructor for insertion.
     */
    public Notification(NType type) {
        assert type != null;
        this.type = type;
        InputStream input = getClass().getResourceAsStream("/notif.properties");
        p = new Properties();
        try {
            p.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * @param id The notification's unique identifier.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param owner The notification's owner.
     */
    public Notification setOwner(User owner) {
        this.owner = owner;
        return this;
    }

    /**
     * @param userParameter The user's parameter value for this notification.
     */
    public void setUserParameter(User userParameter) {
        this.userParameter = userParameter;
    }

    /**
     * @param ideaParameter The idea's parameter value for this notification.
     */
    public void setIdeaParameter(Idee ideaParameter) {
        this.ideaParameter = ideaParameter;
    }

    /**
     * @param groupParameter The group's parameter value for this notification.
     */
    public void setGroupParameter(IdeaGroup groupParameter) {
        this.groupParameter = groupParameter;
    }

    /**
     * @return The notification identifier. Can be null if not persisted yet.
     */
    public Long getId() {
        return id;
    }

    /**
     * @return The person that has this notification
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Used in database insertion.
     *
     * @return The notification type.
     */
    public NType getType() {
        return type;
    }

    /**
     * @return The notification text.
     */
    public String getText() {
        return type.getText(userParameter, ideaParameter, groupParameter);
    }

    /**
     * @return The notification type description.
     */
    public String getDescription() {
        return type.getDescription();
    }

    /**
     * @return The optional user parameter for this notification.
     */
    public Optional<User> getUserParameter() {
        return Optional.ofNullable(userParameter);
    }

    /**
     * @return The optional idea parameter for this notification.
     */
    public Optional<Idee> getIdeaParameter() {
        return Optional.ofNullable(ideaParameter);
    }

    /**
     * @return The optional group parameter for this notification.
     */
    public Optional<IdeaGroup> getGroupParameter() {
        return Optional.ofNullable(groupParameter);
    }

    /**
     * @return A copy of this notification with the same parameters.
     */
    private synchronized Notification duplicates() {
        Notification notification = getType().buildDefault();
        notification.setId(getId());
        notification.setOwner(getOwner());
        getUserParameter().ifPresent(notification::setUserParameter);
        getIdeaParameter().ifPresent(notification::setIdeaParameter);
        getGroupParameter().ifPresent(notification::setGroupParameter);
        return notification;
    }

    /**
     * Creates a row in database and/or sends an email depending on the settings.
     *
     * @param owner The user to send this notification to.
     * @return The created identifier.
     */
    public int sendItTo(User owner) {
        return NotificationsRepository.add(duplicates().setOwner(owner));
    }

    /**
     * Creates a row in database and/or sends an email depending on the settings.
     *
     * @return The created identifier.
     */
    public int send() {
        return NotificationsRepository.add(duplicates());
    }

    /**
     * Send the notification by email.
     *
     * @param emailAdress          The email adress.
     * @param fullURLTillProtected The full URL.
     */
    // FIXME : faire un getEmailText et l'envoyer dans le repository
    public void sendEmail(String emailAdress, Object fullURLTillProtected) {
        String notifText = getText();
        notifText = notifText.replaceAll("<a href=\"protected/",
                                         MessageFormat.format("<a href=\"{0}protected/",
                                                              fullURLTillProtected.toString()));
        notifText = notifText.replaceAll("<a href=\"public/",
                                         MessageFormat.format("<a href=\"{0}public/", fullURLTillProtected.toString()));
        String body = p.get("mail_template").toString().replaceAll("\\$\\$text\\$\\$", notifText);
        EmailSender.sendEmail(emailAdress, "Nos idées de cadeaux - Nouvelle notification !", body);
    }
}
