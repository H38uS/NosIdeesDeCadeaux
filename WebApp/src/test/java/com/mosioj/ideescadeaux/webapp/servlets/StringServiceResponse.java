package com.mosioj.ideescadeaux.webapp.servlets;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;

public class StringServiceResponse extends ServiceResponse<String> {
    /**
     * Class constructor.
     *
     * @param isOK    True if there is no error.
     * @param message The JSon response message.
     */
    public StringServiceResponse(boolean isOK, String message, User connectedUser) {
        super(isOK, message, connectedUser);
    }
}
