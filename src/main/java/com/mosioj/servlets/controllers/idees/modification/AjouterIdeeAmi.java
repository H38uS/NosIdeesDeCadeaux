package com.mosioj.servlets.controllers.idees.modification;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.servlets.controllers.idees.AbstractIdea;
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/ajouter_idee_ami")
public class AjouterIdeeAmi extends AbstractIdea<NetworkAccess> {

	private static final long serialVersionUID = -7053283110787519597L;
	private static final Logger logger = LogManager.getLogger(AjouterIdeeAmi.class);

	public static final String USER_PARAMETER = "id";

	public static final String VIEW_PAGE_URL = "/protected/ajouter_idee_ami.jsp";

	public AjouterIdeeAmi() {
		super(new NetworkAccess(userRelations, USER_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Integer id = ParametersUtils.readInt(request, USER_PARAMETER);
		User user = users.getUser(id);

		request.setAttribute("user", user);
		request.setAttribute("types", categories.getCategories());
		request.setAttribute("priorites", priorities.getPriorities());

		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Integer id = ParametersUtils.readInt(request, USER_PARAMETER);

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
				User currentUser = users.getUser(ParametersUtils.getUserId(request));
				boolean estSurprise = false;
				if ("on".equals(parameters.get("est_surprise"))) {
					if (id != currentUser.id) {
						estSurprise = true;
					}
				}
				int ideaId = idees.addIdea(	id,
											parameters.get("text"),
											parameters.get("type"),
											Integer.parseInt(parameters.get("priority")),
											parameters.get("image"),
											estSurprise ? currentUser : null,
											currentUser);
				Idee idea = getIdeaAndEnrichIt(request, ideaId);
				request.setAttribute("idee", idea);

				if (!estSurprise) {
					notif.addNotification(id, new NotifIdeaAddedByFriend(currentUser, idea));
					notif.removeAllType(id, NotificationType.NO_IDEA);
				}
			}

		}

		User user = users.getUser(id);
		request.setAttribute("user", user);
		request.setAttribute("types", categories.getCategories());
		request.setAttribute("priorites", priorities.getPriorities());

		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}

}
