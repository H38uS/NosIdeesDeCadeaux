package com.mosioj.servlets.service;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/update_notification_parameter")
public class ServiceUpdateNotificationParameter extends AbstractService {

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

		int userId = ParametersUtils.getUserId(request);
		String name = request.getParameter("name");
		String value = request.getParameter("value");
		// FIXME : 0 get parameter attention !!! utiliser parametersUtils
		// FIXME : 0 vérifier le contenu...
		
		if (name != null && value != null) {
			userParameters.insertUpdateParameter(userId, name, value);
		}

		try {
			writeJSonOutput(response, JSONObject.toString("status", "ok"));
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
