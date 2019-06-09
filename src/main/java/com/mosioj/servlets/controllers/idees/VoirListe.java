package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.NotLoggedInException;

@WebServlet("/protected/voir_liste")
public class VoirListe extends MesListes {

	private static final long serialVersionUID = -5233551522645668356L;
	public static final String USER_ID_PARAM = "id";
	public static final String PROTECTED_VOIR_LIST = "/protected/voir_liste";
	private static final Logger logger = LogManager.getLogger(VoirListe.class);

	/**
	 * Class constructor.
	 * 
	 */
	public VoirListe() {
		super(new NetworkAccess(USER_ID_PARAM));
	}
	
	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		
		Object ideaId = request.getSession().getAttribute("added_idea_id");
		if (ideaId != null) {
			request.getSession().removeAttribute("added_idea_id");
			try {
				Idee idea = getIdeaAndEnrichIt(request, Integer.parseInt(ideaId.toString()));
				if (idea.owner == thisOne) {
					request.setAttribute("idee", idea);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.warn("Exception when retrieving added_idea_id: " + e.getMessage());
				// Osef
			}
		}
		
		super.ideesKDoGET(request, response);
	}

	@Override
	protected List<User> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException, NotLoggedInException {
		List<User> ids = new ArrayList<User>();
		User user = ((NetworkAccess) policy).getUser();
		ids.add(user);
		fillsUserIdeas(thisOne, ids);
		return ids;
	}

	@Override
	protected int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException {
		return 1;
	}

	@Override
	protected String getCallingURL() {
		return PROTECTED_VOIR_LIST.substring(1);
	}

	@Override
	protected String getSpecificParameters(HttpServletRequest req) {
		return "";
	}

}
