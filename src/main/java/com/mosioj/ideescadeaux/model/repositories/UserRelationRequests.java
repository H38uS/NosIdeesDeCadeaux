package com.mosioj.ideescadeaux.model.repositories;

import static com.mosioj.ideescadeaux.model.repositories.columns.UserRelationRequestsColumns.REQUEST_DATE;
import static com.mosioj.ideescadeaux.model.repositories.columns.UserRelationRequestsColumns.SENT_BY_USER;
import static com.mosioj.ideescadeaux.model.repositories.columns.UserRelationRequestsColumns.SENT_TO_USER;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.ideescadeaux.model.entities.RelationRequest;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.utils.database.PreparedStatementIdKdo;

public class UserRelationRequests extends Table {

	public static final String TABLE_NAME = "USER_RELATION_REQUESTS";

	/**
	 * 
	 * @param sent_by
	 * @param sent_to
	 * @return True if and only if sent_by has sent a request to sent_to.
	 * @throws SQLException
	 */
	public boolean associationExists(int sent_by, int sent_to) {
		try {
			return getDb().doesReturnRows(	MessageFormat.format(	"select 1 from {0} where {1} = ? and {2} = ?",
																	TABLE_NAME,
																	SENT_BY_USER,
																	SENT_TO_USER),
											sent_by,
											sent_to);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Saves a request from sent_by to sent_to.
	 * 
	 * @param sent_by
	 * @param sent_to
	 * @throws SQLException
	 */
	public void insert(User sent_by, User sent_to) throws SQLException {
		getDb().executeUpdateGeneratedKey(	MessageFormat.format(	"insert into {0} ({1},{2},{3}) values (?,?,now())",
																	TABLE_NAME,
																	SENT_BY_USER,
																	SENT_TO_USER,
																	REQUEST_DATE),
											sent_by.id,
											sent_to.id);
	}

	/**
	 * 
	 * @param userId
	 * @return The list of request of frienship this user has received.
	 * @throws SQLException
	 */
	public List<RelationRequest> getRequests(int userId) throws SQLException {

		List<RelationRequest> requests = new ArrayList<RelationRequest>();
		PreparedStatementIdKdo ps = null;
		try {
			StringBuilder query = new StringBuilder();
			query.append("select {0},{1},{2},u1.{5} as by_name,u1.{6} as by_email,u1.{7} as by_avatar,u2.{5} as to_name,u2.{6} as to_email,u2.{7} as to_avatar ");
			query.append("from {3} urr ");
			query.append("left join {4} u1 on u1.id = urr.{0} ");
			query.append("left join {4} u2 on u2.id = urr.{1} ");
			query.append("where {1} = ? ");
			ps = new PreparedStatementIdKdo(getDb(),
											MessageFormat.format(	query.toString(),
																	SENT_BY_USER,
																	SENT_TO_USER,
																	REQUEST_DATE,
																	TABLE_NAME,
																	Users.TABLE_NAME,
																	UsersColumns.NAME,
																	UsersColumns.EMAIL,
																	UsersColumns.AVATAR));
			ps.bindParameters(userId);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					requests.add(new RelationRequest(	new User(	res.getInt(SENT_BY_USER.name()),
																	res.getString("by_name"),
																	res.getString("by_email"),
																	res.getString("by_avatar")),
														new User(	res.getInt(SENT_TO_USER.name()),
																	res.getString("to_name"),
																	res.getString("to_email"),
																	res.getString("to_avatar")),
														res.getDate(REQUEST_DATE.name())));
				}
			}
		} finally {
			if (ps != null) {
				ps.close();
			}
		}

		return requests;
	}

	/**
	 * Cancel a friendship request :(
	 * 
	 * @param userThatSendTheRequest
	 * @param userThatReceiveTheRequest
	 * @throws SQLException
	 */
	public void cancelRequest(int userThatSendTheRequest, int userThatReceiveTheRequest) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"delete from {0} where {1} = ? and {2} = ?",
														TABLE_NAME,
														SENT_BY_USER,
														SENT_TO_USER),
								userThatSendTheRequest,
								userThatReceiveTheRequest);
	}

	public void removeAllFromAndTo(int userId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"delete from {0} where {1} = ? or {2} = ?",
														TABLE_NAME,
														SENT_BY_USER,
														SENT_TO_USER),
								userId,
								userId);

	}
}
