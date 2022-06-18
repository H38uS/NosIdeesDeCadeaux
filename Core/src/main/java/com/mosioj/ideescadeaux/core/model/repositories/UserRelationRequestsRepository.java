package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.RelationRequest;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.Transaction;

import java.util.List;

public class UserRelationRequestsRepository {

    private UserRelationRequestsRepository() {
        // Forbidden
    }

    /**
     * @param sent_by Sent by this user.
     * @param sent_to Sent to this user.
     * @return True if and only if sent_by has sent a request to sent_to.
     */
    public static boolean associationExists(User sent_by, User sent_to) {
        final String query = "select 1 from USER_RELATION_REQUESTS where sent_by = ?0 and sent_to = ?1";
        return HibernateUtil.doesReturnRows(query, sent_by, sent_to);
    }

    /**
     * @param userId The user id.
     * @return The list of request of frienship this user has received.
     */
    public static List<RelationRequest> getRequests(int userId) {
        final String query = "select urr " +
                             "  from USER_RELATION_REQUESTS urr " +
                             "  left join fetch urr.sent_by " +
                             "  left join fetch urr.sent_to " +
                             " where sent_to_user = :id";
        return HibernateUtil.doQueryFetch(s -> s.createQuery(query, RelationRequest.class)
                                                .setParameter("id", userId)
                                                .list());
    }

    /**
     * Cancel a friendship request :(
     *
     * @param userThatSendTheRequest    The user who sent the request.
     * @param userThatReceiveTheRequest The user who received it.
     */
    public static void cancelRequest(User userThatSendTheRequest, User userThatReceiveTheRequest) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery("delete from USER_RELATION_REQUESTS where sent_by = :by and sent_to = :to")
             .setParameter("by", userThatSendTheRequest)
             .setParameter("to", userThatReceiveTheRequest)
             .executeUpdate();
            t.commit();
        });
    }

}
