package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.SousReservationEntity;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.SousReservationColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class SousReservationRepository extends AbstractRepository {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(SousReservationRepository.class);

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
        List<Idee> ideas = new ArrayList<>();

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {
            ps.bindParameters(user.id);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    IdeesRepository.getIdea(res.getInt("IDEE_ID")).ifPresent(ideas::add);
                }
            }
        } catch (SQLException e) {
            logger.error("Une erreur est survenue...", e);
        }

        return ideas;
    }

    public static void sousReserver(int idea, int userId, String comment) {
        getDb().executeUpdate(MessageFormat.format("insert into {0}({1},{2},{3},{4}) values (?, ?, ?, now()) ",
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

        List<SousReservationEntity> reservations = new ArrayList<>();

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

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {
            ps.bindParameters(ideeId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    reservations.add(new SousReservationEntity(res.getInt(SousReservationColumns.ID.name()),
                                                               res.getInt(SousReservationColumns.IDEE_ID.name()),
                                                               new User(res.getInt(SousReservationColumns.USER_ID.name()),
                                                                        res.getString(UsersColumns.NAME.name()),
                                                                        res.getString(UsersColumns.EMAIL.name()),
                                                                        res.getDate(UsersColumns.BIRTHDAY.name()),
                                                                        res.getString(UsersColumns.AVATAR.name())),
                                                               res.getString(SousReservationColumns.COMMENT.name()),
                                                               res.getTimestamp(SousReservationColumns.DATE_RESERVATION.name())));
                }
            }
        } catch (SQLException e) {
            logger.error("Une erreur est survenue...", e);
        }

        return reservations;
    }
}
