package com.mosioj.ideescadeaux.webapp.utils;

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

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;

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
     * @param url      The url.
     * @param request  The http request.
     * @param response The http response.
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
     * @param url      The url.
     * @param request  The http request.
     * @param response The http response.
     */
    public static void redirectToPage(String url, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            response.sendRedirect(request.getContextPath() + url.replaceAll("[  ]", "%20"));
        } catch (IOException e) {
            throw new ServletException(e.getMessage());
        }
    }

    /**
     * The application properties.
     */
    private static Properties p;

    /**
     * @return True if and only if the server should display technical stacks.
     */
    public static boolean shouldLogStack() {
        if (p == null) {
            p = new Properties();
            try {
                p.load(RootingsUtils.class.getResourceAsStream("/application.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.trace(MessageFormat.format("shouldLogStack: {0}", p.get("shouldLogStack")));
        return "true".equals(p.get("shouldLogStack"));
    }

    /**
     * Set the error text, and root the request to the generic error page.
     *
     * @param thisOne   The current connected user.
     * @param exception The exception
     * @param request   The http request.
     * @param response  The http response.
     */
    public static void rootToGenericSQLError(User thisOne,
                                             Exception exception,
                                             HttpServletRequest request, HttpServletResponse response) throws ServletException {

        logger.error(MessageFormat.format("An error occured: {0}. StackTrace: {1}",
                                          exception.getMessage(),
                                          Arrays.toString(exception.getStackTrace())));

        boolean shouldLogStack = shouldLogStack();
        if (shouldLogStack) {
            exception.printStackTrace();
            request.setAttribute("error", exception.getMessage());
        } else {
            NotificationsRepository.logError(thisOne, exception);
        }
        request.setAttribute("shouldLogStack", shouldLogStack);

        String url = request.getRequestURI()
                            .contains("/protected/") ? PROTECTED_SERVER_ERROR_JSP : PUBLIC_SERVER_ERROR_JSP;
        RequestDispatcher rd = request.getRequestDispatcher(url);

        try {
            rd.forward(request, response);
        } catch (IOException e) {
            throw new ServletException(e.getMessage());
        }
    }
}
