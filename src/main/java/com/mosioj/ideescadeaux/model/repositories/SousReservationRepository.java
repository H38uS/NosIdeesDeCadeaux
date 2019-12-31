package com.mosioj.ideescadeaux.model.repositories;

import static com.mosioj.ideescadeaux.model.repositories.columns.SousReservationColumns.COMMENT;
import static com.mosioj.ideescadeaux.model.repositories.columns.SousReservationColumns.DATE_RESERVATION;
import static com.mosioj.ideescadeaux.model.repositories.columns.SousReservationColumns.ID;
import static com.mosioj.ideescadeaux.model.repositories.columns.SousReservationColumns.IDEE_ID;
import static com.mosioj.ideescadeaux.model.repositories.columns.SousReservationColumns.USER_ID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.ideescadeaux.model.entities.SousReservationEntity;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.utils.database.PreparedStatementIdKdo;

public class SousReservationRepository extends AbstractRepository {

    public static final String TABLE_NAME = "SOUS_RESERVATION";

    private SousReservationRepository() {
        // Forbidden
    }

    public static void sousReserver(int idea, int userId, String comment) {
        getDb().executeUpdate(MessageFormat.format("insert into {0}({1},{2},{3},{4}) values (?, ?, ?, now()) ",
                                                   TABLE_NAME,
                                                   IDEE_ID,
                                                   USER_ID,
                                                   COMMENT,
                                                   DATE_RESERVATION),
                              idea,
                              userId,
                              comment);
    }

    public static List<SousReservationEntity> getSousReservation(int ideeId) throws SQLException {

        List<SousReservationEntity> reservations = new ArrayList<>();

        String query = MessageFormat.format("select t.{0},t.{1},t.{2},t.{3},t.{4},u.{5},u.{6},u.{7}",
                                            ID,
                                            IDEE_ID,
                                            USER_ID,
                                            COMMENT,
                                            DATE_RESERVATION,
                                            UsersColumns.NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.AVATAR) +
                       MessageFormat.format("  from {0} t ", TABLE_NAME) +
                       MessageFormat.format("  inner join {0} u on t.{1} = u.{2} ",
                                            UsersRepository.TABLE_NAME,
                                            USER_ID,
                                            UsersColumns.ID) +
                       MessageFormat.format(" where t.{0} = ? ", IDEE_ID);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    query)) {
            ps.bindParameters(ideeId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    reservations.add(new SousReservationEntity(res.getInt(ID.name()),
                                                               res.getInt(IDEE_ID.name()),
                                                               new User(res.getInt(USER_ID.name()),
                                                                        res.getString(UsersColumns.NAME.name()),
                                                                        res.getString(UsersColumns.EMAIL.name()),
                                                                        res.getString(UsersColumns.AVATAR.name())),
                                                               res.getString(COMMENT.name()),
                                                               res.getTimestamp(DATE_RESERVATION.name())));
                }
            }
        }

        return reservations;
    }
}
