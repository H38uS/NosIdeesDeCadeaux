package com.mosioj.viewhelper;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import com.mosioj.model.table.Notifications;
import com.mosioj.model.table.Users;

/**
 * Provides helper functions to the views. 
 * 
 * @author Jordan Mosio
 *
 */
@WebFilter("/protected/*")
public class LoginHelper implements Filter {

	/**
	 * Class logger.
	 */
	private static final Logger logger = LogManager.getLogger(LoginHelper.class);
	private String sessionAttributeName = DEFAULT_CSRF_TOKEN_ATTR_NAME;
	private static final String DEFAULT_CSRF_TOKEN_ATTR_NAME = HttpSessionCsrfTokenRepository.class
			.getName().concat(".CSRF_TOKEN");


	/**
	 * 
	 * @param userName The user name not formatted.
	 * @return The formatted username.
	 */
	public static String formatUserName(String userName) {
		return userName.substring(0, 1).toUpperCase() + userName.substring(1).toLowerCase();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		MultipartResolver multipartResolver = new StandardServletMultipartResolver();
		HttpServletRequest processedRequest = (HttpServletRequest) request;
		if (multipartResolver.isMultipart(processedRequest)) {
			HttpSession session = processedRequest.getSession(false);
			CsrfToken token = (CsrfToken) session.getAttribute(this.sessionAttributeName);
			if (token == null) {
				final String val = request.getParameter("_csrf");
				logger.trace("Bug with Spring... Getting manually the CSRF token if found.");
				if (val != null && !val.isEmpty()) {
					session.setAttribute(sessionAttributeName, new CsrfToken() {
						private static final long serialVersionUID = 3849500966021482363L;
						@Override
						public String getToken() {
							return val;
						}
						@Override
						public String getParameterName() {
							return "_csrf";
						}
						@Override
						public String getHeaderName() {
							return "X-CSRF-TOKEN";
						}
					});
				}
			}
		}
		
		logger.trace("Do Filtering in helper...");
		String name = ((HttpServletRequest ) request).getRemoteUser();
		logger.trace("Name: " + name);
		if (name != null) {
			name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
			request.setAttribute("username", name);
			
			HttpSession session = ((HttpServletRequest ) request).getSession();
			session.setAttribute("username", name);

			// Storing the Id if not stored yet
			Integer userId = (Integer) session.getAttribute("userid");
			if (userId == null) {
				try {
					// Getting the id
					Users user = new Users();
					userId = user.getId(name);
					// Storing the new one
					session.setAttribute("userid", userId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			request.setAttribute("userid", userId);
			
			Notifications notif = new Notifications();
			try {
				int count = notif.getUserNotificationCount(userId);
				request.setAttribute("notif_count", count);
			} catch (SQLException e) {
				// Osef
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
