package com.mosioj.notifications.instance;

import java.text.MessageFormat;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;

public class NotifAskIfIsUpToDate extends AbstractNotification {

	private final User askedUser;
	private final String ideaText;

	/**
	 * 
	 * @param askedUser
	 * @param ideaId
	 */
	public NotifAskIfIsUpToDate(User askedUser, Idee idea) {
		super(NotificationType.IS_IDEA_UP_TO_DATE);
		this.askedUser = askedUser;
		this.ideaText = idea.getText(50);
		params.put("USER_ID", askedUser.id + "");
		params.put("IDEA_ID", idea.getId() + "");
	}

	@Override
	public String getText() {

		// FIXME compléter les options
		String oui = "<li><a href=\"\">Oui !</a></li>";
		String nonSupr = "<li>Non... Il faudrait la <a href=\"\">supprimer</a>.</li>";
		String nonModif = "<li>Non... Je la <a href=\"\">modifie</a> de suite !</li>";

		return MessageFormat.format("{0} souhaiterait savoir si votre idée \"{1}\" est toujours à jour. <ul> {2}{3}{4}</ul>",
									askedUser.name,
									ideaText,
									oui,
									nonSupr,
									nonModif);
	}

}
