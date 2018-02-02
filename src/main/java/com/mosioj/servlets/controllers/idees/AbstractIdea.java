package com.mosioj.servlets.controllers.idees;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.instance.NotifIdeaModifiedWhenBirthdayIsSoon;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

public abstract class AbstractIdea extends IdeesCadeauxServlet {

	private static final long serialVersionUID = -1774633803227715931L;
	private static final Logger logger = LogManager.getLogger(AbstractIdea.class);
	private File ideasPicturePath;

	protected List<String> errors = new ArrayList<String>();

	/**
	 * 
	 * @param policy The security policy defining whether we can interact with the parameters, etc.
	 */
	public AbstractIdea(SecurityPolicy policy) {
		super(policy);
	}
	
	protected File getIdeaPicturePath() {
		if (ideasPicturePath == null) {
			ideasPicturePath = new File(getServletContext().getInitParameter("work_dir"), "uploaded_pictures/ideas");
		}
		return ideasPicturePath;
	}

	protected void fillIdeaOrErrors(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		errors.clear();

		// Reading parameters
		String text = "";
		String type = "";
		int priority = -1;

		// Parse the request to get file items.
		readMultiFormParameters(request, getIdeaPicturePath());

		text = parameters.get("text");
		type = parameters.get("type");
		priority = Integer.parseInt(parameters.get("priority"));

		if (text.isEmpty() && type.isEmpty() && priority == -1) {
			logger.debug("All parameters are empty.");
			// We can assume we wanted to do a get
			ideesKDoGET(request, response);
			return;
		}

		ParameterValidator valText = ValidatorFactory.getMascValidator(text, "text");
		valText.checkEmpty();

		ParameterValidator valPrio = ValidatorFactory.getFemValidator(priority + "", "priorité");
		valPrio.checkEmpty();
		valPrio.checkIfInteger();

		errors.addAll(valText.getErrors());
		errors.addAll(valPrio.getErrors());
	}

	/**
	 * Ajoute une notification au amis de la personne si son anniversaire approche.
	 * 
	 * @param user
	 * @param idea
	 * @param isNew
	 * @throws SQLException
	 */
	protected void addModificationNotification(User user, Idee idea, boolean isNew) throws SQLException {
		if (user.birthday != null) {
			Calendar birthday = Calendar.getInstance();
			birthday.setTime(new Date(user.birthday.getTime()));
			Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			birthday.set(Calendar.YEAR, today.get(Calendar.YEAR));
			if (birthday.before(today)) {
				birthday.add(Calendar.YEAR, 1);
			}
			today.add(Calendar.DAY_OF_YEAR, NotifIdeaModifiedWhenBirthdayIsSoon.NB_DAYS_BEFORE_BIRTHDAY);
			if (birthday.before(today)) {
				for (User friend : userRelations.getAllUsersInRelation(user.id)) {
					notif.addNotification(friend.id, new NotifIdeaModifiedWhenBirthdayIsSoon(user, idea, isNew));
				}
			}
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param userId
	 * @param idea
	 * @param landingURL
	 * @return True in case of success, false otherwise.
	 * @throws SQLException
	 * @throws ServletException
	 */
	protected boolean sousReserver(HttpServletRequest request, HttpServletResponse response, int userId, Idee idea, String landingURL)
			throws SQLException, ServletException {
			
				List<String> errors = new ArrayList<String>();
				String comment = ParametersUtils.readAndEscape(request, "comment");
				if (comment == null || comment.isEmpty()) {
					errors.add("Le commentaire ne peut pas être vide !");
				}
			
				if (!idees.canSubBook(idea.getId(), userId)) {
					errors.add("L'idée a déjà été réservée, ou vous en avez déjà réservé une sous partie.");
				}
			
				if (!errors.isEmpty()) {
					request.setAttribute("errors", errors);
					RootingsUtils.rootToPage(landingURL, request, response);
					return false;
				}
			
				idees.sousReserver(idea.getId(), userId, comment);
				sousReservation.sousReserver(idea.getId(), userId, comment);
				return true;
			}

}
