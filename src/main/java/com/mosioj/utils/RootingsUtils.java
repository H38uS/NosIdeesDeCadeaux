package com.mosioj.utils;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RootingsUtils {

	/**
	 * Get the dispatcher and forward the request.
	 * 
	 * @param url
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void rootToPage(String url, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher(url);
		rd.forward(req, resp);
	}
	
	/**
	 * Set the error text, and root the request to the generic error page.
	 * 
	 * @param exception
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void rootToGenericSQLError(Exception exception, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("error", exception.getMessage());
		RequestDispatcher rd = req.getRequestDispatcher("/public/server_error.jsp");
		rd.forward(req, resp);
	}
}
