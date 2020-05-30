package com.mosioj.ideescadeaux.webapp.servlets.controllers.relations;

import java.util.Objects;

public class Page {

    public Integer numero;

    public Page(Integer numero) {
        this.numero = numero;
    }

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