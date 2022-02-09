package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.RelationRequest;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UserRelationRequestsColumns;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

public class UserRelationRequestsRepository extends AbstractRepository {

    public static final String TABLE_NAME = "USER_RELATION_REQUESTS";

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
     * Saves a request from sent_by to sent_to.
     *
     * @param sent_by Sent by this user.
     * @param sent_to Sent to this user.
     */
    public static void insert(User sent_by, User sent_to) throws SQLException {
        getDb().executeUpdateGeneratedKey(MessageFormat.format("insert into {0} ({1},{2},{3}) values (?,?,now())",
                                                               TABLE_NAME,
                                                               UserRelationRequestsColumns.SENT_BY_USER,
                                                               UserRelationRequestsColumns.SENT_TO_USER,
                                                               UserRelationRequestsColumns.REQUEST_DATE),
                                          sent_by.id,
                                          sent_to.id);
    }

    /**
     * @param userId The user id.
     * @return The list of request of frienship this user has received.
     */
    public static List<RelationRequest> getRequests(int userId) {
        final String query = "from USER_RELATION_REQUESTS where sent_to_user = :id";
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

    public static void removeAllFromAndTo(int userId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? or {2} = ?",
                                                   TABLE_NAME,
                                                   UserRelationRequestsColumns.SENT_BY_USER,
                                                   UserRelationRequestsColumns.SENT_TO_USER),
                              userId,
                              userId);
    }
}
