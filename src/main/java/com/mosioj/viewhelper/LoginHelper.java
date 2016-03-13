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
		
		String name = ((HttpServletRequest ) request).getRemoteUser();
		if (name != null) {
			name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
			request.setAttribute("username", name);
			
			// Storing the Id if not stored yet
			HttpSession session = ((HttpServletRequest ) request).getSession();
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
