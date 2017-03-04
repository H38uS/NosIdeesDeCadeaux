package com.mosioj.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.Categories;
import com.mosioj.model.table.GroupIdea;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.Priorites;
import com.mosioj.model.table.UserRelationRequests;
import com.mosioj.model.table.UserRelations;
import com.mosioj.model.table.Users;
import com.mosioj.notifications.NotificationManager;
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
	// TODO : faire une notification "oui c'est à jour" / "non je regarde" quand on demande si c'est à jour
	// TODO : upload / edit d'image
	// TODO : réserver une sous partie de l'idée (genre moi je prends le tome XX)
	// TODO : choisir les pseudos de ses relations
	// TODO : bootstrap pour le CSS ??
	// TODO : envoyer des notifications en fonction de la date de naissance à ceux qui suivent la personne
	// TODO : pouvoir créer plusieurs groupes (3 max pour le moment)
	// TODO : quand on rentre dans un groupe, pouvoir dire "ne pas être vu de bidule"
	// TODO : externaliser les requêtes SQL et les tester ? Au moins les grosses ??
	// FIXME : ZCompléter le gdoc avec les modifications faites
	
	// FIXME : pouvoir créer des groupes d'utilisateurs pour les trouver plus facilement
	// FIXME : date de naissance pour le rappel
	// TODO : notification quand un anniversaire approche
	// FIXME : quand on crée un groupe sur un cadeau, faire une option suggérer à
	// FIXME : mettre la date dans les commentaires des messages
	
	// FIXME : faire une passe sécurité pour voir si c'est bien gérer... Faire des classes master dans les packages

	/**
	 * L'interface vers la table USER_RELATIONS.
	 */
	protected UserRelations userRelations;

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
	protected Idees idees;

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
	protected NotificationManager notif;

	/**
	 * The connections to the GROUP_IDEA and GROUP_IDEA_CONTENT tables.
	 */
	protected GroupIdea groupForIdea;

	/**
	 * Class constructor.
	 */
	public IdeesCadeauxServlet() {
		userRelationRequests = new UserRelationRequests();
		userRelations = new UserRelations();
		validatorConnection = new DataSourceIdKDo();
		users = new Users();
		idees = new Idees();
		categories = new Categories();
		priorities = new Priorites();
		notif = new NotificationManager();
		groupForIdea = new GroupIdea();
	}

	/**
	 * For test purposes.
	 * 
	 * @param manager
	 */
	public void setNotificationManager(NotificationManager manager) {
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

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	};

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	}

	public void setCat(Categories cat) {
		categories = cat;
	}

	public void setPrio(Priorites prio) {
		priorities = prio;
	}

}
