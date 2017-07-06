package vn.newai.rest;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.core.Response;

import org.json.JSONObject;

public class ResponseBuilder {
	public static Response buildErrorResponse(String title, int code, String content, Exception exception) {
		JSONObject errorJSON = new JSONObject();
		errorJSON.put("title", title);
		errorJSON.put("code", code);
		errorJSON.put("content", content);

		if (null != exception) {
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			errorJSON.put("error_detail", sw.toString());
		} else
			errorJSON.put("error_detail", "");

		return Response.status(code).entity(errorJSON.toString()).build();
	}

	public static Response buildSuccessResponse(String title, int code, String inputString, String correction) {
		JSONObject errorJSON = new JSONObject();
		errorJSON.put("title", title);
		errorJSON.put("code", code);
		errorJSON.put("input_string", inputString);
		errorJSON.put("correction", correction);
		return Response.status(code).entity(errorJSON.toString()).build();
	}
}
