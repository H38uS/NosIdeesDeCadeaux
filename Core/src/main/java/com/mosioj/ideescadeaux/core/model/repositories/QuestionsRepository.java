package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.entities.text.Question;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class QuestionsRepository {

    private QuestionsRepository() {
        // Forbidden
    }

    /**
     * @param user The user.
     * @param idea The idea.
     * @param text The comment text.
     * @return The saved question.
     */
    public static Question addQuestion(User user, Idee idea, String text) {
        final Question question = Question.getIt(user, idea, text);
        HibernateUtil.saveit(question);
        return question;
    }

    /**
     * @param idea The idea id.
     * @return All users that have posted on this idea.
     */
    public static List<User> getUserListOnQuestion(Idee idea) {
        final String query = """
                 select distinct q.writtenBy
                   from QUESTIONS q
                  where q.idea = :idea
                """;
        return HibernateUtil.doQueryFetch(s -> s.createQuery(query, User.class).setParameter("idea", idea).list());
    }

    public static int getNbQuestions(Idee idea) {
        final String query = """
                 select count(*)
                   from QUESTIONS
                  where idea = :idea
                """;
        return HibernateUtil.doQuerySingle(s -> s.createQuery(query, Long.class)
                                                 .setParameter("idea", idea)
                                                 .uniqueResult())
                            .intValue();
    }

    /**
     * @param idea The idea id.
     * @return The list of comment on this idea.
     */
    public static List<Question> getQuestionsOn(Idee idea) {
        final String query = """
                  from QUESTIONS q
                  left join fetch q.writtenBy
                 where q.idea = :idea
                 order by q.updatedAt desc, q.creationDate desc
                """;
        return HibernateUtil.doQueryFetch(s -> s.createQuery(query, Question.class)
                                                .setParameter("idea", idea)
                                                .list());
    }

    /**
     * @param questionId The comment id.
     * @return The question corresponding to this id.
     */
    public static Optional<Question> getQuestion(int questionId) {
        return HibernateUtil.doQueryOptional(s -> s.createQuery("from QUESTIONS where id = :id", Question.class)
                                                   .setParameter("id", questionId)
                                                   .uniqueResultOptional());
    }

    /**
     * @param question The question to remove.
     */
    public static void delete(Question question) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery("delete from QUESTIONS where id = :id ")
             .setParameter("id", question.getId())
             .executeUpdate();
            t.commit();
        });
    }

    public static void deleteAll(User user) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery("delete from QUESTIONS where writtenBy = :user ")
             .setParameter("user", user)
             .executeUpdate();
            t.commit();
        });
    }

}
