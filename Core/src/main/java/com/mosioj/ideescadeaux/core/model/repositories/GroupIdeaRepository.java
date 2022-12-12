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
import java.util.Set;
import java.util.stream.Collectors;

public class GroupIdeaRepository {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(GroupIdeaRepository.class);

    private GroupIdeaRepository() {
        // Forbidden
    }

    /**
     * @return The base select for the group loading all the details in one go.
     */
    private static String getBaseSelect() {
        return "select g " +
               "  from GROUP_IDEA g " +
               "  left join fetch g.ideaGroupContents contents " +
               "  left join fetch contents.user ";
    }

    /**
     * @param session The Hibernate session.
     * @return All groups in the database.
     */
    public static List<IdeaGroup> getAllGroups(Session session) {
        return session.createQuery(getBaseSelect(), IdeaGroup.class).list();
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
        final String queryText = getBaseSelect() + " where g.id = :groupId";
        return HibernateUtil.doQueryOptional(s -> s.createQuery(queryText, IdeaGroup.class)
                                                   .setParameter("groupId", groupId)
                                                   .uniqueResultOptional());
    }

    /**
     * Removes the provided group. Does NOT remove any participations.
     *
     * @param group The group.
     */
    public static void deleteGroup(IdeaGroup group) {
        if (group != null) {
            NotificationsRepository.terminator().whereGroupIdea(group).terminates();
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
                                                                .map(Set::isEmpty)
                                                                .orElse(true);

        if (isTheGroupEmpty) {
            // Suppression du groupe
            deleteGroup(group);
        }
        return !isTheGroupEmpty;
    }

    /**
     * @param group         The booking group's id.
     * @param connectedUser The connected user.
     * @return The list of users that can contribute to this group. They must also belong to the user relationship.
     */
    public static List<User> getPotentialGroupUser(IdeaGroup group, User connectedUser) {

        Optional<User> ideaOwner = IdeesRepository.getIdeaOwnerFromGroup(group);
        if (ideaOwner.isEmpty()) {
            // No owner ?! No potential users
            logger.error("No idea or idea owner corresponding to group {}...", group.getId());
            return Collections.emptyList();
        }

        List<User> ideaOwnerFriends = UserRelationsRepository.getAllUsersInRelation(ideaOwner.get());
        List<User> myFriends = UserRelationsRepository.getAllUsersInRelation(connectedUser);
        List<User> groupMember = group.getShares().stream().map(IdeaGroupContent::getUser).toList();

        // Les amis du owner
        return ideaOwnerFriends.stream()
                               // qui sont aussi nos amis
                               .filter(myFriends::contains)
                               // ... qui ne sont pas déjà dans le groupe !
                               .filter(u -> !groupMember.contains(u))
                               .collect(Collectors.toList());
    }
}
