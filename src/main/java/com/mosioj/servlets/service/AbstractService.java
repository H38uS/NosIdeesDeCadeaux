package com.mosioj.servlets.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;

public abstract class AbstractService extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 3014602524272535511L;
	private static final Logger logger = LogManager.getLogger(AbstractService.class);

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
	
	protected void writeJSonOutput(HttpServletResponse response, JSonPair... values) {
		try {
			if (values.length > 0) {
				StringBuilder json = new StringBuilder();
				for (JSonPair value : values) {
					json.append(JSONObject.toString(value.left, value.right));
					json.append(",");
				}
				json.deleteCharAt(json.length() - 1);
				
				writeJSonOutput(response, json.toString());
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void writeJSonOutput(HttpServletResponse response, String jsonObject) throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(jsonObject);
		builder.append("}");
		buildResponse(response, builder.toString());
	}
	
	protected JSonPair makeJSonPair(String key, String value) {
		return new JSonPair(key, value);
	}

	protected class JSonPair extends MutablePair<String, String> {
		private static final long serialVersionUID = -8507170359783882799L;
		
		public JSonPair(String key, String value) {
			super(key, value);
		}
	}
}
