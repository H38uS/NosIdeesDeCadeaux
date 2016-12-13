package com.mosioj.model.table;

import static com.mosioj.model.table.columns.CategoriesColumns.ALT;
import static com.mosioj.model.table.columns.CategoriesColumns.IMAGE;
import static com.mosioj.model.table.columns.CategoriesColumns.NOM;
import static com.mosioj.model.table.columns.CategoriesColumns.TITLE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.model.Categorie;
import com.mosioj.utils.database.PreparedStatementIdKdo;

public class Categories extends Table {

	public static final String TABLE_NAME = "CATEGORIES";

	public List<Categorie> getCategories() throws SQLException {

		List<Categorie> categories = new ArrayList<Categorie>();
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(	getDb(),
																MessageFormat.format(	"select {0},{1},{2},{3} from {4}",
																						NOM,
																						IMAGE,
																						ALT,
																						TITLE,
																						TABLE_NAME));
		try {
			if (ps.execute()) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {
					categories.add(new Categorie(	rs.getString(NOM.name()),
													rs.getString(ALT.name()),
													rs.getString(IMAGE.name()),
													rs.getString(TITLE.name())));
				}
			}
		} finally {
			ps.close();
		}

		return categories;
	}
}
