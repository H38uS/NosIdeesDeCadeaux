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
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/modifier_idee")
public class ModifyIdea extends AbstractIdea {

	private static final Logger logger = LogManager.getLogger(ModifyIdea.class);
	private static final long serialVersionUID = -1774633803227715931L;

	public static final String VIEW_PAGE_URL = "/protected/modify_idea.jsp";
	public static final String PROTECTED_MODIFIER_IDEE = "/protected/modifier_idee";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Integer id = ParametersUtils.readInt(req, "id");
		Idee idea = null;

		if (id != null) {
			try {
				idea = getIdeaWithAccessRight(req, id);
				req.setAttribute("types", categories.getCategories());
				req.setAttribute("priorites", priorities.getPriorities());
			} catch (SQLException e) {
				RootingsUtils.rootToGenericSQLError(e, req, resp);
				return;
			}
		}

		req.setAttribute("idea", idea);
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	private Idee getIdeaWithAccessRight(HttpServletRequest req, Integer id) throws SQLException {
		Idee idea;
		idea = idees.getIdea(id);
		if (idea != null && idea.owner != ParametersUtils.getUserId(req)) {
			// On essaie de modifier l'idée de quelqu'un d'autre...
			idea = null;
		}
		return idea;
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String url = PROTECTED_MODIFIER_IDEE;

		String id = request.getParameter("id");
		int ideaId = -1;
		try {
			if (id == null || id.isEmpty()) {
				throw new NumberFormatException();
			}
			ideaId = Integer.parseInt(id);
		} catch (NumberFormatException nfe) {
			RootingsUtils.redirectToPage(url, request, response);
			return;
		}
		url = url + "?id=" + ideaId;

		// Check that we have a file upload request
		if (ServletFileUpload.isMultipartContent(request)) {

			try {
				fillIdeaOrErrors(request, response, url);
			} catch (Exception e) {
				RootingsUtils.rootToGenericSQLError(e, request, response);
				return;
			}


			// Check access
			try {
				Idee idea = getIdeaWithAccessRight(request, ideaId);
				if (idea == null) {
					RootingsUtils.redirectToPage(url, request, response);
					return;
				}
			} catch (SQLException e) {
				RootingsUtils.rootToGenericSQLError(e, request, response);
				return;
			}

			if (!errors.isEmpty()) {
				request.setAttribute("errors", errors);
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

					idees.modifier(	ideaId,
									parameters.get("text"),
									parameters.get("type"),
									parameters.get("priority"),
									image);

				} catch (SQLException e) {
					RootingsUtils.rootToGenericSQLError(e, request, response);
					return;
				}
			}

		}

		RootingsUtils.redirectToPage(url, request, response);
	}

}
