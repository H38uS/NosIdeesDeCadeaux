package com.mosioj.ideescadeaux.servlets.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;

public abstract class AbstractServiceGet<P extends SecurityPolicy> extends IdeesCadeauxGetServlet<P> {

	private static final long serialVersionUID = 3014602524272535511L;
	private static final Logger logger = LogManager.getLogger(AbstractServiceGet.class);

	public AbstractServiceGet(P policy) {
		super(policy);
	}

	/**
	 * 
	 * @param request The http request.
	 * @param response The http response.
	 * @param ans This specific service answer, as a JSon string.
	 */
	protected void buildResponse(HttpServletRequest request, HttpServletResponse response, ServiceResponse ans) {
		try {
			response.getOutputStream().print(ans.asJSon(response));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
}
