package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class PrioritiesRepository {

    private PrioritiesRepository() {
        // Forbidden
    }

    public static List<Priority> getPriorities() {
        return HibernateUtil.doQueryFetch(s -> s.createQuery("from PRIORITES order by ordre desc", Priority.class)
                                                .list());
    }

    public static Optional<Priority> getPriority(int id) {
        return HibernateUtil.doQueryOptional(s -> Optional.ofNullable(s.get(Priority.class, id)));
    }
}
