package com.mosioj.utils;

public class Compteur {

	private int value;
	
	public Compteur(int value) {
		this.value = value;
	}
	
	public void add(int another) {
		value += another;
	}
	
	public int getValue() {
		return value;
	}
}
