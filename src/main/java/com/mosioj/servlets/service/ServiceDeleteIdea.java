package com.mosioj.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.mosioj.servlets.logichelpers.IdeaInteractions;
import com.mosioj.servlets.securitypolicy.IdeaModification;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/delete_idea")
public class ServiceDeleteIdea extends AbstractService<IdeaModification> {

	private static final Logger logger = LogManager.getLogger(ServiceDeleteIdea.class);
	private static final long serialVersionUID = 2642366164643542379L;
	public static final String IDEE_ID_PARAM = "ideeId";

	public ServiceDeleteIdea() {
		super(new IdeaModification(idees, IDEE_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// Do nothing
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		IdeaInteractions logic = new IdeaInteractions();
		Idee idea = policy.getIdea();
		// Reading parameters
		logger.debug(MessageFormat.format("Deleting idea {0}.", idea.getId()));
		
		Set<Integer> notified = new HashSet<>();
		List<User> bookers = idea.getBookers(groupForIdea, sousReservation);
		logger.debug(MessageFormat.format("Liste des personnes qui ont réservé au moment de la suppression: {0}", bookers));
		for (User user : bookers) {
			notif.addNotification(user.id, new NotifBookedRemove(idea, idea.owner.getName()));
			notified.add(user.id);
		}
		
		String image = idea.getImage();
		logger.debug(MessageFormat.format("Image: {0}.", image));
		logic.removeUploadedImage(getIdeaPicturePath(), image);
		
		List<AbstractNotification> notifications = notif.getNotification(ParameterName.IDEA_ID, idea.getId());
		// Pour chaque notification qui concerne cette idée
		for (AbstractNotification notification : notifications) {
			
			// Pour chaque notification qui a un user
			if (notification instanceof NotifUserIdParam) {

				NotifUserIdParam notifUserId = (NotifUserIdParam) notification;
				// Si la personne n'a pas déjà été notifié, et n'est pas le owner de l'idée
				// On lui envoie une notif
				if (!notified.contains(notifUserId.getUserIdParam()) && idea.owner.id != notifUserId.getUserIdParam()) {
					notif.addNotification(	notifUserId.getUserIdParam(),
											new NotifBookedRemove(idea, ParametersUtils.getUserName(request)));
					notified.add(notifUserId.getUserIdParam());
				}
			}

			notif.remove(notification.id);
		}
		
		int userId = ParametersUtils.getUserId(request);
		idees.remove(idea.getId());
		
		if (!idees.hasIdeas(userId)) {
			notif.addNotification(userId, new NotifNoIdea());
		}

		writter.writeJSonOutput(response, makeJSonPair("status", "ok"));
	}
}
