package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.Categorie;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;

import java.util.List;

public class CategoriesRepository {

    private CategoriesRepository() {
        // Forbidden
    }

    /**
     * @return The available list of categories.
     */
    public static List<Categorie> getCategories() {
        return HibernateUtil.doQueryFetch(s -> s.createQuery("from CATEGORIES", Categorie.class).list());
    }
}
