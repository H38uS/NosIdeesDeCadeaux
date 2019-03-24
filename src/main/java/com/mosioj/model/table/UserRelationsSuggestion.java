package com.mosioj.model.table;

import static com.mosioj.model.table.columns.UserRelationsSuggestionColumns.SUGGESTED_BY;
import static com.mosioj.model.table.columns.UserRelationsSuggestionColumns.SUGGESTED_TO;
import static com.mosioj.model.table.columns.UserRelationsSuggestionColumns.SUGGESTION_DATE;
import static com.mosioj.model.table.columns.UserRelationsSuggestionColumns.USER_ID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.RelationSuggestion;
import com.mosioj.model.User;
import com.mosioj.model.table.columns.UsersColumns;
import com.mosioj.utils.database.PreparedStatementIdKdo;

public class UserRelationsSuggestion extends Table {

	public static final String TABLE_NAME = "USER_RELATIONS_SUGGESTION";
	private static final Logger logger = LogManager.getLogger(UserRelationsSuggestion.class);

	/**
	 * 
	 * @param userId
	 * @return True if the given user has received at least one suggestion.
	 * @throws SQLException
	 */
	public boolean hasReceivedSuggestion(int userId) throws SQLException {
		return getDb().doesReturnRows(MessageFormat.format("select 1 from {0} where {1} = ?", TABLE_NAME, SUGGESTED_TO), userId);
	}

	/**
	 * 
	 * @param suggestedTo
	 * @param suggestedBy
	 * @return True if and only if there is at least one suggestion from suggestedBy to suggestedTo.
	 * @throws SQLException
	 */
	public boolean hasReceivedSuggestionFrom(int suggestedTo, int suggestedBy) throws SQLException {
		return getDb().doesReturnRows(	MessageFormat.format(	"select 1 from {0} where {1} = ? and {2} = ?",
																TABLE_NAME,
																SUGGESTED_TO,
																SUGGESTED_BY),
										suggestedTo,
										suggestedBy);
	}

	/**
	 * 
	 * @param suggestedTo
	 * @param userId
	 * @return True if and only if suggestedTo has received a notification for userId.
	 * @throws SQLException
	 */
	public boolean hasReceivedSuggestionOf(int suggestedTo, int userId) throws SQLException {
		return getDb().doesReturnRows(	MessageFormat.format(	"select 1 from {0} where {1} = ? and {2} = ?",
																TABLE_NAME,
																SUGGESTED_TO,
																USER_ID),
										suggestedTo,
										userId);
	}

	/**
	 * Makes a new suggestion.
	 * 
	 * @param userMakingSuggestion The user suggesting.
	 * @param toUser The user receiving the suggestion.
	 * @param suggestedUsers User that may be added.
	 * @throws SQLException
	 */
	public boolean newSuggestion(int userMakingSuggestion, int toUser, int suggestedUser) throws SQLException {
		String query = MessageFormat.format("insert into {0} ({1},{2},{3},{4}) select * FROM (select ?, ?, now(), ?) as vals where not exists (select 1 from {0} where {1} = ? and {2} = ? and {4} = ?)",
											TABLE_NAME,
											SUGGESTED_BY,
											SUGGESTED_TO,
											SUGGESTION_DATE,
											USER_ID);
		logger.trace(query);
		return getDb().executeUpdate(	query,
										userMakingSuggestion,
										toUser,
										suggestedUser,
										userMakingSuggestion,
										toUser,
										suggestedUser) > 0;
	}

	/**
	 * 
	 * @param user
	 * @return The suggestions for this user.
	 * @throws SQLException
	 */
	public List<RelationSuggestion> getUserSuggestions(User user) throws SQLException {

		List<RelationSuggestion> suggestions = new ArrayList<RelationSuggestion>();
		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format(	"select u1.{0} by_id,u1.{1} by_name,u1.{2} by_email,u1.{4} by_avatar,u2.{0} to_id,u2.{1} to_name,u2.{2} to_email,u2.{4} to_avatar,u3.{0} user_id,u3.{1} user_name,u3.{2} user_email,u3.{4} user_avatar,{3} ",
											UsersColumns.ID,
											UsersColumns.NAME,
											UsersColumns.EMAIL,
											SUGGESTION_DATE,
											UsersColumns.AVATAR));
		query.append(MessageFormat.format("  from {0} urs ", TABLE_NAME));
		query.append(MessageFormat.format(" inner join {0} u1 on u1.{1} = urs.{2} ", Users.TABLE_NAME, UsersColumns.ID, SUGGESTED_BY));
		query.append(MessageFormat.format(" inner join {0} u2 on u2.{1} = urs.{2} ", Users.TABLE_NAME, UsersColumns.ID, SUGGESTED_TO));
		query.append(MessageFormat.format(" inner join {0} u3 on u3.{1} = urs.{2} ", Users.TABLE_NAME, UsersColumns.ID, USER_ID));
		query.append(MessageFormat.format(" where {0} = ? ", SUGGESTED_TO));
		query.append(MessageFormat.format(" order by u1.{0}, u3.{1}", UsersColumns.NAME, UsersColumns.NAME));

		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());
		try {
			ps.bindParameters(user.id);
			if (ps.execute()) {

				ResultSet res = ps.getResultSet();
				User currentFrom = null;
				User to = null;
				List<User> userSuggestions = new ArrayList<User>();
				Time time = null;

				while (res.next()) {

					time = res.getTime(SUGGESTION_DATE.name());
					User from = new User(res.getInt("by_id"), res.getString("by_name"), res.getString("by_email"), res.getString("by_avatar"));
					to = new User(res.getInt("to_id"), res.getString("to_name"), res.getString("to_email"), res.getString("to_avatar"));
					userSuggestions.add(new User(res.getInt("user_id"), res.getString("user_name"), res.getString("user_email"), res.getString("user_avatar")));

					if (currentFrom == null) {
						currentFrom = from;
					}

					// Saving the previous user
					if (!currentFrom.equals(from)) {
						suggestions.add(new RelationSuggestion(from, to, userSuggestions, time));
						userSuggestions.clear();
						currentFrom = from;
					}
				}

				if (!userSuggestions.isEmpty()) {
					suggestions.add(new RelationSuggestion(currentFrom, to, userSuggestions, time));
				}

			}
		} finally {
			ps.close();
		}

		return suggestions;
	}

	/**
	 * Removes any suggestions for user to suggestedTo.
	 * 
	 * @param suggestedTo
	 * @param user
	 * @throws SQLException
	 */
	public void removeIfExists(int suggestedTo, int user) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format("delete from {0} where {1} = ? and {2} = ?", TABLE_NAME, SUGGESTED_TO, USER_ID),
								suggestedTo,
								user);
	}

	public void removeAllFromAndTo(int userId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format("delete from {0} where {1} = ? or {2} = ?", TABLE_NAME, SUGGESTED_TO, USER_ID),
								userId,
								userId);
	}

}
