package com.mosioj.ideescadeaux.core.model.repositories.booking;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.entities.text.SousReservation;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.Transaction;

import java.util.List;

public class SousReservationRepository {

    private SousReservationRepository() {
        // Forbidden
    }

    /**
     * Supprime la sous réservation éventuelle de cette idée.
     *
     * @param idee L'idée.
     */
    public static void remove(Idee idee) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery("delete from SOUS_RESERVATION where idea = :idea")
             .setParameter("idea", idee)
             .executeUpdate();
            t.commit();
        });
    }

    /**
     * Supprime la sous réservation éventuelle.
     *
     * @param id The subbooking id.
     */
    public static void remove(Integer id) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery("delete from SOUS_RESERVATION where id = :id")
             .setParameter("id", id)
             .executeUpdate();
            t.commit();
        });
    }

    public static void sousReserver(Idee idea, User user, String comment) {
        SousReservation booking = SousReservation.getIt(user, idea, comment);
        HibernateUtil.saveit(booking);
    }

    public static List<SousReservation> getSousReservation(Idee idea) {
        final String query = """
                  from SOUS_RESERVATION s
                  left join fetch s.user
                 where s.idea = :idea
                """;
        return HibernateUtil.doQueryFetch(s -> s.createQuery(query, SousReservation.class)
                                                .setParameter("idea", idea)
                                                .list());
    }
}