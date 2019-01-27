package com.mosioj.notifications.instance;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.param.NotifUserIdParam;
import com.mosioj.servlets.controllers.idees.ConfirmationEstAJour;
import com.mosioj.servlets.controllers.idees.modification.ModifyIdea;
import com.mosioj.servlets.service.ServiceEstAJour;

public class NotifAskIfIsUpToDate extends AbstractNotification implements NotifUserIdParam {

	private String ideaText;
	private int ideaId;

	/**
	 * 
	 * @param askedUser User who asked whether this idea is up to date or not.
	 * @param idea
	 */
	public NotifAskIfIsUpToDate(User askedUser, Idee idea) {
		super(NotificationType.IS_IDEA_UP_TO_DATE);
		this.ideaText = idea.getTextSummary(50);
		this.ideaId = idea.getId();
		params.put(ParameterName.USER_ID, askedUser.id);
		params.put(ParameterName.IDEA_ID, idea.getId());
	}

	/**
	 * 
	 * @param id The internal database ID.
	 * @param owner The notification owner.
	 * @param text The notification text.
	 * @param parameters The notification parameters.
	 */
	public NotifAskIfIsUpToDate(int id,
								int owner,
								String text,
								Timestamp creationTime,
								boolean isUnread,
								Timestamp readOn,
								Map<ParameterName, Object> parameters) {
		super(NotificationType.IS_IDEA_UP_TO_DATE, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {

		String param = ConfirmationEstAJour.IDEE_FIELD_PARAMETER + "=" + ideaId;
		String oui = MessageFormat.format("<li><a href=\"protected/confirmation_est_a_jour?{0}\">Oui !</a></li>", param);

		param = ServiceEstAJour.IDEE_FIELD_PARAMETER + "=" + ideaId;
		String nonSupr = MessageFormat.format(	"<li>Non... Il faudrait la <a href=\"protected/remove_an_idea?{0}&from=/protected/mes_notifications\">supprimer</a>.</li>",
												param);

		param = ModifyIdea.IDEE_ID_PARAM + "=" + ideaId;
		String nonModif = MessageFormat.format(	"<li>Non... Je la <a href=\"protected/modifier_idee?{0}\">modifie</a> de suite !</li>",
												param);

		return MessageFormat.format("Quelqu''un souhaiterait savoir si votre idée \"{0}\" est toujours à jour. <ul>{1}{2}{3}</ul>",
									ideaText,
									oui,
									nonSupr,
									nonModif);
	}

	@Override
	public int getUserIdParam() {
		return Integer.parseInt(params.get(ParameterName.USER_ID).toString());
	}

}
