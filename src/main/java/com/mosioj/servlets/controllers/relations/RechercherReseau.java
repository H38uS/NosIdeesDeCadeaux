package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Relation;
import com.mosioj.model.User;
import com.mosioj.servlets.controllers.AbstractListes;
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/rechercher_reseau")
public class RechercherReseau extends AbstractListes<Relation, NetworkAccess> {

	private static final long serialVersionUID = 9147880158497428623L;

	private static final String USER_ID_PARAM = "id";
	private static final String SEARCH_USER_PARAM = "looking_for";

	/**
	 * Class constructor.
	 */
	public RechercherReseau() {
		super(new NetworkAccess(USER_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		String nameOrEmail = ParametersUtils.readAndEscape(request, SEARCH_USER_PARAM);
		int networkOwner = ParametersUtils.readInt(request, USER_ID_PARAM).get();
		request.setAttribute("id", networkOwner);
		request.setAttribute("name", model.users.getUser(networkOwner).getName());
		request.setAttribute(SEARCH_USER_PARAM, nameOrEmail);

		super.ideesKDoGET(request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		ideesKDoGET(request, response);
	}

	@Override
	protected String getViewPageURL() {
		return AfficherReseau.DISPATCH_URL;
	}

	@Override
	protected String getCallingURL() {
		return "protected/rechercher_reseau";
	}

	@Override
	protected String getSpecificParameters(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		sb.append("&");
		sb.append(USER_ID_PARAM);
		sb.append("=");
		sb.append(ParametersUtils.readInt(req, USER_ID_PARAM));
		sb.append("&");
		sb.append(SEARCH_USER_PARAM);
		sb.append("=");
		sb.append(ParametersUtils.readAndEscape(req, SEARCH_USER_PARAM));
		return sb.toString();
	}

	@Override
	protected int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException {
		return model.userRelations.getRelationsCount(	ParametersUtils.readInt(req, USER_ID_PARAM).get(),
														ParametersUtils.readAndEscape(req, SEARCH_USER_PARAM));
	}

	@Override
	protected List<Relation> getDisplayedEntities(int firstRow, HttpServletRequest request) throws SQLException, NotLoggedInException {

		String nameOrEmail = ParametersUtils.readAndEscape(request, SEARCH_USER_PARAM);
		List<Relation> relations = model.userRelations.getRelations(ParametersUtils.readInt(request, USER_ID_PARAM).get(),
																	nameOrEmail,
																	firstRow,
																	maxNumberOfResults);
		int userId = thisOne.id;

		// Ajout du flag network
		for (Relation r : relations) {
			if (model.userRelations.associationExists(r.getSecond().id, userId)) {
				r.secondIsInMyNetwork = true;
			} else {
				User other = r.getSecond();
				if (model.userRelationRequests.associationExists(userId, other.id)) {
					other.freeComment = "Vous avez déjà envoyé une demande à " + other.getName();
				}
			}
		}

		return relations;
	}

}
