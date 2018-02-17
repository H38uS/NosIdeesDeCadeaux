package com.mosioj.utils;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

	/**
	 * 
	 * @param request
	 * @param name
	 * @return The parameter, as an integer. If it is not possible, returns null.
	 */
	public static Integer readInt(HttpServletRequest request, String name) {
		int param = -1;
		try {
			param = Integer.parseInt(readIt(request, name));
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

	/**
	 * 
	 * @param request
	 * @return The current user name, or null if no user is logged in.
	 * @throws NotLoggedInException If requested and the user is not logged in.
	 */
	public static String getUserName(HttpServletRequest request) throws NotLoggedInException {
		HttpSession session = request.getSession();
		Object name = session.getAttribute("username");
		if (name == null) {
			throw new NotLoggedInException("Call on getUserName, but username was not found in the session.");
		}
		return (String) name;
	}

	/**
	 * 
	 * @param request
	 * @return The current user id, or null if no user is logged in.
	 * @throws NotLoggedInException If requested and the user is not logged in.
	 */
	public static int getUserId(HttpServletRequest request) throws NotLoggedInException {
		HttpSession session = request.getSession();
		Object id = session.getAttribute("userid");
		if (id == null) {
			throw new NotLoggedInException("Call on getUserId, but userid was not found in the session.");
		}
		return (Integer) id;
	}
}
