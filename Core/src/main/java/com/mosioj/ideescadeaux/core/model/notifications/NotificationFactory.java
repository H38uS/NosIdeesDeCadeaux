package com.mosioj.ideescadeaux.core.model.notifications;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;

import java.time.Instant;

public class NotificationFactory {

    private NotificationFactory() {
        // Factory class
    }

    /**
     * @param type The notification's type to build.
     * @return A new notification builder.
     */
    public static NotificationBuilder builder(NType type) {
        return new NotificationBuilder(type);
    }

    /**
     * An internal builder class to ease the object creation.
     */
    public static class NotificationBuilder {

        /** The object being configured. */
        private final Notification notification;

        /**
         * Class constructor.
         *
         * @param type The notification's type to build.
         */
        public NotificationBuilder(NType type) {
            notification = new Notification(type);
        }

        /**
         * @param identifier The notification's internal identifier.
         * @return The builder's instance.
         */
        public NotificationBuilder withAnID(Long identifier) {
            notification.setId(identifier);
            return this;
        }

        /**
         * Registers a new owner to this notification.
         *
         * @param owner The notification's owner.
         * @return The builder's instance.
         */
        public NotificationBuilder belongsTo(User owner) {
            notification.setOwner(owner);
            return this;
        }

        /**
         * @param userParameter The user's parameter value for this notification.
         * @return The builder's instance.
         */
        public NotificationBuilder withUserParameter(User userParameter) {
            notification.setUserParameter(userParameter);
            return this;
        }

        /**
         * @param ideaParameter The idea's parameter value for this notification.
         * @return The builder's instance.
         */
        public NotificationBuilder withIdeaParameter(Idee ideaParameter) {
            notification.setIdeaParameter(ideaParameter);
            return this;
        }

        /**
         * @param groupParameter The group's parameter value for this notification.
         * @return The builder's instance.
         */
        public NotificationBuilder withGroupParameter(IdeaGroup groupParameter) {
            notification.setGroupParameter(groupParameter);
            return this;
        }

        /**
         * @param creationTime When this notification was created.
         * @return The builder's instance.
         */
        public NotificationBuilder withCreationTime(Instant creationTime) {
            notification.setCreationTime(creationTime);
            return this;
        }

        /**
         * @return The configured notification.
         */
        public Notification build() {
            return notification;
        }
    }

}
