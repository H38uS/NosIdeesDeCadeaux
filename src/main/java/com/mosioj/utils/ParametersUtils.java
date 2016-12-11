package com.mosioj.utils;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ParametersUtils {

	/**
	 * 
	 * @param request The processing request.
	 * @param name The parameter name.
	 * @return The parameter value, or empty string if not provided.
	 * @throws UnsupportedEncodingException 
	 */
	public static String readIt(HttpServletRequest request, String name) throws UnsupportedEncodingException {
		String res = request.getParameter(name);
		return res == null ? "" : new String(res.getBytes("ISO-8859-1"), "UTF-8");
	}

	/**
	 * 
	 * @param request
	 * @return The current user name, or null if no user is logged in.
	 */
	public static String getUserName(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (String) session.getAttribute("username");
	}

	/**
	 * 
	 * @param request
	 * @return The current user id, or null if no user is logged in.
	 */
	public static int getUserId(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (Integer) session.getAttribute("userid");
	}
}
