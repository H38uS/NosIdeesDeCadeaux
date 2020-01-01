package com.mosioj.ideescadeaux.servlets.controllers.relations;

public class Page {
	
	public Object numero;

	public Page(Object numero) {
		this.numero = numero;
	}

	public String getNumero() {
		return numero.toString();
	}
}