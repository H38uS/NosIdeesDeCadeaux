package com.mosioj.ideescadeaux.webapp.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Optional;

public class ParametersUtils {

    /** Size of icon in mobile views */
    public static final int MOBILE_PICTURE_WIDTH = 42;

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(ParametersUtils.class);

    /** Application work directory where to store pictures and so on */
    private static final String WORK_DIR;

    /** Picture directory inside the working directory */
    private static final File IDEAS_PICTURE_PATH;

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
     * @param request       The http request.
     * @param parameterName The name of the parameter to read.
     * @return The String to pass to the database
     */
    public static String readNameOrEmail(HttpServletRequest request, String parameterName) {

        String nameOrEmail = readAndEscape(request, parameterName);
        logger.trace(MessageFormat.format("Receive:{0}", nameOrEmail));

        if (nameOrEmail == null || nameOrEmail.trim().isEmpty()) {
            return nameOrEmail;
        }

        int open = nameOrEmail.lastIndexOf("(");
        int close = nameOrEmail.lastIndexOf(")");
        if (open > 0 && close > 0 && open < close) {
            // Comes from some completion trick
            nameOrEmail = nameOrEmail.substring(open + 1, close);
        }

        logger.trace(MessageFormat.format("Returned:{0}", nameOrEmail.trim()));
        return nameOrEmail.trim();
    }

    /**
     * @return The idea picture path.
     */
    public static File getIdeaPicturePath() {
        return IDEAS_PICTURE_PATH;
    }

    /**
     * @return The work directory.
     */
    public static String getWorkDir() {
        return WORK_DIR;
    }

    static {
        String tmp = ApplicationProperties.getProp().getProperty("work_dir");
        if (StringUtils.isBlank(tmp)) {
            tmp = "/temp";
        }
        WORK_DIR = tmp;
        logger.info("Work directory initialized to {}", WORK_DIR);

        IDEAS_PICTURE_PATH = new File(WORK_DIR, "uploaded_pictures/ideas");
        logger.info("Idea picture path directory initialized to {}", IDEAS_PICTURE_PATH);
    }

}
