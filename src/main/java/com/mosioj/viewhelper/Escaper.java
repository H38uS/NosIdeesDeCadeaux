package com.mosioj.viewhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Escaper {

	private static final Logger logger = LogManager.getLogger(Escaper.class);

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
		String res = text.replaceAll("\n", "<br/>").replaceAll("(https?://[^\\s]*)", "<a href=\"$0\" target=\"_blank\">$0</a>");

		List<String> tmp = new ArrayList<String>(Arrays.asList(res.split("<a href=\"([^\\s]*)\" target=\"_blank\">")));
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
		return html.replaceAll("<br/>", "\n").replaceAll("<a href=\"?([^\\s\"]*)\"?( target=\"?_blank\"?)?>[^\\s]*</a>", "$1");
	}

	/**
	 * 
	 * @param initialName The initial upload name.
	 * @return The computed name to use, with the png extension.
	 */
	public static String computeImageName(String initialName) {

		logger.debug("Initial image name: " + initialName);
		String fileName = initialName;
		if (fileName.contains(".")) {
			// Drop all of them
			fileName = fileName.substring(0, fileName.indexOf("."));
		}
		if (fileName.isEmpty()) {
			fileName = "IMG";
		}
		
		Random r = new Random();
		int id = Math.abs(r.nextInt());
		int maxSize = 60;
		if (fileName.length() > maxSize) {
			fileName = fileName.substring(0, maxSize - 4) + "_" + id;
		} else {
			fileName = fileName + "_" + id;
		}

		fileName = fileName.replaceAll("'", "");
		fileName = fileName.replaceAll("[éêè]", "e");
		fileName = fileName.replaceAll("î", "i");
		fileName = fileName.replaceAll("ô", "o");
		fileName = fileName.replaceAll("[ùû]", "u");
		fileName = fileName.replaceAll("[àâ]", "a");
		fileName = StringEscapeUtils.escapeHtml4(fileName);

		logger.debug("Computed image name: " + fileName + ".png");
		return fileName + ".png";
	}

}
