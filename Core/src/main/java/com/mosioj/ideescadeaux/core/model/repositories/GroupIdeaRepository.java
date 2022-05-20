package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroupContent;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class GroupIdeaRepository extends AbstractRepository {

    private GroupIdeaRepository() {
        // Forbidden
    }

    /**
     * @param session The Hibernate session.
     * @return All groups in the database.
     */
    public static List<IdeaGroup> getAllGroups(Session session) {
        return session.createQuery("from GROUP_IDEA", IdeaGroup.class).list();
    }

    /**
     * Creates an initial group for an idea. Does not map it to the idea.
     *
     * @param total  Total amount wanted for this idea.
     * @param amount Amount paid by this user.
     * @param user   First user belonging to this new group.
     * @return The created group.
     */
    public static IdeaGroup createAGroup(double total, double amount, User user) {
        final IdeaGroup ideaGroup = new IdeaGroup();
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            // Group creation
            ideaGroup.total = total;
            s.save(ideaGroup);
            // Saving first participation
            s.save(new IdeaGroupContent(ideaGroup, user, amount));
            t.commit();
        });
        return ideaGroup;
    }

    /**
     * @param groupId The group id.
     * @return The optional group object.
     */
    public static Optional<IdeaGroup> getGroupDetails(Integer groupId) {
        if (groupId == null) {
            return Optional.empty();
        }
        return HibernateUtil.doQueryOptional(s -> Optional.ofNullable(s.get(IdeaGroup.class, groupId)));
    }

    /**
     * Removes the provided group. Does NOT remove any participations.
     *
     * @param group The group.
     */
    public static void deleteGroup(IdeaGroup group) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery("Delete from GROUP_IDEA where id = :id")
             .setParameter("id", group.getId())
             .executeUpdate();
            t.commit();
        });
    }

    /**
     * @param user  The user.
     * @param group The group.
     * @return true if there is at least one member left
     */
    public static boolean removeUserFromGroup(User user, IdeaGroup group) {
        GroupIdeaContentRepository.removeParticipationOfTo(group, user);
        boolean isTheGroupEmpty = getGroupDetails(group.getId()).map(IdeaGroup::getShares)
                                                                .map(List::isEmpty)
                                                                .orElse(true);

        if (isTheGroupEmpty) {
            // Suppression du groupe
            deleteGroup(group);
        }
        return !isTheGroupEmpty;
    }
}
