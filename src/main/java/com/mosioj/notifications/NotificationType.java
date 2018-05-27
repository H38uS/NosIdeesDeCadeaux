package com.mosioj.notifications;

import com.mosioj.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.notifications.instance.NotifBookedRemove;
import com.mosioj.notifications.instance.NotifConfirmedUpToDate;
import com.mosioj.notifications.instance.NotifDemandeAcceptee;
import com.mosioj.notifications.instance.NotifDemandeRefusee;
import com.mosioj.notifications.instance.NotifFriendshipDropped;
import com.mosioj.notifications.instance.NotifGroupSuggestion;
import com.mosioj.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.notifications.instance.NotifIdeaModifiedWhenBirthdayIsSoon;
import com.mosioj.notifications.instance.NotifNewCommentOnIdea;
import com.mosioj.notifications.instance.NotifNewQuestionOnIdea;
import com.mosioj.notifications.instance.NotifNewRelationSuggestion;
import com.mosioj.notifications.instance.NotifNoIdea;
import com.mosioj.notifications.instance.NotifNouvelleDemandeAmi;
import com.mosioj.notifications.instance.NotifRecurentIdeaUnbook;

/**
 * 50 caracters max.
 * 
 * @author Jordan Mosio
 *
 */
public enum NotificationType {

	NO_IDEA(NotifNoIdea.class, "Lorsque vous n'avez pas d'idées"), //
	BOOKED_REMOVE(NotifBookedRemove.class, "Un ami a supprimé une idée que vous aviez réservée"), //
	GROUP_IDEA_SUGGESTION(NotifGroupSuggestion.class, "On vous invite à un groupe sur une idée"), //
	IS_IDEA_UP_TO_DATE(NotifAskIfIsUpToDate.class, "On vous demande si l'idée est à jour"), //
	CONFIRMED_UP_TO_DATE(NotifConfirmedUpToDate.class, "On vous confirme que l'idée est à jour"), //
	NEW_RELATION_SUGGESTION(NotifNewRelationSuggestion.class, "On vous suggère une demande d'ami"), //
	NEW_COMMENT_ON_IDEA(NotifNewCommentOnIdea.class, "Quelqu'un a posté un nouveau commentaire sur une idée"), //
	NEW_QUESTION_ON_IDEA(NotifNewQuestionOnIdea.class, "Quelqu'un a posté une nouvelle question ou nouvelle réponse sur une idée"), //
	IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON(NotifIdeaModifiedWhenBirthdayIsSoon.class, "Un ami qui va bientôt fêter son anniversaire a modifié une idée"), //
	FRIENDSHIP_DROPPED(NotifFriendshipDropped.class, "Quelqu'un a supprimé votre relation"), //
	NEW_FRIENSHIP_REQUEST(NotifNouvelleDemandeAmi.class, "Quelqu'un vous fait une demande d'ami"), //
	ACCEPTED_FRIENDSHIP(NotifDemandeAcceptee.class, "Quelqu'un a accepté votre demande"), //
	REJECTED_FRIENDSHIP(NotifDemandeRefusee.class, "Quelqu'un a refusé votre demande d'amis"), //
	RECURENT_IDEA_UNBOOK(NotifRecurentIdeaUnbook.class, "Un amis précise qu'une idée déjà reçue est toujours d'actualité"), //
	IDEA_ADDED_BY_FRIEND(NotifIdeaAddedByFriend.class, "Un ami vous a ajouté une idée");

	private final Class<? extends AbstractNotification> notificationClassName;
	private final String description;

	private NotificationType(Class<? extends AbstractNotification> notificationClassName, String description) {
		this.notificationClassName = notificationClassName;
		this.description = description;
	}

	/**
	 * 
	 * @return The notification class.
	 */
	public Class<? extends AbstractNotification> getNotificationClassName() {
		return notificationClassName;
	}
	
	public String getDescription() {
		return description;
	}
}
