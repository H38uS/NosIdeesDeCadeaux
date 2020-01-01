package com.mosioj.ideescadeaux.model.entities;

import org.apache.commons.lang3.text.WordUtils;

public class Categorie {

    private final String name;
    private final String alt;
    private final String image;
    private final String title;

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
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

}
