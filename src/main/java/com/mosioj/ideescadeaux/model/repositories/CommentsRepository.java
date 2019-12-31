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

public class CommentsRepository extends AbstractRepository {

    public static final String TABLE_NAME = "COMMENTS";

    private CommentsRepository() {
        // Forbidden
    }

    /**
     * @param userId The user id.
     * @param ideaId The idea id.
     * @param text   The text of the comment.
     */
    public static void addComment(int userId, Integer ideaId, String text) throws SQLException {
        getDb().executeUpdateGeneratedKey(MessageFormat.format("insert into {0} ({1},{2},{3},{4}) values (?,?,?, now())",
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
     * @param ideaId The idea.
     * @return All users that have posted on this idea.
     */
    public static List<User> getUserListOnComment(int ideaId) throws SQLException {

        List<User> users = new ArrayList<>();

        String query = MessageFormat.format("select distinct u.{0},u.{1},u.{2},u.{3} ",
                                            UsersColumns.ID,
                                            UsersColumns.NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.AVATAR) +
                       MessageFormat.format("  from {0} q ", TABLE_NAME) +
                       MessageFormat.format("  join {0} u ", UsersRepository.TABLE_NAME) +
                       MessageFormat.format("    on q.{0} = u.{1} ", WRITTEN_BY, UsersColumns.ID) +
                       MessageFormat.format(" where q.{0} = ? ", IDEA_ID);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    query)) {
            ps.bindParameters(ideaId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    users.add(new User(res.getInt(UsersColumns.ID.name()),
                                       res.getString(UsersColumns.NAME.name()),
                                       res.getString(UsersColumns.EMAIL.name()),
                                       res.getString(UsersColumns.AVATAR.name())));
                }
            }
        }

        return users;
    }

    public static int getNbComments(int ideaId) throws SQLException {
        return getDb().selectCountStar("select count(*) from " + TABLE_NAME + " where " + IDEA_ID + " = ?", ideaId);
    }

    /**
     * @param ideaId The idea id.
     * @return The list of comment on this idea.
     */
    public static List<Comment> getCommentsOn(int ideaId) throws SQLException {

        List<Comment> comments = new ArrayList<>();

        String query = MessageFormat.format("select c.{0}, c.{1}, c.{2}, u.{3}, u.{4}, u.{5} as userId, c.{6},u.{7} ",
                                            ID,
                                            IDEA_ID,
                                            TEXT,
                                            UsersColumns.NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.ID,
                                            WRITTEN_ON,
                                            UsersColumns.AVATAR) +
                       MessageFormat.format("  from {0} c ", TABLE_NAME) +
                       MessageFormat.format(" inner join {0} u on u.{1} = c.{2} ",
                                            UsersRepository.TABLE_NAME,
                                            UsersColumns.ID,
                                            WRITTEN_BY) +
                       MessageFormat.format(" where c.{0} = ? ", IDEA_ID) +
                       MessageFormat.format(" order by c.{0} desc ", WRITTEN_ON);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    query)) {
            ps.bindParameters(ideaId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    comments.add(new Comment(res.getInt(ID.name()),
                                             res.getString(TEXT.name()),
                                             new User(res.getInt("userId"),
                                                      res.getString(UsersColumns.NAME.name()),
                                                      res.getString(UsersColumns.EMAIL.name()),
                                                      res.getString(UsersColumns.AVATAR.name())),
                                             res.getInt(IDEA_ID.name()),
                                             res.getTimestamp(WRITTEN_ON.name())));
                }
            }
        }

        return comments;
    }

    /**
     * @param commentId The comment id.
     * @return The comment corresponding to this id.
     */
    public static Comment getComment(Integer commentId) throws SQLException {

        String query = MessageFormat.format("select c.{0}, c.{1}, c.{2}, u.{3}, u.{4}, u.{5} as userId, c.{6},u.{7} ",
                                            ID,
                                            IDEA_ID,
                                            TEXT,
                                            UsersColumns.NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.ID,
                                            WRITTEN_ON,
                                            UsersColumns.AVATAR) +
                       MessageFormat.format("  from {0} c ", TABLE_NAME) +
                       MessageFormat.format(" inner join {0} u on u.{1} = c.{2} ",
                                            UsersRepository.TABLE_NAME,
                                            UsersColumns.ID,
                                            WRITTEN_BY) +
                       MessageFormat.format(" where c.{0} = ? ", ID);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {
            ps.bindParameters(commentId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                if (res.next()) {
                    return new Comment(res.getInt(ID.name()),
                                       res.getString(TEXT.name()),
                                       new User(res.getInt("userId"),
                                                res.getString(UsersColumns.NAME.name()),
                                                res.getString(UsersColumns.EMAIL.name()),
                                                res.getString(UsersColumns.AVATAR.name())),
                                       res.getInt(IDEA_ID.name()),
                                       res.getTimestamp(WRITTEN_ON.name()));
                }
            }
        }

        return null;
    }

    /**
     * @param commentId The comment id.
     */
    public static void delete(int commentId) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, ID), commentId);
    }

    /**
     * @param userId The user id.
     */
    public static void deleteAll(int userId) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, WRITTEN_BY), userId);
    }

}
