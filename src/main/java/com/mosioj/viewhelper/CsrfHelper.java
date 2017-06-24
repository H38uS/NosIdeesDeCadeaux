package com.mosioj.viewhelper;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.mosioj.model.table.UserParameters;
import com.mosioj.utils.ParametersUtils;

public class CsrfHelper implements Filter {

	private String sessionAttributeName = DEFAULT_CSRF_TOKEN_ATTR_NAME;
	private static final String DEFAULT_CSRF_TOKEN_ATTR_NAME = HttpSessionCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN");

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession();
		if (session != null) {
			CsrfToken token = (CsrfToken) session.getAttribute(this.sessionAttributeName);
			if (token != null) {
				// Persist it
				UserParameters up = new UserParameters();
				try {
					up.insertUpdateParameter(ParametersUtils.getUserId((HttpServletRequest) request), "CSRF", token.getToken());
				} catch (SQLException e) {
					// Nothing to do
				}
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Nothing to do
	}

	@Override
	public void destroy() {
		// Nothing to do
	}

}
