package com.mosioj.ideescadeaux.webapp.servlets.rootservlet;

import com.mosioj.ideescadeaux.webapp.servlets.IdeesCadeauxServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public abstract class ServiceGetAndPost<P extends SecurityPolicy> extends IdeesCadeauxServlet<P> {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(ServiceGetAndPost.class);

    public ServiceGetAndPost(P policy) {
        super(policy);
    }

    /**
     * @param response The http response.
     * @param ans      This specific service answer, as a JSon string.
     */
    protected void buildResponse(HttpServletResponse response, ServiceResponse<?> ans) {
        try {
            response.getOutputStream().print(ans.asJSon(response));
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    protected void dealWithUnauthorizedPolicyAccess(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    P policy) {
        buildResponse(response, ServiceResponse.ko(policy.getLastReason(), isAdmin(request), thisOne));
    }

    @Override
    public final void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) {
        try {
            serviceGet(request, response);
        } catch (Exception e) {
            logger.error(e);
            buildResponse(response,
                          ServiceResponse.ko("Une erreur est survenue... " + e.getMessage(),
                                             isAdmin(request),
                                             thisOne));
        }
    }

    @Override
    public final void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) {
        try {
            servicePost(request, response);
        } catch (Exception e) {
            logger.error(e);
            buildResponse(response,
                          ServiceResponse.ko("Une erreur est survenue... " + e.getMessage(),
                                             isAdmin(request),
                                             thisOne));
        }
    }

    /**
     * Internal app service get, may throw exceptions
     *
     * @param request  The http request.
     * @param response The http response.
     */
    public abstract void serviceGet(HttpServletRequest request, HttpServletResponse response) throws SQLException;

    /**
     * @param request  The http request.
     * @param response The http response.
     */
    public abstract void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException;
}
