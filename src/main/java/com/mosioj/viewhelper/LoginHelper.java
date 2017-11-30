package com.mosioj.viewhelper;

import java.io.File;
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

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.mosioj.model.User;
import com.mosioj.model.table.Notifications;
import com.mosioj.model.table.UserParameters;
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
	private static final String DEFAULT_CSRF_TOKEN_ATTR_NAME = HttpSessionCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN");

	/**
	 * 
	 * @param userName The user name not formatted.
	 * @return The formatted username.
	 */
	public static String formatUserName(String userName) {
		return userName.substring(0, 1).toUpperCase() + userName.substring(1).toLowerCase();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		logger.trace("Do Filtering in helper...");

		HttpSession session = ((HttpServletRequest) request).getSession();
		String name = ((HttpServletRequest) request).getRemoteUser();
		logger.trace("Name: " + name);
		if (name != null) {

			name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
			request.setAttribute("username", name);
			session.setAttribute("username", name);

			// Storing the Id if not stored yet
			Integer userId = (Integer) session.getAttribute("userid");
			if (userId == null) {
				try {
					// Getting the id
					Users user = new Users();
					userId = user.getId(name);
					User connected = user.getUser(userId);
					// Storing the new one
					session.setAttribute("userid", userId);
					session.setAttribute("emailorname", connected.getName());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			Object emailorname = session.getAttribute("emailorname");
			if (emailorname == null) {
				try {
					Users user = new Users();
					User connected = user.getUser(userId);
					session.setAttribute("emailorname", connected.getName());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			String workDir = session.getServletContext().getInitParameter("work_dir");
			request.setAttribute("work_dir", workDir);
			request.setAttribute("userid", userId);
			request.setAttribute("emailorname", emailorname);

			File work = new File(workDir);
			if (!work.exists()) {
				work.mkdirs();
			}
			File avatars = new File(work, "uploaded_pictures/avatars");
			if (!avatars.exists()) {
				// Création des sous dossiers
				avatars.mkdirs();
				FileUtils.copyDirectory(new File(session.getServletContext().getRealPath("/public/uploaded_pictures/avatars")),
										avatars);
			}
			request.setAttribute("avatars", "protected/files/uploaded_pictures/avatars");

			Notifications notif = new Notifications();
			try {
				int count = notif.getUserNotificationCount(userId);
				request.setAttribute("notif_count", count);
			} catch (SQLException e) {
				// Osef
			}
		}

		if (session != null && session.getAttribute("username") != null) {
			CsrfToken token = (CsrfToken) session.getAttribute(this.sessionAttributeName);
			if (token == null) {
				logger.trace("Bug with Spring... Getting manually the CSRF token if found.");
				logger.trace("BackUpCsrfToken => " + session.getAttribute("BackUpCsrfToken"));
				UserParameters up = new UserParameters();
				try {
					int userId = (int) session.getAttribute("userid");
					final String tokenValue = up.getParameter(userId, "CSRF");
					session.setAttribute(sessionAttributeName, new CsrfToken() {
						private static final long serialVersionUID = 2161348459850900068L;

						@Override
						public String getToken() {
							return tokenValue;
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
				} catch (SQLException e) {
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
