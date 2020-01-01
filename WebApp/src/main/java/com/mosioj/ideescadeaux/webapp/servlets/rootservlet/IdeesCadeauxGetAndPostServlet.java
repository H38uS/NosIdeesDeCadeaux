package com.mosioj.ideescadeaux.webapp.servlets.rootservlet;

import com.mosioj.ideescadeaux.webapp.servlets.IdeesCadeauxServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class IdeesCadeauxGetAndPostServlet<P extends SecurityPolicy> extends IdeesCadeauxServlet<P> {

    private static final long serialVersionUID = -1513319177739695079L;
	private static final Logger logger = LogManager.getLogger(IdeesCadeauxGetAndPostServlet.class);

    public IdeesCadeauxGetAndPostServlet(P policy) {
        super(policy);
    }

    /**
     * @param response The http response.
     * @param ans      This specific service answer, as a JSon string.
     */
    protected void buildResponse(HttpServletResponse response, ServiceResponse ans) {
        try {
            response.getOutputStream().print(ans.asJSon(response));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }
}
