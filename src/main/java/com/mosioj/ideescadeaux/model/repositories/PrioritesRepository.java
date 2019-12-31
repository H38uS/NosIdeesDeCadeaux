package com.mosioj.ideescadeaux.model.repositories;

import static com.mosioj.ideescadeaux.model.repositories.columns.PrioritesColumns.ID;
import static com.mosioj.ideescadeaux.model.repositories.columns.PrioritesColumns.IMAGE;
import static com.mosioj.ideescadeaux.model.repositories.columns.PrioritesColumns.NOM;
import static com.mosioj.ideescadeaux.model.repositories.columns.PrioritesColumns.ORDRE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.ideescadeaux.model.entities.Priorite;
import com.mosioj.ideescadeaux.utils.database.PreparedStatementIdKdo;

public class PrioritesRepository extends AbstractRepository {

    public static final String TABLE_NAME = "PRIORITES";

    private PrioritesRepository() {
        // Forbidden
    }

    public static List<Priorite> getPriorities() throws SQLException {

        List<Priorite> priorities = new ArrayList<>();

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    MessageFormat.format(
                                                                            "select {0},{1},{2},{3} from {4} order by {3} desc",
                                                                            ID,
                                                                            NOM,
                                                                            IMAGE,
                                                                            ORDRE,
                                                                            TABLE_NAME))) {
            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                while (rs.next()) {
                    priorities.add(new Priorite(rs.getInt(ID.name()),
                                                rs.getString(NOM.name()),
                                                rs.getString(IMAGE.name()),
                                                rs.getInt(ORDRE.name())));
                }
            }
        }

        return priorities;
    }

}
