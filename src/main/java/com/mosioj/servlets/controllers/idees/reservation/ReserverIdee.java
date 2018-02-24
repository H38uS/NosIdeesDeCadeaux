package com.mosioj.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.controllers.idees.MesListes;
import com.mosioj.servlets.securitypolicy.IdeaInteractionBookingUpToDate;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/reserver")
public class ReserverIdee extends IdeesCadeauxServlet {

	private static final Logger logger = LogManager.getLogger(ReserverIdee.class);
	private static final long serialVersionUID = 7349100644264613480L;
	private static final String IDEA_ID_PARAM = "idee";
	public static final String FROM_URL = "from";

	/**
	 * Class constructor
	 */
	public ReserverIdee() {
		super(new IdeaInteractionBookingUpToDate(userRelations, idees, IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		String from = ParametersUtils.readIt(req, FROM_URL);
		logger.debug(MessageFormat.format("Deleting idea from: {0}", from));
		if (from == null || from.trim().isEmpty()) {
			from = MesListes.PROTECTED_MES_LISTES;
		}
		Integer idea = ParametersUtils.readInt(req, IDEA_ID_PARAM);
		int userId = ParametersUtils.getUserId(req);

		if (idees.canBook(idea, userId)) {
			idees.reserver(idea, userId);
		}

		RootingsUtils.redirectToPage(from, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
	}

}
