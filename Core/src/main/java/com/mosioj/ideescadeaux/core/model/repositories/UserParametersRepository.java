package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.UserParameter;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.Transaction;

import java.util.*;
import java.util.stream.Collectors;

public class UserParametersRepository {

    private UserParametersRepository() {
        // Forbidden
    }

    public static void deleteAllUserParameters(int userId) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.createQuery("delete from USER_PARAMETERS where user_id = :user_id")
             .setParameter("user_id", userId)
             .executeUpdate();
            t.commit();
        });
    }

    /**
     * Updates or insert a new parameter for this user.
     *
     * @param user       The user.
     * @param paramName  The parameter name.
     * @param paramValue The new value to insert/update.
     */
    public static void insertUpdateParameter(User user, String paramName, String paramValue) {
        Optional<UserParameter> parameter = getParameter(user.id, paramName);
        parameter.ifPresent(p -> {
            p.parameterValue = paramValue;
            HibernateUtil.update(p);
        });
        if (parameter.isEmpty()) {
            HibernateUtil.saveit(new UserParameter(user, paramName, paramValue));
        }
    }

    public static Optional<UserParameter> getParameter(int userId, String paramName) {
        final String query = "from USER_PARAMETERS where parameter_name = :name and user_id = :user_id";
        return HibernateUtil.doQueryOptional(s -> s.createQuery(query, UserParameter.class)
                                                   .setParameter("name", paramName)
                                                   .setParameter("user_id", userId)
                                                   .uniqueResultOptional());
    }

    /**
     * @param user The user.
     * @return The notification parameters for this user.
     */
    public static List<UserParameter> getUserNotificationParameters(final User user) {

        // Existing parameters set by the user
        List<UserParameter> definedParameters = HibernateUtil.doQueryFetch(
                s -> s.createQuery("from USER_PARAMETERS where user_id = :user_id", UserParameter.class)
                      .setParameter("user_id", user.id)
                      .list());

        // Remove any unknown parameter
        definedParameters.removeAll(definedParameters.stream().filter(p -> !NType.exists(p.parameterName)).toList());

        // Adding any missing ones
        Set<NType> allTypes = new HashSet<>(Arrays.asList(NType.values()));
        allTypes.removeAll(definedParameters.stream()
                                            .map(UserParameter::getParameterName)
                                            .filter(NType::exists)
                                            .map(NType::valueOf)
                                            .collect(Collectors.toSet()));
        definedParameters.addAll(allTypes.stream().map(n -> new UserParameter(user, n.name(), "EMAIL_SITE")).toList());

        // Sort by name
        definedParameters.sort(Comparator.comparing(a -> a.parameterName));

        return definedParameters;
    }
}
