package com.mosioj.servlets.controllers.idees;

import java.io.IOException;
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
import com.mosioj.servlets.securitypolicy.IdeaModification;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/modifier_idee")
public class ModifyIdea extends AbstractIdea {

	private static final Logger logger = LogManager.getLogger(ModifyIdea.class);
	private static final long serialVersionUID = -1774633803227715931L;

	private static final String IDEE_ID_PARAM = "id";
	public static final String VIEW_PAGE_URL = "/protected/modify_idea.jsp";
	public static final String PROTECTED_MODIFIER_IDEE = "/protected/modifier_idee";

	/**
	 * Class constructor.
	 */
	public ModifyIdea() {
		super(new IdeaModification(idees, IDEE_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Integer id = ParametersUtils.readInt(req, IDEE_ID_PARAM);

		try {
			Idee idea = idees.getIdea(id);
			req.setAttribute("types", categories.getCategories());
			req.setAttribute("priorites", priorities.getPriorities());
			req.setAttribute("idea", idea);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, req, resp);
			return;
		}

		// FIXME : utiliser les sessions pour passer les paramètres entre post et get !
		Object sessionErrors = req.getSession().getAttribute("errors");
		if (sessionErrors != null) {
			req.setAttribute("errors", sessionErrors);
			req.getSession().removeAttribute("errors");
		}

		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String url = PROTECTED_MODIFIER_IDEE;

		Integer ideaId = ParametersUtils.readInt(request, IDEE_ID_PARAM);
		url = url + "?id=" + ideaId;

		// Check that we have a file upload request
		if (ServletFileUpload.isMultipartContent(request)) {

			try {
				fillIdeaOrErrors(request, response, url);
			} catch (Exception e) {
				RootingsUtils.rootToGenericSQLError(e, request, response);
				return;
			}

			if (!errors.isEmpty()) {
				request.getSession().setAttribute("errors", errors);
			} else {
				try {
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

				} catch (SQLException e) {
					RootingsUtils.rootToGenericSQLError(e, request, response);
					return;
				}
			}

		}

		RootingsUtils.redirectToPage(url, request, response);
	}

}
