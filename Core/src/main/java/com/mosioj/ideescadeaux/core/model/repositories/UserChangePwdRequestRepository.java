package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.repositories.columns.UserChangePwdRequestColumns;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.MessageFormat;

public class UserChangePwdRequestRepository extends AbstractRepository {

    private static final String TABLE_NAME = "USER_CHANGE_PWD_REQUEST";
    private static final int NB_DAYS_FOR_REINIT = 3;
    private static final Logger logger = LogManager.getLogger(UserChangePwdRequestRepository.class);

    private UserChangePwdRequestRepository() {
        // Forbidden
    }

    /**
     * @param userId  The user id.
     * @param tokenId The token id.
     * @return True if and only if the user has asked for a new password.
     */
    public static boolean isAValidCombinaison(int userId, int tokenId) throws SQLException {
        String query = MessageFormat.format(
                "select 1 from {0} where {1} = ? and {2} = ? and TIMESTAMPDIFF(SECOND, NOW(), {3}) > 0",
                TABLE_NAME,
                UserChangePwdRequestColumns.USER_ID,
                UserChangePwdRequestColumns.TOKEN_ID,
                UserChangePwdRequestColumns.EXPIRATION);
        logger.debug(query);
        return getDb().doesReturnRows(query,
                                      userId,
                                      tokenId);
    }

    /**
     * Deletes previous demand for this user.
     *
     * @param userId The user id.
     */
    public static void deleteAssociation(int userId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?",
                                                   TABLE_NAME,
                                                   UserChangePwdRequestColumns.USER_ID), userId);
    }

    /**
     * Creates a new entry for the given user.
     *
     * @param userId  The user id.
     * @param tokenId The token id.
     */
    public static void createNewRequest(int userId, int tokenId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format(
                "insert into {0} ({1},{2},{3}) values (?, ?, DATE_ADD(NOW(), INTERVAL {4} DAY))",
                TABLE_NAME,
                UserChangePwdRequestColumns.USER_ID,
                UserChangePwdRequestColumns.TOKEN_ID,
                UserChangePwdRequestColumns.EXPIRATION,
                NB_DAYS_FOR_REINIT),
                              userId,
                              tokenId);
    }
}
