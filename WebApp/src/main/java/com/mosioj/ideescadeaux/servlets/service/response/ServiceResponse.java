package com.mosioj.ideescadeaux.servlets.service.response;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.utils.GsonFactory;

public class ServiceResponse<T> {

    private static final Logger logger = LogManager.getLogger(ServiceResponse.class);

    @Expose
    private final String status;

    @Expose
    private final T message;

    @Expose
    private final boolean isAdmin;

    /**
     * Class constructor.
     * @param isOK       True if there is no error.
     * @param message    The JSon response message.
     * @param isAdmin    Whether the user is an admin.
     */
    public ServiceResponse(boolean isOK, T message, boolean isAdmin) {
        status = isOK ? "OK" : "KO";
        this.message = message;
        this.isAdmin = isAdmin;
    }

    /**
     * Factory method for working protected answers with empty message.
     *
     * @param isAdmin Whether the user is an admin.
     * @return The response built from parameters.
     */
    public static ServiceResponse<String> ok(boolean isAdmin) {
        return new ServiceResponse<>(true, "", isAdmin);
    }

    /**
     * Factory method for working protected answers.
     *
     * @param message The JSon response message.
     * @param isAdmin Whether the user is an admin.
     * @return The response built from parameters.
     */
    public static <T> ServiceResponse<T> ok(T message, boolean isAdmin) {
        return new ServiceResponse<>(true, message, isAdmin);
    }

    /**
     * Factory method for non working answers in protected sessions.
     *
     * @param message    The JSon response message.
     * @param isAdmin    Whether the user is an admin.
     * @return The response built from parameters.
     */
    public static <T> ServiceResponse<T> ko(T message, boolean isAdmin) {
        return new ServiceResponse<>(false, message, isAdmin);
    }

    /**
     * @return the status
     */
    public boolean isOK() {
        return "OK".equals(status);
    }

    /**
     * @return the message
     */
    public T getMessage() {
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
        return "ServiceResponse{" +
               "status='" + status + '\'' +
               ", message=" + message +
               ", isAdmin=" + isAdmin +
               '}';
    }
}
