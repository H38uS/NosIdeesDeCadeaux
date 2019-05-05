package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mosioj.model.Relation;
import com.mosioj.model.User;
import com.mosioj.servlets.controllers.AbstractListes;
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

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

		Integer user = ParametersUtils.readInt(request, USER_ID_PARAM).get();
		int userId = thisOne.id;

		if (userId == user) {
			// Uniquement sur notre compte
			request.setAttribute("demandes", model.userRelationRequests.getRequests(userId));
			request.setAttribute("suggestions", model.userRelationsSuggestion.hasReceivedSuggestion(userId));
		}

		request.setAttribute("id", user);
		request.setAttribute("name", model.users.getUser(user).getMyDName());

		HttpSession session = request.getSession();
		Object accepted = session.getAttribute("accepted");
		if (accepted != null) {
			request.setAttribute("accepted", accepted);
			session.removeAttribute("accepted");
		}

		super.ideesKDoGET(request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(	SELF_VIEW + "?" + USER_ID_PARAM + "=" + ParametersUtils.readInt(request, USER_ID_PARAM),
										request,
										response);
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
		return "&" + USER_ID_PARAM + "=" + ParametersUtils.readInt(req, USER_ID_PARAM);
	}

	@Override
	protected int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException {
		return model.userRelations.getRelationsCount(model.users.getUser(ParametersUtils.readInt(req, USER_ID_PARAM).get()));
	}

	@Override
	protected List<Relation> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException, NotLoggedInException {

		User user = thisOne;
		List<Relation> relations = model.userRelations.getRelations(ParametersUtils.readInt(req, USER_ID_PARAM).get(),
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
