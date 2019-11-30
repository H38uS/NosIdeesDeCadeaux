package com.mosioj.ideescadeaux.model.table;

import static com.mosioj.ideescadeaux.model.table.columns.UserChangePwdRequestColumns.EXPIRATION;
import static com.mosioj.ideescadeaux.model.table.columns.UserChangePwdRequestColumns.TOKEN_ID;
import static com.mosioj.ideescadeaux.model.table.columns.UserChangePwdRequestColumns.USER_ID;

import java.sql.SQLException;
import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.servlets.controllers.relations.AfficherReseau;

public class UserChangePwdRequest extends Table {

	private static final String TABLE_NAME = "USER_CHANGE_PWD_REQUEST";
	private static final int NB_DAYS_FOR_REINIT = 3;
	private static final Logger logger = LogManager.getLogger(AfficherReseau.class);

	/**
	 * 
	 * @param userId The user id.
	 * @param tokenId The token id.
	 * @return True if and only if the user has asked for a new password.
	 * @throws SQLException
	 */
	public boolean isAValidCombinaison(int userId, int tokenId) throws SQLException {
		String query = MessageFormat.format(	"select 1 from {0} where {1} = ? and {2} = ? and TIMESTAMPDIFF(SECOND, NOW(), {3}) > 0",
																TABLE_NAME,
																USER_ID,
																TOKEN_ID,
																EXPIRATION);
		logger.debug(query);
		return getDb().doesReturnRows(	query,
										userId,
										tokenId);
	}

	/**
	 * Deletes previous demand for this user.
	 * 
	 * @param userId
	 * @throws SQLException
	 */
	public void deleteAssociation(int userId) throws SQLException {
		getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, USER_ID), userId);
	}

	/**
	 * Creates a new entry for the given user.
	 * 
	 * @param userId
	 * @param tokenId
	 * @throws SQLException
	 */
	public void createNewRequest(int userId, int tokenId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"insert into {0} ({1},{2},{3}) values (?, ?, DATE_ADD(NOW(), INTERVAL {4} DAY))",
														TABLE_NAME,
														USER_ID,
														TOKEN_ID,
														EXPIRATION,
														NB_DAYS_FOR_REINIT),
								userId,
								tokenId);
	}
}
