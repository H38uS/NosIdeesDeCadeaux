package com.mosioj.utils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.table.Notifications;

public class RootingsUtils {

	/**
	 * The common error page.
	 */
	public static final String PUBLIC_SERVER_ERROR_JSP = "/public/server_error.jsp";
	public static final String PROTECTED_SERVER_ERROR_JSP = "/protected/server_error.jsp";

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
	public static void rootToPage(String url, HttpServletRequest request, HttpServletResponse response) throws ServletException {
		RequestDispatcher rd = request.getRequestDispatcher(url);
		try {
			rd.forward(request, response);
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
	public static void redirectToPage(String url, HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			response.sendRedirect(request.getContextPath() + url.replaceAll(" ", "%20"));
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
			p = new Properties();
			p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));
		}
		logger.trace(MessageFormat.format("shouldLogStack: {0}", p.get("shouldLogStack")));
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
	public static void rootToGenericSQLError(	Exception exception,
												HttpServletRequest req,
												HttpServletResponse resp) throws ServletException, IOException {

		logger.error(MessageFormat.format(	"An error occured: {0}. StackTrace: {1}",
											exception.getMessage(),
											Arrays.toString(exception.getStackTrace())));

		boolean shouldLogStack = shouldLogStack();
		if (shouldLogStack) {
			exception.printStackTrace();
			req.setAttribute("error", exception.getMessage());
		} else {
			Notifications notif = new Notifications();
			notif.logError(exception, req);
		}
		req.setAttribute("shouldLogStack", shouldLogStack);

		String url = req.getRequestURI().contains("/protected/") ? PROTECTED_SERVER_ERROR_JSP : PUBLIC_SERVER_ERROR_JSP;
		RequestDispatcher rd = req.getRequestDispatcher(url);

		try {
			rd.forward(req, resp);
		} catch (IOException e) {
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * Used when no idea is defined for a given group.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException 
	 */
	public static void rootToUnexistingGroupError(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		RequestDispatcher rd = request.getRequestDispatcher("/protected/erreur_parametre_ou_droit.jsp");
		request.setAttribute("error_message", "L'idée ne semble plus exister pour ce groupe.");

		try {
			rd.forward(request, response);
		} catch (IOException e) {
			throw new ServletException(e.getMessage());
		}
	}
}
