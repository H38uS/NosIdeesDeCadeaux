package com.mosioj.servlets.controllers.idees;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.IdeaGroup;
import com.mosioj.model.Idee;
import com.mosioj.model.Share;
import com.mosioj.model.User;
import com.mosioj.notifications.instance.NotifBookedRemove;
import com.mosioj.notifications.instance.NotifNoIdea;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/remove_an_idea")
public class RemoveOneIdea extends AbstractIdea {

	private static final long serialVersionUID = -1774633803227715931L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Reading parameters
		Integer id = ParametersUtils.readInt(request, "ideeId");
		if (id == null) {
			RootingsUtils.redirectToPage(MaListe.PROTECTED_MA_LISTE, request, response);
			return;
		}

		try {
			// FIXME sécurité...
			Idee idea = idees.getIdea(id);
			if (idea != null && idea.isBooked()) {
				User owner = idea.owner;
				User booker = idea.getBookingOwner();
				if (booker != null) {
					notif.addNotification(booker.id, new NotifBookedRemove(idea.getText(), owner.getName()));
				} else {
					// Il s'agit d'un groupe
					IdeaGroup group = groupForIdea.getGroupDetails(idea.getGroupKDO());
					for (Share share : group.getShares()) {
						User groupUser = share.getUser();
						notif.addNotification(groupUser.id, new NotifBookedRemove(idea.getText(), owner.getName()));
					}
				}
				String image = idea.getImage();
				removeUploadedImage(image);
			}

			int userId = ParametersUtils.getUserId(request);
			idees.remove(userId, id);

			if (!idees.hasIdeas(userId)) {
				notif.addNotification(userId, new NotifNoIdea());
			}

		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
			return;
		}

		RootingsUtils.redirectToPage(MaListe.PROTECTED_MA_LISTE, request, response);
	}
}
