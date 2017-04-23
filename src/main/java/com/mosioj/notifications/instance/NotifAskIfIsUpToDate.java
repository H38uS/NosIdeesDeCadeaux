package com.mosioj.notifications.instance;

import java.text.MessageFormat;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.servlets.controllers.idees.ConfirmationEstAJour;
import com.mosioj.servlets.controllers.idees.ModifyIdea;
import com.mosioj.servlets.controllers.idees.RemoveOneIdea;

public class NotifAskIfIsUpToDate extends AbstractNotification {

	private final User askedUser;
	private final String ideaText;
	private final int ideaId;

	/**
	 * 
	 * @param askedUser
	 * @param ideaId
	 */
	public NotifAskIfIsUpToDate(User askedUser, Idee idea) {
		super(NotificationType.IS_IDEA_UP_TO_DATE);
		this.askedUser = askedUser;
		this.ideaText = idea.getTextSummary(50);
		this.ideaId = idea.getId();
		params.put("USER_ID", askedUser.id + "");
		params.put("IDEA_ID", idea.getId() + "");
	}

	@Override
	public String getText() {

		String oui = MessageFormat.format(	"<li><a href=\"protected/confirmation_est_a_jour?{0}={1}\">Oui !</a></li>",
											ConfirmationEstAJour.IDEE_FIELD_PARAMETER,
											ideaId);
		String nonSupr = MessageFormat.format(	"<li>Non... Il faudrait la <a href=\"protected/remove_an_idea?{0}={1}\">supprimer</a>.</li>",
												RemoveOneIdea.IDEE_ID_PARAM,
												ideaId);
		String nonModif = MessageFormat.format(	"<li>Non... Je la <a href=\"protected/modifier_idee?{0}={1}\">modifie</a> de suite !</li>",
												ModifyIdea.IDEE_ID_PARAM,
												ideaId);

		return MessageFormat.format("{0} souhaiterait savoir si votre idée \"{1}\" est toujours à jour. <ul> {2}{3}{4}</ul>",
									askedUser.name,
									ideaText,
									oui,
									nonSupr,
									nonModif);
	}

}
