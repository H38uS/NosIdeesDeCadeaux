package com.mosioj.servlets.service;

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
import com.mosioj.servlets.logichelpers.CompteInteractions;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/enregistrement_mon_compte")
public class ServiceEnregistrementMonCompte extends AbstractService {

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
			int userId = ParametersUtils.getUserId(request);

			CompteInteractions helper = new CompteInteractions();
			List<String> errors = helper.processSave(filePath, parameters, userId);
			if (errors == null || errors.isEmpty()) {
				status = "ok";
				message = "";
				User user = users.getUser(userId);
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

		writeJSonOutput(response,
						makeJSonPair("status", status),
						makeJSonPair("errors", message),
						makeJSonPair("avatar", avatar),
						makeJSonPair("avatarLarge", avatarLarge),
						makeJSonPair("avatarSmall", avatarSmall),
						makeJSonPair("avatars", request.getAttribute("avatars").toString()));
	}

}
