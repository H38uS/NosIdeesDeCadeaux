package com.mosioj.viewhelper;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

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

import com.mosioj.model.User;
import com.mosioj.model.table.Users;
import com.mosioj.utils.database.NoRowsException;

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

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpSession session = httpServletRequest.getSession();
		String name = httpServletRequest.getRemoteUser();
		logger.trace(MessageFormat.format("Name: {0} requesting URL: {1}", name, httpServletRequest.getRequestURL().toString()));
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
					try {
						userId = user.getId(name);
						User connected = user.getUser(userId);
						// Storing the new one
						session.setAttribute("userid", userId);
						session.setAttribute("emailorname", connected.getName());
					} catch (NoRowsException e) {
						// Impossible: le nom existe forcément
						// Sait-on jamais, on le log
						logger.fatal(MessageFormat.format("L''utilisateur {0} est connecté mais n''a pas de compte...", name));
					}
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
					emailorname = connected.getName();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			// FIXME : 0 faut vraiment stocker le user...
			Object avatar = session.getAttribute("connected_user_avatar");
			if (avatar == null) {
				try {
					Users user = new Users();
					User connected = user.getUser(userId);
					session.setAttribute("connected_user_avatar", connected.getAvatarSrcSmall());
					avatar = connected.getAvatarSrcSmall();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			String workDir = session.getServletContext().getInitParameter("work_dir");
			request.setAttribute("work_dir", workDir);
			request.setAttribute("userid", userId);
			request.setAttribute("emailorname", emailorname);
			request.setAttribute("connected_user_avatar", avatar);

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
			request.setAttribute("ideas_pictures", "protected/files/uploaded_pictures/ideas");

			// Child connection
			Object initial = session.getAttribute("initial_user_id");
			if (initial != null) {
				try {
					int initial_id = Integer.parseInt(initial.toString());
					request.setAttribute("initial_user_id", initial_id);
					Users users = new Users();
					request.setAttribute("initial_user_name", users.getUser(initial_id).getName());
				} catch (Exception e) {
				}
			}
			
			request.setAttribute("is_admin", httpServletRequest.isUserInRole("ROLE_ADMIN"));
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
