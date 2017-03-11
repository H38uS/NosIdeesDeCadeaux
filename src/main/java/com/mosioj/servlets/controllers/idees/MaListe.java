package com.mosioj.servlets.controllers.idees;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Categorie;
import com.mosioj.model.Idee;
import com.mosioj.model.Priorite;
import com.mosioj.notifications.instance.NotifNoIdea;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/ma_liste")
public class MaListe extends AbstractIdea {

	/**
	 * Class logger.
	 */
	private static final Logger logger = LogManager.getLogger(MaListe.class);
	private static final long serialVersionUID = -1774633803227715931L;

	public static final String VIEW_PAGE_URL = "/protected/ma_liste.jsp";
	public static final String PROTECTED_MA_LISTE = "/protected/ma_liste";

	/**
	 * Class constructor.
	 */
	public MaListe() {
		// No security : we will see only our content.
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		List<Idee> ideas = null;
		List<Categorie> cat = null;
		List<Priorite> prio = null;
		try {
			ideas = idees.getOwnerIdeas(ParametersUtils.getUserId(req));
			cat = categories.getCategories();
			prio = priorities.getPriorities();
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, req, resp);
			return;
		}
		req.setAttribute("idees", ideas);
		req.setAttribute("types", cat);
		req.setAttribute("priorites", prio);

		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Check that we have a file upload request
		if (ServletFileUpload.isMultipartContent(request)) {

			try {
				fillIdeaOrErrors(request, response, PROTECTED_MA_LISTE);
			} catch (Exception e1) {
				RootingsUtils.rootToGenericSQLError(e1, request, response);
				return;
			}

			if (!errors.isEmpty()) {
				request.setAttribute("errors", errors);
			} else {
				try {
					logger.info(MessageFormat.format(	"Adding a new idea [''{0}'' / ''{1}'' / ''{2}'']",
														parameters.get("text"),
														parameters.get("type"),
														parameters.get("priority")));
					int userId = ParametersUtils.getUserId(request);
					idees.addIdea(	userId,
									parameters.get("text"),
									parameters.get("type"),
									Integer.parseInt(parameters.get("priority")),
									parameters.get("image"));
					notif.remove(userId, new NotifNoIdea());
				} catch (SQLException e) {
					RootingsUtils.rootToGenericSQLError(e, request, response);
					return;
				}
			}

		}

		RootingsUtils.redirectToPage(PROTECTED_MA_LISTE, request, response);
	}

}
