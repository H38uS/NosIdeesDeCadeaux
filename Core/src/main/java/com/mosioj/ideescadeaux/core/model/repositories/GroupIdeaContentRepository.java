package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroupContent;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Optional;

public class GroupIdeaContentRepository {

    /**
     * Clas constructor.
     */
    private GroupIdeaContentRepository() {
        // Forbidden
    }

    /**
     * @param group   The idea group.
     * @param user    The user that might have participated.
     * @param session The Hibernate session.
     * @return The corresponding user, if it exists.
     */
    public static Optional<IdeaGroupContent> getParticipationOf(IdeaGroup group, User user, Session session) {
        final String queryString = "FROM IdeaGroupContent WHERE group_id = :group and user_id = :user";
        Query<IdeaGroupContent> query = session.createQuery(queryString, IdeaGroupContent.class);
        query.setParameter("group", group);
        query.setParameter("user", user);
        return query.uniqueResultOptional();
    }

    /**
     * @param group The idea group.
     * @param user  The user that might have participated.
     * @return The user corresponding to this ID or null if not found.
     */
    public static Optional<IdeaGroupContent> getParticipationOf(IdeaGroup group, User user) {
        return HibernateUtil.doQueryOptional(s -> getParticipationOf(group, user, s));
    }

    /**
     * Adds a new participation.
     *
     * @param group  The group to participate to.
     * @param user   The user that wants to participate.
     * @param amount The amount.
     */
    public static void addNewAmount(IdeaGroup group, User user, double amount) {
        final IdeaGroupContent participation = new IdeaGroupContent(group, user, amount);
        HibernateUtil.saveit(participation);
        group.getShares().add(participation);
    }

    /**
     * @param group     The group.
     * @param user      The user.
     * @param newAmount The new amount
     */
    public static void updateAmount(IdeaGroup group, User user, double newAmount) {
        getParticipationOf(group, user).ifPresent(p -> {
            p.amount = newAmount;
            HibernateUtil.update(p);
        });
    }

    /**
     * Removes the participation of the user to the group if it exists.
     *
     * @param group The group.
     * @param user  The user.
     */
    public static void removeParticipationOfTo(IdeaGroup group, User user) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery("delete from IdeaGroupContent where group_id = :group and user_id = :user")
             .setParameter("group", group)
             .setParameter("user", user)
             .executeUpdate();
            t.commit();
        });
    }

    /**
     * @param session The Hibernate session.
     * @param user    The user that will remove all participations.
     */
    public static void removeAllParticipationsOf(Session session, User user) {
        Transaction t = session.beginTransaction();
        session.createQuery("delete from IdeaGroupContent where user_id = :user")
               .setParameter("user", user)
               .executeUpdate();
        t.commit();
    }

}
