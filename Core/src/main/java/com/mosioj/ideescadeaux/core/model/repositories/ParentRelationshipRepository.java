package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.ParentRelationshipColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ParentRelationshipRepository extends AbstractRepository {

    public static final String TABLE_NAME = "PARENT_RELATIONSHIP";

    private ParentRelationshipRepository() {
        // Forbidden
    }

    /**
     * @param parentId The parent id.
     * @return Tous les comptes qui sont gérés par procuration
     */
    public static List<User> getChildren(int parentId) throws SQLException {

        List<User> users = new ArrayList<>();

        String sb = MessageFormat.format(" select u.{0}, u.{1}, u.{2}, u.{3}, u.{4} ",
                                         ParentRelationshipColumns.ID,
                                         UsersColumns.NAME,
                                         UsersColumns.EMAIL,
                                         UsersColumns.BIRTHDAY,
                                         UsersColumns.AVATAR) +
                    MessageFormat.format("   from {0} t ", TABLE_NAME) +
                    MessageFormat.format("   left join {0} u", UsersRepository.TABLE_NAME) +
                    MessageFormat.format("     on u.{0} = t.{1}", UsersColumns.ID, ParentRelationshipColumns.CHILD_ID) +
                    MessageFormat.format("  where t.{0} = ?", ParentRelationshipColumns.PARENT_ID);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    sb)) {
            ps.bindParameters(parentId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    users.add(new User(res.getInt(ParentRelationshipColumns.ID.name()),
                                       res.getString(UsersColumns.NAME.name()),
                                       res.getString(UsersColumns.EMAIL.name()),
                                       res.getDate(UsersColumns.BIRTHDAY.name()),
                                       res.getString(UsersColumns.AVATAR.name())));
                }
            }

        }

        return users;
    }

    /**
     * @param childId The child's id.
     * @return Les détenants de la procuration. Peut être vide.
     */
    public static List<User> getParents(int childId) throws SQLException {

        List<User> users = new ArrayList<>();

        String sb = MessageFormat.format(" select u.{0}, u.{1}, u.{2}, u.{3}, u.{4} ",
                                         ParentRelationshipColumns.ID,
                                         UsersColumns.NAME,
                                         UsersColumns.EMAIL,
                                         UsersColumns.BIRTHDAY,
                                         UsersColumns.AVATAR) +
                    MessageFormat.format("   from {0} t ", TABLE_NAME) +
                    MessageFormat.format("   left join {0} u", UsersRepository.TABLE_NAME) +
                    MessageFormat.format("     on u.{0} = t.{1}",
                                         UsersColumns.ID,
                                         ParentRelationshipColumns.PARENT_ID) +
                    MessageFormat.format("  where t.{0} = ?", ParentRelationshipColumns.CHILD_ID);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    sb)) {
            ps.bindParameters(childId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    users.add(new User(res.getInt(ParentRelationshipColumns.ID.name()),
                                       res.getString(UsersColumns.NAME.name()),
                                       res.getString(UsersColumns.EMAIL.name()),
                                       res.getDate(UsersColumns.BIRTHDAY.name()),
                                       res.getString(UsersColumns.AVATAR.name())));
                }
            }

        }

        return users;
    }

    /**
     * @param parentId The parent's id.
     * @param childId  The child's id.
     * @return True if and only if this relation already exists.
     */
    public static boolean noRelationExists(int parentId, int childId) {
        return !getDb().doesReturnRows(MessageFormat.format("select 1 from {0} where {1} = ? and {2} = ?",
                                                            TABLE_NAME,
                                                            ParentRelationshipColumns.PARENT_ID,
                                                            ParentRelationshipColumns.CHILD_ID),
                                       parentId,
                                       childId);
    }

    /**
     * Deletes all the parents of the given child if it exists.
     *
     * @param child The parent's child.
     */
    public static void deleteParents(User child) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?",
                                                   TABLE_NAME,
                                                   ParentRelationshipColumns.CHILD_ID), child.id);
    }

    /**
     * @param parentId The parent id.
     * @param childId  The child id.
     */
    public static void addProcuration(int parentId, int childId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("insert into {0} ({1},{2},{3}) values (?, ?, now())",
                                                   TABLE_NAME,
                                                   ParentRelationshipColumns.PARENT_ID,
                                                   ParentRelationshipColumns.CHILD_ID,
                                                   ParentRelationshipColumns.CREATION_DATE),
                              parentId,
                              childId);
    }

    public static void deleteAllRelationForUser(int userId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? or {2} = ?",
                                                   TABLE_NAME,
                                                   ParentRelationshipColumns.PARENT_ID,
                                                   ParentRelationshipColumns.CHILD_ID),
                              userId,
                              userId);
    }
}
