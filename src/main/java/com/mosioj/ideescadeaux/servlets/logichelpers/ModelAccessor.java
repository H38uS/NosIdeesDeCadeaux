package com.mosioj.ideescadeaux.servlets.logichelpers;

import com.mosioj.ideescadeaux.model.table.Categories;
import com.mosioj.ideescadeaux.model.table.Comments;
import com.mosioj.ideescadeaux.model.table.GroupIdea;
import com.mosioj.ideescadeaux.model.table.Idees;
import com.mosioj.ideescadeaux.model.table.Notifications;
import com.mosioj.ideescadeaux.model.table.ParentRelationship;
import com.mosioj.ideescadeaux.model.table.Priorites;
import com.mosioj.ideescadeaux.model.table.Questions;
import com.mosioj.ideescadeaux.model.table.SousReservation;
import com.mosioj.ideescadeaux.model.table.UserParameters;
import com.mosioj.ideescadeaux.model.table.UserRelationRequests;
import com.mosioj.ideescadeaux.model.table.UserRelations;
import com.mosioj.ideescadeaux.model.table.UserRelationsSuggestion;
import com.mosioj.ideescadeaux.model.table.Users;

public class ModelAccessor {
	
	/**
	 * Interface vers la table USERS.
	 */
	public final Users users = new Users();

	/**
	 * The connections to the CATEGORIES table.
	 */
	public final Categories categories = new Categories();

	/**
	 * The connections to the PRIORITIES table.
	 */
	public final Priorites priorities = new Priorites();

	/**
	 * The connections to the NOTIFICATION table. Static because it can be used in constructor for security checks.
	 */
	public final Notifications notif = new Notifications();

	/**
	 * The connections to the GROUP_IDEA and GROUP_IDEA_CONTENT tables.
	 */
	public final GroupIdea groupForIdea = new GroupIdea();

	/**
	 * The connections to the USER_RELATIONS_SUGGESTION table.
	 */
	public final UserRelationsSuggestion userRelationsSuggestion = new UserRelationsSuggestion();

	/**
	 * The connections to the USER_PARAMETERS table.
	 */
	public final UserParameters userParameters = new UserParameters();

	/**
	 * The connections to the SOUS_RESERVATION table.
	 */
	public final SousReservation sousReservation = new SousReservation();

	/**
	 * L'interface vers la table USER_RELATIONS. Static because it can be used in constructor for security checks.
	 */
	public final UserRelations userRelations = new UserRelations();
	/**
	 * Interface vers la table USER_RELATION_REQUESTS.
	 */
	public final UserRelationRequests userRelationRequests = new UserRelationRequests();
	/**
	 * The connections to the IDEES table. Static because it can be used in constructor for security checks.
	 */
	public final Idees idees = new Idees();
	/**
	 * The connections to the COMMENTS table. Static because it can be used in constructor for security checks.
	 */
	public final Comments comments = new Comments();
	/**
	 * The connections to the QUESTIONS table. Static because it can be used in constructor for security checks.
	 */
	public final Questions questions = new Questions();
	/**
	 * The connection to the PARENT_RELATIONSHIP table.
	 */
	public final ParentRelationship parentRelationship = new ParentRelationship();

}
