package com.mosioj.ideescadeaux.viewhelper;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

public class JSonResponseWriter {

	private static final Logger logger = LogManager.getLogger(JSonResponseWriter.class);

	private void buildResponse(HttpServletResponse response, String content) throws IOException {

		if (response.getCharacterEncoding() != null) {
			content = new String(content.getBytes("UTF-8"), response.getCharacterEncoding());
		}

		response.getOutputStream().println(content);
	}

	public void writeJSonArrayOutput(HttpServletResponse response, String... jsonObjects) throws IOException {

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

	public void writeJSonOutput(HttpServletResponse response, JSonPair... values) {
		try {
			if (values.length > 0) {
				StringBuilder json = new StringBuilder();
				json.append("{");
				for (JSonPair value : values) {
					json.append(JSONObject.toString(value.left, value.right));
					json.append(",");
				}
				json.deleteCharAt(json.length() - 1);
				json.append("}");
				buildResponse(response, json.toString());
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public JSonPair makeJSonPair(String key, String value) {
		return new JSonPair(key, value);
	}

	public class JSonPair extends MutablePair<String, String> {
		private static final long serialVersionUID = -8507170359783882799L;

		public JSonPair(String key, String value) {
			super(key, value);
		}
	}
}
