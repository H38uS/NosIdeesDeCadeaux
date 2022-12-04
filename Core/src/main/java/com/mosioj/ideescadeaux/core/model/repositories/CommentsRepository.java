package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.core.model.entities.Comment;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.CommentsColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.core.utils.Escaper;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommentsRepository extends AbstractRepository {

    public static final String TABLE_NAME = "COMMENTS";
    private static final Logger logger = LogManager.getLogger(CommentsRepository.class);

    private CommentsRepository() {
        // Forbidden
    }

    /**
     * @param userId The user id.
     * @param ideaId The idea id.
     * @param text   The text of the comment.
     */
    public static void addComment(int userId, Integer ideaId, String text) {
        text = StringEscapeUtils.unescapeHtml4(text);
        text = Escaper.escapeIdeaText(text);
        text = Escaper.transformSmileyToCode(text);
        getDb().executeInsert(MessageFormat.format("insert into {0} ({1},{2},{3},{4}) values (?,?,?, now())",
                                                   TABLE_NAME,
                                                   CommentsColumns.IDEA_ID,
                                                   CommentsColumns.TEXT,
                                                   CommentsColumns.WRITTEN_BY,
                                                   CommentsColumns.WRITTEN_ON),
                              ideaId,
                              text,
                              userId);
    }

    /**
     * @param ideaId The idea.
     * @return All users that have posted on this idea.
     */
    public static List<User> getUserListOnComment(int ideaId) throws SQLException {

        List<User> users = new ArrayList<>();

        String query = MessageFormat.format("select distinct u.{0},u.{1},u.{2},u.{3},u.{4} ",
                                            UsersColumns.ID,
                                            UsersColumns.NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.BIRTHDAY,
                                            UsersColumns.AVATAR) +
                       MessageFormat.format("  from {0} q ", TABLE_NAME) +
                       MessageFormat.format("  join {0} u ", UsersRepository.TABLE_NAME) +
                       MessageFormat.format("    on q.{0} = u.{1} ", CommentsColumns.WRITTEN_BY, UsersColumns.ID) +
                       MessageFormat.format(" where q.{0} = ? ", CommentsColumns.IDEA_ID);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    query)) {
            ps.bindParameters(ideaId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    users.add(new User(res.getInt(UsersColumns.ID.name()),
                                       res.getString(UsersColumns.NAME.name()),
                                       res.getString(UsersColumns.EMAIL.name()),
                                       res.getDate(UsersColumns.BIRTHDAY.name()),
                                       res.getString(UsersColumns.AVATAR.name())));
                }
            }
        }

        return users;
    }

    /**
     * @param ideaId The idea.
     * @return The number of comments on this idea.
     */
    public static int getNbComments(int ideaId) throws SQLException {
        return getDb().selectCountStar("select count(*) from " +
                                       TABLE_NAME +
                                       " where " +
                                       CommentsColumns.IDEA_ID +
                                       " = ?", ideaId);
    }

    /**
     * @param ideaId The idea id.
     * @return The list of comment on this idea.
     */
    public static List<Comment> getCommentsOn(int ideaId) throws SQLException {

        List<Comment> comments = new ArrayList<>();

        String query = MessageFormat.format(
                "select c.{0}, c.{1}, c.{2}, u.{3}, u.{4}, u.{5} as userId, c.{6},u.{7},u.{8} ",
                CommentsColumns.ID,
                CommentsColumns.IDEA_ID,
                CommentsColumns.TEXT,
                UsersColumns.NAME,
                UsersColumns.EMAIL,
                UsersColumns.ID,
                CommentsColumns.WRITTEN_ON,
                UsersColumns.AVATAR,
                UsersColumns.BIRTHDAY) +
                       MessageFormat.format("  from {0} c ", TABLE_NAME) +
                       MessageFormat.format(" inner join {0} u on u.{1} = c.{2} ",
                                            UsersRepository.TABLE_NAME,
                                            UsersColumns.ID,
                                            CommentsColumns.WRITTEN_BY) +
                       MessageFormat.format(" where c.{0} = ? ", CommentsColumns.IDEA_ID) +
                       MessageFormat.format(" order by c.{0} desc ", CommentsColumns.WRITTEN_ON);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    query)) {
            ps.bindParameters(ideaId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    comments.add(new Comment(res.getInt(CommentsColumns.ID.name()),
                                             Escaper.transformCodeToSmiley(res.getString(CommentsColumns.TEXT.name())),
                                             new User(res.getInt("userId"),
                                                      res.getString(UsersColumns.NAME.name()),
                                                      res.getString(UsersColumns.EMAIL.name()),
                                                      res.getDate(UsersColumns.BIRTHDAY.name()),
                                                      res.getString(UsersColumns.AVATAR.name())),
                                             res.getInt(CommentsColumns.IDEA_ID.name()),
                                             res.getTimestamp(CommentsColumns.WRITTEN_ON.name())));
                }
            }
        }

        return comments;
    }

    /**
     * @param commentId The comment id.
     * @return The comment corresponding to this id.
     */
    public static Optional<Comment> getComment(int commentId) {

        String query = MessageFormat.format(
                "select c.{0}, c.{1}, c.{2}, u.{3}, u.{4}, u.{5} as userId, c.{6},u.{7},u.{8} ",
                CommentsColumns.ID,
                CommentsColumns.IDEA_ID,
                CommentsColumns.TEXT,
                UsersColumns.NAME,
                UsersColumns.EMAIL,
                UsersColumns.ID,
                CommentsColumns.WRITTEN_ON,
                UsersColumns.AVATAR,
                UsersColumns.BIRTHDAY) +
                       MessageFormat.format("  from {0} c ", TABLE_NAME) +
                       MessageFormat.format(" inner join {0} u on u.{1} = c.{2} ",
                                            UsersRepository.TABLE_NAME,
                                            UsersColumns.ID,
                                            CommentsColumns.WRITTEN_BY) +
                       MessageFormat.format(" where c.{0} = ? ", CommentsColumns.ID);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {
            ps.bindParameters(commentId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                if (res.next()) {
                    final User writer = new User(res.getInt("userId"),
                                                 res.getString(UsersColumns.NAME.name()),
                                                 res.getString(UsersColumns.EMAIL.name()),
                                                 res.getDate(UsersColumns.BIRTHDAY.name()),
                                                 res.getString(UsersColumns.AVATAR.name()));
                    return Optional.of(new Comment(res.getInt(CommentsColumns.ID.name()),
                                                   Escaper.transformCodeToSmiley(res.getString(CommentsColumns.TEXT.name())),
                                                   writer,
                                                   res.getInt(CommentsColumns.IDEA_ID.name()),
                                                   res.getTimestamp(CommentsColumns.WRITTEN_ON.name())));
                }
            }
        } catch (SQLException e) {
            logger.warn(e);
        }

        return Optional.empty();
    }

    /**
     * @param commentId The comment id.
     */
    public static void delete(int commentId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, CommentsColumns.ID),
                              commentId);
    }

    /**
     * Removes all comments written on this idea.
     *
     * @param idee The idea.
     */
    public static void deleteAll(Idee idee) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? ",
                                                   CommentsRepository.TABLE_NAME,
                                                   CommentsColumns.IDEA_ID), idee.getId());
    }


    /**
     * @param userId The user id.
     */
    public static void deleteAll(int userId) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?",
                                                   TABLE_NAME,
                                                   CommentsColumns.WRITTEN_BY), userId);
    }

}
