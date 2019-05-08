package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.text.MessageFormat;
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
import com.mosioj.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicyGetAndPost;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

public abstract class AbstractIdea<P extends SecurityPolicyGetAndPost> extends IdeesCadeauxGetAndPostServlet<P> {

	private static final long serialVersionUID = -1774633803227715931L;
	private static final Logger logger = LogManager.getLogger(AbstractIdea.class);

	private static final String FROM_URL = "from";

	protected List<String> errors = new ArrayList<String>();

	/**
	 * 
	 * @param policy The security policy defining whether we can interact with the parameters, etc.
	 */
	public AbstractIdea(P policy) {
		super(policy);
	}

	/**
	 * Tries to read a from request parameters or from parameters map. If it looks coming from the web site, returns it.
	 * Otherwise, returns the default value.
	 * 
	 * @param request Current request being processed.
	 * @param defaultValue Default value for the next redirection.
	 * @return The next page to be redirected to.
	 */
	protected String getFrom(HttpServletRequest request, String defaultValue) {

		String from = ParametersUtils.readIt(request, FROM_URL);
		logger.debug(MessageFormat.format("Resolving request from: {0}", from));

		if (from == null || from.trim().isEmpty()) {

			if (parameters == null) {
				return defaultValue;
			}

			// Trying to resolve it from parameters
			from = parameters.get(FROM_URL);
			if (from == null || from.trim().isEmpty()) {
				return defaultValue;
			}
		}

		if (!from.startsWith("/")) {
			// Looks like it is not coming from the website...
			return defaultValue;
		}

		return from;
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
	 * 
	 * @param user The user.
	 * @return True if and only if the birthday of this user is set up, and will come in less than
	 *         NotifIdeaModifiedWhenBirthdayIsSoon.NB_DAYS_BEFORE_BIRTHDAY.
	 */
	protected boolean isBirthdayClose(User user) {

		if (user.birthday == null) {
			return false;
		}

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
		logger.debug(MessageFormat.format("Resovled birthday: {0}", birthday));

		return birthday.before(today);
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
		if (isBirthdayClose(user)) {
			for (User friend : model.userRelations.getAllUsersInRelation(user)) {
				model.notif.addNotification(friend.id, new NotifIdeaModifiedWhenBirthdayIsSoon(user, idea, isNew));
			}
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param user
	 * @param idea
	 * @param landingURL
	 * @return True in case of success, false otherwise.
	 * @throws SQLException
	 * @throws ServletException
	 */
	protected boolean sousReserver(	HttpServletRequest request,
									HttpServletResponse response,
									User user,
									Idee idea,
									String landingURL) throws SQLException, ServletException {

		List<String> errors = new ArrayList<String>();
		String comment = ParametersUtils.readAndEscape(request, "comment");
		if (comment == null || comment.isEmpty()) {
			errors.add("Le commentaire ne peut pas être vide !");
		}

		if (!model.idees.canSubBook(idea.getId(), user.id)) {
			errors.add("L'idée a déjà été réservée, ou vous en avez déjà réservé une sous partie.");
		}

		if (!errors.isEmpty()) {
			request.setAttribute("errors", errors);
			RootingsUtils.rootToPage(landingURL, request, response);
			return false;
		}

		model.idees.sousReserver(idea.getId(), user.id, comment);
		model.sousReservation.sousReserver(idea.getId(), user.id, comment);
		return true;
	}

}
