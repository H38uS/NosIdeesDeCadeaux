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

import com.mosioj.model.Idee;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.notifications.instance.NotifConfirmedUpToDate;
import com.mosioj.servlets.securitypolicy.IdeaModification;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.viewhelper.Escaper;

@WebServlet("/protected/modifier_idee")
public class ModifyIdea extends AbstractIdea {

	private static final Logger logger = LogManager.getLogger(ModifyIdea.class);
	private static final long serialVersionUID = -1774633803227715931L;

	public static final String IDEE_ID_PARAM = "id";
	public static final String VIEW_PAGE_URL = "/protected/modify_idea.jsp";
	public static final String PROTECTED_MODIFIER_IDEE = "/protected/modifier_idee";

	/**
	 * Class constructor.
	 */
	public ModifyIdea() {
		super(new IdeaModification(idees, IDEE_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		Integer id = ParametersUtils.readInt(req, IDEE_ID_PARAM);

		Idee idea = idees.getIdea(id);
		idea.text = Escaper.htmlToText(idea.text);

		req.setAttribute("types", categories.getCategories());
		req.setAttribute("priorites", priorities.getPriorities());
		req.setAttribute("idea", idea);

		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		String url = PROTECTED_MODIFIER_IDEE;

		Integer ideaId = ParametersUtils.readInt(request, IDEE_ID_PARAM);
		url = url + "?id=" + ideaId;

		// Check that we have a file upload request
		if (ServletFileUpload.isMultipartContent(request)) {

			fillIdeaOrErrors(request, response, url);

			if (!errors.isEmpty()) {
				request.getSession().setAttribute("errors", errors);
			} else {
				logger.info(MessageFormat.format(	"Modifying an idea [''{0}'' / ''{1}'' / ''{2}'']",
													parameters.get("text"),
													parameters.get("type"),
													parameters.get("priority")));

				String image = parameters.get("image");
				String old = parameters.get("old_picture");
				if (image == null || image.isEmpty()) {
					image = old;
				} else {
					// Modification de l'image
					// On supprime la précédente
					removeUploadedImage(old);
					logger.debug(MessageFormat.format("Updating image from {0} to {1}.", old, image));
				}

				idees.modifier(ideaId, parameters.get("text"), parameters.get("type"), parameters.get("priority"), image);

				List<AbstractNotification> notifications = notif.getNotification(ParameterName.IDEA_ID, ideaId);
				for (AbstractNotification notification : notifications) {
					if (notification instanceof NotifAskIfIsUpToDate) {
						NotifAskIfIsUpToDate isUpToDate = (NotifAskIfIsUpToDate) notification;
						notif.addNotification(	isUpToDate.getUserIdParam(),
												new NotifConfirmedUpToDate(	users.getUser(ParametersUtils.getUserId(request)),
																			idees.getIdea(ideaId)));
						notif.remove(notification.id);
					}
				}
			}

		}

		RootingsUtils.redirectToPage(url, request, response);
	}

}
