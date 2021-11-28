package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.core.model.entities.RelationSuggestion;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UserRelationsSuggestionColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class UserRelationsSuggestionRepository extends AbstractRepository {

    public static final String TABLE_NAME = "USER_RELATIONS_SUGGESTION";
    private static final Logger logger = LogManager.getLogger(UserRelationsSuggestionRepository.class);

    private UserRelationsSuggestionRepository() {
        // Forbidden
    }

    /**
     * @param suggestedTo The person who receives the suggestion.
     * @param userId      The user id.
     * @return True if and only if suggestedTo has received a notification for userId.
     */
    public static boolean hasReceivedSuggestionOf(int suggestedTo, int userId) {
        return getDb().doesReturnRows(MessageFormat.format("select 1 from {0} where {1} = ? and {2} = ?",
                                                           TABLE_NAME,
                                                           UserRelationsSuggestionColumns.SUGGESTED_TO,
                                                           UserRelationsSuggestionColumns.USER_ID),
                                      suggestedTo,
                                      userId);
    }

    /**
     * Makes a new suggestion.
     *
     * @param userMakingSuggestion The user suggesting.
     * @param toUser               The user receiving the suggestion.
     * @param suggestedUser        User that may be added.
     */
    public static boolean newSuggestion(int userMakingSuggestion, int toUser, int suggestedUser) throws SQLException {
        String query = MessageFormat.format(
                "insert into {0} ({1},{2},{3},{4}) select * FROM (select ?, ?, now(), ?) as vals where not exists (select 1 from {0} where {1} = ? and {2} = ? and {4} = ?)",
                TABLE_NAME,
                UserRelationsSuggestionColumns.SUGGESTED_BY,
                UserRelationsSuggestionColumns.SUGGESTED_TO,
                UserRelationsSuggestionColumns.SUGGESTION_DATE,
                UserRelationsSuggestionColumns.USER_ID);
        logger.trace(query);
        return getDb().executeUpdate(query,
                                     userMakingSuggestion,
                                     toUser,
                                     suggestedUser,
                                     userMakingSuggestion,
                                     toUser,
                                     suggestedUser) > 0;
    }

    /**
     * @param user The user.
     * @return The suggestions for this user.
     */
    public static List<RelationSuggestion> getUserSuggestions(User user) throws SQLException {

        List<RelationSuggestion> suggestions = new ArrayList<>();

        String query = MessageFormat.format(
                "select u1.{0} by_id,   u1.{1} by_name,   u1.{2} by_email,   u1.{3} by_avatar,   u1.{4} by_birthday," +
                "              u2.{0} to_id,   u2.{1} to_name,   u2.{2} to_email,   u2.{3} to_avatar,   u2.{4} to_birthday," +
                "              u3.{0} user_id, u3.{1} user_name, u3.{2} user_email, u3.{3} user_avatar, u3.{4} user_birthday," +
                "              " +
                UserRelationsSuggestionColumns.SUGGESTION_DATE,
                UsersColumns.ID,
                UsersColumns.NAME,
                UsersColumns.EMAIL,
                UsersColumns.AVATAR,
                UsersColumns.BIRTHDAY) +
                       MessageFormat.format("  from {0} urs ", TABLE_NAME) +
                       MessageFormat.format(" inner join {0} u1 on u1.{1} = urs.{2} ",
                                            UsersRepository.TABLE_NAME,
                                            UsersColumns.ID,
                                            UserRelationsSuggestionColumns.SUGGESTED_BY) +
                       MessageFormat.format(" inner join {0} u2 on u2.{1} = urs.{2} ",
                                            UsersRepository.TABLE_NAME,
                                            UsersColumns.ID,
                                            UserRelationsSuggestionColumns.SUGGESTED_TO) +
                       MessageFormat.format(" inner join {0} u3 on u3.{1} = urs.{2} ",
                                            UsersRepository.TABLE_NAME,
                                            UsersColumns.ID,
                                            UserRelationsSuggestionColumns.USER_ID) +
                       MessageFormat.format(" where {0} = ? ", UserRelationsSuggestionColumns.SUGGESTED_TO) +
                       MessageFormat.format(" order by u1.{0}, u3.{1}", UsersColumns.NAME, UsersColumns.NAME);

        logger.trace(query);
        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    query)) {
            ps.bindParameters(user.id);
            if (ps.execute()) {

                ResultSet res = ps.getResultSet();
                User currentFrom = null;
                User to = null;
                List<User> userSuggestions = new ArrayList<>();
                Time time = null;

                while (res.next()) {

                    time = res.getTime(UserRelationsSuggestionColumns.SUGGESTION_DATE.name());
                    User from = new User(res.getInt("by_id"),
                                         res.getString("by_name"),
                                         res.getString("by_email"),
                                         res.getDate("by_birthday"),
                                         res.getString("by_avatar"));
                    to = new User(res.getInt("to_id"),
                                  res.getString("to_name"),
                                  res.getString("to_email"),
                                  res.getDate("to_birthday"),
                                  res.getString("to_avatar"));
                    userSuggestions.add(new User(res.getInt("user_id"),
                                                 res.getString("user_name"),
                                                 res.getString("user_email"),
                                                 res.getDate("user_birthday"),
                                                 res.getString("user_avatar")));

                    if (currentFrom == null) {
                        currentFrom = from;
                    }

                    // Saving the previous user
                    if (!currentFrom.equals(from)) {
                        suggestions.add(new RelationSuggestion(from, to, userSuggestions, time));
                        userSuggestions.clear();
                        currentFrom = from;
                    }
                }

                if (!userSuggestions.isEmpty()) {
                    suggestions.add(new RelationSuggestion(currentFrom, to, userSuggestions, time));
                }

            }
        }

        return suggestions;
    }

    /**
     * Removes any suggestions for user to suggestedTo.
     *
     * @param suggestedTo The user we initially sent the suggestion to.
     * @param user        The connected user.
     */
    public static void removeIfExists(int suggestedTo, int user) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? and {2} = ?",
                                                   TABLE_NAME,
                                                   UserRelationsSuggestionColumns.SUGGESTED_TO,
                                                   UserRelationsSuggestionColumns.USER_ID),
                              suggestedTo,
                              user);
    }

    public static void removeAllFromAndTo(int userId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? or {2} = ?",
                                                   TABLE_NAME,
                                                   UserRelationsSuggestionColumns.SUGGESTED_TO,
                                                   UserRelationsSuggestionColumns.USER_ID),
                              userId,
                              userId);
    }

}
