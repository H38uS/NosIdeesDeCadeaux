package com.mosioj.ideescadeaux.model.repositories;

import static com.mosioj.ideescadeaux.model.repositories.columns.ParentRelationshipColumns.CHILD_ID;
import static com.mosioj.ideescadeaux.model.repositories.columns.ParentRelationshipColumns.CREATION_DATE;
import static com.mosioj.ideescadeaux.model.repositories.columns.ParentRelationshipColumns.ID;
import static com.mosioj.ideescadeaux.model.repositories.columns.ParentRelationshipColumns.PARENT_ID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.utils.database.PreparedStatementIdKdo;

public class ParentRelationship extends Table {

    public static final String TABLE_NAME = "PARENT_RELATIONSHIP";

    /**
     * @param parentId The parent id.
     * @return Tous les comptes qui sont gérés par procuration
     */
    public List<User> getChildren(int parentId) throws SQLException {

        List<User> users = new ArrayList<>();

        String sb = MessageFormat.format(" select u.{0}, u.{1}, u.{2}, u.{3} ",
                                         ID,
                                         UsersColumns.NAME,
                                         UsersColumns.EMAIL,
                                         UsersColumns.AVATAR) +
                    MessageFormat.format("   from {0} t ", TABLE_NAME) +
                    MessageFormat.format("   left join {0} u", Users.TABLE_NAME) +
                    MessageFormat.format("     on u.{0} = t.{1}", UsersColumns.ID, CHILD_ID) +
                    MessageFormat.format("  where t.{0} = ?", PARENT_ID);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    sb)) {
            ps.bindParameters(parentId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    users.add(new User(res.getInt(ID.name()),
                                       res.getString(UsersColumns.NAME.name()),
                                       res.getString(UsersColumns.EMAIL.name()),
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
    public List<User> getParents(int childId) throws SQLException {

        List<User> users = new ArrayList<>();

        String sb = MessageFormat.format(" select u.{0}, u.{1}, u.{2}, u.{3} ",
                                         ID,
                                         UsersColumns.NAME,
                                         UsersColumns.EMAIL,
                                         UsersColumns.AVATAR) +
                    MessageFormat.format("   from {0} t ", TABLE_NAME) +
                    MessageFormat.format("   left join {0} u", Users.TABLE_NAME) +
                    MessageFormat.format("     on u.{0} = t.{1}", UsersColumns.ID, PARENT_ID) +
                    MessageFormat.format("  where t.{0} = ?", CHILD_ID);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    sb)) {
            ps.bindParameters(childId);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    users.add(new User(res.getInt(ID.name()),
                                       res.getString(UsersColumns.NAME.name()),
                                       res.getString(UsersColumns.EMAIL.name()),
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
    public boolean noRelationExists(int parentId, int childId) throws SQLException {
        return !getDb().doesReturnRows(MessageFormat.format("select 1 from {0} where {1} = ? and {2} = ?",
                                                            TABLE_NAME,
                                                            PARENT_ID,
                                                            CHILD_ID),
                                       parentId,
                                       childId);
    }

    /**
     * Deletes all the parents of the given child if it exists.
     *
     * @param child The parent's child.
     */
    public void deleteParents(User child) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, CHILD_ID), child.id);
    }

    /**
     * @param parentId The parent id.
     * @param childId  The child id.
     */
    public void addProcuration(int parentId, int childId) {
        getDb().executeUpdate(MessageFormat.format("insert into {0} ({1},{2},{3}) values (?, ?, now())",
                                                   TABLE_NAME,
                                                   PARENT_ID,
                                                   CHILD_ID,
                                                   CREATION_DATE),
                              parentId,
                              childId);
    }

    public void deleteAllRelationForUser(int userId) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? or {2} = ?",
                                                   TABLE_NAME,
                                                   PARENT_ID,
                                                   CHILD_ID),
                              userId,
                              userId);
    }
}
