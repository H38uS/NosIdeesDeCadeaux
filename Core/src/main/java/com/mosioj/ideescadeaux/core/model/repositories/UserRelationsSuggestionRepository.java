package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.RelationSuggestion;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.Transaction;

import java.util.List;

public class UserRelationsSuggestionRepository {

    private UserRelationsSuggestionRepository() {
        // Forbidden
    }

    /**
     * @param suggestedTo The person who receives the suggestion.
     * @param userId      The user id.
     * @return True if and only if suggestedTo has received a notification for userId.
     */
    public static boolean hasReceivedSuggestionOf(int suggestedTo, int userId) {
        final String text = "select 1 from USER_RELATIONS_SUGGESTION where suggested_to = ?0 and user_id = ?1";
        return HibernateUtil.doesReturnRows(text, suggestedTo, userId);
    }

    /**
     * Makes a new suggestion.
     *
     * @param userMakingSuggestion The user suggesting.
     * @param toUser               The user receiving the suggestion.
     * @param suggestedUser        User that may be added.
     */
    public static void newSuggestion(User userMakingSuggestion, User toUser, User suggestedUser) {
        HibernateUtil.saveit(new RelationSuggestion(userMakingSuggestion, toUser, suggestedUser));
    }

    /**
     * @param user The user.
     * @return The suggestions for this user.
     */
    public static List<RelationSuggestion> getUserSuggestions(User user) {
        final String queryString = "from USER_RELATIONS_SUGGESTION where suggested_to = :to";
        return HibernateUtil.doQueryFetch(s -> s.createQuery(queryString, RelationSuggestion.class)
                                                .setParameter("to", user.id)
                                                .list());
    }

    /**
     * Removes any suggestions for user to suggestedTo.
     *
     * @param suggestedTo The user we initially sent the suggestion to.
     * @param user        The connected user.
     */
    public static void removeIfExists(int suggestedTo, int user) {
        final String text = "delete from USER_RELATIONS_SUGGESTION where suggested_to = :to and user_id = :id";
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery(text)
             .setParameter("to", suggestedTo)
             .setParameter("id", user)
             .executeUpdate();
            t.commit();
        });
    }

}
