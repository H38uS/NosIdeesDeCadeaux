package com.mosioj.ideescadeaux.model.repositories;

import static com.mosioj.ideescadeaux.model.repositories.columns.CommentsColumns.ID;
import static com.mosioj.ideescadeaux.model.repositories.columns.CommentsColumns.IDEA_ID;
import static com.mosioj.ideescadeaux.model.repositories.columns.CommentsColumns.TEXT;
import static com.mosioj.ideescadeaux.model.repositories.columns.CommentsColumns.WRITTEN_BY;
import static com.mosioj.ideescadeaux.model.repositories.columns.CommentsColumns.WRITTEN_ON;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.ideescadeaux.model.entities.Comment;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.utils.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.viewhelper.Escaper;

public class Comments extends Table {

	public static final String TABLE_NAME = "COMMENTS";

	/**
	 * 
	 * @param userId
	 * @param ideaId
	 * @param text
	 * @throws SQLException
	 */
	public void addComment(int userId, Integer ideaId, String text) throws SQLException {
		getDb().executeUpdateGeneratedKey(	MessageFormat.format(	"insert into {0} ({1},{2},{3},{4}) values (?,?,?, now())",
																	TABLE_NAME,
																	IDEA_ID,
																	TEXT,
																	WRITTEN_BY,
																	WRITTEN_ON),
											ideaId,
											Escaper.textToHtml(text),
											userId);
	}

	/**
	 * 
	 * @param ideaId
	 * @return All users that have posted on this idea.
	 * @throws SQLException
	 */
	public List<User> getUserListOnComment(int ideaId) throws SQLException {

		List<User> users = new ArrayList<User>();

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format(	"select distinct u.{0},u.{1},u.{2},u.{3} ",
											UsersColumns.ID,
											UsersColumns.NAME,
											UsersColumns.EMAIL,
											UsersColumns.AVATAR));
		query.append(MessageFormat.format("  from {0} q ", TABLE_NAME));
		query.append(MessageFormat.format("  join {0} u ", Users.TABLE_NAME));
		query.append(MessageFormat.format("    on q.{0} = u.{1} ", WRITTEN_BY, UsersColumns.ID));
		query.append(MessageFormat.format(" where q.{0} = ? ", IDEA_ID));

		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());
		try {
			ps.bindParameters(ideaId);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					users.add(new User(	res.getInt(UsersColumns.ID.name()),
										res.getString(UsersColumns.NAME.name()),
										res.getString(UsersColumns.EMAIL.name()),
										res.getString(UsersColumns.AVATAR.name())));
				}
			}
		} finally {
			ps.close();
		}

		return users;
	}

	public int getNbComments(int ideaId) throws SQLException {
		return getDb().selectCountStar("select count(*) from " + TABLE_NAME + " where " + IDEA_ID + " = ?", ideaId);
	}

	/**
	 * 
	 * @param ideaId
	 * @return The list of comment on this idea.
	 * @throws SQLException
	 */
	public List<Comment> getCommentsOn(int ideaId) throws SQLException {

		List<Comment> comments = new ArrayList<Comment>();

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format(	"select c.{0}, c.{1}, c.{2}, u.{3}, u.{4}, u.{5} as userId, c.{6},u.{7} ",
											ID,
											IDEA_ID,
											TEXT,
											UsersColumns.NAME,
											UsersColumns.EMAIL,
											UsersColumns.ID,
											WRITTEN_ON,
											UsersColumns.AVATAR));
		query.append(MessageFormat.format("  from {0} c ", TABLE_NAME));
		query.append(MessageFormat.format(" inner join {0} u on u.{1} = c.{2} ", Users.TABLE_NAME, UsersColumns.ID, WRITTEN_BY));
		query.append(MessageFormat.format(" where c.{0} = ? ", IDEA_ID));
		query.append(MessageFormat.format(" order by c.{0} desc ", WRITTEN_ON));

		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());
		try {
			ps.bindParameters(ideaId);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					comments.add(new Comment(	res.getInt(ID.name()),
												res.getString(TEXT.name()),
												new User(	res.getInt("userId"),
															res.getString(UsersColumns.NAME.name()),
															res.getString(UsersColumns.EMAIL.name()),
															res.getString(UsersColumns.AVATAR.name())),
												res.getInt(IDEA_ID.name()),
												res.getTimestamp(WRITTEN_ON.name())));
				}
			}
		} finally {
			ps.close();
		}

		return comments;
	}

	/**
	 * 
	 * @param commentId
	 * @return The comment corresponding to this id.
	 * @throws SQLException
	 */
	public Comment getComment(Integer commentId) throws SQLException {

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format(	"select c.{0}, c.{1}, c.{2}, u.{3}, u.{4}, u.{5} as userId, c.{6},u.{7} ",
											ID,
											IDEA_ID,
											TEXT,
											UsersColumns.NAME,
											UsersColumns.EMAIL,
											UsersColumns.ID,
											WRITTEN_ON,
											UsersColumns.AVATAR));
		query.append(MessageFormat.format("  from {0} c ", TABLE_NAME));
		query.append(MessageFormat.format(" inner join {0} u on u.{1} = c.{2} ", Users.TABLE_NAME, UsersColumns.ID, WRITTEN_BY));
		query.append(MessageFormat.format(" where c.{0} = ? ", ID));

		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());

		try {
			ps.bindParameters(commentId);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				if (res.next()) {
					return new Comment(	res.getInt(ID.name()),
										res.getString(TEXT.name()),
										new User(	res.getInt("userId"),
													res.getString(UsersColumns.NAME.name()),
													res.getString(UsersColumns.EMAIL.name()),
													res.getString(UsersColumns.AVATAR.name())),
										res.getInt(IDEA_ID.name()),
										res.getTimestamp(WRITTEN_ON.name()));
				}
			}
		} finally {
			ps.close();
		}

		return null;
	}

	/**
	 * 
	 * @param commentId
	 * @throws SQLException
	 */
	public void delete(int commentId) throws SQLException {
		getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, ID), commentId);
	}

	/**
	 * 
	 * @param commentId
	 * @throws SQLException
	 */
	public void deleteAll(int userId) throws SQLException {
		getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, WRITTEN_BY), userId);
	}

}
