package com.mosioj.model.table;

import static com.mosioj.model.table.columns.IsUpToDateColumns.IDEE_ID;
import static com.mosioj.model.table.columns.IsUpToDateColumns.USER_ID;

import java.text.MessageFormat;;

public class IsUpToDateQuestions extends Table {

	public static final String TABLE_NAME = "IS_UP_TO_DATE";

	/**
	 * Removes the association if it exists.
	 * 
	 * @param ideeId
	 * @param userId
	 * @return The number of rows deleted.
	 */
	public int deleteAssociation(int ideeId, int userId) {
		return getDb().executeUpdate(	MessageFormat.format("delete from {0} where {1} = ? and {2} = ?", TABLE_NAME, IDEE_ID, USER_ID),
										ideeId,
										userId);
	}

	/**
	 * 
	 * @param id
	 * @return Nb of rows deleted.
	 */
	public int deleteAssociations(int ideeId) {
		return getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, IDEE_ID), ideeId);
	}

	/**
	 * 
	 * @param ideeId
	 * @param userId
	 * @return Number of rows inserted.
	 */
	public int addAssociation(int ideeId, int userId) {
		return getDb().executeUpdate(	MessageFormat.format("insert into {0} ({1}, {2}) values (?, ?)", TABLE_NAME, IDEE_ID, USER_ID),
										ideeId,
										userId);
	}

}