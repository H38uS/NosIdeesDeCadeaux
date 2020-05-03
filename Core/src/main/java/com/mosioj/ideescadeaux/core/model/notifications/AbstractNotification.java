package com.mosioj.ideescadeaux.core.model.notifications;

import com.mosioj.ideescadeaux.core.utils.EmailSender;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractNotification {

    private static final Logger logger = LogManager.getLogger(AbstractNotification.class);

    /**
     * The notification type, useful for database insertion.
     */
    private final NotificationType type;

    private final Properties p;
    public int id;
    public int owner;
    public String text;
    private Timestamp creationTime;
    protected Map<ParameterName, Object> params = new HashMap<>();

    private boolean isUnread;
    private Timestamp readOn;

    /**
     * Default constructor for insertion.
     */
    public AbstractNotification(NotificationType type) {
        this.type = type;
        InputStream input = getClass().getResourceAsStream("/notif.properties");
        p = new Properties();
        try {
            p.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    /**
     * @param type         The notification type, useful for database insertion.
     * @param id           The internal database ID.
     * @param owner        The notification owner.
     * @param text         The notification text.
     * @param parameters   The notification parameters.
     * @param creationTime When the notification has been created.
     * @param readOn       When this notification was read.
     * @param isUnread     Whether it is read or unread.
     */
    public AbstractNotification(NotificationType type,
                                int id,
                                int owner,
                                String text,
                                Map<ParameterName, Object> parameters,
                                Timestamp creationTime,
                                boolean isUnread,
                                Timestamp readOn) {
        this(type);
        this.id = id;
        this.owner = owner;
        this.text = text;
        this.creationTime = creationTime;
        this.params = parameters;
        this.isUnread = isUnread;
        this.readOn = readOn;
    }

    public boolean getIsUnread() {
        return isUnread;
    }

    public String getReadOn() {
        return MyDateFormatViewer.formatMine(readOn);
    }

    /**
     * Used in database insertion.
     *
     * @return The notification type.
     */
    public String getType() {
        return type == null ? "" : type.name();
    }

    /**
     * Send the notification by email.
     *
     * @param emailAdress          The email adress.
     * @param fullURLTillProtected The full URL.
     */
    public void sendEmail(String emailAdress, Object fullURLTillProtected) {
        String notifText = getTextToInsert();
        notifText = notifText.replaceAll("<a href=\"protected/",
                                         MessageFormat.format("<a href=\"{0}protected/",
                                                              fullURLTillProtected.toString()));
        notifText = notifText.replaceAll("<a href=\"public/",
                                         MessageFormat.format("<a href=\"{0}public/", fullURLTillProtected.toString()));
        String body = p.get("mail_template").toString().replaceAll("\\$\\$text\\$\\$", notifText);
        EmailSender.sendEmail(emailAdress, "Nos idées de cadeaux - Nouvelle notification !", body);
    }

    /**
     * @return The notification text.
     */
    public abstract String getTextToInsert();

    /**
     * @return The notification text.
     */
    public String getText() {
        return text;
    }

    /**
     * @return The notification type description.
     */
    public String getDescription() {
        return type == null ? "" : type.getDescription();
    }

    /**
     * @return The parameter list of this notification.
     */
    public Map<ParameterName, Object> getParameters() {
        return params;
    }

    public int getId() {
        return id;
    }

    // FIXME : utiliser un user
    public int getOwner() {
        return owner;
    }

    public String getCreationTime() {
        return MyDateFormatViewer.formatMine(creationTime);
    }

}
