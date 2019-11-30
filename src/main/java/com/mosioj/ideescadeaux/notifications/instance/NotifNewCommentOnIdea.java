package com.mosioj.ideescadeaux.notifications.instance;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.param.NotifUserIdParam;

public class NotifNewCommentOnIdea extends AbstractNotification implements NotifUserIdParam {

	private String userName;
	private Idee idea;

	/**
	 * 
	 * @param user La personne qui a ajouté un nouveau commentaire
	 * @param groupId
	 * @param idea
	 */
	public NotifNewCommentOnIdea(User user, Idee idea) {
		super(NotificationType.NEW_COMMENT_ON_IDEA);
		this.userName = user.getName();
		this.idea = idea;
		params.put(ParameterName.IDEA_ID, idea.getId());
	}

	/**
	 * 
	 * @param id The internal database ID.
	 * @param owner The notification owner.
	 * @param text The notification text.
	 * @param parameters The notification parameters.
	 */
	public NotifNewCommentOnIdea(int id, int owner, String text, Timestamp creationTime, boolean isUnread, Timestamp readOn, Map<ParameterName, Object> parameters) {
		super(NotificationType.NEW_COMMENT_ON_IDEA, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		final String link = "<a href=\"protected/idee_commentaires?idee=" + idea.getId() + "\">le lire</a>";
		return MessageFormat.format("{0} a ajouté un nouveau commentaire sur l''idée \"{1}\". Aller {2}.", userName, idea.getTextSummary(50), link);
	}

	@Override
	public int getUserIdParam() {
		return getOwner();
	}

}
