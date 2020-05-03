package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.core.model.entities.RelationRequest;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UserRelationRequestsColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
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
    public static boolean associationExists(int sent_by, int sent_to) {
        return getDb().doesReturnRows(MessageFormat.format("select 1 from {0} where {1} = ? and {2} = ?",
                                                           TABLE_NAME,
                                                           UserRelationRequestsColumns.SENT_BY_USER,
                                                           UserRelationRequestsColumns.SENT_TO_USER),
                                      sent_by,
                                      sent_to);
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
    public static List<RelationRequest> getRequests(int userId) throws SQLException {

        List<RelationRequest> requests = new ArrayList<>();
        String query =
                "select {0},{1},{2},u1.{5} as by_name,u1.{6} as by_email,u1.{7} as by_avatar,u2.{5} as to_name,u2.{6} as to_email,u2.{7} as to_avatar " +
                "from {3} urr " +
                "left join {4} u1 on u1.id = urr.{0} " +
                "left join {4} u2 on u2.id = urr.{1} " +
                "where {1} = ? ";

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    MessageFormat.format(query,
                                                                                         UserRelationRequestsColumns.SENT_BY_USER,
                                                                                         UserRelationRequestsColumns.SENT_TO_USER,
                                                                                         UserRelationRequestsColumns.REQUEST_DATE,
                                                                                         TABLE_NAME,
                                                                                         UsersRepository.TABLE_NAME,
                                                                                         UsersColumns.NAME,
                                                                                         UsersColumns.EMAIL,
                                                                                         UsersColumns.AVATAR))) {
            ps.bindParameters(userId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    requests.add(new RelationRequest(new User(res.getInt(UserRelationRequestsColumns.SENT_BY_USER.name()),
                                                              res.getString("by_name"),
                                                              res.getString("by_email"),
                                                              res.getString("by_avatar")),
                                                     new User(res.getInt(UserRelationRequestsColumns.SENT_TO_USER.name()),
                                                              res.getString("to_name"),
                                                              res.getString("to_email"),
                                                              res.getString("to_avatar")),
                                                     res.getDate(UserRelationRequestsColumns.REQUEST_DATE.name())));
                }
            }
        }
        return requests;
    }

    /**
     * Cancel a friendship request :(
     *
     * @param userThatSendTheRequest    The user who sent the request.
     * @param userThatReceiveTheRequest The user who received it.
     */
    public static void cancelRequest(int userThatSendTheRequest, int userThatReceiveTheRequest) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? and {2} = ?",
                                                   TABLE_NAME,
                                                   UserRelationRequestsColumns.SENT_BY_USER,
                                                   UserRelationRequestsColumns.SENT_TO_USER),
                              userThatSendTheRequest,
                              userThatReceiveTheRequest);
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
