package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.Categorie;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;

import java.util.List;
import java.util.Optional;

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

    /**
     * @return The matching category object if found.
     */
    public static Optional<Categorie> getCategory(String type) {
        if (type == null) {
            return Optional.empty();
        }
        return HibernateUtil.doQueryOptional(s -> Optional.ofNullable(s.get(Categorie.class, type)));
    }
}
