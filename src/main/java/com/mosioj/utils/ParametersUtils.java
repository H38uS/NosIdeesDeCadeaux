package com.mosioj.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ParametersUtils {

	/**
	 * 
	 * @param request The processing request.
	 * @param name The parameter name.
	 * @return The parameter value, or null if not provided.
	 */
	public static String readIt(HttpServletRequest request, String name) {
		String res = request.getParameter(name);
		return res == null ? "" : res;
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
