package com.mosioj.utils;

import javax.servlet.http.HttpServletRequest;

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
}
