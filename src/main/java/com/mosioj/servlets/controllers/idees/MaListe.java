package com.mosioj.servlets.controllers.idees;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Categorie;
import com.mosioj.model.Idee;
import com.mosioj.model.Priorite;
import com.mosioj.notifications.instance.NotifNoIdea;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.validators.ParameterValidator;

@WebServlet("/protected/ma_liste")
public class MaListe extends IdeesCadeauxServlet {

	/**
	 * Class logger.
	 */
	private static final Logger logger = LogManager.getLogger(MaListe.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -1774633803227715931L;

	public static final String VIEW_PAGE_URL = "/protected/ma_liste.jsp";

	// Maximum 10M
	private static final int MAX_MEM_SIZE = 1024 * 1024 * 10;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// FIXME : pouvoir modifier ses idées
		// FIXME : supprimer les photos quand on supprime l'idée
		// FIXME : supprimer les photos quand on modifie la photo d'une idée
		// FIXME : générer un nom pour la photo
		// FIXME : cleaner le csrf de la requête
		// TODO : faire des miniatures et garder les grandes images

		// Check that we have a file upload request
		if (ServletFileUpload.isMultipartContent(request)) {

			File filePath = new File(getServletContext().getRealPath("/public/uploaded_pictures"));

			DiskFileItemFactory factory = new DiskFileItemFactory();
			// maximum size that will be stored in memory
			factory.setSizeThreshold(MAX_MEM_SIZE);
			factory.setRepository(filePath);

			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(MAX_MEM_SIZE);

			// Reading parameters
			String text = "";
			String type = "";
			String priority = "";
			String image = "";

			try {
				// Parse the request to get file items.
				for (FileItem fi : upload.parseRequest(request)) {
					if (!fi.isFormField()) {
						String fileName = fi.getName();
						if (!fileName.trim().isEmpty() && image.isEmpty()) {
							image = fileName;
							File file = new File(filePath, fileName);
							logger.debug("Uploading file : " + file);
							fi.write(file);
						}
					} else {
						if ("text".equals(fi.getFieldName())) {
							text = fi.getString();
						}
						if ("type".equals(fi.getFieldName())) {
							type = fi.getString();
						}
						if ("priority".equals(fi.getFieldName())) {
							priority = fi.getString();
						}
					}
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}

			if (text.isEmpty() && type.isEmpty() && priority.isEmpty()) {
				logger.debug("All parameters are empty.");
				// We can assume we wanted to do a get
				doGet(request, response);
				return;
			}

			ParameterValidator valText = new ParameterValidator(text, "text", "Le ");
			valText.checkEmpty();

			ParameterValidator valPrio = new ParameterValidator(priority, "priorité", "La ");
			valPrio.checkEmpty();
			valPrio.checkIfInteger();

			List<String> errors = new ArrayList<String>();
			errors.addAll(valText.getErrors());
			errors.addAll(valPrio.getErrors());

			if (!errors.isEmpty()) {
				request.setAttribute("errors", errors);
			} else {
				try {
					logger.info(MessageFormat.format(	"Adding a new idea [''{0}'' / ''{1}'' / ''{2}'']",
														text,
														type,
														priority));
					int userId = ParametersUtils.getUserId(request);
					idees.addIdea(userId, text, type, priority, image);
					notif.remove(userId, new NotifNoIdea());
				} catch (SQLException e) {
					RootingsUtils.rootToGenericSQLError(e, request, response);
					return;
				}
			}

		}

		doGet(request, response);
	}

}
