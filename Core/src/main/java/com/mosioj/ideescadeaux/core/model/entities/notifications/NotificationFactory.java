package com.mosioj.ideescadeaux.core.model.entities.notifications;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;

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
         * @return The configured notification.
         */
        public Notification build() {
            return notification;
        }
    }

}
