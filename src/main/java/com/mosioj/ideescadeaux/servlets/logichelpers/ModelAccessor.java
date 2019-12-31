package com.mosioj.ideescadeaux.servlets.logichelpers;

import com.mosioj.ideescadeaux.model.repositories.Categories;
import com.mosioj.ideescadeaux.model.repositories.Comments;
import com.mosioj.ideescadeaux.model.repositories.Idees;
import com.mosioj.ideescadeaux.model.repositories.Notifications;
import com.mosioj.ideescadeaux.model.repositories.ParentRelationship;
import com.mosioj.ideescadeaux.model.repositories.Priorites;
import com.mosioj.ideescadeaux.model.repositories.Questions;
import com.mosioj.ideescadeaux.model.repositories.SousReservation;
import com.mosioj.ideescadeaux.model.repositories.UserParameters;
import com.mosioj.ideescadeaux.model.repositories.UserRelationRequests;
import com.mosioj.ideescadeaux.model.repositories.UserRelations;
import com.mosioj.ideescadeaux.model.repositories.UserRelationsSuggestion;
import com.mosioj.ideescadeaux.model.repositories.Users;

public class ModelAccessor {

    // FIXME : 0 tous les supprimer et mettre en statique ?

    /**
     * Interface vers la table USERS.
     */
    public final Users users = new Users();

    /**
     * The connections to the PRIORITIES table.
     */
    public final Priorites priorities = new Priorites();

    /**
     * The connections to the NOTIFICATION table. Static because it can be used in constructor for security checks.
     */
    public final Notifications notif = new Notifications();

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
