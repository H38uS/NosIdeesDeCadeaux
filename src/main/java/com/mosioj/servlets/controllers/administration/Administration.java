package com.mosioj.servlets.controllers.administration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.servlets.securitypolicy.generic.AllAccessToGet;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/administration/administration")
public class Administration extends IdeesCadeauxGetServlet<AllAccessToGet> {

	private static final long serialVersionUID = 1944117196491457908L;
	private static final Logger logger = LogManager.getLogger(Administration.class);

	public static final String DISPATCH_URL = "/administration/administration.jsp";

	/**
	 * Class constructor.
	 */
	public Administration() {
		super(new AllAccessToGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		logger.info("Getting administration page from user: " + thisOne);
		List<User> allUsers = model.users.getAllUsers();
		request.setAttribute("users", allUsers);

		File logDir = new File(getServletContext().getInitParameter("work_dir"), "logs");
		try {
			request.setAttribute("log_folder", logDir.getCanonicalPath());
			List<File> logFiles = Arrays.asList(logDir.listFiles())
										.stream()
										.filter(f -> f.getName().endsWith(".log"))
										.sorted((left, right) -> right.compareTo(left))
										.limit(10)
										.collect(Collectors.toList());
			request.setAttribute("log_files", logFiles);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		}

		RootingsUtils.rootToPage(DISPATCH_URL, request, response);
	}
}
