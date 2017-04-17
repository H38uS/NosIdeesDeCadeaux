package com.mosioj.viewhelper;

public class Escaper {

	private Escaper() {
		// Forbidden
	}

	/**
	 * 
	 * @param text
	 * @return The html equivalent of this text.
	 */
	public static String textToHtml(String text) {
		return text.replaceAll("\n", "<br/>").replaceAll("(https?://[^\\s]*)", "<a href=\"$0\">$0</a>");
	}

	/**
	 * 
	 * @param html
	 * @return The text equivalent of this html string.
	 */
	public static String htmlToText(String html) {
		return html.replaceAll("<br/>", "\n").replaceAll("<a href=[^\\s]*>([^\\s]*)</a>", "$1");
	}

}
