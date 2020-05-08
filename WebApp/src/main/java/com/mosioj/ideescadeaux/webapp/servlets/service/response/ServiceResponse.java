package com.mosioj.ideescadeaux.webapp.servlets.service.response;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.utils.GsonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class ServiceResponse<T> {

    private static final Logger logger = LogManager.getLogger(ServiceResponse.class);

    @Expose
    private final String status;

    @Expose
    private final T message;

    @Expose
    private final boolean isAdmin;

    @Expose
    private final User connectedUser;

    /**
     * Class constructor.
     *
     * @param isOK          True if there is no error.
     * @param message       The JSon response message.
     * @param isAdmin       Whether the user is an admin.
     * @param connectedUser The connected user or null if none.
     */
    public ServiceResponse(boolean isOK, T message, boolean isAdmin, User connectedUser) {
        status = isOK ? "OK" : "KO";
        this.message = message;
        this.isAdmin = isAdmin;
        this.connectedUser = connectedUser;
    }

    /**
     * Factory method for working protected answers with empty message.
     *
     * @param isAdmin       Whether the user is an admin.
     * @param connectedUser The connected user or null if none.
     * @return The response built from parameters.
     */
    public static ServiceResponse<String> ok(boolean isAdmin, User connectedUser) {
        return new ServiceResponse<>(true, "", isAdmin, connectedUser);
    }

    /**
     * Factory method for working protected answers.
     *
     * @param message       The JSon response message.
     * @param isAdmin       Whether the user is an admin.
     * @param connectedUser The connected user or null if none.
     * @return The response built from parameters.
     */
    public static <T> ServiceResponse<T> ok(T message, boolean isAdmin, User connectedUser) {
        return new ServiceResponse<>(true, message, isAdmin, connectedUser);
    }

    /**
     * Factory method for non working answers in protected sessions.
     *
     * @param message       The JSon response message.
     * @param isAdmin       Whether the user is an admin.
     * @param connectedUser The connected user or null if none.
     * @return The response built from parameters.
     */
    public static <T> ServiceResponse<T> ko(T message, boolean isAdmin, User connectedUser) {
        return new ServiceResponse<>(false, message, isAdmin, connectedUser);
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
