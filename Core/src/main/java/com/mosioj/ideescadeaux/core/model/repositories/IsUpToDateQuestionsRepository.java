package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.IsUpToDate;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.Transaction;

public class IsUpToDateQuestionsRepository {

    private IsUpToDateQuestionsRepository() {
        // Forbidden
    }

    /**
     * @param idea The idea.
     */
    public static void deleteAssociations(Idee idea) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery("delete from IS_UP_TO_DATE where idea = :idea ")
             .setParameter("idea", idea)
             .executeUpdate();
            t.commit();
        });
    }

    /**
     * userId is asking if this ideaId is up to date.
     *
     * @param idea The idea id.
     * @param user The user id.
     */
    public static void addAssociation(Idee idea, User user) {
        IsUpToDate request = IsUpToDate.getIt(user, idea);
        HibernateUtil.saveit(request);
    }

    /**
     * @param idea The idea.
     * @param user The user.
     * @return True if this user has already asked if this idea is up to date.
     */
    public static boolean associationExists(Idee idea, User user) {
        final String query = """
                select 1
                  from IS_UP_TO_DATE
                 where idea = ?1
                   and askedBy = ?2
                """;
        return HibernateUtil.doesReturnRows(query, idea, user);
    }

}
