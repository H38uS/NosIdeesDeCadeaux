package com.mosioj.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.table.Categories;
import com.mosioj.model.table.GroupIdea;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.Notifications;
import com.mosioj.model.table.Priorites;
import com.mosioj.model.table.UserRelationRequests;
import com.mosioj.model.table.UserRelations;
import com.mosioj.model.table.UserRelationsSuggestion;
import com.mosioj.model.table.Users;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.database.DataSourceIdKDo;

/**
 * An intermediate servlet for test purpose. Increase the visibility of tested method.
 * 
 * @author Jordan Mosio
 *
 */
@SuppressWarnings("serial")
public abstract class IdeesCadeauxServlet extends HttpServlet {

	// TODO : vérifier que l'on redirige bien vers le site quand on est dans une frame etc => vérifier l'URL

	// TODO : réserver une sous partie de l'idée (genre moi je prends le tome XX)
	// TODO : choisir les pseudos de ses relations
	// TODO : bootstrap pour le CSS ??
	// TODO : envoyer des notifications en fonction de la date de naissance à ceux qui suivent la personne
	// TODO : externaliser les requêtes SQL et les tester ? Au moins les grosses ??
	// FIXME : 5 ZCompléter le gdoc avec les modifications faites

	// TODO : pouvoir créer des groupes d'utilisateurs pour les trouver plus facilement
	// TODO : notification quand un anniversaire approche

	// FIXME : 2 pouvoir commenter une idée
	// FIXME : 3 mettre la date dans les commentaires des messages

	// FIXME : 4 pouvoir ajouter des idées à d'autres personnes
	// TODO : pouvoir ajouter des surprises
	// TODO : controle parental
	// TODO : auto logout en javascript

	// TODO : catcher quand la session a expiré, pour faire une joli page
	// TODO : configurer le nombre de jour pour le rappel d'anniversaire

	// FIXME : 1 quand on accepte une relation, pouvoir lui en suggérer d'autres

	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATETIME_DISPLAY_FORMAT = "dd MMM yyyy à HH:mm:ss";

	private static final Logger logger = LogManager.getLogger(IdeesCadeauxServlet.class);

	/**
	 * L'interface vers la table USER_RELATIONS.
	 */
	protected static UserRelations userRelations = new UserRelations();

	/**
	 * Interface vers la table USER_RELATION_REQUESTS.
	 */
	protected UserRelationRequests userRelationRequests;

	/**
	 * Interface vers la table USERS.
	 */
	protected Users users;

	/**
	 * The connection to use for parameters.
	 */
	protected DataSourceIdKDo validatorConnection;

	/**
	 * The connections to the IDEES table.
	 */
	protected static Idees idees = new Idees();

	/**
	 * The connections to the CATEGORIES table.
	 */
	protected Categories categories;

	/**
	 * The connections to the PRIORITIES table.
	 */
	protected Priorites priorities;

	/**
	 * The connections to the NOTIFICATION table.
	 */
	protected static Notifications notif = new Notifications();

	/**
	 * The connections to the GROUP_IDEA and GROUP_IDEA_CONTENT tables.
	 */
	protected GroupIdea groupForIdea;

	/**
	 * The connections to the USER_RELATIONS_SUGGESTION table.
	 */
	protected UserRelationsSuggestion userRelationsSuggestion;

	/**
	 * The security policy defining whether we can interact with the parameters, etc.
	 */
	private final SecurityPolicy policy;

	/**
	 * Class constructor.
	 * 
	 * @param policy The security policy defining whether we can interact with the parameters, etc.
	 */
	public IdeesCadeauxServlet(SecurityPolicy policy) {
		userRelationRequests = new UserRelationRequests();
		validatorConnection = new DataSourceIdKDo();
		users = new Users();
		categories = new Categories();
		priorities = new Priorites();
		groupForIdea = new GroupIdea();
		userRelationsSuggestion = new UserRelationsSuggestion();
		this.policy = policy;
	}

	/**
	 * For test purposes.
	 * 
	 * @param manager
	 */
	public void setNotificationManager(Notifications manager) {
		notif = manager;
	}

	/**
	 * For test purposes.
	 * 
	 * @param pUserRelations
	 */
	public void setUserRelations(UserRelations pUserRelations) {
		userRelations = pUserRelations;
	}

	/**
	 * For test purposes.
	 * 
	 * @param pUserRelationRequests
	 */
	public void setUserRelationRequests(UserRelationRequests pUserRelationRequests) {
		userRelationRequests = pUserRelationRequests;
	}

	/**
	 * For test purposes.
	 * 
	 * @param manager
	 */
	public void setValidatorConnection(DataSourceIdKDo manager) {
		validatorConnection = manager;
	}

	/**
	 * For test purposes.
	 * 
	 * @param pUsers
	 */
	public void setUsers(Users pUsers) {
		users = pUsers;
	}

	/**
	 * For test purposes.
	 * 
	 * @param pIdees
	 */
	public void setIdees(Idees pIdees) {
		idees = pIdees;
	}

	/**
	 * Internal class for GET processing, post security checks.
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public abstract void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (!policy.isGetRequestAllowed()) {
			super.doGet(req, resp);
			return;
		}

		try {

			if (!policy.hasRightToInteractInGetRequest(req, resp)) {
				req.setAttribute("error_message", policy.getLastReason());
				logger.warn(MessageFormat.format(	"Inapropriate GET access from user {0} on {1}. Reason: {2}",
													ParametersUtils.getUserId(req),
													req.getRequestURL(),
													policy.getLastReason()));
				RootingsUtils.rootToPage("/protected/erreur_parametre_ou_droit.jsp", req, resp);
				return;
			}

			// Security has passed, perform the logic
			ideesKDoGET(req, resp);
		} catch (SQLException e) {
			// Default error management
			RootingsUtils.rootToGenericSQLError(e, req, resp);
		}
	};

	/**
	 * Internal class for POST processing, post security checks.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public abstract void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (!policy.isGetRequestAllowed()) {
			super.doGet(request, response);
			return;
		}

		try {

			if (!policy.hasRightToInteractInPostRequest(request, response)) {
				request.setAttribute("error_message", policy.getLastReason());
				logger.warn(MessageFormat.format(	"Inapropriate POST access from user {0} on {1}. Reason: {2}",
													ParametersUtils.getUserId(request),
													request.getRequestURL(),
													policy.getLastReason()));
				RootingsUtils.rootToPage("/protected/erreur_parametre_ou_droit.jsp", request, response);
				return;
			}

			// Security has passed, perform the logic
			ideesKDoPOST(request, response);

		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
		}
	}

	public void setCat(Categories cat) {
		categories = cat;
	}

	public void setPrio(Priorites prio) {
		priorities = prio;
	}

	protected java.sql.Date getAsDate(String date) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
		Date parsed;
		try {
			parsed = format.parse(date);
		} catch (ParseException e) {
			return null;
		}
		java.sql.Date sql = new java.sql.Date(parsed.getTime());
		return sql;
	}

	/**
	 * 
	 * @param request
	 * @param defaultURL The default URL to redirect to if we cannot find the caller.
	 * @return The URL to redirect to.
	 */
	protected String getURLCalling(HttpServletRequest request, String defaultURL) {

		String basePath = "NosIdeesDeCadeaux";
		String rootTo = defaultURL;
		String caller = request.getHeader("Referer");

		if (caller != null && caller.contains(basePath)) {
			rootTo = caller.substring(caller.indexOf(basePath) + basePath.length());
		}
		return rootTo;
	}

}
