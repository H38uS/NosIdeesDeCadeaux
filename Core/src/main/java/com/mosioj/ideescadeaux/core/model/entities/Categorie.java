package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import org.apache.commons.text.WordUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity(name = "CATEGORIES")
public class Categorie {

    @Id
    @Column(name = "nom", length = 20, unique = true)
    private String name;

    @Expose
    @Column(length = 20)
    private String alt;

    @Expose
    @Column(length = 20)
    private String image;

    @Expose
    @Column(length = 50)
    private String title;

    public Categorie() {
        // For Hibernate
    }

    public String getName() {
        return name;
    }

    public String getAlt() {
        return WordUtils.capitalize(alt);
    } // FIXME supprimer quand plus utilisé dans les jsp

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categorie categorie = (Categorie) o;
        if (name == null) {
            return categorie.name == null;
        }
        return name.equals(categorie.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
