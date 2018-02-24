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
import com.mosioj.servlets.securitypolicy.SurpriseModification;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/supprimer_surprise")
public class SupprimerSurprise extends IdeesCadeauxServlet {

	private static final Logger logger = LogManager.getLogger(SupprimerSurprise.class);
	private static final long serialVersionUID = -8244829899125982644L;
	private static final String IDEA_ID_PARAM = "idee";
	public static final String FROM_URL = "from";

	/**
	 * Class constructor
	 */
	public SupprimerSurprise() {
		super(new SurpriseModification(userRelations, idees, IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse resp) throws ServletException, SQLException {
		String from = ParametersUtils.readIt(request, FROM_URL);
		logger.debug(MessageFormat.format("Deleting idea from: {0}", from));
		if (from == null || from.trim().isEmpty()) {
			from = MesListes.PROTECTED_MES_LISTES;
		}
		Integer idea = ParametersUtils.readInt(request, IDEA_ID_PARAM);
		idees.remove(idea);
		RootingsUtils.redirectToPage(from, request, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
	}

}
