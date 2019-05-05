package com.mosioj.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifRecurentIdeaUnbook;
import com.mosioj.servlets.controllers.idees.AbstractIdea;
import com.mosioj.servlets.controllers.idees.MesListes;
import com.mosioj.servlets.securitypolicy.IdeaInteractionBookingUpToDate;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/reserver")
public class ReserverIdee extends AbstractIdea<IdeaInteractionBookingUpToDate> {

	private static final Logger logger = LogManager.getLogger(ReserverIdee.class);
	private static final long serialVersionUID = 7349100644264613480L;
	public static final String IDEA_ID_PARAM = "idee";

	/**
	 * Class constructor
	 */
	public ReserverIdee() {
		super(new IdeaInteractionBookingUpToDate(IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		Idee idea = policy.getIdea();
		int userId = thisOne.id;
		logger.debug(MessageFormat.format("Réservation de l''idée {0} par {1}.", idea.getId(), userId));
		
		if (model.idees.canBook(idea.getId(), userId)) {
			model.idees.reserver(idea.getId(), userId);
			for (AbstractNotification n : model.notif.getNotification(ParameterName.IDEA_ID, idea.getId())) {
				if (n instanceof NotifRecurentIdeaUnbook) {
					model.notif.remove(n.id);
				}
			}
		}
		
		RootingsUtils.redirectToPage(getFrom(request, MesListes.PROTECTED_MES_LISTES), request, response);
	}

}
