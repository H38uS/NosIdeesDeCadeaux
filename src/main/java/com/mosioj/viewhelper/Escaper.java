package com.mosioj.viewhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Escaper {

	private Escaper() {
		// Forbidden
	}

	private static final int MAX_LENGTH = 30;

	/**
	 * 
	 * @param text
	 * @return The html equivalent of this text.
	 */
	public static String textToHtml(String text) {
		String res = text.replaceAll("\n", "<br/>").replaceAll("(https?://[^\\s]*)", "<a href=\"$0\">$0</a>");

		List<String> tmp = new ArrayList<String>(Arrays.asList(res.split("<a href=\"([^\\s]*)\">")));
		if (!tmp.isEmpty())
			tmp.remove(0);

		for (String link : tmp) {
			link = link.substring(0, link.indexOf("</a>"));
			if (link.length() > MAX_LENGTH) {
				String newOne = ">" + link.substring(0, MAX_LENGTH) + "[...]<";
				res = res.replace(">" + link + "<", newOne);
			}
		}
		return res;
	}

	/**
	 * 
	 * @param html
	 * @return The text equivalent of this html string.
	 */
	public static String htmlToText(String html) {
		return html.replaceAll("<br/>", "\n").replaceAll("<a href=\"([^\\s]*)\">[^\\s]*</a>", "$1");
	}

}
