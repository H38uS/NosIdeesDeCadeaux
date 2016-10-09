package com.mosioj.model.table;

import static com.mosioj.model.table.columns.PrioritesColumns.ID;
import static com.mosioj.model.table.columns.PrioritesColumns.IMAGE;
import static com.mosioj.model.table.columns.PrioritesColumns.NOM;
import static com.mosioj.model.table.columns.PrioritesColumns.ORDRE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.model.Priorite;

public class Priorites extends Table {

	private static final String TABLE_NAME = "PRIORITES";

	public List<Priorite> getPriorities() throws SQLException {

		List<Priorite> priorities = new ArrayList<Priorite>();

		Connection con = getDb().getAConnection();
		try {
			PreparedStatement ps = con.prepareStatement(MessageFormat.format(	"select {0},{1},{2},{3} from {4}",
																				ID,
																				NOM,
																				IMAGE,
																				ORDRE,
																				TABLE_NAME));
			getDb().bindParameters(ps);
			if (ps.execute()) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {
					priorities.add(new Priorite(rs.getInt(ID.name()), rs.getString(NOM.name()),
							rs.getString(IMAGE.name()), rs.getInt(ORDRE.name())));
				}
			}
		} finally {
			con.close();
		}

		return priorities;
	}

}