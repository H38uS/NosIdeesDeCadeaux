package com.mosioj.ideescadeaux.model.repositories;

import static com.mosioj.ideescadeaux.model.repositories.columns.CategoriesColumns.ALT;
import static com.mosioj.ideescadeaux.model.repositories.columns.CategoriesColumns.IMAGE;
import static com.mosioj.ideescadeaux.model.repositories.columns.CategoriesColumns.NOM;
import static com.mosioj.ideescadeaux.model.repositories.columns.CategoriesColumns.TITLE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.ideescadeaux.model.entities.Categorie;
import com.mosioj.ideescadeaux.utils.database.PreparedStatementIdKdo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CategoriesRepository extends AbstractRepository {

    private static final Logger logger = LogManager.getLogger(CategoriesRepository.class);
    public static final String TABLE_NAME = "CATEGORIES";

    private CategoriesRepository() {
        // Forbidden
    }

    /**
     * @return The available list of categories.
     */
    public static List<Categorie> getCategories() {

        List<Categorie> categories = new ArrayList<>();
        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    MessageFormat.format(
                                                                            "select {0},{1},{2},{3} from {4}",
                                                                            NOM,
                                                                            IMAGE,
                                                                            ALT,
                                                                            TITLE,
                                                                            TABLE_NAME))) {
            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                while (rs.next()) {
                    categories.add(new Categorie(rs.getString(NOM.name()),
                                                 rs.getString(ALT.name()),
                                                 rs.getString(IMAGE.name()),
                                                 rs.getString(TITLE.name())));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(MessageFormat.format("Error while fetching categories: {0}.", e.getMessage()));
        }

        return categories;
    }
}
