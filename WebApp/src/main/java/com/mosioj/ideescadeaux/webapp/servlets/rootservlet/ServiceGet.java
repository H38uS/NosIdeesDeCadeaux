package com.mosioj.ideescadeaux.webapp.servlets.rootservlet;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ServiceGet<P extends SecurityPolicy> extends ServiceGetAndPost<P> {

    /**
     * Class constructor.
     *
     * @param policy The security policy defining whether we can interact with the parameters, etc.
     */
    public ServiceGet(P policy) {
        super(policy);
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {
        final String message = "POST method is not supported by this service.";
        buildResponse(response, ServiceResponse.ko(message, isAdmin(request), thisOne));
    }
}
