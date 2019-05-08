package com.mosioj.utils;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParametersUtils {

	private static final Logger logger = LogManager.getLogger(ParametersUtils.class);

	/**
	 * Attention: ne surtout pas utiliser dans les redirect post -> get.
	 * 
	 * @param request The processing request.
	 * @param name The parameter name.
	 * @return The parameter value, or empty string if not provided.
	 * @throws UnsupportedEncodingException
	 */
	public static String readIt(HttpServletRequest request, String name) {
		String res = request.getParameter(name);
		logger.trace(MessageFormat.format("{0} is:{1}", name, res));
		try {
			return res == null ? "" : new String(res.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Double readDouble(HttpServletRequest request, String name) {
		double param = -1;
		try {
			param = Double.parseDouble(readIt(request, name).replaceAll("[  ]", "").replaceAll("%C2%A0", ""));
		} catch (NumberFormatException e) {
			return null;
		}
		return param;
	}

	/**
	 * Reads and escape HTML4 caracters.
	 * 
	 * @param request
	 * @param name
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String readAndEscape(HttpServletRequest request, String name) {
		return StringEscapeUtils.escapeHtml4(readIt(request, name));
	}
}
