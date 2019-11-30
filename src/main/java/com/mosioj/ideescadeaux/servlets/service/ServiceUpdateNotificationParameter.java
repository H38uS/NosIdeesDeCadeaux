package com.mosioj.ideescadeaux.servlets.service;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.utils.ParametersUtils;

@WebServlet("/protected/service/update_notification_parameter")
public class ServiceUpdateNotificationParameter extends AbstractServicePost<AllAccessToPostAndGet> {

	private static final long serialVersionUID = 8087174276226168482L;
	private static final Logger logger = LogManager.getLogger(ServiceUpdateNotificationParameter.class);

	public ServiceUpdateNotificationParameter() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

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
