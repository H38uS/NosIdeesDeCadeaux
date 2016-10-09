package com.mosioj.model;

public class Idee {

	private final int id;
	private final String text;
	private final String type;
	private final Categorie cat;

	public Idee(int pId, String pText, String pType, String catImage, String catAlt, String catTitle) {
		id = pId;
		text = pText.replaceAll("&lt;br/&gt;", "\n");
		type = pType;
		cat = pType.isEmpty() ? null : new Categorie(pType, catAlt, catImage, catTitle);
	}

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getType() {
		return type;
	}

	public Categorie getCategory() {
		return cat;
	}
}
