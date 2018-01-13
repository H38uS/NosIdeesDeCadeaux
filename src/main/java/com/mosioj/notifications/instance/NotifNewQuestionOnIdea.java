package com.mosioj.notifications.instance;

import java.text.MessageFormat;
import java.util.Map;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.param.NotifUserIdParam;

public class NotifNewQuestionOnIdea extends AbstractNotification implements NotifUserIdParam {

	private String userName;
	private Idee idea;

	/**
	 * 
	 * @param user La personne que l'on notifie.
	 * @param groupId
	 * @param idea
	 */
	public NotifNewQuestionOnIdea(User user, Idee idea) {
		super(NotificationType.NEW_QUESTION_ON_IDEA);
		this.userName = user.name;
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
	public NotifNewQuestionOnIdea(int id, int owner, String text, Map<ParameterName, Object> parameters) {
		super(NotificationType.NEW_QUESTION_ON_IDEA, id, owner, text, parameters);
	}

	@Override
	public String getTextToInsert() {
		final String link = MessageFormat.format("<a href=\"protected/idee_questions?idee={0}\">le lire</a>", idea.getId());
		return MessageFormat.format("{0} a ajouté une nouvelle question / une nouvelle réponse sur l''idée \"{1}\". Aller {2}.", userName, idea.getTextSummary(50), link);
	}

	@Override
	public int getUserIdParam() {
		return getOwner();
	}

}
