package com.mosioj.servlets.controllers.compte;

import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

	public static final String VIEW_PAGE_URL = "/protected/mon_compte.jsp";
	public static final String URL = "/protected/mon_compte";

	private static File filePath;

	public MonCompte() {
		super(new AllAccessToPostAndGet());
	}
	
	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse resp) throws ServletException, SQLException {

		int userId = ParametersUtils.getUserId(request);
		User current = users.getUser(userId);
		request.setAttribute("user", current);

		HttpSession session = request.getSession(); // FIXME idem les autres
		if (session.getAttribute("sauvegarde_ok") != null) {
			request.setAttribute("sauvegarde_ok", session.getAttribute("sauvegarde_ok"));
			session.removeAttribute("sauvegarde_ok");
		}
		if (session.getAttribute("errors_info_gen") != null) {
			request.setAttribute("errors_info_gen", session.getAttribute("errors_info_gen"));
			session.removeAttribute("errors_info_gen");
		}
		
		List<UserParameter> userNotificationParameters = userParameters.getUserNotificationParameters(userId);
		request.setAttribute("notif_types", userNotificationParameters);
		
		request.setAttribute("parents", parentRelationship.getParents(userId));
		request.setAttribute("children", parentRelationship.getChildren(userId));

		request.setAttribute("possible_values", NotificationActivation.values());
		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		// Check that we have a file upload request
		if (ServletFileUpload.isMultipartContent(request)) {

			if (filePath == null) {
				filePath = new File(getServletContext().getInitParameter("work_dir"), "uploaded_pictures/avatars");
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
				
				String newPwd = parameters.get("new_password").trim();
				String confPwd = parameters.get("conf_password").trim();
				
				
				if (newPwd != null && !newPwd.isEmpty()) {
					List<String> pwdErrors1 = checkPwd(getValidatorPwd(newPwd));
					List<String> pwdErrors2 = checkPwd(getValidatorPwd(confPwd));
					if (!newPwd.equals(confPwd)) {
						errors.add("Les deux mots de passe entrés ne correspondent pas.");
					}
					errors.addAll(pwdErrors1);
					errors.addAll(pwdErrors2);
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
					if (!"default.png".equals(old)) {
						removeUploadedImage(filePath, old);
					}
					logger.debug(MessageFormat.format("Updating image from {0} to {1}.", old, image));
				}
				user.avatar = image;

				if (errors.isEmpty()) {
					users.update(user);
					request.getSession().setAttribute("emailorname", user.getName());
					if (!newPwd.isEmpty()) {
						String digested = hashPwd(newPwd, errors);
						users.updatePassword(userId, digested);
					}
					request.getSession().setAttribute("sauvegarde_ok", true);
				}
			}

		}

		RootingsUtils.redirectToPage(URL, request, response);
	}
	
}
