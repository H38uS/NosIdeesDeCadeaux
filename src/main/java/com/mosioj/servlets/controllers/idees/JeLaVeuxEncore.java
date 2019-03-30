package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.instance.NotifRecurentIdeaUnbook;
import com.mosioj.servlets.securitypolicy.IdeaModification;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/je_le_veux_encore")
public class JeLaVeuxEncore extends AbstractIdea<IdeaModification> {

	private static final long serialVersionUID = 5633779078170135048L;
	public static final String IDEA_ID_PARAM = "idee";

	public JeLaVeuxEncore() {
		super(new IdeaModification(IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Idee idea = policy.getIdea();

		Set<User> toBeNotified = new HashSet<User>();

		// On notifie toujours ceux qui ont réservé
		toBeNotified.addAll(idea.getBookers(model.groupForIdea, model.sousReservation));

		// Puis si l'anniversaire est proche, tous les amis !
		User current = ParametersUtils.getConnectedUser(request);
		if (isBirthdayClose(current)) {
			toBeNotified.addAll(model.userRelations.getAllUsersInRelation(current));
		}

		// Notification
		for (User user : toBeNotified) {
			model.notif.addNotification(user.id, new NotifRecurentIdeaUnbook(current, idea));
		}

		// On supprime les réservations
		model.idees.toutDereserver(idea.getId());

		request.setAttribute("from", getFrom(request, MaListe.PROTECTED_MA_LISTE).substring(1));
		RootingsUtils.rootToPage("/protected/je_le_veux_encore.jsp", request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MaListe.PROTECTED_MA_LISTE, request, response);
	}

}
