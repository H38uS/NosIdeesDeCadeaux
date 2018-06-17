package com.mosioj.servlets.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;

public abstract class AbstractService extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 3014602524272535511L;

	public AbstractService(SecurityPolicy policy) {
		super(policy);
	}

	private void buildResponse(HttpServletResponse response, String content) throws IOException {

		if (response.getCharacterEncoding() != null) {
			content = new String(content.getBytes("UTF-8"), response.getCharacterEncoding());
		}

		response.getOutputStream().println(content);

	}

	protected void writeJSonArrayOutput(HttpServletResponse response, String... jsonObjects) throws IOException {

		StringBuilder resp = new StringBuilder();
		resp.append("[");
		for (String json : jsonObjects) {
			resp.append("{");
			resp.append(json);
			resp.append("},");
		}
		resp.deleteCharAt(resp.length() - 1);
		resp.append("]");

		buildResponse(response, resp.toString());

	}

	protected void writeJSonOutput(HttpServletResponse response, String jsonObject) throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(jsonObject);
		builder.append("}");
		buildResponse(response, builder.toString());
	}

}
