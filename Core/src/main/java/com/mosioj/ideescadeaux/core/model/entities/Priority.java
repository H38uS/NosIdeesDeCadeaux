package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "PRIORITES")
public class Priority {

    /** The table's id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "nom", length = 40, unique = true)
    public String name;

    @Column(name = "ordre", unique = true)
    public int order;

    @Column(length = 3000)
    @Expose
    public String image;

    public Priority() {
        // For Hibernate
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Priority priority = (Priority) o;
        return id == priority.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
