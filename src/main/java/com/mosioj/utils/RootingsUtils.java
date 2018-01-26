package com.mosioj.utils;

import java.io.IOException;
import java.util.Properties;

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
	 * Get the dispatcher and forward the request. Should be used with *.JSP files only !!
	 * 
	 * @param url
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void rootToPage(String url, HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		RequestDispatcher rd = req.getRequestDispatcher(url);
		try {
			rd.forward(req, resp);
		} catch (IOException e) {
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * Get the dispatcher and forward the request. Should be used to get rid of post feature. <b>*** Warning : the
	 * landing page should NOT require csrf tokens. ***</b></br>
	 * Use it only when redirecting from a wrong URL.
	 * 
	 * @param url
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void redirectToPage(String url, HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		try {
			resp.sendRedirect(req.getContextPath() + url);
		} catch (IOException e) {
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * The application properties.
	 */
	private static Properties p;
	
	/**
	 * 
	 * @return True if and only if the server should display technical stacks.
	 * @throws IOException
	 */
	private static boolean shouldLogStack() throws IOException {
		if (p == null) {
			Properties p = new Properties();
			p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("mail.properties"));
		}
		return "true".equals(p.get("shouldLogStack"));
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
	public static void rootToGenericSQLError(Exception exception, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.error("An error occured: " + exception.getMessage());
		exception.printStackTrace();
		
		boolean shouldLogStack = shouldLogStack();
		if (shouldLogStack) {
			req.setAttribute("error", exception.getMessage());
		}
		req.setAttribute("shouldLogStack", shouldLogStack);
		
		RequestDispatcher rd = req.getRequestDispatcher(PUBLIC_SERVER_ERROR_JSP);
		try {
			rd.forward(req, resp);
		} catch (IOException e) {
			throw new ServletException(e.getMessage());
		}
	}
}
