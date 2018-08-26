package com.mosioj.servlets.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import com.mosioj.servlets.securitypolicy.NotificationModification;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/notification_delete")
public class NotificationDeleteService extends AbstractService {

	private static final long serialVersionUID = 2642366164643542379L;
	private static final String NOTIFICATION_PARAMETER = "notif_id";

	private static final Logger logger = LogManager.getLogger(NotificationDeleteService.class);

	public NotificationDeleteService() {
		super(new NotificationModification(notif, NOTIFICATION_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// Do nothing
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		notif.remove(ParametersUtils.readInt(request, NOTIFICATION_PARAMETER));
		logger.info(MessageFormat.format(	"Suppression de la notification {0}",
											ParametersUtils.readInt(request, NOTIFICATION_PARAMETER)));
		try {
			writeJSonOutput(response, JSONObject.toString("status", "ok"));
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
