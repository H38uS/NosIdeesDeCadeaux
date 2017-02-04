package com.mosioj.model;

public class Idee {

	private final int id;
	private final String text;
	private final String type;
	private final Categorie cat;
	private final User bookingOwner;
	private final int group;
	private final String image;

	public Idee(int pId, String pText, String pType, User pBookingOwner, int pGroupKDO, String image, String catImage, String catAlt, String catTitle) {
		id = pId;
		text = pText;
		type = pType;
		cat = pType.isEmpty() ? null : new Categorie(pType, catAlt, catImage, catTitle);
		bookingOwner = pBookingOwner;
		group = pGroupKDO;
		this.image = image;
	}

	public int getId() {
		return id;
	}

	/**
	 * 
	 * @return The text displayed in textarea, with \n.
	 */
	public String getText() {
		return text;
	}

	/**
	 * 
	 * @return The idea text stored in DB, that will be presented to the browser.
	 */
	public String getHtml() {
		return text;
	}

	public String getType() {
		return type;
	}

	public String getImage() {
		return image;
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
