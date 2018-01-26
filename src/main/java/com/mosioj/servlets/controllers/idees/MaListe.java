package com.mosioj.servlets.controllers.idees;

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
import com.mosioj.notifications.NotificationType;
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
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		List<Idee> ideas = idees.getOwnerIdeas(ParametersUtils.getUserId(req));
		for (Idee idee : ideas) {
			idee.hasQuestion = questions.getNbQuestions(idee.getId()) > 0;
		}
		List<Categorie> cat = categories.getCategories();
		List<Priorite> prio = priorities.getPriorities();

		req.setAttribute("idees", ideas);
		req.setAttribute("types", cat);
		req.setAttribute("priorites", prio);

		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		// Check that we have a file upload request
		if (ServletFileUpload.isMultipartContent(request)) {

			fillIdeaOrErrors(request, response);

			if (!errors.isEmpty()) {
				request.setAttribute("errors", errors);
			} else {
				logger.info(MessageFormat.format(	"Adding a new idea [''{0}'' / ''{1}'' / ''{2}'']",
													parameters.get("text"),
													parameters.get("type"),
													parameters.get("priority")));
				int userId = ParametersUtils.getUserId(request);
				int ideaId = idees.addIdea(	userId,
											parameters.get("text"),
											parameters.get("type"),
											Integer.parseInt(parameters.get("priority")),
											parameters.get("image"),
											null);
				addModificationNotification(users.getUser(userId), idees.getIdea(ideaId), true);
				notif.removeAllType(userId, NotificationType.NO_IDEA);
			}

		}

		ideesKDoGET(request, response);
	}

}
