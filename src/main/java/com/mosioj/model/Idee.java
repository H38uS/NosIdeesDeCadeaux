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
	private final Priorite priorite;
	private final Timestamp bookedOn;
	private final Timestamp lastModified;
	private final boolean isPartiallyBooked;
	public String displayClass = "";
	public boolean hasComment = false;
	public boolean hasQuestion = false;
	private User surpriseBy;

	public Idee(int pId, User owner, String pText, String pType, User pBookingOwner, int pGroupKDO, String image, String catImage,
			String catAlt, String catTitle, Priorite priorite, Timestamp bookedOn, Timestamp lastModified, String isPartiallyBooked, User surpriseBy) {
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
		this.surpriseBy = surpriseBy;
	}

	/**
	 * 
	 * @return The owner of the surprise if it exists
	 */
	public User getSurpriseBy() {
		return surpriseBy;
	}

	/**
	 * 
	 * @return True if and only if there are some comments on this idea
	 */
	public boolean hasComment() {
		return hasComment;
	}

	/**
	 * 
	 * @return True if and only if there are some questions on this idea
	 */
	public boolean hasQuestion() {
		return hasQuestion;
	}

	/**
	 * 
	 * @return True if the idea is booked (by a owner, or a group)
	 */
	public boolean isBooked() {
		return bookingOwner != null || group > 0;
	}

	public Priorite getPriorite() {
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
			StringBuilder sb = new StringBuilder();
			boolean needSemiColon = false;
			for (int i = 0; i < maxLength - 3; i++) {
				char c = initial.charAt(i);
				sb.append(c);
				if (needSemiColon && c == ';') {
					needSemiColon = false;
				}
				if (c == '&') {
					needSemiColon = true;
				}
			}
			int i = maxLength - 3;
			while (needSemiColon) {
				if (i == initial.length()) {
					break;
				}
				char c = initial.charAt(i);
				sb.append(c);
				i++;
				if (c == ';') {
					break;
				}
			}
			sb.append("...");
			return  sb.toString();
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
