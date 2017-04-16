package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.servlets.controllers.MesListes;
import com.mosioj.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/est_a_jour")
public class EstAJour extends AbstractIdea {

	private static final long serialVersionUID = -2229577569569388562L;
	private static final String IDEE_FIELD_PARAMETER = "idee";
	private static final String VIEW_PAGE_URL = "est_a_jour_succes.jsp";
	private static final String ERROR_PAGE_URL = "est_a_jour_error.jsp";

	public EstAJour() {
		super(new IdeaInteraction(userRelations, idees, IDEE_FIELD_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		Integer id = ParametersUtils.readInt(req, IDEE_FIELD_PARAMETER);
		Idee idea = idees.getIdea(id);
		
		NotifAskIfIsUpToDate isUpToDateNotif = new NotifAskIfIsUpToDate(users.getUser(ParametersUtils.getUserId(req)), idea);
		if (notif.hasNotification(idea.owner.id, isUpToDateNotif)) {
			req.setAttribute("name", idea.owner.name);
			req.setAttribute("error_message", "Une demande sur cette idée a déjà été envoyée.");
			RootingsUtils.rootToPage(ERROR_PAGE_URL, req, resp);
		} else {
			notif.addNotification(idea.owner.id, isUpToDateNotif);
			req.setAttribute("text", idea.getText(50));
			req.setAttribute("user", idea.owner.name);
			RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
		}
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
	}

}
