package com.mosioj.notifications;

import com.mosioj.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.notifications.instance.NotifBookedRemove;
import com.mosioj.notifications.instance.NotifConfirmedUpToDate;
import com.mosioj.notifications.instance.NotifFriendshipDropped;
import com.mosioj.notifications.instance.NotifGroupSuggestion;
import com.mosioj.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.notifications.instance.NotifIdeaModifiedWhenBirthdayIsSoon;
import com.mosioj.notifications.instance.NotifNewCommentOnIdea;
import com.mosioj.notifications.instance.NotifNewRelationSuggestion;
import com.mosioj.notifications.instance.NotifNoIdea;

/**
 * 50 caracters max.
 * 
 * @author Jordan Mosio
 *
 */
public enum NotificationType {

	NO_IDEA(NotifNoIdea.class), //
	BOOKED_REMOVE(NotifBookedRemove.class), //
	GROUP_IDEA_SUGGESTION(NotifGroupSuggestion.class), //
	IS_IDEA_UP_TO_DATE(NotifAskIfIsUpToDate.class), //
	CONFIRMED_UP_TO_DATE(NotifConfirmedUpToDate.class), //
	NEW_RELATION_SUGGESTION(NotifNewRelationSuggestion.class), //
	NEW_COMMENT_ON_IDEA(NotifNewCommentOnIdea.class), //
	IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON(NotifIdeaModifiedWhenBirthdayIsSoon.class), //
	FRIENDSHIP_DROPPED(NotifFriendshipDropped.class), //
	IDEA_ADDED_BY_FRIEND(NotifIdeaAddedByFriend.class);

	private final Class<? extends AbstractNotification> notificationClassName;

	private NotificationType(Class<? extends AbstractNotification> notificationClassName) {
		this.notificationClassName = notificationClassName;
	}

	/**
	 * 
	 * @return The notification class.
	 */
	public Class<? extends AbstractNotification> getNotificationClassName() {
		return notificationClassName;
	}
}
