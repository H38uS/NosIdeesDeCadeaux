package com.mosioj.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.Categories;
import com.mosioj.model.table.GroupeJoinRequests;
import com.mosioj.model.table.Groupes;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.Priorites;
import com.mosioj.model.table.Users;
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
	// FIXME : pouvoir éditer une idée
	// TODO : réserver une sous partie de l'idée (genre moi je prends le tome XX)
	// TODO : choisir les pseudos de ses relations
	// TODO : bootstrap pour le CSS ??
	// FIXME : faire une option "Mon Compte" pour modifier:
	//				- Affichage des groupes
	//				- Gérer son (ses ?) groupes
	//				- Gérer les paramètres de notification (mail vs dans le site)
	//				- Changer ses infos (pseudo, date de naissance, etc)
	// TODO : envoyer des notifications en fonction de la date de naissance à ceux qui suivent la personne
	// FIXME : ne pas pouvoir créer deux groupes avec le même nom
	// TODO : pouvoir créer plusieurs groupes (3 max pour le moment)
	// FIXME : quand on rentre dans un groupe, pouvoir dire "ne pas être vu de bidule"
	// TODO : externaliser les requêtes SQL et les tester ? Au moins les grosses ??
	// FIXME : ZCompléter le gdoc avec les modifications faites

	/**
	 * L'interface vers la table GROUPES.
	 */
	protected Groupes groupes;

	/**
	 * Interface vers la table GROUPE_JOIN_REQUESTS.
	 */
	protected GroupeJoinRequests groupesJoinRequest;

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
	 * Class constructor.
	 */
	public IdeesCadeauxServlet() {
		groupes = new Groupes();
		groupesJoinRequest = new GroupeJoinRequests();
		validatorConnection = new DataSourceIdKDo();
		users = new Users();
		idees = new Idees();
		categories = new Categories();
		priorities = new Priorites();
	}

	/**
	 * For test purposes.
	 * 
	 * @param pGroupes
	 */
	public void setGroupes(Groupes pGroupes) {
		groupes = pGroupes;
	}

	/**
	 * For test purposes.
	 * 
	 * @param pGroupeJoinRequests
	 */
	public void setGroupeJoinRequests(GroupeJoinRequests pGroupeJoinRequests) {
		groupesJoinRequest = pGroupeJoinRequests;
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
