package com.mosioj.model.table;

import static com.mosioj.model.table.columns.IdeeColumns.GROUPE_KDO_ID;
import static com.mosioj.model.table.columns.IdeeColumns.ID;
import static com.mosioj.model.table.columns.IdeeColumns.IDEE;
import static com.mosioj.model.table.columns.IdeeColumns.OWNER;
import static com.mosioj.model.table.columns.IdeeColumns.PRIORITE;
import static com.mosioj.model.table.columns.IdeeColumns.RESERVE;
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
import com.mosioj.model.User;
import com.mosioj.model.table.columns.CategoriesColumns;

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
		try {
			StringBuilder query = new StringBuilder();
			query.append(MessageFormat.format(	"select i.{0}, i.{1}, i.{2}, i.{3}, i.{4}, c.image, c.alt, c.title ",
												ID,
												IDEE,
												TYPE,
												RESERVE,
												GROUPE_KDO_ID));
			query.append(MessageFormat.format("from {0} i ", TABLE_NAME));
			query.append(MessageFormat.format("left join {0} c ", Categories.TABLE_NAME));
			query.append("on i.type = c.nom ");
			query.append(MessageFormat.format("where i.{0} = ?", OWNER));

			PreparedStatement ps = con.prepareStatement(query.toString());
			getDb().bindParameters(ps, ownerId);
			if (ps.execute()) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {
					User bookingOwner = null;
					if (rs.getString(RESERVE.name()) != null) {
						int id = rs.getInt(RESERVE.name());
						bookingOwner = new User(id);
					}
					ideas.add(new Idee(	rs.getInt(ID.name()),
										rs.getString(IDEE.name()),
										rs.getString(TYPE.name()),
										bookingOwner,
										rs.getInt(GROUPE_KDO_ID.name()),
										rs.getString(CategoriesColumns.IMAGE.name()),
										rs.getString(CategoriesColumns.ALT.name()),
										rs.getString(CategoriesColumns.TITLE.name())));
				}
			}
		} finally {
			con.close();
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

		try {
			logger.info(MessageFormat.format("Insert query: {0}", insert.toString()));
			PreparedStatement ps = con.prepareStatement(insert.toString());
			logger.info(MessageFormat.format("Parameters: [{0}, {1}, {2}, {3}]", ownerId, text, type, priorite));
			getDb().bindParameters(ps, ownerId, text, type, priorite);
			ps.execute();
		} finally {
			con.close();
		}
	}

	/**
	 * Book an idea.
	 * 
	 * @param idea
	 * @param userId
	 * @throws SQLException
	 */
	public void reserver(int idea, int userId) throws SQLException {

		Connection con = getDb().getAConnection();

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format("update {0} ", TABLE_NAME));
		query.append("set reserve = ? ");
		query.append("where id = ? ");

		try {
			logger.trace("Query: " + query.toString());
			PreparedStatement ps = con.prepareStatement(query.toString());
			getDb().bindParameters(ps, userId, idea);
			ps.execute();
		} finally {
			con.close();
		}
	}
}
