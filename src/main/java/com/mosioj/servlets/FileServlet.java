package com.mosioj.servlets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet("/protected/files/*")
public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = -4352920790249582095L;
	private static final Logger logger = LogManager.getLogger(FileServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String filename = new String(request.getPathInfo().substring(1).getBytes("ISO-8859-1"), "UTF-8");
		logger.trace(MessageFormat.format("Requesting file: {0}, {1}", filename, request.getCharacterEncoding()));
		File file = new File(getServletContext().getInitParameter("work_dir"), filename);
		response.setHeader("Content-Type", getServletContext().getMimeType(filename));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
		if (file.exists()) {
			Files.copy(file.toPath(), response.getOutputStream());
		} else {
			logger.error(MessageFormat.format("File {0} does not exist.", file));
		}
	}

}