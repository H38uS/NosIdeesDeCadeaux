package com.mosioj.ideescadeaux.servlets.service.response;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.utils.GsonFactory;

public class ServiceResponse {

    private static final Logger logger = LogManager.getLogger(ServiceResponse.class);

    @Expose
    private final String status;

    @Expose
    private final Object message;

    @Expose
    private final boolean isAdmin;

    @Expose
    private final boolean isLoggedIn;

    /**
     * Class constructor.
     *
     * @param isOK       True if there is no error.
     * @param message    The JSon response message.
     * @param isLoggedIn Whether the user is logged in.
     * @param isAdmin    Whether the user is an admin.
     */
    public ServiceResponse(boolean isOK, Object message, boolean isLoggedIn, boolean isAdmin) {
        status = isOK ? "OK" : "KO";
        this.message = message;
        this.isLoggedIn = isLoggedIn;
        this.isAdmin = isAdmin;
    }

    /**
     * Factory method for working protected answers with empty message.
     *
     * @param isAdmin    Whether the user is an admin.
     * @return The response built from parameters.
     */
    public static ServiceResponse ok(boolean isAdmin) {
        return new ServiceResponse(true, "", true, isAdmin);
    }

    /**
     * Factory method for working protected answers.
     *
     * @param message    The JSon response message.
     * @param isAdmin    Whether the user is an admin.
     * @return The response built from parameters.
     */
    public static ServiceResponse ok(Object message, boolean isAdmin) {
        return new ServiceResponse(true, message, true, isAdmin);
    }

    /**
     * Factory method for working answers.
     *
     * @param message    The JSon response message.
     * @param isLoggedIn Whether the user is logged in.
     * @param isAdmin    Whether the user is an admin.
     * @return The response built from parameters.
     */
    public static ServiceResponse ok(Object message, boolean isLoggedIn, boolean isAdmin) {
        return new ServiceResponse(true, message, isLoggedIn, isAdmin);
    }

    /**
     * Factory method for non working answers.
     *
     * @param message    The JSon response message.
     * @param isLoggedIn Whether the user is logged in.
     * @param isAdmin    Whether the user is an admin.
     * @return The response built from parameters.
     */
    public static ServiceResponse ko(Object message, boolean isLoggedIn, boolean isAdmin) {
        return new ServiceResponse(false, message, isLoggedIn, isAdmin);
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the message
     */
    public Object getMessage() {
        return message;
    }

    /**
     * @param response The HTTP response.
     * @return The JSon representation of this response.
     */
    public String asJSon(HttpServletResponse response) {
        String content = GsonFactory.getIt().toJson(this);
        try {
            content = new String(content.getBytes(StandardCharsets.UTF_8), response.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return content;
    }

    @Override
    public String toString() {
        return "ServiceResponse [status=" + status + ", message=" + message + "]";
    }
}
