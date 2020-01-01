package com.mosioj.ideescadeaux.model.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.ideescadeaux.model.entities.Priorite;
import com.mosioj.ideescadeaux.utils.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.model.repositories.columns.PrioritesColumns;

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
                                                                            PrioritesColumns.ID,
                                                                            PrioritesColumns.NOM,
                                                                            PrioritesColumns.IMAGE,
                                                                            PrioritesColumns.ORDRE,
                                                                            TABLE_NAME))) {
            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                while (rs.next()) {
                    priorities.add(new Priorite(rs.getInt(PrioritesColumns.ID.name()),
                                                rs.getString(PrioritesColumns.NOM.name()),
                                                rs.getString(PrioritesColumns.IMAGE.name()),
                                                rs.getInt(PrioritesColumns.ORDRE.name())));
                }
            }
        }

        return priorities;
    }

}
