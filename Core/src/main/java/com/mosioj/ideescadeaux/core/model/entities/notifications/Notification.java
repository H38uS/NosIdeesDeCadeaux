package com.mosioj.ideescadeaux.core.model.entities.notifications;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity(name = "NOTIFICATIONS")
public class Notification {

    /** The notification's unique identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    /** The notification type, useful for database insertion. Cannot be null. */
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private NType type;

    /** The notification's owner. */
    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

    // Paramètres

    /** The optional user parameter for this notification. */
    @ManyToOne
    @JoinColumn(name = "user_id_param")
    private User userParameter;

    /** The optional idea parameter for this notification. */
    @ManyToOne
    @JoinColumn(name = "idea_id_param")
    private Idee ideaParameter;

    /** The optional group parameter for this notification. */
    @ManyToOne
    @JoinColumn(name = "group_id_param")
    private IdeaGroup groupParameter;

    /** The optional creation date for this notification. */
    @Column(updatable = false, name = "creation_date")
    @CreationTimestamp
    private LocalDateTime creationTime;

    /** Whether this notification is read or not (Y = unread, N = read) */
    @Column(length = 1, name = "is_unread")
    public String isUnread = "N";

    /** When it was read, or null */
    @Column(name = "read_on")
    public LocalDateTime readOn;

    public Notification() {
        // For Hibernate
    }

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
    public void setId(int id) {
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
    public int getId() {
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
        return Optional.ofNullable(creationTime)
                       .map(MyDateFormatViewer::formatMine)
                       .orElse(StringUtils.EMPTY);
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
     */
    public Notification sendItTo(User owner) {
        return NotificationsRepository.add(duplicates().setOwner(owner));
    }

    /**
     * Creates a row in database and/or sends an email depending on the settings.
     */
    public void send() {
        NotificationsRepository.add(duplicates());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }
}
