package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;

@Entity(name = "PRIORITES")
public class Priorite {

    /** The table's id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom", length = 20, unique = true)
    private String name;

    @Column(name = "ordre")
    private int order;

    @Expose
    @Column(length = 3000)
    public String image;

    public Priorite() {
        // For Hibernate
    }

    public Priorite(int pId, String pName, String pImage, int pOrder) {
        id = pId;
        name = pName;
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
