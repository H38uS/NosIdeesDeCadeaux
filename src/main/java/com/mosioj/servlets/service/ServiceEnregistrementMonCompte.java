package com.mosioj.servlets.service;

import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.logichelpers.CompteInteractions;
import com.mosioj.servlets.logichelpers.IdeaInteractions;
import com.mosioj.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

@WebServlet("/protected/service/enregistrement_mon_compte")
public class ServiceEnregistrementMonCompte extends AbstractService<AllAccessToPostAndGet> {

	private static final long serialVersionUID = -3371121559895996016L;
	private static final Logger logger = LogManager.getLogger(ServiceEnregistrementMonCompte.class);

	private static File filePath;

	public ServiceEnregistrementMonCompte() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// Do nothing
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		String status = "ko";
		String message = "Le formulaire n'a pas le bon format.";

		String avatar = "";
		String avatarLarge = "";
		String avatarSmall = "";

		if (ServletFileUpload.isMultipartContent(request)) {

			if (filePath == null) {
				filePath = new File(getServletContext().getInitParameter("work_dir"), "uploaded_pictures/avatars");
				logger.info(MessageFormat.format("Setting file path to: {0}", filePath.getAbsolutePath()));
				filePath.mkdirs();
			}

			readMultiFormParameters(request, filePath);
			int userId = thisOne.id;

			List<String> errors = processSave(filePath, parameters, userId);
			if (errors == null || errors.isEmpty()) {
				status = "ok";
				message = "";
				User user = model.users.getUser(userId);
				request.setAttribute("connected_user", user);
				request.getSession().setAttribute("connected_user", user);
				avatar = user.getAvatar();
				avatarLarge = user.getAvatarSrcLarge();
				avatarSmall = user.getAvatarSrcSmall();
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("<ul>");
				for (String error : errors) {
					sb.append("<li>").append(error).append("</li>");
				}
				sb.append("</ul>");
				message = sb.toString();
			}

		}

		writter.writeJSonOutput(response,
								makeJSonPair("status", status),
								makeJSonPair("errors", message),
								makeJSonPair("avatar", avatar),
								makeJSonPair("avatarLarge", avatarLarge),
								makeJSonPair("avatarSmall", avatarSmall),
								makeJSonPair("avatars", request.getAttribute("avatars").toString()));
	}

	// La base est en UTC, il faut donc ne pas utiliser MySimpleDateFormat.
	// Ou alors, avec Hibernate et que la base soit en Europe/Paris.
	public java.sql.Date getAsDate(String date) {
		SimpleDateFormat format = new SimpleDateFormat(IdeesCadeauxServlet.DATE_FORMAT);
		Date parsed;
		try {
			parsed = format.parse(date);
		} catch (ParseException e) {
			return null;
		}
		java.sql.Date sql = new java.sql.Date(parsed.getTime());
		return sql;
	}

	public List<String> processSave(File filePath, Map<String, String> parameters, int userId) throws SQLException {

		CompteInteractions ci = new CompteInteractions();
		String info = parameters.get("modif_info_gen");
		List<String> errors = null;

		if ("true".equals(info)) {

			String email = parameters.get("email").trim();
			String name = parameters.get("name").trim();

			errors = ci.checkEmail(ci.getValidatorEmail(email), userId, true);

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
				List<String> pwdErrors1 = ci.checkPwd(ci.getValidatorPwd(newPwd));
				List<String> pwdErrors2 = ci.checkPwd(ci.getValidatorPwd(confPwd));
				if (!newPwd.equals(confPwd)) {
					errors.add("Les deux mots de passe entrés ne correspondent pas.");
				}
				errors.addAll(pwdErrors1);
				errors.addAll(pwdErrors2);
			}

			User user = model.users.getUser(userId);
			user.email = email;
			user.name = name;
			user.birthday = getAsDate(birthday);

			String image = parameters.get("image");
			String old = parameters.get("old_picture");
			if (image == null || image.isEmpty() || "null".equals(image)) {
				if (old != null && !old.equals("undefined")) {
					image = old;
				} else {
					image = null;
				}
			} else {
				// Modification de l'image
				// On supprime la précédente
				if (!"default.png".equals(old)) {
					IdeaInteractions helper = new IdeaInteractions();
					helper.removeUploadedImage(filePath, old);
				}
				logger.debug(MessageFormat.format("Updating image from {0} to {1}.", old, image));
			}
			user.avatar = image;

			if (errors.isEmpty()) {
				logger.debug(MessageFormat.format("Updating user {0}. Email: {1}, name: {2}", user, email, name));
				model.users.update(user);
				if (!newPwd.isEmpty()) {
					String digested = ci.hashPwd(newPwd, errors);
					model.users.updatePassword(userId, digested);
				}
			}

		}
		return errors;
	}

}
