package vn.newai.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import vn.newai.elastic.ElasticTest;

@Path("/checker")
public class SpellingCheckService {
	/** Data template to send to Elastic server */
	private static final String DATA_TEMPLATE_PATH = SpellingCheckService.class.getResource("../../../../../").getPath() + "conf/data_template.txt";
	/** Elastic search server URL */
	private static final String ELASTIC_SERVER_URL_PATH = SpellingCheckService.class.getResource("../../../../../").getPath() + "conf/elasticsearch_url.txt";

	/** Authentication string */
	private static final String AUTH_STRING_PATH = SpellingCheckService.class.getResource("../../../../../").getPath() + "conf/auth_string.txt";

	@SuppressWarnings("unused")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response spellingChecker(@HeaderParam("authorization") String authString, InputStream inputStream) {
		/*-Check authentication*/
		if (!isAuthenticated(authString))
			return ResponseBuilder.buildErrorResponse("Unauthorized", 401, "Authentication failed", null);

		/*-user authenticated*/
		StringBuilder stringBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = in.readLine()) != null) {
				line = line.replace("\r", "").replace("\r", "").replace("\t", "");
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			System.out.println("[Error]: while reading input stream");
			return ResponseBuilder.buildErrorResponse("Internal Server Error", 500, "Error while reading input stream", e);
		}

		/*-Check if can parse input string to JSONObject*/
		JSONObject inputJSON = null;
		try {
			inputJSON = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			System.out.println("[Error]: Invalid JSON format");
			return ResponseBuilder.buildErrorResponse("Bad Request", 400, "Invalid JSON format", e);
		}

		/*-Check if inputJSON is null*/
		if (null != inputJSON) {
			/*-Get correction*/
			try {
				String inputText = inputJSON.getString("text");
				ElasticTest elasticTest = new ElasticTest(DATA_TEMPLATE_PATH, ELASTIC_SERVER_URL_PATH);
				return ResponseBuilder.buildSuccessResponse("OK", 200, inputText, elasticTest.getSpellingCorrection(inputText));
			} catch (IOException e) {
				System.out.println("[Error]: Cannot read data_template.txt");
				return ResponseBuilder.buildErrorResponse("Internal Server Error", 500, "Cannot read data_template.txt", e);
			} catch (JSONException e) {
				System.out.println("[Error]: Invalid JSON property name");
				return ResponseBuilder.buildErrorResponse("Bad Request", 400, "Invalid JSON property name", e);
			}
		}

		return ResponseBuilder.buildErrorResponse("Bad Request", 400, "JSON string is null", null);
	}

	private boolean isAuthenticated(String authString) {
		if (authString.split("\\s+").length >= 1) {
			try {
				String userPass = authString.split("\\s+")[1];
				// userPass = new String(Base64.getDecoder().decode(userPass));
				if (userPass.equals(FileUtils.readFileToString(new File(AUTH_STRING_PATH), "UTF-8")))
					return true;
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("[Error]: Cannot read auth_string.txt");
			}
		}
		return false;
	}

}
