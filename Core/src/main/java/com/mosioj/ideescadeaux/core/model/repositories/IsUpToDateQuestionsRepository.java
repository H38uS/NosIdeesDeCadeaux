package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.IsUpToDateColumns;

import java.text.MessageFormat;

public class IsUpToDateQuestionsRepository extends AbstractRepository {

    public static final String TABLE_NAME = "IS_UP_TO_DATE";

    private IsUpToDateQuestionsRepository() {
        // Forbidden
    }

    /**
     * @param ideeId The idea id.
     */
    public static void deleteAssociations(int ideeId) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?",
                                                   TABLE_NAME,
                                                   IsUpToDateColumns.IDEE_ID),
                              ideeId);
    }

    /**
     * userId is asking if this ideaId is up to date.
     *
     * @param ideeId The idea id.
     * @param userId The user id.
     */
    public static void addAssociation(int ideeId, int userId) {
        getDb().executeInsert(MessageFormat.format("insert into {0} ({1}, {2}) values (?, ?)",
                                                   TABLE_NAME,
                                                   IsUpToDateColumns.IDEE_ID,
                                                   IsUpToDateColumns.USER_ID),
                              ideeId,
                              userId);
    }

    /**
     * @param idea The idea.
     * @param user The user.
     * @return True if this user has already asked if this idea is up to date.
     */
    public static boolean associationExists(Idee idea, User user) {
        return getDb().doesReturnRows("select 1 from " + TABLE_NAME + " where " + IsUpToDateColumns.IDEE_ID + " = ?"
                                      + " and " + IsUpToDateColumns.USER_ID + " = ?", idea.getId(), user.id);
    }
}
