package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroupContent;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GroupIdeaRepository {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(GroupIdeaRepository.class);

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
        if (group != null) {
            HibernateUtil.doSomeWork(s -> {
                Transaction t = s.beginTransaction();
                s.createQuery("Delete from GROUP_IDEA where id = :id")
                 .setParameter("id", group.getId())
                 .executeUpdate();
                t.commit();
            });
        }
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

    /**
     * @param user The user that might have participated.
     * @return All the groups this user participated in.
     */
    public static List<IdeaGroup> getParticipationOf(User user) {
        final String query = "select igc.group " +
                             "  from IdeaGroupContent igc " +
                             " where igc.user = :user ";
        return HibernateUtil.doQueryFetch(s -> s.createQuery(query, IdeaGroup.class).setParameter("user", user).list());
    }

    /**
     * @param group         The booking group's id.
     * @param connectedUser The connected user.
     * @return The list of users that can contribute to this group. They must also belong to the user relationship.
     */
    public static List<User> getPotentialGroupUser(IdeaGroup group, User connectedUser) {

        Optional<User> ideaOwner = IdeesRepository.getIdeaOwnerFromGroup(group);
        if (!ideaOwner.isPresent()) {
            // No owner ?! No potential users
            logger.error("No idea or idea owner corresponding to group {}...", group.getId());
            return Collections.emptyList();
        }

        List<User> ideaOwnerFriends = UserRelationsRepository.getAllUsersInRelation(ideaOwner.get());
        List<User> myFriends = UserRelationsRepository.getAllUsersInRelation(connectedUser);
        List<User> groupMember = group.getShares().stream().map(IdeaGroupContent::getUser).collect(Collectors.toList());

        // Les amis du owner
        return ideaOwnerFriends.stream()
                               // qui sont aussi nos amis
                               .filter(myFriends::contains)
                               // ... qui ne sont pas déjà dans le groupe !
                               .filter(u -> !groupMember.contains(u))
                               .collect(Collectors.toList());
    }
}
