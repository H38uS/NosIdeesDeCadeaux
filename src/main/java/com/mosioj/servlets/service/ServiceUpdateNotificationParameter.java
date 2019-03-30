package com.mosioj.servlets.service;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.notifications.NotificationType;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/update_notification_parameter")
public class ServiceUpdateNotificationParameter extends AbstractService<AllAccessToPostAndGet> {

	private static final long serialVersionUID = 8087174276226168482L;
	private static final Logger logger = LogManager.getLogger(ServiceUpdateNotificationParameter.class);

	public ServiceUpdateNotificationParameter() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// Nothing to do
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		User thisOne = ParametersUtils.getConnectedUser(request);
		String name = ParametersUtils.readAndEscape(request, "name");
		String value = ParametersUtils.readAndEscape(request, "value");

		String statut = "ko";

		try {
			if (name != null && value != null) {
				NotificationType.valueOf(name);
				model.userParameters.insertUpdateParameter(thisOne, name, value);
				statut = "ok";
			}
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
		}

		writter.writeJSonOutput(response, makeJSonPair("status", statut));
	}

}
