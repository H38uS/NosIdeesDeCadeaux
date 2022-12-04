package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.ParentRelationshipColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.query.NativeQuery;

import java.sql.Date;
import java.text.MessageFormat;
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
    public static List<User> getChildren(int parentId) {

        String query = MessageFormat.format(" select u.{0}, u.{1}, u.{2}, u.{3}, u.{4} ",
                                            ParentRelationshipColumns.ID,
                                            UsersColumns.NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.BIRTHDAY,
                                            UsersColumns.AVATAR) +
                       MessageFormat.format("   from {0} t ", TABLE_NAME) +
                       MessageFormat.format("   left join {0} u", UsersRepository.TABLE_NAME) +
                       MessageFormat.format("     on u.{0} = t.{1}",
                                            UsersColumns.ID,
                                            ParentRelationshipColumns.CHILD_ID) +
                       MessageFormat.format("  where t.{0} = ?", ParentRelationshipColumns.PARENT_ID);

        List<Object[]> res = HibernateUtil.doQueryFetch(s -> {
            NativeQuery<Object[]> sqlQuery = s.createSQLQuery(query);
            sqlQuery.setParameter(1, parentId);
            return sqlQuery.list();
        });

        return res.stream()
                  .map(r -> new User((Integer) r[0], (String) r[1], (String) r[2], (Date) r[3], (String) r[4]))
                  .toList();
    }

    /**
     * @param childId The child's id.
     * @return Les détenants de la procuration. Peut être vide.
     */
    public static List<User> getParents(int childId) {

        String query = MessageFormat.format(" select u.{0}, u.{1}, u.{2}, u.{3}, u.{4} ",
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

        List<Object[]> res = HibernateUtil.doQueryFetch(s -> {
            NativeQuery<Object[]> sqlQuery = s.createSQLQuery(query);
            sqlQuery.setParameter(1, childId);
            return sqlQuery.list();
        });

        return res.stream()
                  .map(r -> new User((Integer) r[0], (String) r[1], (String) r[2], (Date) r[3], (String) r[4]))
                  .toList();
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
    public static void deleteParents(User child) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?",
                                                   TABLE_NAME,
                                                   ParentRelationshipColumns.CHILD_ID), child.id);
    }

    /**
     * @param parentId The parent id.
     * @param childId  The child id.
     */
    public static void addProcuration(int parentId, int childId) {
        getDb().executeInsert(MessageFormat.format("insert into {0} ({1},{2},{3}) values (?, ?, now())",
                                                   TABLE_NAME,
                                                   ParentRelationshipColumns.PARENT_ID,
                                                   ParentRelationshipColumns.CHILD_ID,
                                                   ParentRelationshipColumns.CREATION_DATE),
                              parentId,
                              childId);
    }

    public static void deleteAllRelationForUser(int userId) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? or {2} = ?",
                                                   TABLE_NAME,
                                                   ParentRelationshipColumns.PARENT_ID,
                                                   ParentRelationshipColumns.CHILD_ID),
                              userId,
                              userId);
    }
}
