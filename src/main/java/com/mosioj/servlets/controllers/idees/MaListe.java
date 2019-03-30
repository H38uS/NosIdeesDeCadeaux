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
import com.mosioj.model.Priorite;
import com.mosioj.model.User;
import com.mosioj.notifications.NotificationType;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/ma_liste")
public class MaListe extends AbstractIdea<AllAccessToPostAndGet> {

	/**
	 * Class logger.
	 */
	private static final Logger logger = LogManager.getLogger(MaListe.class);
	private static final long serialVersionUID = -1774633803227715931L;

	public static final String VIEW_PAGE_URL = "/protected/completer_ma_liste.jsp";
	public static final String PROTECTED_MA_LISTE = "/protected/ma_liste";

	/**
	 * Class constructor.
	 */
	public MaListe() {
		// No security : we will see only our content.
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse resp) throws ServletException, SQLException {

		List<Categorie> cat = model.categories.getCategories();
		List<Priorite> prio = model.priorities.getPriorities();

		request.setAttribute("types", cat);
		request.setAttribute("priorites", prio);

		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, resp);
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
				User user = ParametersUtils.getConnectedUser(request);
				int ideaId = model.idees.addIdea(	user,
											parameters.get("text"),
											parameters.get("type"),
											Integer.parseInt(parameters.get("priority")),
											parameters.get("image"),
											null,
											user);
				addModificationNotification(user, model.idees.getIdeaWithoutEnrichment(ideaId), true);
				model.notif.removeAllType(user, NotificationType.NO_IDEA);

				request.getSession().setAttribute("added_idea_id", ideaId);

				RootingsUtils.redirectToPage(	VoirListe.PROTECTED_VOIR_LIST + "?" + VoirListe.USER_ID_PARAM + "=" + user.id,
												request,
												response);
				return;
			}

		}

		ideesKDoGET(request, response);
	}

}
