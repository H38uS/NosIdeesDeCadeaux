package com.mosioj.ideescadeaux.model.repositories;

import com.mosioj.ideescadeaux.model.repositories.columns.IsUpToDateColumns;

import java.text.MessageFormat;

public class IsUpToDateQuestionsRepository extends AbstractRepository {

    public static final String TABLE_NAME = "IS_UP_TO_DATE";

    private IsUpToDateQuestionsRepository() {
        // Forbidden
    }

    /**
     * Removes the association if it exists.
     *
	 * @param ideeId The idea id.
	 * @param userId The user id.
     * @return The number of rows deleted.
     */
    public static int deleteAssociation(int ideeId, int userId) {
        return getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? and {2} = ?",
                                                          TABLE_NAME,
                                                          IsUpToDateColumns.IDEE_ID,
                                                          IsUpToDateColumns.USER_ID),
                                     ideeId,
                                     userId);
    }

    /**
     * @param ideeId The idea id.
     */
    public static void deleteAssociations(int ideeId) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, IsUpToDateColumns.IDEE_ID),
                              ideeId);
    }

    /**
     * @param ideeId The idea id.
     * @param userId The user id.
     * @return Number of rows inserted.
     */
    public static int addAssociation(int ideeId, int userId) {
        return getDb().executeUpdate(MessageFormat.format("insert into {0} ({1}, {2}) values (?, ?)",
                                                          TABLE_NAME,
                                                          IsUpToDateColumns.IDEE_ID,
                                                          IsUpToDateColumns.USER_ID),
                                     ideeId,
                                     userId);
    }

}
