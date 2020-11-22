package com.mosioj.ideescadeaux.core.model.notifications;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Optional;

public class Notification {

    // FIXME : migration
    // ALTER TABLE `NOTIFICATIONS` DROP `text`;
    // drop table NOTIFICATION_PARAMETERS;

    /** The notification type, useful for database insertion. Cannot be null. */
    private final NType type;

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

    /** The optional creation date for this notification. */
    private String creationTime;

    /**
     * Default constructor for insertion.
     */
    public Notification(NType type) {
        assert type != null;
        this.type = type;
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
     * @param creationTime When this notification was created.
     */
    public void setCreationTime(Instant creationTime) {
        this.creationTime = Optional.ofNullable(creationTime)
                                    .map(MyDateFormatViewer::formatMine)
                                    .orElse(StringUtils.EMPTY);
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
        try {
            return type.getText(userParameter, ideaParameter, groupParameter);
        } catch (Exception e) {
            return type.getDescription();
        }
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
     * @return The creation time of this notification.
     */
    public String getCreationTime() {
        return creationTime;
    }

    /**
     * @return A copy of this notification with the same parameters.
     */
    private synchronized Notification duplicates() {
        Notification notification = getType().buildDefault();
        notification.setId(getId());
        notification.setOwner(getOwner());
        notification.creationTime = getCreationTime();
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
}
