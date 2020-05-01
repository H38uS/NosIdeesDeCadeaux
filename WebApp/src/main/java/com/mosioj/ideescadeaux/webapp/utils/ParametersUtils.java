package com.mosioj.ideescadeaux.webapp.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Optional;

public class ParametersUtils {

    private static final Logger logger = LogManager.getLogger(ParametersUtils.class);

    private static String workDir;

    /**
     * Attention: ne surtout pas utiliser dans les redirect post -> get.
     *
     * @param request The processing request.
     * @param name    The parameter name.
     * @return The parameter value, or empty string if not provided.
     */
    public static String readItFromSession(HttpServletRequest request, String name) {
        Object res = request.getSession().getAttribute(name);
        logger.trace(MessageFormat.format("{0} is:{1}", name, res));
        return res == null ? "" : new String(res.toString().getBytes(StandardCharsets.ISO_8859_1),
                                             StandardCharsets.UTF_8);
    }

    /**
     * Attention: ne surtout pas utiliser dans les redirect post -> get.
     *
     * @param request The processing request.
     * @param name    The parameter name.
     * @return The parameter value, or empty string if not provided.
     */
    public static String readIt(HttpServletRequest request, String name) {
        String res = request.getParameter(name);
        logger.trace(MessageFormat.format("{0} is:{1}", name, res));
        return res == null ? "" : new String(res.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    /**
     * @param request The http request.
     * @param name    The parameter name.
     * @return The parameter, as an integer. If it is not possible, returns null.
     */
    public static Optional<Integer> readInt(HttpServletRequest request, String name) {
        try {
            return Optional.of(Integer.parseInt(ParametersUtils.readIt(request, name)
                                                               .replaceAll("[  ]", "")
                                                               .replaceAll("%C2%A0", "")));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> readDouble(HttpServletRequest request, String name) {
        double param;
        try {
            param = Double.parseDouble(readIt(request, name).replaceAll("[  ]", "").replaceAll("%C2%A0", ""));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        return Optional.of(param);
    }

    /**
     * @param request The http request.
     * @param name    The parameter name.
     * @return The parameter, as an integer. If it is not possible, returns null.
     */
    public static Optional<Integer> readIntFromSession(HttpServletRequest request, String name) {
        try {
            return Optional.of(Integer.parseInt(ParametersUtils.readItFromSession(request, name)
                                                               .replaceAll("[  ]", "")
                                                               .replaceAll("%C2%A0", "")));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Reads and escape HTML4 caracters.
     *
     * @param request The http request.
     * @param name    The name of the parameter.
     * @return The escaped string. Cannot be null.
     */
    public static String readAndEscape(HttpServletRequest request, String name) {
        return StringEscapeUtils.escapeHtml4(readIt(request, name));
    }

    /**
     *
     * @param context The servlet context used to initialize the value.
     * @return The work directory.
     */
    public static synchronized String getWorkDir(ServletContext context) {
        if (workDir == null) {
            workDir = context.getInitParameter("work_dir");
            if ("${work_dir}".equalsIgnoreCase(workDir)) {
                workDir = "C:\\temp";
            }
            logger.info("Work directory initialized to {}", workDir);
        }
        return workDir;
    }
}
