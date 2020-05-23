package com.mosioj.ideescadeaux.core.model.notifications.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.param.NotifUserIdParam;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

public class NotifNewQuestionOnIdea extends AbstractNotification implements NotifUserIdParam {

	private String userName;
	private Idee idea;

	/**
	 * 
	 * @param user La personne qui vient d'ajouter une question ou une réponse.
	 * @param idea The idea.
	 * @param toOwner If the owner can answer.
	 */
	public NotifNewQuestionOnIdea(User user, Idee idea, boolean toOwner) {
		super(NotificationType.NEW_QUESTION_ON_IDEA);
		this.userName = toOwner ? "Quelqu'un" : user.getName();
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
	public NotifNewQuestionOnIdea(int id, User owner, String text, Timestamp creationTime, boolean isUnread, Timestamp readOn, Map<ParameterName, Object> parameters) {
		super(NotificationType.NEW_QUESTION_ON_IDEA, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		final String link = "<a href=\"protected/idee_questions?idee=" + idea.getId() + "\">le lire</a>";
		return MessageFormat.format("{0} a ajouté une nouvelle question / une nouvelle réponse sur l''idée \"{1}\". Aller {2}.", userName, idea.getTextSummary(50), link);
	}

	@Override
	public int getUserIdParam() {
		return getOwner().id;
	}

}
