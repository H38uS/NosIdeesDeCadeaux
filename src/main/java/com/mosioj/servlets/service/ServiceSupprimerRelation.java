package com.mosioj.servlets.service;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifFriendshipDropped;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/supprimer_relation")
public class ServiceSupprimerRelation extends AbstractService<AllAccessToPostAndGet> {

	private static final long serialVersionUID = -4896678945281607617L;
	private static final Logger logger = LogManager.getLogger(ServiceSupprimerRelation.class);
	public static final String USER_PARAMETER = "id";

	public ServiceSupprimerRelation() {
		super(new NetworkAccess(USER_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// RAS
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) {

		String status = "ok";
		String message = "";

		try {
			Integer user = ParametersUtils.readInt(request, USER_PARAMETER);
			User thisOne = ParametersUtils.getConnectedUser(request);
			model.userRelations.deleteAssociation(user, thisOne.id);
			model.notif.removeAllType(thisOne, NotificationType.ACCEPTED_FRIENDSHIP, ParameterName.USER_ID, user);
			model.notif.removeAllType(model.users.getUser(user), NotificationType.ACCEPTED_FRIENDSHIP, ParameterName.USER_ID, thisOne);

			// Send a notification
			model.notif.addNotification(user, new NotifFriendshipDropped(thisOne));
		} catch (SQLException | NotLoggedInException e) {
			status = "ko";
			message = e.getMessage();
			logger.warn(e);
		}

		writter.writeJSonOutput(response, makeJSonPair("status", status), makeJSonPair("error_message", message));
	}

}
