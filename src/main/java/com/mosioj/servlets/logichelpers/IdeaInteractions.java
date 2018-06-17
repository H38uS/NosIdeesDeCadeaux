package com.mosioj.servlets.logichelpers;

import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.model.table.GroupIdea;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.Notifications;
import com.mosioj.model.table.SousReservation;
import com.mosioj.model.table.Users;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.notifications.instance.NotifBookedRemove;
import com.mosioj.notifications.instance.NotifNoIdea;
import com.mosioj.notifications.instance.param.NotifUserIdParam;
import com.mosioj.utils.ParametersUtils;

public class IdeaInteractions {

	private static final Logger logger = LogManager.getLogger(IdeaInteractions.class);

	protected Notifications notif = new Notifications();
	protected GroupIdea groupForIdea = new GroupIdea();
	protected SousReservation sousReservation = new SousReservation();
	protected Idees idees = new Idees();
	protected Users users = new Users();

	protected void removeUploadedImage(File path, String image) {
		if (image != null && !image.isEmpty()) {
			image = StringEscapeUtils.unescapeHtml4(image);
			logger.debug(MessageFormat.format("Deleting pictures ({1}) in {0} folder...", path, image));
			File small = new File(path, "small/" + image);
			small.delete();
			File large = new File(path, "large/" + image);
			large.delete();
		}
	}

	/**
	 * Deletes an idea from the DB.
	 * 
	 * @param idea
	 * @param request
	 * @param response
	 * @throws SQLException
	 * @throws ServletException
	 */
	public void removeIt(Idee idea, File ideaPicturePath, HttpServletRequest request) throws SQLException, ServletException {

		// Reading parameters
		logger.debug(MessageFormat.format("Deleting idea {0}.", idea.getId()));

		Set<Integer> notified = new HashSet<>();
		for (User user : idea.getBookers(groupForIdea, sousReservation)) {
			notif.addNotification(user.id, new NotifBookedRemove(idea, idea.owner.getName()));
			notified.add(user.id);
		}

		String image = idea.getImage();
		logger.debug(MessageFormat.format("Image: {0}.", image));
		removeUploadedImage(ideaPicturePath, image);

		List<AbstractNotification> notifications = notif.getNotification(ParameterName.IDEA_ID, idea.getId());
		for (AbstractNotification notification : notifications) {
			if (notification instanceof NotifUserIdParam) {
				NotifUserIdParam notifUserId = (NotifUserIdParam) notification;
				if (!notified.contains(notifUserId.getUserIdParam())) {
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
