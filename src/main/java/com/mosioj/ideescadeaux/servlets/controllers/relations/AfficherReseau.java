package com.mosioj.ideescadeaux.servlets.controllers.relations;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mosioj.ideescadeaux.model.Relation;
import com.mosioj.ideescadeaux.model.User;
import com.mosioj.ideescadeaux.servlets.controllers.AbstractListes;
import com.mosioj.ideescadeaux.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.utils.NotLoggedInException;

@WebServlet("/protected/afficher_reseau")
public class AfficherReseau extends AbstractListes<Relation, NetworkAccess> {

	private static final long serialVersionUID = 9147880158497428623L;

	public static final String USER_ID_PARAM = "id";
	public static final String SELF_VIEW = "/protected/afficher_reseau";
	public static final String DISPATCH_URL = "/protected/afficher_reseau.jsp";

	/**
	 * Class constructor.
	 */
	public AfficherReseau() {
		super(new NetworkAccess(USER_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		User friend = policy.getUser();
		int userId = thisOne.id;

		if (userId == friend.id) {
			// Uniquement sur notre compte
			request.setAttribute("demandes", model.userRelationRequests.getRequests(userId));
			request.setAttribute("suggestions", model.userRelationsSuggestion.hasReceivedSuggestion(userId));
		}

		request.setAttribute("id", friend.id);
		request.setAttribute("name", friend.getMyDName());

		HttpSession session = request.getSession();
		Object accepted = session.getAttribute("accepted");
		if (accepted != null) {
			request.setAttribute("accepted", accepted);
			session.removeAttribute("accepted");
		}

		super.ideesKDoGET(request, response);
	}

	@Override
	protected String getViewPageURL() {
		return DISPATCH_URL;
	}

	@Override
	protected String getCallingURL() {
		return SELF_VIEW.substring(1);
	}

	@Override
	protected String getSpecificParameters(HttpServletRequest req) {
		return "&" + USER_ID_PARAM + "=" + policy.getUser().id;
	}

	@Override
	protected int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException {
		return model.userRelations.getRelationsCount(policy.getUser());
	}

	@Override
	protected List<Relation> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException, NotLoggedInException {

		User user = thisOne;
		List<Relation> relations = model.userRelations.getRelations(policy.getUser().id,
																	firstRow,
																	maxNumberOfResults);

		// Ajout du flag network
		for (Relation r : relations) {
			if (model.userRelations.associationExists(r.getSecond().id, user.id)) {
				r.secondIsInMyNetwork = true;
			} else {
				User other = r.getSecond();
				if (model.userRelationRequests.associationExists(user.id, other.id)) {
					other.freeComment = "Vous avez déjà envoyé une demande à " + other.getName();
				}
			}
		}

		return relations;
	}

}
