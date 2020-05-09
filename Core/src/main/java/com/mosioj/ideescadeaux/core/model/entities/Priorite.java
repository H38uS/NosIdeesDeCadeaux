package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;

public class Priorite {

    private final String name;
    private final int id;
    private final int order;

    @Expose
    public String image;

    public Priorite(int pId, String pName, String pImage, int pOrder) {
        name = pName;
        id = pId;
        image = pImage;
        order = pOrder;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    public String getImage() {
        return image;
    }
}
