package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.booking.GroupIdeaContentRepository;
import com.mosioj.ideescadeaux.core.model.repositories.booking.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.booking.SousReservationRepository;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class IdeesRepository {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(IdeesRepository.class);

    private IdeesRepository() {
        // Forbidden
    }

    /**
     * @return The idea base select to load all linked entities in one go
     */
    private static String getIdeaBaseSelect() {
        return """
                 select i
                   from IDEES i
                   left join fetch i.owner
                   left join fetch i.bookedBy
                   left join fetch i.surpriseBy
                   left join fetch i.createdBy
                   left join fetch i.group g
                   left join fetch g.ideaGroupContents contents
                   left join fetch contents.user
                   left join fetch i.categorie
                   left join fetch i.priority p
                """;
    }

    /**
     * Retrieves all ideas of a person.
     *
     * @param owner The person for which we are getting all the ideas.
     * @return The person's ideas list.
     */
    public static Set<Idee> getIdeasOf(User owner) {
        final String query = getIdeaBaseSelect() +
                             " where i.owner = :owner " +
                             "   and coalesce(i.status, 'THERE') <> 'DELETED' " +
                             " order by p.order desc, i.text, i.lastModified desc, i.id desc";
        return new LinkedHashSet<>(HibernateUtil.doQueryFetch(s -> s.createQuery(query, Idee.class)
                                                                  .setParameter("owner", owner)
                                                                  .list()));
    }

    /**
     * @param idIdee The idea's id.
     * @return All fields for this idea.
     */
    public static Optional<Idee> getIdea(Integer idIdee) {
        if (idIdee == null) {
            return Optional.empty();
        }
        final String query = getIdeaBaseSelect() + """
                 where i.id = :id
                   and coalesce(i.status, 'THERE') <> 'DELETED'
                """;
        return HibernateUtil.doQueryOptionalFromListOperation(s -> s.createQuery(query, Idee.class)
                                                                    .setParameter("id", idIdee)
                                                                    .list());
    }

    /**
     * @param idIdee The deleted idea's id.
     * @return All fields for this idea.
     */
    public static Optional<Idee> getDeletedIdea(Integer idIdee) {
        if (idIdee == null) {
            return Optional.empty();
        }
        final String query = getIdeaBaseSelect() +
                             " where i.id = :id" +
                             "   and coalesce(i.status, 'THERE') = 'DELETED'";
        return HibernateUtil.doQueryOptionalFromListOperation(s -> s.createQuery(query, Idee.class)
                                                                    .setParameter("id", idIdee)
                                                                    .list());
    }

    /**
     * @param group The booking group's id.
     * @return The idea id of the idea booked by this group.
     */
    public static Optional<Idee> getIdeaFromGroup(IdeaGroup group) {
        final String query = getIdeaBaseSelect() +
                             " where i.group = :group" +
                             "   and coalesce(i.status, 'THERE') <> 'DELETED'";
        return HibernateUtil.doQueryOptionalFromListOperation(s -> s.createQuery(query, Idee.class)
                                                                    .setParameter("group", group)
                                                                    .list());
    }

    /**
     * Works on both deleted and current Ideas.
     *
     * @param group The booking group's id.
     * @return The owner of the idea booked by this group, or null if it does not exist.
     */
    public static Optional<User> getIdeaOwnerFromGroup(IdeaGroup group) {
        return getIdeaFromGroup(group).map(Idee::getOwner);
    }

    /**
     * Saves and initialize the new idea.
     *
     * @param builder The idea builder.
     * @return The newly created idea.
     */
    public static Idee saveTheIdea(Idee.IdeaBuilder builder) {
        final Idee idea = builder.build();
        idea.lastModified = LocalDateTime.now();
        HibernateUtil.saveit(idea);
        idea.postLoad();
        return idea;
    }

    /**
     * Book an idea.
     *
     * @param idea The idea's id.
     * @param user The person who is booking the idea.
     */
    public static void reserver(Idee idea, User user) {
        idea.bookedBy = user;
        idea.bookedOn = LocalDateTime.now();
        HibernateUtil.update(idea);
    }

    /**
     * Ajoute une sous-réservation à cette idée.
     *
     * @param idea The idea's id.
     */
    public static void sousReserver(Idee idea) {
        idea.isSubBooked = "Y";
        idea.bookedOn = LocalDateTime.now();
        HibernateUtil.update(idea);
    }

    /**
     * Book the idea with a group.
     *
     * @param idea  The idea's id.
     * @param group The booking group's id.
     */
    public static void bookByGroup(Idee idea, IdeaGroup group) {
        idea.group = group;
        idea.bookedOn = LocalDateTime.now();
        HibernateUtil.update(idea);
    }

    /**
     * Unbook an idea if the booker is the user id.
     *
     * @param idea The idea's id.
     * @param user The person who has previously booked the idea.
     */
    public static void dereserver(Idee idea, User user) {
        if (user.equals(idea.bookedBy)) {
            idea.bookedBy = null;
            idea.bookedOn = null;
            HibernateUtil.update(idea);
        }
    }

    /**
     * False if :
     * <ul>
     * <li>The idea belongs to the user</li>
     * <li>The idea is not in the user relationship</li>
     * <li>The idea is already booked (by a group or a person)</li>
     * </ul>
     *
     * @param idea The idea's id.
     * @param user The person's id who is trying to book.
     * @return True if and only if the idea can be booked.
     */
    public static boolean canBook(Idee idea, User user) {
        return UserRelationsRepository.associationExists(idea.getOwner(), user) &&
               idea.bookedBy == null &&
               idea.group == null &&
               "N".equals(idea.isSubBooked);
    }

    /**
     * Supprime toutes les réservations sauf les sous réservations...
     *
     * @param idee L'idée qu'on doit déréserver.
     */
    public static void toutDereserverSaufSousReservation(Idee idee) {

        // Suppression des groupes potentiels
        GroupIdeaContentRepository.removeParticipationTo(idee.group);
        GroupIdeaRepository.deleteGroup(idee.group);

        // Mise a zero des flags
        idee.group = null;
        idee.bookedBy = null;
        idee.bookedOn = null;

        HibernateUtil.update(idee);
    }

    /**
     * Supprime tout type de réservation sur l'idée. Fait aussi le ménage pour les groupes sous-jacent etc.
     *
     * @param idee L'idée qu'on doit déréserver.
     */
    public static void toutDereserver(Idee idee) {

        // Suppression des sous-réservations
        SousReservationRepository.remove(idee);
        idee.isSubBooked = "N";
        HibernateUtil.update(idee);

        // Des autres réservations (simple ou groupe)
        toutDereserverSaufSousReservation(idee);
    }

    /**
     * Removes all elements tight to this idea as well as this idea. Mostly for test purposes.
     *
     * @param idea The idea.
     */
    public static void trueRemove(Idee idea) {
        toutDereserver(idea);
        CommentsRepository.deleteAll(idea);
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            if (idea.group != null) {
                GroupIdeaRepository.deleteGroup(idea.group);
                NotificationsRepository.terminator().whereIdea(idea).terminates();
            }
            s.createQuery("delete from IDEES where id = :id").setParameter("id", idea.getId()).executeUpdate();
            t.commit();
        });
    }

    /**
     * Drops this idea.
     *
     * @param idea The idea's id.
     */
    public static void remove(Idee idea) throws SQLException {
        final int ideaId = idea.getId();
        logger.debug("Suppression de l'idée: {}", ideaId);
        if (idea.isASurprise()) {
            trueRemove(idea);
        } else {
            idea.status = "DELETED";
            idea.lastModified = LocalDateTime.now();
            HibernateUtil.update(idea);
        }
    }

    /**
     * @param user The user's id.
     * @return True if the user has at least one idea.
     */
    public static boolean hasIdeas(User user) {
        return HibernateUtil.doesReturnRows("select 1 from IDEES where owner = ?1", user);
    }

    /**
     * Touch the idea to say it is up to date.
     *
     * @param idee The idea's id.
     */
    public static void touch(Idee idee) {
        idee.lastModified = LocalDateTime.now();
        HibernateUtil.update(idee);
    }

    /**
     * @return All images used for ideas.
     */
    public static List<String> getAllImages() {
        return HibernateUtil.doQueryFetch(s -> s.createQuery(
                "select image from IDEES where image is not null and image <> ''",
                String.class).list());
    }

    /**
     * Restores an idea from the history into the user's list.
     *
     * @param idea           The idea to restore.
     * @param restoreBooking Whether to keeping the booking information or clear them.
     */
    public static void restoreIdea(Idee idea, boolean restoreBooking) {
        idea.status = null;
        idea.lastModified = LocalDateTime.now();
        HibernateUtil.update(idea);
        // Suppression des réservations si demandé
        if (!restoreBooking) {
            toutDereserver(idea);
        }
    }
}
