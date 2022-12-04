package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.Comment;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.CommentsColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.core.utils.Escaper;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.apache.commons.text.StringEscapeUtils;
import org.hibernate.query.NativeQuery;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class QuestionsRepository extends AbstractRepository {

    public static final String TABLE_NAME = "QUESTIONS";

    private QuestionsRepository() {
        // Forbidden
    }

    /**
     * @param userId The user id.
     * @param ideaId The idea id.
     * @param text   The comment text.
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
     * @param ideaId The idea id.
     * @return All users that have posted on this idea.
     */
    public static List<User> getUserListOnComment(int ideaId) {

        String query = MessageFormat.format("select distinct u.{0},u.{1},u.{2},u.{3},u.{4}",
                                            UsersColumns.ID,
                                            UsersColumns.NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.BIRTHDAY,
                                            UsersColumns.AVATAR) +
                       MessageFormat.format("  from {0} q ", TABLE_NAME) +
                       MessageFormat.format("  join {0} u ", UsersRepository.TABLE_NAME) +
                       MessageFormat.format("    on q.{0} = u.{1} ", CommentsColumns.WRITTEN_BY, UsersColumns.ID) +
                       MessageFormat.format(" where q.{0} = ? ", CommentsColumns.IDEA_ID);

        List<Object[]> res = HibernateUtil.doQueryFetch(s -> {
            NativeQuery<Object[]> sqlQuery = s.createSQLQuery(query);
            sqlQuery.setParameter(1, ideaId);
            return sqlQuery.list();
        });
        return res.stream()
                  .map(r -> new User((Integer) r[0], (String) r[1], (String) r[2], (Date) r[3], (String) r[4]))
                  .toList();
    }

    public static int getNbQuestions(int ideaId) throws SQLException {
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
    public static List<Comment> getCommentsOn(int ideaId) {

        String query = MessageFormat.format(
                "select c.{0}, c.{1}, c.{2}, u.{3}, u.{4}, u.{5}, u.{6} as userId, c.{7}, u.{8} ",
                CommentsColumns.ID,
                CommentsColumns.IDEA_ID,
                CommentsColumns.TEXT,
                UsersColumns.NAME,
                UsersColumns.EMAIL,
                UsersColumns.BIRTHDAY,
                UsersColumns.ID,
                CommentsColumns.WRITTEN_ON,
                UsersColumns.AVATAR) +
                       MessageFormat.format("  from {0} c ", TABLE_NAME) +
                       MessageFormat.format(" inner join {0} u on u.{1} = c.{2} ",
                                            UsersRepository.TABLE_NAME,
                                            UsersColumns.ID,
                                            CommentsColumns.WRITTEN_BY) +
                       MessageFormat.format(" where c.{0} = ? ", CommentsColumns.IDEA_ID) +
                       MessageFormat.format(" order by c.{0} desc ", CommentsColumns.WRITTEN_ON);

        List<Object[]> res = HibernateUtil.doQueryFetch(s -> {
            NativeQuery<Object[]> sqlQuery = s.createSQLQuery(query);
            sqlQuery.setParameter(1, ideaId);
            return sqlQuery.list();
        });

        return res.stream()
                  .map(r -> new Comment((Integer) r[0],
                                        Escaper.transformCodeToSmiley((String) r[2]),
                                        new User((Integer) r[6],
                                                 (String) r[3],
                                                 (String) r[4],
                                                 (Date) r[5],
                                                 (String) r[8]),
                                        (Integer) r[1],
                                        (Timestamp) r[7]))
                  .toList();
    }

    /**
     * @param commentId The comment id.
     * @return The comment corresponding to this id.
     */
    public static Optional<Comment> getComment(Integer commentId) {

        String query = MessageFormat.format(
                "select c.{0}, c.{1}, c.{2}, u.{3}, u.{4}, u.{5}, u.{6} as userId, c.{7}, u.{8} ",
                CommentsColumns.ID,
                CommentsColumns.IDEA_ID,
                CommentsColumns.TEXT,
                UsersColumns.NAME,
                UsersColumns.EMAIL,
                UsersColumns.BIRTHDAY,
                UsersColumns.ID,
                CommentsColumns.WRITTEN_ON,
                UsersColumns.AVATAR) +
                       MessageFormat.format("  from {0} c ", TABLE_NAME) +
                       MessageFormat.format(" inner join {0} u on u.{1} = c.{2} ",
                                            UsersRepository.TABLE_NAME,
                                            UsersColumns.ID,
                                            CommentsColumns.WRITTEN_BY) +
                       MessageFormat.format(" where c.{0} = ? ", CommentsColumns.ID);

        Optional<Object[]> res = HibernateUtil.doQueryOptional(s -> {
            NativeQuery<Object[]> sqlQuery = s.createSQLQuery(query);
            sqlQuery.setParameter(1, commentId);
            return sqlQuery.uniqueResultOptional();
        });

        return res.map(r -> new Comment((Integer) r[0],
                                        Escaper.transformCodeToSmiley((String) r[2]),
                                        new User((Integer) r[6],
                                                 (String) r[3],
                                                 (String) r[4],
                                                 (Date) r[5],
                                                 (String) r[8]),
                                        (Integer) r[1],
                                        (Timestamp) r[7]));
    }

    /**
     * @param commentId The comment id.
     */
    public static void delete(int commentId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, CommentsColumns.ID),
                              commentId);
    }

    public static void deleteAll(int userId) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?",
                                                   TABLE_NAME,
                                                   CommentsColumns.WRITTEN_BY), userId);
    }

}
