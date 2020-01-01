package com.mosioj.ideescadeaux.core.model.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Optional;

import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.core.model.repositories.columns.GroupIdeaColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.GroupIdeaContentColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.IdeeColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.User;

public class GroupIdeaRepository extends AbstractRepository {

    private static final Logger LOGGER = LogManager.getLogger(GroupIdeaRepository.class);

    public static final String TABLE_NAME = "GROUP_IDEA";
    public static final String TABLE_NAME_CONTENT = "GROUP_IDEA_CONTENT";

    private GroupIdeaRepository() {
        // Forbidden
    }

    /**
     * Creates an initial group for an idea. Does not map it to the idea.
     *
     * @param total  Total amount wanted for this idea.
     * @param amount Amount paid by this user.
     * @param userId First user belonging to this new group.
     * @return The group's idea
     */
    public static int createAGroup(double total, double amount, int userId) throws SQLException {
        int id = getDb().executeUpdateGeneratedKey(MessageFormat.format("insert into {0} ({1}) values (?)",
                                                                        TABLE_NAME,
                                                                        GroupIdeaColumns.NEEDED_PRICE),
                                                   total);
        addNewAmount(amount, userId, id);
        return id;
    }

    /**
     * Adds a new participation.
     *
     * @param amount  The amount.
     * @param userId  The user id.
     * @param groupId The group id.
     */
    public static void addNewAmount(double amount, int userId, int groupId) throws SQLException {
        getDb().executeUpdateGeneratedKey(MessageFormat.format(
                "insert into {0} ({1},{2},{3},{4}) values (?, ?, ?, now())",
                TABLE_NAME_CONTENT,
                GroupIdeaContentColumns.GROUP_ID,
                GroupIdeaContentColumns.USER_ID,
                GroupIdeaContentColumns.PRICE,
                GroupIdeaContentColumns.JOIN_DATE),
                                          groupId,
                                          userId,
                                          amount);
    }

    /**
     * @param groupId The group id.
     * @return The optional group object.
     */
    public static Optional<IdeaGroup> getGroupDetails(int groupId) throws SQLException {

        Optional<IdeaGroup> group = Optional.empty();
        StringBuilder q = new StringBuilder();
        q.append("select gi.{0}, gic.{1}, gic.{2}, u.{8}, u.{9}, u.{10} \n ");
        q.append("  from {3} gi, {4} gic \n ");
        q.append("  left join {7} u on u.id = gic.{1} \n ");
        q.append(" where gi.{5} = gic.{6} and gi.{5} = ? ");

        LOGGER.debug(q.toString());

        String query = MessageFormat.format(q.toString(),
                                            GroupIdeaColumns.NEEDED_PRICE,
                                            GroupIdeaContentColumns.USER_ID,
                                            GroupIdeaContentColumns.PRICE,
                                            TABLE_NAME,
                                            TABLE_NAME_CONTENT,
                                            GroupIdeaColumns.ID,
                                            GroupIdeaContentColumns.GROUP_ID,
                                            UsersRepository.TABLE_NAME,
                                            UsersColumns.NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.AVATAR);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {
            ps.bindParameters(groupId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();

                if (res.next()) {
                    IdeaGroup ideaGroup = new IdeaGroup(groupId, res.getDouble(GroupIdeaColumns.NEEDED_PRICE.name()));
                    ideaGroup.addUser(new User(res.getInt(GroupIdeaContentColumns.USER_ID.name()),
                                               res.getString(UsersColumns.NAME.name()),
                                               res.getString(UsersColumns.EMAIL.name()),
                                               res.getString(UsersColumns.AVATAR.name())),
                                      res.getDouble(GroupIdeaContentColumns.PRICE.name()));

                    while (res.next()) {
                        ideaGroup.addUser(new User(res.getInt(GroupIdeaContentColumns.USER_ID.name()),
                                                   res.getString(UsersColumns.NAME.name()),
                                                   res.getString(UsersColumns.EMAIL.name()),
                                                   res.getString(UsersColumns.AVATAR.name())),
                                          res.getDouble(GroupIdeaContentColumns.PRICE.name()));
                    }

                    group = Optional.of(ideaGroup);
                }
            }
        }

        return group;
    }

    /**
     * @param groupId   The group id.
     * @param user      The user id.
     * @param newAmount The new amount
     */
    public static void updateAmount(Integer groupId, User user, double newAmount) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("update {0} set {1} = ? where {2} = ? and {3} = ?",
                                                   TABLE_NAME_CONTENT,
                                                   GroupIdeaContentColumns.PRICE,
                                                   GroupIdeaContentColumns.USER_ID,
                                                   GroupIdeaContentColumns.GROUP_ID),
                              newAmount,
                              user.id,
                              groupId);
    }

    /**
     * @param user    The user.
     * @param groupId The group id.
     * @return true if there is at least one member left
     */
    public static boolean removeUserFromGroup(User user, Integer groupId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? and {2} = ?",
                                                   TABLE_NAME_CONTENT,
                                                   GroupIdeaContentColumns.USER_ID,
                                                   GroupIdeaContentColumns.GROUP_ID),
                              user.id,
                              groupId);
        if (!getDb().doesReturnRows(MessageFormat.format("select 1 from {0} where {1} = ? ",
                                                         TABLE_NAME_CONTENT,
                                                         GroupIdeaContentColumns.GROUP_ID), groupId)) {
            getDb().executeUpdate("delete from " + TABLE_NAME + " where " + GroupIdeaColumns.ID + " = ?", groupId);
            getDb().executeUpdate(MessageFormat.format("update {0} set {1} = null, {2} = null where {1} = ?",
                                                       IdeesRepository.TABLE_NAME,
                                                       IdeeColumns.GROUPE_KDO_ID,
                                                       IdeeColumns.RESERVE_LE),
                                  groupId);
            return false;
        }
        return true;
    }

    /**
     * Removes the given user from all existing groups.
     *
     * @param user The user to remove.
     */
    public static void removeUserFromAllGroups(User user) {

        String query = MessageFormat.format("select distinct {0} from {1} where {2} = ? ",
                                            GroupIdeaContentColumns.GROUP_ID,
                                            TABLE_NAME_CONTENT,
                                            GroupIdeaContentColumns.USER_ID);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {

            ps.bindParameters(user.id);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    removeUserFromGroup(user, res.getInt(GroupIdeaContentColumns.GROUP_ID.name()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(MessageFormat.format(
                    "Error while fetching query to remove user id {0} from groups. Exception: {1}",
                    user,
                    e.getMessage()));
        }

    }

}
