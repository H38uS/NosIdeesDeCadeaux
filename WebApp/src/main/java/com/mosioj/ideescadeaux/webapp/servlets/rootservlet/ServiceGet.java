package com.mosioj.ideescadeaux.webapp.servlets.rootservlet;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

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
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        throw new SQLException("POST method is not supported by this service.");
    }
}
