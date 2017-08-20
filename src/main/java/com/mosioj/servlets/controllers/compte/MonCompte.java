package com.mosioj.servlets.controllers.compte;

import java.io.File;
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

import com.mosioj.model.User;
import com.mosioj.model.UserParameter;
import com.mosioj.notifications.NotificationActivation;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

@WebServlet("/protected/mon_compte")
public class MonCompte extends DefaultCompte {

	private static final long serialVersionUID = -101081965549681889L;
	private static final Logger logger = LogManager.getLogger(MonCompte.class);

	public static final String AVATARS_PATH = "/public/uploaded_pictures/avatars";
	public static final String VIEW_PAGE_URL = "/protected/mon_compte.jsp";
	public static final String URL = "/protected/mon_compte";

	private static File filePath;

	public MonCompte() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		int userId = ParametersUtils.getUserId(req);
		User current = users.getUser(userId);
		req.setAttribute("user", current);

		List<UserParameter> userNotificationParameters = userParameters.getUserNotificationParameters(userId);
		req.setAttribute("notif_types", userNotificationParameters);

		req.setAttribute("possible_values", NotificationActivation.values());
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		// Check that we have a file upload request
		if (ServletFileUpload.isMultipartContent(request)) {

			if (filePath == null) {
				filePath = new File(getServletContext().getRealPath(AVATARS_PATH));
				logger.info(MessageFormat.format("Setting file path to: {0}", filePath.getAbsolutePath()));
				filePath.mkdirs();
			}

			readMultiFormParameters(request, filePath);
			String info = parameters.get("modif_info_gen");
			if ("true".equals(info)) {
				String email = parameters.get("email").trim();
				String name = parameters.get("name").trim();

				int userId = ParametersUtils.getUserId(request);
				List<String> errors = checkEmail(getValidatorEmail(email), userId, true);
				request.getSession().setAttribute("errors_info_gen", errors);

				String birthday = parameters.get("birthday");
				if (!birthday.isEmpty()) {
					logger.debug(MessageFormat.format("Date de naissance: {0}", birthday));
					ParameterValidator val = ValidatorFactory.getFemValidator(birthday, "date d'anniversaire");
					val.checkDateFormat();
					errors.addAll(val.getErrors());
				}

				User user = users.getUser(userId);
				user.email = email;
				user.name = name;
				user.birthday = getAsDate(birthday);

				String image = parameters.get("image");
				String old = parameters.get("old_picture");
				if (image == null || image.isEmpty()) {
					image = old;
				} else {
					// Modification de l'image
					// On supprime la précédente
					removeUploadedImage(filePath, old);
					logger.debug(MessageFormat.format("Updating image from {0} to {1}.", old, image));
				}
				user.avatar = image;

				if (errors.isEmpty()) {
					users.update(user);
				}
			}

		}

		RootingsUtils.redirectToPage(URL, request, response);
	}
}
