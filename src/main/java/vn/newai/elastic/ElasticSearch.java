package vn.newai.elastic;

import java.io.IOException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import vn.newai.utility.FileReader;

public class ElasticSearch {
	private String dataTemplatePath;
	private String elasticServerURL;

	public ElasticSearch(String dataTemplatePath, String elasticServerURLPath) {
		this.dataTemplatePath = dataTemplatePath;
		this.elasticServerURL = "";
		this.readElasticURL(elasticServerURLPath);
	}

	private void readElasticURL(String elasticServerURLPath) {
		try {
			this.elasticServerURL = FileReader.readTextFile(elasticServerURLPath);
		} catch (IOException e) {
			System.out.println("[Error]: Cannot read elasticsearch_url.txt");
		}
	}

	private String readDataTemplate(String param) {
		try {
			return FileReader.readTextFile(this.dataTemplatePath).replace("{{param}}", param);
		} catch (IOException e) {
			System.out.println("[Error]: Cannot read data_template.txt");
		}
		return "";
	}

	public String checkSpelling(String text) {
		try {
			// String encoding =
			// Base64.getEncoder().encodeToString(("elastic:changeme").getBytes());
			HttpPost httppost = new HttpPost(this.elasticServerURL);
			// httppost.setHeader("Authorization", "Basic " + encoding);

			StringEntity params = new StringEntity(readDataTemplate(text), Consts.UTF_8);
			httppost.setEntity(params);

			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity);

			JSONObject result = new JSONObject(content);
			JSONObject suggest = result.getJSONObject("suggest");
			JSONObject simple_phrase = suggest.getJSONArray("simple_phrase").getJSONObject(0);
			JSONArray options = simple_phrase.getJSONArray("options");
			if (options.toList().size() > 0) {
				JSONObject correction = options.getJSONObject(0);
				//System.out.println(correction.getString("text") + "\t" + String.format("%.8f", correction.getDouble("score")) + "\t" + simple_phrase.getString("text"));
				return correction.getString("text");
			}
			/*-else{
				System.out.println(text);
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}
}
