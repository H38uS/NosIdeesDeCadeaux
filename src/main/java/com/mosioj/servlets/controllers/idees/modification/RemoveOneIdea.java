package com.mosioj.servlets.controllers.idees.modification;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifBookedRemove;
import com.mosioj.notifications.instance.NotifNoIdea;
import com.mosioj.notifications.instance.param.NotifUserIdParam;
import com.mosioj.servlets.controllers.idees.AbstractIdea;
import com.mosioj.servlets.controllers.idees.MaListe;
import com.mosioj.servlets.securitypolicy.IdeaModification;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/remove_an_idea")
public class RemoveOneIdea extends AbstractIdea {

	public static final String IDEE_ID_PARAM = "ideeId";
	private static final Logger logger = LogManager.getLogger(RemoveOneIdea.class);

	public RemoveOneIdea() {
		super(new IdeaModification(idees, IDEE_ID_PARAM));
	}

	private static final long serialVersionUID = -1774633803227715931L;

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		removeIt(request, response);
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		removeIt(req, resp);
	}

	private void removeIt(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException {

		// Reading parameters
		Integer id = ParametersUtils.readInt(request, IDEE_ID_PARAM);

		Idee idea = getIdeeFromSecurityChecks();
		for (User user : idea.getBookers(groupForIdea, sousReservation)) {
			notif.addNotification(user.id, new NotifBookedRemove(idea, idea.owner.getName()));
		}

		String image = idea.getImage();
		logger.debug(MessageFormat.format("Image: {0}.", image));
		removeUploadedImage(getIdeaPicturePath(), image);

		List<AbstractNotification> notifications = notif.getNotification(ParameterName.IDEA_ID, id);
		for (AbstractNotification notification : notifications) {
			if (notification instanceof NotifUserIdParam) {
				NotifUserIdParam notifUserId = (NotifUserIdParam) notification;
				notif.addNotification(notifUserId.getUserIdParam(), new NotifBookedRemove(idea, ParametersUtils.getUserName(request)));
			}
			notif.remove(notification.id);
		}

		int userId = ParametersUtils.getUserId(request);
		idees.remove(id);

		if (!idees.hasIdeas(userId)) {
			notif.addNotification(userId, new NotifNoIdea());
		}

		RootingsUtils.redirectToPage(getFrom(request, MaListe.PROTECTED_MA_LISTE), request, response);
	}
}
