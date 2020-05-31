package com.mosioj.ideescadeaux.webapp.servlets.controllers.relations;

import com.google.gson.annotations.Expose;

import java.util.Objects;

public class Page {

    /** The page number. */
    @Expose
    private final Integer numero;

    /** The page number. */
    @Expose
    private boolean isSelected;

    /**
     * Builds a new page.
     *
     * @param numero The page number.
     */
    public Page(final Integer numero) {
        this.numero = numero;
    }

    /**
     * Sets the selected property of this page.
     *
     * @param isSelected Whether this page is selected.
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    // FIXME : supprimer quand toutes les pages gérées en service
    public String getNumero() {
        return numero.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return numero.equals(page.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }
}