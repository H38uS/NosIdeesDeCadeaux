package com.mosioj.model.table;

import static com.mosioj.model.table.columns.IdeeColumns.A_SOUS_RESERVATION;
import static com.mosioj.model.table.columns.IdeeColumns.GROUPE_KDO_ID;
import static com.mosioj.model.table.columns.IdeeColumns.ID;
import static com.mosioj.model.table.columns.IdeeColumns.IDEE;
import static com.mosioj.model.table.columns.IdeeColumns.IMAGE;
import static com.mosioj.model.table.columns.IdeeColumns.MODIFICATION_DATE;
import static com.mosioj.model.table.columns.IdeeColumns.OWNER;
import static com.mosioj.model.table.columns.IdeeColumns.SURPRISE_PAR;
import static com.mosioj.model.table.columns.IdeeColumns.PRIORITE;
import static com.mosioj.model.table.columns.IdeeColumns.RESERVE;
import static com.mosioj.model.table.columns.IdeeColumns.RESERVE_LE;
import static com.mosioj.model.table.columns.IdeeColumns.TYPE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.model.Priorite;
import com.mosioj.model.User;
import com.mosioj.model.table.columns.CategoriesColumns;
import com.mosioj.model.table.columns.CommentsColumns;
import com.mosioj.model.table.columns.GroupIdeaColumns;
import com.mosioj.model.table.columns.GroupIdeaContentColumns;
import com.mosioj.model.table.columns.IdeeColumns;
import com.mosioj.model.table.columns.PrioritesColumns;
import com.mosioj.model.table.columns.SousReservationColumns;
import com.mosioj.model.table.columns.UserRelationsColumns;
import com.mosioj.model.table.columns.UsersColumns;
import com.mosioj.utils.database.PreparedStatementIdKdo;
import com.mosioj.utils.database.PreparedStatementIdKdoInserter;
import com.mosioj.viewhelper.Escaper;

public class Idees extends Table {

	public static final String TABLE_NAME = "IDEES";

	/**
	 * Class logger.
	 */
	private static final Logger logger = LogManager.getLogger(Idees.class);

	/**
	 * Fills the idea structure from a result set query. /!\ The result set must be valid, and have a row available.
	 * 
	 * @param rs
	 * @return The new idea.
	 * @throws SQLException
	 */
	private Idee createIdeaFromQuery(ResultSet rs) throws SQLException {
		User bookingOwner = null;
		if (rs.getString(RESERVE.name()) != null) {
			bookingOwner = new User(rs.getInt("userId"), rs.getString("userName"), rs.getString(UsersColumns.EMAIL.name()));
		}
		User owner = new User(rs.getInt("ownerId"), rs.getString("ownerName"), rs.getString("ownerEmail"));

		User surpriseBy = null;
		if (rs.getString("surpriseName") != null) {
			surpriseBy = new User(rs.getInt("surpriseId"), rs.getString("surpriseName"), rs.getString("surpriseEmail"));
		}

		Idee idea = new Idee(	rs.getInt(ID.name()),
								owner,
								rs.getString(IDEE.name()),
								rs.getString(TYPE.name()),
								bookingOwner,
								rs.getInt(GROUPE_KDO_ID.name()),
								rs.getString("id_image"),
								rs.getString(CategoriesColumns.IMAGE.name()),
								rs.getString(CategoriesColumns.ALT.name()),
								rs.getString(CategoriesColumns.TITLE.name()),
								new Priorite(	rs.getInt(PRIORITE.name()),
												rs.getString("PRIORITY_NAME"),
												rs.getString("PRIORITY_PICTURE"),
												rs.getInt("PRIORITY_ORDER")),
								rs.getTimestamp(RESERVE_LE.name()),
								rs.getTimestamp(MODIFICATION_DATE.name()),
								rs.getString(A_SOUS_RESERVATION.name()),
								surpriseBy);
		return idea;
	}

	/**
	 * 
	 * @return The SQL select/joins to select ideas.
	 */
	private StringBuilder getIdeaBasedSelect() {

		String cTableName = Categories.TABLE_NAME;
		CategoriesColumns cNom = CategoriesColumns.NOM;

		StringBuilder columns = new StringBuilder();
		columns.append(MessageFormat.format("select i.{0}, ", ID));
		columns.append(MessageFormat.format("       i.{0}, ", IDEE));
		columns.append(MessageFormat.format("       i.{0}, ", TYPE));
		columns.append(MessageFormat.format("       i.{0}, ", RESERVE));
		columns.append(MessageFormat.format("       i.{0}, ", GROUPE_KDO_ID));
		columns.append(MessageFormat.format("       i.{0} as id_image, ", IMAGE));
		columns.append(MessageFormat.format("       i.{0}, ", PRIORITE));
		columns.append(MessageFormat.format("       p.{0} as PRIORITY_NAME, ", PrioritesColumns.NOM));
		columns.append(MessageFormat.format("       p.{0} as PRIORITY_PICTURE, ", PrioritesColumns.IMAGE));
		columns.append(MessageFormat.format("       p.{0} as PRIORITY_ORDER, ", PrioritesColumns.ORDRE));
		columns.append(MessageFormat.format("       i.{0}, ", RESERVE_LE));
		columns.append(MessageFormat.format("       i.{0}, ", MODIFICATION_DATE));
		columns.append(MessageFormat.format("       i.{0}, ", A_SOUS_RESERVATION));
		columns.append(MessageFormat.format("       c.{0}, ", CategoriesColumns.IMAGE));
		columns.append(MessageFormat.format("       c.{0}, ", CategoriesColumns.ALT));
		columns.append(MessageFormat.format("       c.{0}, ", CategoriesColumns.TITLE));
		columns.append(MessageFormat.format("       u.{0} as userId, ", UsersColumns.ID));
		columns.append(MessageFormat.format("       u.{0} as userName, ", UsersColumns.NAME));
		columns.append(MessageFormat.format("       u.{0}, ", UsersColumns.EMAIL));
		columns.append(MessageFormat.format("       u1.{0} as ownerId, ", UsersColumns.ID));
		columns.append(MessageFormat.format("       u1.{0} as ownerName, ", UsersColumns.NAME));
		columns.append(MessageFormat.format("       u1.{0} as ownerEmail, ", UsersColumns.EMAIL));
		columns.append(MessageFormat.format("       u2.{0} as surpriseId, ", UsersColumns.ID));
		columns.append(MessageFormat.format("       u2.{0} as surpriseName, ", UsersColumns.NAME));
		columns.append(MessageFormat.format("       u2.{0} as surpriseEmail ", UsersColumns.EMAIL));

		StringBuilder query = new StringBuilder(columns);
		query.append(MessageFormat.format("  from {0} i ", TABLE_NAME));
		query.append(MessageFormat.format("  left join {0} p on i.{1} = p.{2} ", Priorites.TABLE_NAME, PRIORITE, PrioritesColumns.ID));
		query.append(MessageFormat.format("  left join {0} c on i.{1} = c.{2} ", cTableName, TYPE, cNom));
		query.append(MessageFormat.format("  left join {0} u on u.id = i.{1} ", Users.TABLE_NAME, RESERVE));
		query.append(MessageFormat.format("  left join {0} u1 on u1.id = i.{1} ", Users.TABLE_NAME, OWNER));
		query.append(MessageFormat.format("  left join {0} u2 on u2.id = i.{1} ", Users.TABLE_NAME, SURPRISE_PAR));

		return query;
	}

	/**
	 * Retrieves all ideas of a person.
	 * 
	 * @param ownerId
	 * @return
	 * @throws SQLException
	 */
	public List<Idee> getOwnerIdeas(int ownerId) throws SQLException {

		List<Idee> ideas = new ArrayList<Idee>();

		StringBuilder query = getIdeaBasedSelect();
		query.append(MessageFormat.format("where i.{0} = ?", OWNER));
		query.append(MessageFormat.format(" order by p.{0} desc,{1}, {2} desc, {3} desc", PrioritesColumns.ORDRE, IDEE, MODIFICATION_DATE, ID));

		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());

		try {
			ps.bindParameters(ownerId);
			if (ps.execute()) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {
					ideas.add(createIdeaFromQuery(rs));
				}
			}
		} finally {
			ps.close();
		}

		return ideas;
	}

	/**
	 * 
	 * @param idIdee
	 * @return All fields for this idea.
	 * @throws SQLException
	 */
	public Idee getIdea(int idIdee) throws SQLException {

		StringBuilder query = getIdeaBasedSelect();
		query.append(MessageFormat.format("where i.{0} = ?", ID));
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());

		try {
			ps.bindParameters(idIdee);
			if (ps.execute()) {
				ResultSet rs = ps.getResultSet();
				if (rs.next()) {
					return createIdeaFromQuery(rs);
				}
			}
		} finally {
			ps.close();
		}

		return null;
	}

	/**
	 * 
	 * @param groupId
	 * @return The idea id of the idea booked by this group.
	 * @throws SQLException
	 */
	public int getIdeaId(int groupId) throws SQLException {
		return getDb().selectInt(MessageFormat.format("select {0} from {1} where {2} = ?", ID, TABLE_NAME, GROUPE_KDO_ID), groupId);
	}

	/**
	 * 
	 * @param groupId
	 * @return The owner of the idea booked by this group, or null if it does not exist.
	 * @throws SQLException
	 */
	public User getIdeaOwnerFromGroup(int groupId) throws SQLException {

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format("select u.{0}, u.{1}, u.{2} ", UsersColumns.ID, UsersColumns.NAME, UsersColumns.EMAIL));
		query.append(MessageFormat.format("from {0} i ", TABLE_NAME));
		query.append(MessageFormat.format("inner join {0} u on i.{1} = u.{2} ", Users.TABLE_NAME, OWNER, UsersColumns.ID));
		query.append(MessageFormat.format(" where i.{0} = ?", GROUPE_KDO_ID));

		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());

		try {
			ps.bindParameters(groupId);
			if (ps.execute()) {
				ResultSet rs = ps.getResultSet();
				if (rs.next()) {
					return new User(rs.getInt(UsersColumns.ID.name()),
									rs.getString(UsersColumns.NAME.name()),
									rs.getString(UsersColumns.EMAIL.name()));
				}
			}
		} finally {
			ps.close();
		}

		return null;

	}

	/**
	 * 
	 * @param groupId
	 * @param userId
	 * @return The list of users that can contribute to this group. They must also belongs to the user relationship.
	 * @throws SQLException
	 */
	public List<User> getPotentialGroupUser(int groupId, int userId) throws SQLException {

		List<User> users = new ArrayList<User>();

		StringBuilder query = new StringBuilder();

		query.append("\n");
		query.append(MessageFormat.format("select u.{0}, u.{1}, u.{2} \n", UsersColumns.ID, UsersColumns.NAME, UsersColumns.EMAIL));

		// On sélectionne toutes les relations (= second_user) du owner (= first_user) de l'idée...
		query.append(MessageFormat.format("  from {0} ur \n", UserRelations.TABLE_NAME));

		// [ Pour récupérer les infos des users ]
		query.append(MessageFormat.format(" inner join {0} u \n", Users.TABLE_NAME));
		query.append(MessageFormat.format("    on u.{0} = ur.{1} \n", UsersColumns.ID, UserRelationsColumns.SECOND_USER));

		// Récupération du owner de l'idée de ce groupe
		query.append(MessageFormat.format(" inner join {0} i \n", TABLE_NAME));
		query.append(MessageFormat.format("    on ur.{0} = i.{1} \n", UserRelationsColumns.FIRST_USER, OWNER));
		query.append(MessageFormat.format("   and i.{0} = ? \n", GROUPE_KDO_ID));

		// On filtre sur les personnes qui sont amis avec l'utilisateur connecté
		query.append(MessageFormat.format(" inner join {0} friends \n", UserRelations.TABLE_NAME));
		query.append(MessageFormat.format("    on friends.{0} = ? \n", UserRelationsColumns.FIRST_USER));
		query.append(MessageFormat.format(	"   and friends.{0} = ur.{1} \n",
											UserRelationsColumns.SECOND_USER,
											UserRelationsColumns.SECOND_USER));

		// ... Qui ne sont pas déjà dans le groupe !
		query.append(MessageFormat.format(	" where not exists (select 1 from {0} g where g.{1} = ? and g.{2} = ur.{3}) \n",
											GroupIdea.TABLE_NAME_CONTENT,
											GroupIdeaContentColumns.GROUP_ID,
											GroupIdeaContentColumns.USER_ID,
											UserRelationsColumns.SECOND_USER));
		query.append(MessageFormat.format("  order by coalesce(u.{0}, {1})", UsersColumns.NAME, UsersColumns.EMAIL));

		logger.trace(query);
		logger.trace(MessageFormat.format("GroupId: {0} / UserId: {1}", groupId, userId));
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());

		try {
			ps.bindParameters(groupId, userId, groupId);
			if (ps.execute()) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {
					users.add(new User(	rs.getInt(UsersColumns.ID.name()),
										rs.getString(UsersColumns.NAME.name()),
										rs.getString(UsersColumns.EMAIL.name())));
				}
			}
		} finally {
			ps.close();
		}

		return users;
	}

	/**
	 * 
	 * @param ideeId
	 * @return L'utilisateur qui a réservé l'idée, ou null si aucun ne l'a fait.
	 */
	public Integer isBookedBy(int ideeId) {
		try {
			return getDb().selectInt("select " + RESERVE + " from " + TABLE_NAME + " where " + ID + " = ?", ideeId);
		} catch (SQLException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param ideaId
	 * @param userId
	 * @return True if and only if the user has sub booked the idea.
	 * @throws SQLException
	 */
	public boolean isSubBookBy(int ideaId, int userId) throws SQLException {
		return getDb().selectInt(	MessageFormat.format(	"select count(*) from {0} where {1} = ? and {2} = ?",
															SousReservation.TABLE_NAME,
															SousReservationColumns.IDEE_ID,
															SousReservationColumns.USER_ID),
									ideaId,
									userId) > 0;
	}

	/**
	 * Add a new idea in the IDEES table.
	 * 
	 * @param ownerId
	 * @param text
	 * @param type
	 * @param priorite
	 * @param est_surprise
	 * @throws SQLException
	 */
	public int addIdea(int ownerId, String text, String type, int priorite, String image, User surprisePar) throws SQLException {

		StringBuilder insert = new StringBuilder();
		insert.append("insert into ");
		insert.append(TABLE_NAME);
		insert.append(" (");
		insert.append(OWNER).append(",");
		insert.append(IDEE).append(",");
		insert.append(TYPE).append(",");
		insert.append(IMAGE).append(",");
		insert.append(MODIFICATION_DATE).append(",");
		insert.append(SURPRISE_PAR).append(",");
		insert.append(PRIORITE);
		insert.append(") values (?, ?, ?, ?, now(), ?, ?)");

		logger.info(MessageFormat.format("Insert query: {0}", insert.toString()));
		PreparedStatementIdKdoInserter ps = new PreparedStatementIdKdoInserter(getDb(), insert.toString());
		try {
			text = Escaper.textToHtml(text);
			logger.info(MessageFormat.format(	"Parameters: [{0}, {1}, {2}, {3}, {4}, {5}]",
												ownerId,
												text,
												type,
												image,
												surprisePar,
												priorite));
			ps.bindParameters(ownerId, text, type, image, surprisePar == null ? null : surprisePar.id, priorite);

			return ps.executeUpdate();

		} finally {
			ps.close();
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

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format("update {0} ", TABLE_NAME));
		query.append("set reserve = ?, reserve_le = now() ");
		query.append("where id = ? ");

		logger.trace("Query: " + query.toString());
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());
		try {
			ps.bindParameters(userId, idea);
			ps.execute();
		} finally {
			ps.close();
		}
	}

	/**
	 * Ajoute une sous-réservation à cette idée.
	 * 
	 * @param idea
	 * @param userId
	 * @param comment
	 * @throws SQLException
	 */
	public void sousReserver(int idea, int userId, String comment) throws SQLException {

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format("update {0} ", TABLE_NAME));
		query.append("set " + A_SOUS_RESERVATION + " = 'Y', reserve_le = now() ");
		query.append("where id = ? ");

		logger.trace("Query: " + query.toString());
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());
		try {
			ps.bindParameters(idea);
			ps.execute();
		} finally {
			ps.close();
		}
	}

	/**
	 * Book the idea with a group.
	 * 
	 * @param id
	 * @param groupId
	 * @throws SQLException
	 */
	public void bookByGroup(int id, int groupId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"update {0} set {1} = ?, {2} = now() where {3} = ?",
														TABLE_NAME,
														GROUPE_KDO_ID,
														RESERVE_LE,
														ID),
								groupId,
								id);
	}

	/**
	 * Unbook an idea if the booker is the user id.
	 * 
	 * @param idea
	 * @param userId
	 * @throws SQLException
	 */
	public void dereserver(int idea, int userId) throws SQLException {

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format("update {0} ", TABLE_NAME));
		query.append("set reserve = null, reserve_le = null ");
		query.append("where id = ? and reserve = ?");

		logger.trace("Query: " + query.toString());
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());
		try {
			ps.bindParameters(idea, userId);
			ps.execute();
		} finally {
			ps.close();
		}
	}

	/**
	 * Supprime la sous réservation de la personne.
	 * 
	 * @param ideaId
	 * @param userId
	 * @throws SQLException
	 */
	public void dereserverSousPartie(int ideaId, int userId) throws SQLException {
		int nb = getDb().executeUpdate(	MessageFormat.format(	"delete from {0} where {1} = ? and {2} = ?",
																SousReservation.TABLE_NAME,
																SousReservationColumns.IDEE_ID,
																SousReservationColumns.USER_ID),
										ideaId,
										userId);
		if (nb > 0 && getDb().selectInt(
										MessageFormat.format(	"select count(*) from {0} where {1} = ?",
																SousReservation.TABLE_NAME,
																SousReservationColumns.IDEE_ID),
										ideaId) == 0) {
			getDb().executeUpdate(	MessageFormat.format(	"update {0} set {1} = ''N'' where {2} = ?",
															TABLE_NAME,
															A_SOUS_RESERVATION,
															ID),
									ideaId);
		}
	}

	/**
	 * False if :
	 * <ul>
	 * <li>The idea belongs to the user</li>
	 * <li>The idea is not in the user relationship</li>
	 * <li>The idea is already booked (by a group or a person)</li>
	 * </ul>
	 * 
	 * @param idea
	 * @param userId
	 * @return True if and only if the idea can be booked.
	 * @throws SQLException
	 */
	public boolean canBook(int idea, int userId) throws SQLException {

		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) ");
		sb.append("from {0} i ");
		sb.append("inner join {4} r on (i.{5} = r.{6} and r.{7} = ?) or (i.{5} = r.{7} and r.{6} = ?) ");
		sb.append("where i.id = ? and {1} is null and i.{2} is null and {3} <> ? and {8} = ''N''");

		String query = MessageFormat.format(sb.toString(),
											TABLE_NAME,
											RESERVE,
											GROUPE_KDO_ID,
											IdeeColumns.OWNER,
											UserRelations.TABLE_NAME,
											OWNER,
											UserRelationsColumns.FIRST_USER,
											UserRelationsColumns.SECOND_USER,
											A_SOUS_RESERVATION);
		logger.debug(query);
		return getDb().selectInt(query, userId, userId, idea, userId) > 0;
	}

	/**
	 * 
	 * @param id
	 * @param userId
	 * @return True if and only if a sub part of the idea can be booked.
	 * @throws SQLException
	 */
	public boolean canSubBook(int idea, int userId) throws SQLException {

		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) ");
		sb.append("from {0} i ");
		sb.append("inner join {4} r on (i.{5} = r.{6} and r.{7} = ?) or (i.{5} = r.{7} and r.{6} = ?) ");
		sb.append("where i.id = ? and {1} is null and i.{2} is null and {3} <> ? ");
		sb.append(MessageFormat.format(	"  and not exists (select 1 from {0} where i.id = {1} and {2} = ?)",
										SousReservation.TABLE_NAME,
										SousReservationColumns.IDEE_ID,
										SousReservationColumns.USER_ID));

		String query = MessageFormat.format(sb.toString(),
											TABLE_NAME,
											RESERVE,
											GROUPE_KDO_ID,
											IdeeColumns.OWNER,
											UserRelations.TABLE_NAME,
											OWNER,
											UserRelationsColumns.FIRST_USER,
											UserRelationsColumns.SECOND_USER);
		logger.debug(query);
		return getDb().selectInt(query, userId, userId, idea, userId, userId) > 0;
	}

	/**
	 * Drops this idea.
	 * 
	 * @param idea
	 * @throws SQLException
	 */
	public void remove(Integer idea) throws SQLException {
		int groupId = getDb().selectInt("select " + GROUPE_KDO_ID + " from " + TABLE_NAME + " where " + ID + " = ?", idea);
		getDb().executeUpdate(	MessageFormat.format(	"delete from {0} where {1} = ?",
														GroupIdea.TABLE_NAME_CONTENT,
														GroupIdeaContentColumns.GROUP_ID),
								groupId);
		getDb().executeUpdate(	MessageFormat.format("delete from {0} where {1} = ? ", GroupIdea.TABLE_NAME, GroupIdeaColumns.ID),
								groupId);
		getDb().executeUpdate(	MessageFormat.format("delete from {0} where {1} = ? ", Comments.TABLE_NAME, CommentsColumns.IDEA_ID),
								idea);
		logger.debug(MessageFormat.format("Suppression de l''idée: {0}", idea));
		getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, ID), idea);
	}

	/**
	 * 
	 * @param userId
	 * @return True if the user has at least one idea.
	 * @throws SQLException
	 */
	public boolean hasIdeas(int userId) throws SQLException {
		return getDb().doesReturnRows(MessageFormat.format("select 1 from {0} where {1} = ? limit 1", TABLE_NAME, OWNER), userId);
	}

	/**
	 * Touch the idea to say it is up to date.
	 * 
	 * @param ideaId
	 * @throws SQLException
	 */
	public void touch(int ideaId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format("update {0} set {1} = now() where {2} = ?", TABLE_NAME, MODIFICATION_DATE, ID),
								ideaId);
	}

	/**
	 * Modifie les champs suivants d'une idée existante.
	 * 
	 * @param id
	 * @param text
	 * @param type
	 * @param priority
	 * @param image
	 * @throws SQLException
	 */
	public void modifier(int id, String text, String type, String priority, String image) throws SQLException {
		text = Escaper.textToHtml(text);
		getDb().executeUpdate(	MessageFormat.format(	"update {0} set {1} = ?, {2} = ?, {3} = ?, {4} = ?, {5} = now() where {6} = ?",
														TABLE_NAME,
														IDEE,
														TYPE,
														PRIORITE,
														IMAGE,
														MODIFICATION_DATE,
														ID),
								text,
								type,
								priority,
								image,
								id);
	}

}
