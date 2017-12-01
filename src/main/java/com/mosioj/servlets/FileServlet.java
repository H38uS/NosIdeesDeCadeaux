package com.mosioj.servlets;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/protected/files/*")
public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = -4352920790249582095L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String filename = URLDecoder.decode(request.getPathInfo().substring(1), "UTF-8");
		File file = new File(getServletContext().getInitParameter("work_dir"), filename);
		response.setHeader("Content-Type", getServletContext().getMimeType(filename));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
		if (file.exists()) {
			Files.copy(file.toPath(), response.getOutputStream());
		}
	}

}