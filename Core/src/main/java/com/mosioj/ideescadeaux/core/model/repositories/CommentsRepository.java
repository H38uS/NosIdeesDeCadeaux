package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Comment;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class CommentsRepository {

    private CommentsRepository() {
        // Forbidden
    }

    /**
     * @param user The user.
     * @param idea The idea.
     * @param text The comment text.
     * @return The posted message.
     */
    public static Comment addComment(User user, Idee idea, String text) {
        final Comment comment = Comment.getIt(user, idea, text);
        HibernateUtil.saveit(comment);
        return comment;
    }

    /**
     * @param idea The idea.
     * @return All users that have posted on this idea.
     */
    public static List<User> getUserListOnComment(Idee idea) {
        final String query = """
                 select distinct c.writtenBy
                   from COMMENTS c
                  where c.idea = :idea
                """;
        return HibernateUtil.doQueryFetch(s -> s.createQuery(query, User.class).setParameter("idea", idea).list());
    }

    /**
     * @param idea The idea id.
     * @return The list of comment on this idea.
     */
    public static List<Comment> getCommentsOn(Idee idea) {
        final String query = """
                  from COMMENTS q
                  left join fetch q.writtenBy
                 where q.idea = :idea
                 order by q.updatedAt desc, q.creationDate desc
                """;
        return HibernateUtil.doQueryFetch(s -> s.createQuery(query, Comment.class)
                                                .setParameter("idea", idea)
                                                .list());
    }

    /**
     * @param commentId The comment id.
     * @return The comment corresponding to this id.
     */
    public static Optional<Comment> getComment(int commentId) {
        return HibernateUtil.doQueryOptional(s -> s.createQuery("from COMMENTS where id = :id", Comment.class)
                                                   .setParameter("id", commentId)
                                                   .uniqueResultOptional());
    }

    /**
     * @param comment The comment id.
     */
    public static void delete(Comment comment) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery("delete from COMMENTS where id = :id ")
             .setParameter("id", comment.getId())
             .executeUpdate();
            t.commit();
        });
    }

    /**
     * Removes all comments written on this idea.
     *
     * @param idee The idea.
     */
    public static void deleteAll(Idee idee) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery("delete from COMMENTS where idea = :idea ")
             .setParameter("idea", idee)
             .executeUpdate();
            t.commit();
        });
    }


    /**
     * @param user The user id.
     */
    public static void deleteAll(User user) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery("delete from COMMENTS where writtenBy = :user ")
             .setParameter("user", user)
             .executeUpdate();
            t.commit();
        });
    }

}
