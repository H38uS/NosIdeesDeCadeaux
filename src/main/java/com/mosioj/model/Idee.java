package com.mosioj.model;

public class Idee {

	private final int id;
	private final String text;
	private final String type;
	private final Categorie cat;
	private final User bookingOwner;
	private final int group;

	public Idee(int pId, String pText, String pType, User pBookingOwner, int pGroupKDO, String catImage, String catAlt, String catTitle) {
		id = pId;
		text = pText.replaceAll("&lt;br/&gt;", "\n");
		type = pType;
		cat = pType.isEmpty() ? null : new Categorie(pType, catAlt, catImage, catTitle);
		bookingOwner = pBookingOwner;
		group = pGroupKDO;
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

	/**
	 * 
	 * @return The person who booked this idea. Null if nobodies books it, or it a group did it.
	 */
	public User getBookingOwner() {
		return bookingOwner;
	}

	public int getGroupKDO() {
		return group;
	}
}
