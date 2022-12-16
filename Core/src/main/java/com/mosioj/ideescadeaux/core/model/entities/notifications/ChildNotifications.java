package com.mosioj.ideescadeaux.core.model.entities.notifications;

import com.mosioj.ideescadeaux.core.model.entities.User;

import java.util.Set;

public class ChildNotifications {

    private final User child;
    private final Set<Notification> notifications;

    /**
     * @param child         The child user
     * @param notifications The child notifications.
     */
    public ChildNotifications(User child, Set<Notification> notifications) {
        this.child = child;
        this.notifications = notifications;
    }

    /**
     * @return The child name.
     */
    public String getName() {
        return child.getName();
    }

    /**
     * @return The child notifications.
     */
    public Set<Notification> getNotifications() {
        return notifications;
    }
}
