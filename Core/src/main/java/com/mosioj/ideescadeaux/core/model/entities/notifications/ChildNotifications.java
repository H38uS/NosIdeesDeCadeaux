package com.mosioj.ideescadeaux.core.model.entities.notifications;

import com.mosioj.ideescadeaux.core.model.entities.User;

import java.util.List;

public class ChildNotifications {

    private final User child;
    private final List<Notification> notifications;

    /**
     * @param child         The child user
     * @param notifications The child notifications.
     */
    public ChildNotifications(User child, List<Notification> notifications) {
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
    public List<Notification> getNotifications() {
        return notifications;
    }
}
