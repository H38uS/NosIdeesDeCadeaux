package com.mosioj.ideescadeaux.model.repositories;

import static com.mosioj.ideescadeaux.model.repositories.columns.IsUpToDateColumns.IDEE_ID;
import static com.mosioj.ideescadeaux.model.repositories.columns.IsUpToDateColumns.USER_ID;

import java.text.MessageFormat;

public class IsUpToDateQuestions extends Table {

    public static final String TABLE_NAME = "IS_UP_TO_DATE";

    /**
     * Removes the association if it exists.
     *
	 * @param ideeId The idea id.
	 * @param userId The user id.
     * @return The number of rows deleted.
     */
    public int deleteAssociation(int ideeId, int userId) {
        return getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? and {2} = ?",
                                                          TABLE_NAME,
                                                          IDEE_ID,
                                                          USER_ID),
                                     ideeId,
                                     userId);
    }

    /**
     * @param ideeId The idea id.
     */
    public void deleteAssociations(int ideeId) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, IDEE_ID),
                              ideeId);
    }

    /**
     * @param ideeId The idea id.
     * @param userId The user id.
     * @return Number of rows inserted.
     */
    public int addAssociation(int ideeId, int userId) {
        return getDb().executeUpdate(MessageFormat.format("insert into {0} ({1}, {2}) values (?, ?)",
                                                          TABLE_NAME,
                                                          IDEE_ID,
                                                          USER_ID),
                                     ideeId,
                                     userId);
    }

}
