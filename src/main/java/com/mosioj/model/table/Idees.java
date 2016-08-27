package com.mosioj.model.table;

import static com.mosioj.model.table.columns.IdeeColumns.ID;
import static com.mosioj.model.table.columns.IdeeColumns.IDEE;
import static com.mosioj.model.table.columns.IdeeColumns.OWNER;
import static com.mosioj.model.table.columns.IdeeColumns.PRIORITE;
import static com.mosioj.model.table.columns.IdeeColumns.TYPE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;

public class Idees extends Table {

	public static final String TABLE_NAME = "IDEES";

	/**
	 * Class logger.
	 */
	private static final Logger logger = LogManager.getLogger(Idees.class);

	/**
	 * Retrieves all ideas of a person.
	 * 
	 * @param ownerId
	 * @return
	 * @throws SQLException
	 */
	public List<Idee> getOwnerIdeas(int ownerId) throws SQLException {

		List<Idee> ideas = new ArrayList<Idee>();

		Connection con = getDb().getAConnection();
		PreparedStatement ps = con.prepareStatement("select * from " + TABLE_NAME + " where " + OWNER + " = ?");
		getDb().bindParameters(ps, ownerId);
		if (ps.execute()) {
			ResultSet rs = ps.getResultSet();
			while (rs.next()) {
				ideas.add(new Idee(rs.getInt(ID.name()), rs.getString(IDEE.name()), rs.getString(TYPE.name())));
			}
		}

		return ideas;
	}

	/**
	 * Add a new idea in the IDEES table.
	 * 
	 * @param ownerId
	 * @param text
	 * @param type
	 * @param priorite
	 * @throws SQLException
	 */
	public void addIdea(int ownerId, String text, String type, String priorite) throws SQLException {

		Connection con = getDb().getAConnection();

		StringBuilder insert = new StringBuilder();
		insert.append("insert into ");
		insert.append(TABLE_NAME);
		insert.append(" (");
		insert.append(OWNER).append(",");
		insert.append(IDEE).append(",");
		insert.append(TYPE).append(",");
		insert.append(PRIORITE);
		insert.append(") values (?, ?, ?, ?)");

		logger.info(MessageFormat.format("Insert query: {0}", insert.toString()));
		PreparedStatement ps = con.prepareStatement(insert.toString());
		logger.info(MessageFormat.format("Parameters: [{0}, {1}, {2}, {3}]", ownerId, text, type, priorite));
		getDb().bindParameters(ps, ownerId, text, type, priorite);
		ps.execute();
	}
}
