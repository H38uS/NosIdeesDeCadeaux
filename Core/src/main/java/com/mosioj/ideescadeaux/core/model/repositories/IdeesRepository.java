package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdoInserter;
import com.mosioj.ideescadeaux.core.model.entities.*;
import com.mosioj.ideescadeaux.core.model.repositories.columns.*;
import com.mosioj.ideescadeaux.core.utils.Escaper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IdeesRepository extends AbstractRepository {

    private static final Logger logger = LogManager.getLogger(IdeesRepository.class);

    public static final String TABLE_NAME = "IDEES";

    private IdeesRepository() {
        // Forbidden
    }

    /**
     * Fills the idea structure from a result set query. /!\ The result set must be valid, and have a row available.
     *
     * @param rs The result set generated by the last query execution.
     * @return The new idea.
     */
    private static Idee createIdeaFromQuery(ResultSet rs) throws SQLException {
        User bookingOwner = null;
        if (rs.getString(IdeeColumns.RESERVE.name()) != null) {
            bookingOwner = new User(rs.getInt("userId"),
                                    rs.getString("userName"),
                                    rs.getString(UsersColumns.EMAIL.name()),
                                    rs.getDate(UsersColumns.BIRTHDAY.name()),
                                    rs.getString(UsersColumns.AVATAR.name()));
        }
        User owner = new User(rs.getInt("ownerId"),
                              rs.getString("ownerName"),
                              rs.getString("ownerEmail"),
                              rs.getDate("ownerBirthday"),
                              rs.getString("ownerAvatar"));

        User surpriseBy = null;
        if (rs.getString("surpriseName") != null) {
            surpriseBy = new User(rs.getInt("surpriseId"),
                                  rs.getString("surpriseName"),
                                  rs.getString("surpriseEmail"),
                                  rs.getDate("surpriseBirthday"),
                                  rs.getString("surpriseAvatar"));
        }

        IdeaGroup group = Optional.ofNullable(rs.getString(IdeeColumns.GROUPE_KDO_ID.name()))
                                  .map(Integer::parseInt)
                                  .flatMap(GroupIdeaRepository::getGroupDetails)
                                  .orElse(null);

        Categorie categorie = null;
        if (!StringUtils.isBlank(rs.getString(IdeeColumns.TYPE.name()))) {
            categorie = new Categorie(rs.getString(IdeeColumns.TYPE.name()),
                                      rs.getString(CategoriesColumns.ALT.name()),
                                      rs.getString(CategoriesColumns.IMAGE.name()),
                                      rs.getString(CategoriesColumns.TITLE.name()));
        }

        // Computing the booking information
        final boolean isPartiallyBooked = "Y".equals(rs.getString(IdeeColumns.A_SOUS_RESERVATION.name()));
        final Timestamp bookedOn = rs.getTimestamp(IdeeColumns.RESERVE_LE.name());
        BookingInformation bookingInformation;
        if (bookingOwner == null) {
            if (group == null) {
                if (isPartiallyBooked) {
                    bookingInformation = BookingInformation.fromAPartialReservation(bookedOn);
                } else {
                    bookingInformation = BookingInformation.noBooking();
                }
            } else {
                bookingInformation = BookingInformation.fromAGroup(group, bookedOn);
            }
        } else {
            bookingInformation = BookingInformation.fromASingleUser(bookingOwner, bookedOn);
        }

        return new Idee(rs.getInt(IdeeColumns.ID.name()),
                        owner,
                        Escaper.transformCodeToSmiley(rs.getString(IdeeColumns.IDEE.name())),
                        categorie,
                        rs.getString("id_image"),
                        new Priorite(rs.getInt(IdeeColumns.PRIORITE.name()),
                                     rs.getString("PRIORITY_NAME"),
                                     rs.getString("PRIORITY_PICTURE"),
                                     rs.getInt("PRIORITY_ORDER")),
                        rs.getTimestamp(IdeeColumns.MODIFICATION_DATE.name()),
                        surpriseBy,
                        bookingInformation);
    }

    /**
     * @return The SQL select/joins to select ideas.
     */
    private static StringBuilder getIdeaBasedSelect() {

        CategoriesColumns cNom = CategoriesColumns.NOM;

        StringBuilder columns = new StringBuilder();
        columns.append(MessageFormat.format("select i.{0}, ", IdeeColumns.ID));
        columns.append(MessageFormat.format("       i.{0}, ", IdeeColumns.IDEE));
        columns.append(MessageFormat.format("       i.{0}, ", IdeeColumns.TYPE));
        columns.append(MessageFormat.format("       i.{0}, ", IdeeColumns.RESERVE));
        columns.append(MessageFormat.format("       i.{0}, ", IdeeColumns.GROUPE_KDO_ID));
        columns.append(MessageFormat.format("       i.{0} as id_image, ", IdeeColumns.IMAGE));
        columns.append(MessageFormat.format("       i.{0}, ", IdeeColumns.PRIORITE));
        columns.append(MessageFormat.format("       p.{0} as PRIORITY_NAME, ", PrioritesColumns.NOM));
        columns.append(MessageFormat.format("       p.{0} as PRIORITY_PICTURE, ", PrioritesColumns.IMAGE));
        columns.append(MessageFormat.format("       p.{0} as PRIORITY_ORDER, ", PrioritesColumns.ORDRE));
        columns.append(MessageFormat.format("       i.{0}, ", IdeeColumns.RESERVE_LE));
        columns.append(MessageFormat.format("       i.{0}, ", IdeeColumns.MODIFICATION_DATE));
        columns.append(MessageFormat.format("       i.{0}, ", IdeeColumns.A_SOUS_RESERVATION));
        columns.append(MessageFormat.format("       c.{0}, ", CategoriesColumns.IMAGE));
        columns.append(MessageFormat.format("       c.{0}, ", CategoriesColumns.ALT));
        columns.append(MessageFormat.format("       c.{0}, ", CategoriesColumns.TITLE));
        columns.append(MessageFormat.format("       u.{0} as userId, ", UsersColumns.ID));
        columns.append(MessageFormat.format("       u.{0} as userName, ", UsersColumns.NAME));
        columns.append(MessageFormat.format("       u.{0}, ", UsersColumns.EMAIL));
        columns.append(MessageFormat.format("       u.{0}, ", UsersColumns.BIRTHDAY));
        columns.append(MessageFormat.format("       u.{0}, ", UsersColumns.AVATAR));
        columns.append(MessageFormat.format("       u1.{0} as ownerId, ", UsersColumns.ID));
        columns.append(MessageFormat.format("       u1.{0} as ownerName, ", UsersColumns.NAME));
        columns.append(MessageFormat.format("       u1.{0} as ownerEmail, ", UsersColumns.EMAIL));
        columns.append(MessageFormat.format("       u1.{0} as ownerBirthday, ", UsersColumns.BIRTHDAY));
        columns.append(MessageFormat.format("       u1.{0} as ownerAvatar, ", UsersColumns.AVATAR));
        columns.append(MessageFormat.format("       u2.{0} as surpriseId, ", UsersColumns.ID));
        columns.append(MessageFormat.format("       u2.{0} as surpriseName, ", UsersColumns.NAME));
        columns.append(MessageFormat.format("       u2.{0} as surpriseEmail, ", UsersColumns.EMAIL));
        columns.append(MessageFormat.format("       u2.{0} as surpriseBirthday, ", UsersColumns.BIRTHDAY));
        columns.append(MessageFormat.format("       u2.{0} as surpriseAvatar ", UsersColumns.AVATAR));

        StringBuilder query = new StringBuilder(columns);
        query.append(MessageFormat.format("  from {0} i ", TABLE_NAME));
        query.append(MessageFormat.format("  left join {0} p on i.{1} = p.{2} ",
                                          PrioritesRepository.TABLE_NAME,
                                          IdeeColumns.PRIORITE,
                                          PrioritesColumns.ID));
        query.append(MessageFormat.format("  left join {0} c on i.{1} = c.{2} ",
                                          CategoriesRepository.TABLE_NAME,
                                          IdeeColumns.TYPE,
                                          cNom));
        query.append(MessageFormat.format("  left join {0} u on u.id = i.{1} ",
                                          UsersRepository.TABLE_NAME,
                                          IdeeColumns.RESERVE));
        query.append(MessageFormat.format("  left join {0} u1 on u1.id = i.{1} ",
                                          UsersRepository.TABLE_NAME,
                                          IdeeColumns.OWNER));
        query.append(MessageFormat.format("  left join {0} u2 on u2.id = i.{1} ",
                                          UsersRepository.TABLE_NAME,
                                          IdeeColumns.SURPRISE_PAR));

        return query;
    }

    /**
     * Retrieves all ideas of a person.
     *
     * @param ownerId The person for which we are getting all the ideas.
     * @return The person's ideas list.
     */
    public static List<Idee> getIdeasOf(int ownerId) {

        List<Idee> ideas = new ArrayList<>();

        StringBuilder query = getIdeaBasedSelect();
        query.append(MessageFormat.format("where i.{0} = ?", IdeeColumns.OWNER));
        query.append(MessageFormat.format(" order by p.{0} desc,{1}, {2} desc, {3} desc",
                                          PrioritesColumns.ORDRE,
                                          IdeeColumns.IDEE,
                                          IdeeColumns.MODIFICATION_DATE,
                                          IdeeColumns.ID));

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString())) {
            ps.bindParameters(ownerId);
            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                while (rs.next()) {
                    ideas.add(createIdeaFromQuery(rs));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return ideas;
    }

    /**
     * @param thisOne The person.
     * @return All the ideas where this user has a booking, or belongs to a group or a sub part.
     */
    public static List<Idee> getIdeasWhereIDoParticipateIn(User thisOne) throws SQLException {

        List<Idee> ideas = new ArrayList<>();

        StringBuilder query = getIdeaBasedSelect();

        // Toutes les sous-réservations
        query.append(MessageFormat.format(" left join {0} s on i.{1} = s.{2} and s.{3} = ? \n",
                                          SousReservationRepository.TABLE_NAME,
                                          IdeeColumns.ID,
                                          SousReservationColumns.IDEE_ID,
                                          SousReservationColumns.USER_ID));

        // Les groupes
        query.append(MessageFormat.format(" left join {0} g on i.{1} = g.{2} \n",
                                          GroupIdeaRepository.TABLE_NAME,
                                          IdeeColumns.GROUPE_KDO_ID,
                                          GroupIdeaColumns.ID));
        query.append(MessageFormat.format(" left join {0} gc on g.{1} = gc.{2} and gc.{3} = ? \n",
                                          GroupIdeaRepository.TABLE_NAME_CONTENT,
                                          GroupIdeaColumns.ID,
                                          GroupIdeaContentColumns.GROUP_ID,
                                          GroupIdeaContentColumns.USER_ID));

        // On sélectionne uniquement les idées
        // - Qu'on a réservé
        // - Qu'on a sous-réservé
        // - Dont on fait parti d'un groupe
        query.append(MessageFormat.format(" where i.{0} = ? or s.{1} is not null or gc.{2} is not null \n",
                                          IdeeColumns.RESERVE,
                                          SousReservationColumns.ID,
                                          GroupIdeaContentColumns.GROUP_ID));

        String queryText = query.toString();
        logger.trace(MessageFormat.format("{0}, {1}", queryText, thisOne.id));
        long start = System.nanoTime();

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), queryText)) {
            ps.bindParameters(thisOne.id, thisOne.id, thisOne.id);
            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                while (rs.next()) {
                    ideas.add(createIdeaFromQuery(rs));
                }
            }
        }
        long end = System.nanoTime();
        logger.debug(MessageFormat.format("Query executed in {0} ms for user {1}", (end - start) / 1000000L, thisOne));

        return ideas;
    }

    /**
     * @param idIdee The idea's id.
     * @return All fields for this idea.
     */
    public static Optional<Idee> getIdea(int idIdee) {

        StringBuilder query = getIdeaBasedSelect();
        query.append(MessageFormat.format("where i.{0} = ?", IdeeColumns.ID));

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString())) {
            ps.bindParameters(idIdee);
            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                if (rs.next()) {
                    return Optional.of(createIdeaFromQuery(rs));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return Optional.empty();
    }

    /**
     * @param groupId The booking group's id.
     * @return The idea id of the idea booked by this group.
     */
    public static Optional<Idee> getIdeaFromGroup(int groupId) throws SQLException {

        StringBuilder query = getIdeaBasedSelect();
        query.append(MessageFormat.format(" where i.{0} = ( ", IdeeColumns.ID));
        query.append(MessageFormat.format("    select {0} from {1} where {2} = ?",
                                          IdeeColumns.ID,
                                          TABLE_NAME,
                                          IdeeColumns.GROUPE_KDO_ID));
        query.append(" ) ");

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString())) {
            ps.bindParameters(groupId);
            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                if (rs.next()) {
                    return Optional.of(createIdeaFromQuery(rs));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * @param groupId The booking group's id.
     * @return The owner of the idea booked by this group, or null if it does not exist.
     */
    public static Optional<User> getIdeaOwnerFromGroup(int groupId) throws SQLException {
        String query = MessageFormat.format("select u.{0}, u.{1}, u.{2}, u.{3}, u.{4} ",
                                            UsersColumns.ID,
                                            UsersColumns.NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.BIRTHDAY,
                                            UsersColumns.AVATAR) +
                       MessageFormat.format("from {0} i ", TABLE_NAME) +
                       MessageFormat.format("inner join {0} u on i.{1} = u.{2} ",
                                            UsersRepository.TABLE_NAME,
                                            IdeeColumns.OWNER,
                                            UsersColumns.ID) +
                       MessageFormat.format(" where i.{0} = ?", IdeeColumns.GROUPE_KDO_ID);
        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {
            ps.bindParameters(groupId);
            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                if (rs.next()) {
                    return Optional.of(new User(rs.getInt(UsersColumns.ID.name()),
                                                rs.getString(UsersColumns.NAME.name()),
                                                rs.getString(UsersColumns.EMAIL.name()),
                                                rs.getDate(UsersColumns.BIRTHDAY.name()),
                                                rs.getString(UsersColumns.AVATAR.name())));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * @param groupId The booking group's id.
     * @param userId  The connected user.
     * @return The list of users that can contribute to this group. They must also belongs to the user relationship.
     */
    public static List<User> getPotentialGroupUser(int groupId, int userId) throws SQLException {

        List<User> users = new ArrayList<>();

        StringBuilder query = new StringBuilder();

        query.append("\n");
        query.append(MessageFormat.format("select u.{0}, u.{1}, u.{2}, u.{3}, u.{4} \n",
                                          UsersColumns.ID,
                                          UsersColumns.NAME,
                                          UsersColumns.EMAIL,
                                          UsersColumns.BIRTHDAY,
                                          UsersColumns.AVATAR));

        // On sélectionne toutes les relations (= second_user) du owner (= first_user) de l'idée...
        query.append(MessageFormat.format("  from {0} ur \n", UserRelationsRepository.TABLE_NAME));

        // [ Pour récupérer les infos des users ]
        query.append(MessageFormat.format(" inner join {0} u \n", UsersRepository.TABLE_NAME));
        query.append(MessageFormat.format("    on u.{0} = ur.{1} \n",
                                          UsersColumns.ID,
                                          UserRelationsColumns.SECOND_USER));

        // Récupération du owner de l'idée de ce groupe
        query.append(MessageFormat.format(" inner join {0} i \n", TABLE_NAME));
        query.append(MessageFormat.format("    on ur.{0} = i.{1} \n",
                                          UserRelationsColumns.FIRST_USER,
                                          IdeeColumns.OWNER));
        query.append(MessageFormat.format("   and i.{0} = ? \n", IdeeColumns.GROUPE_KDO_ID));

        // On filtre sur les personnes qui sont amis avec l'utilisateur connecté
        query.append(MessageFormat.format(" inner join {0} friends \n", UserRelationsRepository.TABLE_NAME));
        query.append(MessageFormat.format("    on friends.{0} = ? \n", UserRelationsColumns.FIRST_USER));
        query.append(MessageFormat.format("   and friends.{0} = ur.{1} \n",
                                          UserRelationsColumns.SECOND_USER,
                                          UserRelationsColumns.SECOND_USER));

        // ... Qui ne sont pas déjà dans le groupe !
        query.append(MessageFormat.format(
                " where not exists (select 1 from {0} g where g.{1} = ? and g.{2} = ur.{3}) \n",
                GroupIdeaRepository.TABLE_NAME_CONTENT,
                GroupIdeaContentColumns.GROUP_ID,
                GroupIdeaContentColumns.USER_ID,
                UserRelationsColumns.SECOND_USER));
        query.append(MessageFormat.format("  order by coalesce(u.{0}, {1})", UsersColumns.NAME, UsersColumns.EMAIL));

        logger.trace(query);
        logger.trace(MessageFormat.format("GroupId: {0} / UserId: {1}", groupId, userId));

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString())) {
            ps.bindParameters(groupId, userId, groupId);
            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                while (rs.next()) {
                    users.add(new User(rs.getInt(UsersColumns.ID.name()),
                                       rs.getString(UsersColumns.NAME.name()),
                                       rs.getString(UsersColumns.EMAIL.name()),
                                       rs.getDate(UsersColumns.BIRTHDAY.name()),
                                       rs.getString(UsersColumns.AVATAR.name())));
                }
            }
        }

        return users;
    }

    /**
     * @param ideaId The idea's id.
     * @param user   The person.
     * @return True if and only if the user has sub booked the idea.
     */
    public static boolean isSubBookBy(int ideaId, User user) {
        return getDb().selectCountStar(MessageFormat.format("select count(*) from {0} where {1} = ? and {2} = ?",
                                                            SousReservationRepository.TABLE_NAME,
                                                            SousReservationColumns.IDEE_ID,
                                                            SousReservationColumns.USER_ID),
                                       ideaId,
                                       user.id) > 0;
    }

    /**
     * Add a new idea in the IDEES table.
     *
     * @param owner       New idea's owner.
     * @param text        New idea's text.
     * @param type        New idea's type.
     * @param priorite    New idea's priority.
     * @param image       New idea's picture.
     * @param surprisePar True if this is a surprise.
     * @param createdBy   New idea's creator (can be different from the owner, especially for surprise).
     * @return The idea identifier.
     */
    public static int addIdea(User owner,
                              String text,
                              String type,
                              int priorite,
                              String image,
                              User surprisePar,
                              User createdBy) throws SQLException {

        type = type == null ? "" : type;
        int createdById = createdBy == null ? owner.id : createdBy.id;

        StringBuilder insert = new StringBuilder();
        insert.append("insert into ");
        insert.append(TABLE_NAME);
        insert.append(" (");
        insert.append(IdeeColumns.OWNER).append(",");
        insert.append(IdeeColumns.IDEE).append(",");
        insert.append(IdeeColumns.TYPE).append(",");
        insert.append(IdeeColumns.IMAGE).append(",");
        insert.append(IdeeColumns.MODIFICATION_DATE).append(",");
        insert.append(IdeeColumns.SURPRISE_PAR).append(",");
        insert.append(IdeeColumns.CREE_LE).append(",");
        insert.append(IdeeColumns.CREE_PAR).append(",");
        insert.append(IdeeColumns.PRIORITE);
        insert.append(") values (?, ?, ?, ?, now(), ?, now(), ?, ?)");

        logger.debug(MessageFormat.format("Insert query: {0}", insert.toString()));

        try (PreparedStatementIdKdoInserter ps = new PreparedStatementIdKdoInserter(getDb(), insert.toString())) {
            text = StringEscapeUtils.unescapeHtml4(text);
            text = Escaper.escapeIdeaText(text);
            text = Escaper.transformSmileyToCode(text);
            logger.debug(MessageFormat.format("Parameters: [{0}, {1}, {2}, {3}, {4}, {5}]",
                                              owner.id,
                                              text,
                                              type,
                                              image,
                                              surprisePar,
                                              priorite));
            ps.bindParameters(owner,
                              text,
                              type,
                              image,
                              surprisePar == null ? null : surprisePar.id,
                              createdById,
                              priorite);

            return ps.executeUpdate();
        }

    }

    /**
     * Book an idea.
     *
     * @param idea   The idea's id.
     * @param userId The person who is booking the idea.
     */
    public static void reserver(int idea, int userId) throws SQLException {

        StringBuilder query = new StringBuilder();
        query.append(MessageFormat.format("update {0} ", TABLE_NAME));
        query.append("set reserve = ?, reserve_le = now() ");
        query.append("where id = ? ");

        logger.trace("Query: " + query.toString());
        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString())) {
            ps.bindParameters(userId, idea);
            ps.execute();
        }
    }

    /**
     * Ajoute une sous-réservation à cette idée.
     *
     * @param idea The idea's id.
     */
    public static void sousReserver(int idea) throws SQLException {

        StringBuilder query = new StringBuilder();
        query.append(MessageFormat.format("update {0} ", TABLE_NAME));
        query.append("set ").append(IdeeColumns.A_SOUS_RESERVATION).append(" = 'Y', reserve_le = now() ");
        query.append("where id = ? ");

        logger.trace("Query: " + query.toString());
        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString())) {
            ps.bindParameters(idea);
            ps.execute();
        }
    }

    /**
     * Book the idea with a group.
     *
     * @param id      The idea's id.
     * @param groupId The booking group's id.
     */
    public static void bookByGroup(int id, int groupId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("update {0} set {1} = ?, {2} = now() where {3} = ?",
                                                   TABLE_NAME,
                                                   IdeeColumns.GROUPE_KDO_ID,
                                                   IdeeColumns.RESERVE_LE,
                                                   IdeeColumns.ID),
                              groupId,
                              id);
    }

    /**
     * Unbook an idea if the booker is the user id.
     *
     * @param idea   The idea's id.
     * @param userId The person who has previously booked the idea.
     */
    public static void dereserver(int idea, int userId) throws SQLException {

        StringBuilder query = new StringBuilder();
        query.append(MessageFormat.format("update {0} ", TABLE_NAME));
        query.append("set reserve = null, reserve_le = null ");
        query.append("where id = ? and reserve = ?");

        logger.trace("Query: " + query.toString());
        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString())) {
            ps.bindParameters(idea, userId);
            ps.execute();
        }
    }

    /**
     * Supprime la sous réservation de la personne.
     *
     * @param ideaId The idea's id.
     * @param user   The person who has previously booked a subpart of the idea.
     */
    public static void dereserverSousPartie(int ideaId, User user) throws SQLException {
        int nb = getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? and {2} = ?",
                                                            SousReservationRepository.TABLE_NAME,
                                                            SousReservationColumns.IDEE_ID,
                                                            SousReservationColumns.USER_ID),
                                       ideaId,
                                       user.id);
        if (nb > 0 && getDb().selectCountStar(MessageFormat.format("select count(*) from {0} where {1} = ?",
                                                                   SousReservationRepository.TABLE_NAME,
                                                                   SousReservationColumns.IDEE_ID),
                                              ideaId) == 0) {
            getDb().executeUpdate(MessageFormat.format("update {0} set {1} = ''N'' where {2} = ?",
                                                       TABLE_NAME,
                                                       IdeeColumns.A_SOUS_RESERVATION,
                                                       IdeeColumns.ID),
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
     * @param idea   The idea's id.
     * @param userId The person's id who is trying to book.
     * @return True if and only if the idea can be booked.
     */
    public static boolean canBook(int idea, int userId) {

        String queryText = "select count(*) " +
                           "  from {0} i " +
                           " inner join {4} r on (i.{5} = r.{6} and r.{7} = ?) or (i.{5} = r.{7} and r.{6} = ?) " +
                           " where i.id = ? and {1} is null and i.{2} is null and {3} <> ? and {8} = ''N''";

        String query = MessageFormat.format(queryText,
                                            TABLE_NAME,
                                            IdeeColumns.RESERVE,
                                            IdeeColumns.GROUPE_KDO_ID,
                                            IdeeColumns.OWNER,
                                            UserRelationsRepository.TABLE_NAME,
                                            IdeeColumns.OWNER,
                                            UserRelationsColumns.FIRST_USER,
                                            UserRelationsColumns.SECOND_USER,
                                            IdeeColumns.A_SOUS_RESERVATION);
        logger.trace(query);
        return getDb().selectCountStar(query, userId, userId, idea, userId) > 0;
    }

    /**
     * @param idea   The idea's id.
     * @param userId The user id.
     * @return True if and only if a sub part of the idea can be booked.
     */
    public static boolean canSubBook(int idea, int userId) {

        String queryText = "select count(*) " +
                           "  from {0} i " +
                           " inner join {4} r on (i.{5} = r.{6} and r.{7} = ?) or (i.{5} = r.{7} and r.{6} = ?) " +
                           " where i.id = ? and {1} is null and i.{2} is null and {3} <> ? " +
                           MessageFormat.format("  and not exists (select 1 from {0} where i.id = {1} and {2} = ?)",
                                                SousReservationRepository.TABLE_NAME,
                                                SousReservationColumns.IDEE_ID,
                                                SousReservationColumns.USER_ID);

        String query = MessageFormat.format(queryText,
                                            TABLE_NAME,
                                            IdeeColumns.RESERVE,
                                            IdeeColumns.GROUPE_KDO_ID,
                                            IdeeColumns.OWNER,
                                            UserRelationsRepository.TABLE_NAME,
                                            IdeeColumns.OWNER,
                                            UserRelationsColumns.FIRST_USER,
                                            UserRelationsColumns.SECOND_USER);
        logger.trace(query);
        return getDb().selectCountStar(query, userId, userId, idea, userId, userId) > 0;
    }


    /**
     * Supprime tout type de réservation sur l'idée. Fait aussi le ménage pour les groupes sous-jacent etc.
     *
     * @param idea L'idée qu'on doit déréserver.
     */
    public static void toutDereserver(int idea) throws SQLException {

        // Suppression des groupes potentiels
        getDb().selectInt("select " + IdeeColumns.GROUPE_KDO_ID + " from IDEES where " + IdeeColumns.ID + " = ?", idea)
               .ifPresent(groupId -> {
                              try {
                                  getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?",
                                                                             GroupIdeaRepository.TABLE_NAME_CONTENT,
                                                                             GroupIdeaContentColumns.GROUP_ID),
                                                        groupId);
                                  getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? ",
                                                                             GroupIdeaRepository.TABLE_NAME,
                                                                             GroupIdeaColumns.ID),
                                                        groupId);
                              } catch (SQLException e) {
                                  e.printStackTrace();
                              }
                          }
               );

        // Des sous-reservations
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?",
                                                   SousReservationRepository.TABLE_NAME,
                                                   SousReservationColumns.IDEE_ID),
                              idea);

        // Mise a zero des flags
        getDb().executeUpdate(MessageFormat.format(
                "update {0} set {2} = ''N'', {3} = null, {4} = null, {5} = null where {1} = ?",
                TABLE_NAME,
                IdeeColumns.ID,
                IdeeColumns.A_SOUS_RESERVATION,
                IdeeColumns.RESERVE,
                IdeeColumns.RESERVE_LE,
                IdeeColumns.GROUPE_KDO_ID),
                              idea);

    }

    /**
     * Drops this idea.
     *
     * @param idea The idea's id.
     */
    public static void remove(int idea) throws SQLException {
        try {
            int nb = getDb().executeUpdate(MessageFormat.format("insert into IDEES_HIST select * from {0} where {1} = ?",
                                                                TABLE_NAME,
                                                                IdeeColumns.ID),
                                           idea);
            if (nb != 1) {
                logger.warn(MessageFormat.format("Strange count of idea history: {0}. Idea was idea n#{1}", nb, idea));
            } else {
                getDb().executeUpdate(MessageFormat.format("update IDEES_HIST set {0} = now() where {1} = ?",
                                                           IdeeColumns.MODIFICATION_DATE,
                                                           IdeeColumns.ID),
                                      idea);
            }
        } catch (Exception ignored) {
        }
        toutDereserver(idea);
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? ",
                                                   CommentsRepository.TABLE_NAME,
                                                   CommentsColumns.IDEA_ID),
                              idea);
        logger.debug(MessageFormat.format("Suppression de l''idée: {0}", idea));
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, IdeeColumns.ID), idea);
    }

    /**
     * @param userId The user's id.
     * @return True if the user has at least one idea.
     */
    public static boolean hasIdeas(int userId) {
        return getDb().doesReturnRows(MessageFormat.format("select 1 from {0} where {1} = ? limit 1",
                                                           TABLE_NAME,
                                                           IdeeColumns.OWNER), userId);
    }

    /**
     * Touch the idea to say it is up to date.
     *
     * @param ideaId The idea's id.
     */
    public static void touch(int ideaId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("update {0} set {1} = now() where {2} = ?",
                                                   TABLE_NAME,
                                                   IdeeColumns.MODIFICATION_DATE,
                                                   IdeeColumns.ID),
                              ideaId);
    }

    /**
     * Modifie les champs suivants d'une idée existante.
     *
     * @param id       The idea's id.
     * @param text     The idea's text.
     * @param type     The idea's type.
     * @param priority The idea's priority.
     * @param image    The idea's picture.
     */
    public static void modifier(int id, String text, String type, String priority, String image) throws SQLException {
        text = StringEscapeUtils.unescapeHtml4(text);
        text = Escaper.escapeIdeaText(text);
        text = Escaper.transformSmileyToCode(text);
        getDb().executeUpdate(MessageFormat.format(
                "update {0} set {1} = ?, {2} = ?, {3} = ?, {4} = ?, {5} = now() where {6} = ?",
                TABLE_NAME,
                IdeeColumns.IDEE,
                IdeeColumns.TYPE,
                IdeeColumns.PRIORITE,
                IdeeColumns.IMAGE,
                IdeeColumns.MODIFICATION_DATE,
                IdeeColumns.ID),
                              text,
                              type,
                              priority,
                              image,
                              id);
    }

    /**
     * Purges all idea's (including the history) of a particular user.
     *
     * @param userId The user's id.
     */
    public static void removeAll(int userId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, IdeeColumns.OWNER),
                              userId);
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?",
                                                   "IDEES_HIST",
                                                   IdeeColumns.OWNER),
                              userId);
    }

    /**
     * @param ideeId The idee to look.
     * @param userId The person's id.
     * @return True if this user has asked about this idea.
     */
    public static boolean hasUserAskedIfUpToDate(int ideeId, int userId) {
        return getDb().doesReturnRows(MessageFormat.format("select 1 from {0} where {1} = ? and {2} = ?",
                                                           IsUpToDateQuestionsRepository.TABLE_NAME,
                                                           IsUpToDateColumns.IDEE_ID,
                                                           IsUpToDateColumns.USER_ID),
                                      ideeId,
                                      userId);
    }

    /**
     * @return All images used for ideas.
     */
    public static List<String> getAllImages() throws SQLException {

        List<String> res = new ArrayList<>();
        String query = "select IMAGE from " + TABLE_NAME + " where IMAGE is not null and IMAGE <> ''";

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {
            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                while (rs.next()) {
                    res.add(rs.getString("IMAGE"));
                }
            }
        }

        return res;
    }

}
