package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.SousReservationEntity;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.SousReservationColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.query.NativeQuery;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class SousReservationRepository extends AbstractRepository {

    public static final String TABLE_NAME = "SOUS_RESERVATION";

    private SousReservationRepository() {
        // Forbidden
    }

    /**
     * Supprime la sous réservation éventuelle de cette idée.
     *
     * @param idee L'idée.
     */
    public static void remove(Idee idee) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?",
                                                   SousReservationRepository.TABLE_NAME,
                                                   SousReservationColumns.IDEE_ID), idee.getId());
    }

    /**
     * Supprime la sous réservation éventuelle de cette idée pour cet utilisateur.
     *
     * @param idee L'idée.
     * @param user L'utilisateur qui avait fait une sous-réservation.
     */
    public static void cancel(Idee idee, User user) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? and {2} = ?",
                                                   SousReservationRepository.TABLE_NAME,
                                                   SousReservationColumns.IDEE_ID,
                                                   SousReservationColumns.USER_ID),
                              idee,
                              user.id);
    }

    /**
     * @param user The user.
     * @return The list of ideas the user is participating in.
     */
    public static List<Idee> getMySubBooking(User user) {

        final String query = "select IDEE_ID from SOUS_RESERVATION where USER_ID = ?";
        List<Integer> res = HibernateUtil.doQueryFetch(s -> {
            NativeQuery<Integer> sqlQuery = s.createSQLQuery(query);
            sqlQuery.setParameter(1, user.id);
            return sqlQuery.list();
        });
        return res.stream().map(IdeesRepository::getIdea).filter(Optional::isPresent).map(Optional::get).toList();
    }

    public static void sousReserver(int idea, int userId, String comment) {
        getDb().executeInsert(MessageFormat.format("insert into {0}({1},{2},{3},{4}) values (?, ?, ?, now()) ",
                                                   TABLE_NAME,
                                                   SousReservationColumns.IDEE_ID,
                                                   SousReservationColumns.USER_ID,
                                                   SousReservationColumns.COMMENT,
                                                   SousReservationColumns.DATE_RESERVATION),
                              idea,
                              userId,
                              comment);
    }

    public static List<SousReservationEntity> getSousReservation(int ideeId) {

        String query = MessageFormat.format("select t.{0},t.{1},t.{2},t.{3},t.{4},u.{5},u.{6},u.{7},u.{8}",
                                            SousReservationColumns.ID,
                                            SousReservationColumns.IDEE_ID,
                                            SousReservationColumns.USER_ID,
                                            SousReservationColumns.COMMENT,
                                            SousReservationColumns.DATE_RESERVATION,
                                            UsersColumns.NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.BIRTHDAY,
                                            UsersColumns.AVATAR) +
                       MessageFormat.format("  from {0} t ", TABLE_NAME) +
                       MessageFormat.format("  inner join {0} u on t.{1} = u.{2} ",
                                            UsersRepository.TABLE_NAME,
                                            SousReservationColumns.USER_ID,
                                            UsersColumns.ID) +
                       MessageFormat.format(" where t.{0} = ? ", SousReservationColumns.IDEE_ID);

        List<Object[]> res = HibernateUtil.doQueryFetch(s -> {
            NativeQuery<Object[]> sqlQuery = s.createSQLQuery(query);
            sqlQuery.setParameter(1, ideeId);
            return sqlQuery.list();
        });

        return res.stream()
                  .map(r -> new SousReservationEntity((Integer) r[0],
                                                      (Integer) r[1],
                                                      new User((Integer) r[2],
                                                               (String) r[5],
                                                               (String) r[6],
                                                               (Date) r[7],
                                                               (String) r[8]),
                                                      (String) r[3],
                                                      (Timestamp) r[4]))
                  .toList();
    }
}