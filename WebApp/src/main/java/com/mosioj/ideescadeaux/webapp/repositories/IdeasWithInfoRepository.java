package com.mosioj.ideescadeaux.webapp.repositories;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class IdeasWithInfoRepository {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(IdeasWithInfoRepository.class);

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
                   left join fetch i.questions
                   left join fetch i.comments
                   left join fetch i.upToDateRequest
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
     * Retrieves all ideas of a person that were previously deleted.
     * Filters surprises.
     *
     * @param owner The person for which we are getting all the ideas.
     * @return The person's ideas list.
     */
    public static Set<Idee> getDeletedIdeasOf(User owner) {
        final String query = getIdeaBaseSelect() +
                             "where i.owner = :owner " +
                             "  and coalesce(i.status, 'THERE') = 'DELETED' " +
                             "order by i.lastModified desc, i.id desc";
        return new LinkedHashSet<>(HibernateUtil.doQueryFetch(s -> s.createQuery(query, Idee.class)
                                                                    .setParameter("owner", owner)
                                                                    .list()));
    }

    /**
     * @param thisOne The person.
     * @return All the ideas where this user has a booking, or belongs to a group or a sub part.
     */
    public static Set<Idee> getIdeasWhereIDoParticipateIn(User thisOne) {

        long start = System.nanoTime();

        // On sélectionne uniquement les idées
        // 1. Qu'on a réservé
        // 2. Dont on fait partie d'un groupe
        // 3. Qu'on a sous-réservé

        final String query = getIdeaBaseSelect() + """
                   left join i.partialBooking pb
                  where coalesce(i.status, 'THERE') <> 'DELETED'
                    and (
                        i.bookedBy = :user or
                        contents.user = :user or
                        pb.user = :user
                    )
                """;

        Set<Idee> booked = new LinkedHashSet<>(HibernateUtil.doQueryFetch(s -> s.createQuery(query, Idee.class)
                                                                                .setParameter("user", thisOne)
                                                                                .list()));

        long end = System.nanoTime();
        logger.debug(MessageFormat.format("Query executed in {0} ms for user {1}", (end - start) / 1000000L, thisOne));

        return booked;
    }

    public static Optional<Idee> getIdea(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        final String query = getIdeaBaseSelect() + """
                 where i.id = :id
                   and coalesce(i.status, 'THERE') <> 'DELETED'
                """;
        return HibernateUtil.doQueryOptionalFromListOperation(s -> s.createQuery(query, Idee.class)
                                                                    .setParameter("id", id)
                                                                    .list());
    }
}
