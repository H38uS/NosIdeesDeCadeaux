package com.mosioj.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.servlets.securitypolicy.IdeaInteractionBookingUpToDate;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/est_a_jour")
public class ServiceEstAJour extends AbstractService<IdeaInteractionBookingUpToDate> {

	private static final long serialVersionUID = 2642366164643542379L;
	public static final String IDEE_FIELD_PARAMETER = "idee";
	private static final Logger logger = LogManager.getLogger(ServiceEstAJour.class);

	public ServiceEstAJour() {
		super(new IdeaInteractionBookingUpToDate(userRelations, idees, IDEE_FIELD_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// Do nothing
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		Idee idea = policy.getIdea();
		String status = askIfUpToDate(idea, request) ? "ok" : "ko";
		writter.writeJSonOutput(response, makeJSonPair("status", status));
	}

	/**
	 * 
	 * @param idea
	 * @param request
	 * @return True if the notification has been added, false if already sent.
	 * @throws ServletException
	 * @throws SQLException
	 */
	public boolean askIfUpToDate(Idee idea, HttpServletRequest request) throws ServletException, SQLException {

		int userId = ParametersUtils.getUserId(request);
		logger.debug(MessageFormat.format("Demande de validité sur l''idée {0} de {1}.", idea.getId(), userId));

		NotifAskIfIsUpToDate isUpToDateNotif = new NotifAskIfIsUpToDate(users.getUser(userId), idea);
		if (!notif.hasNotification(idea.owner.id, isUpToDateNotif)) {
			notif.addNotification(idea.owner.id, isUpToDateNotif);
			return true;
		}

		return false;
	}
}
