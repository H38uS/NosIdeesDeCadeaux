package com.mosioj.utils;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RootingsUtils {

	/**
	 * The common error page.
	 */
	public static final String PUBLIC_SERVER_ERROR_JSP = "/public/server_error.jsp";

	/**
	 * Class logger.
	 */
	private static final Logger logger = LogManager.getLogger(RootingsUtils.class);

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
		logger.error("An error occured: " + exception.getMessage());
		exception.printStackTrace();
		req.setAttribute("error", exception.getMessage());
		RequestDispatcher rd = req.getRequestDispatcher(PUBLIC_SERVER_ERROR_JSP);
		rd.forward(req, resp);
	}
}
