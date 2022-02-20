package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import org.apache.commons.text.WordUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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

    public Categorie(String pName, String pAlt, String pImage, String pTitle) {
        name = pName;
        alt = pAlt;
        image = pImage;
        title = pTitle;
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

}
