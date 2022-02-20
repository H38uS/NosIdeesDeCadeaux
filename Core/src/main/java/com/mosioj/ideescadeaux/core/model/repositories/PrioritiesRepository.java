package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.Priorite;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;

import java.util.List;

public class PrioritiesRepository {

    private PrioritiesRepository() {
        // Forbidden
    }

    public static List<Priorite> getPriorities() {
        return HibernateUtil.doQueryFetch(s -> s.createQuery("from PRIORITES order by ordre desc", Priorite.class)
                                                .list());
    }

}
