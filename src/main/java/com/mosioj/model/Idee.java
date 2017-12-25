package com.mosioj.model;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.viewhelper.Escaper;

public class Idee {

	private final int id;
	public String text;
	private final String type;
	private final Categorie cat;
	private final User bookingOwner;
	private final int group;
	private final String image;
	public final User owner;
	private final int priorite;
	private final Timestamp bookedOn;
	private final Timestamp lastModified;
	private final boolean isPartiallyBooked;
	public String displayClass = "";

	public Idee(int pId, User owner, String pText, String pType, User pBookingOwner, int pGroupKDO, String image, String catImage,
			String catAlt, String catTitle, int priorite, Timestamp bookedOn, Timestamp lastModified, String isPartiallyBooked) {
		id = pId;
		text = pText;
		type = pType;
		cat = pType.isEmpty() ? null : new Categorie(pType, catAlt, catImage, catTitle);
		bookingOwner = pBookingOwner;
		group = pGroupKDO;
		this.image = image;
		this.owner = owner;
		this.priorite = priorite;
		this.bookedOn = bookedOn;
		this.lastModified = lastModified;
		this.isPartiallyBooked = "Y".equals(isPartiallyBooked);
	}

	/**
	 * 
	 * @return True if the idea is booked (by a owner, or a group)
	 */
	public boolean isBooked() {
		return bookingOwner != null || group > 0;
	}

	public int getPriorite() {
		return priorite;
	}

	/**
	 * 
	 * @return The css class to use for this idea.
	 */
	public String getDisplayClass() {
		return displayClass;
	}

	public int getId() {
		return id;
	}
	
	public boolean isPartiallyBooked() {
		return isPartiallyBooked;
	}

	public String getBookingDate() {
		if (bookedOn == null)
			return null;
		return new SimpleDateFormat(IdeesCadeauxServlet.DATETIME_DISPLAY_FORMAT).format(bookedOn);
	}

	public String getModificationDate() {
		if (lastModified == null)
			return null;
		return new SimpleDateFormat(IdeesCadeauxServlet.DATETIME_DISPLAY_FORMAT).format(lastModified);
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
	 * @param maxLength
	 * @return The idea text, with a maximum of maxLength characters.
	 */
	public String getTextSummary(int maxLength) {

		String initial = Escaper.htmlToText(getText());
		if (initial.length() > maxLength) {
			return initial.substring(0, maxLength - 3) + "...";
		}

		return initial;
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

	public String getImageSrcSmall() {
		return MessageFormat.format("small/{0}", image);
	}

	public String getImageSrcLarge() {
		return MessageFormat.format("large/{0}", image);
	}

	public Categorie getCategory() {
		return cat;
	}

	/**
	 * 
	 * @return The person's idea.
	 */
	public User getOwner() {
		return owner;
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
