package vn.newai.elastic;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
//import java.util.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ElasticTest {
	private String dataTemplatePath;

	/*-public static void main(String[] args) throws ClientProtocolException, IOException {
		String s = FileUtils.readFileToString(new File("/Users/hungthieu/Documents/eclipseWS/spelling/src/main/webapp/test.txt"), "UTF-8");
		//System.out.println("result: " + checkDocSpelling(escapeString(s)).replace("\\n", "\n"));
		ElasticTest test= new  ElasticTest("/Users/hungthieu/Documents/eclipseWS/spelling/src/main/webapp/data_template.txt");
		test.getSpellingCorrection(s);
	}*/

	public ElasticTest(String dataTemplatePath) {
		this.dataTemplatePath = dataTemplatePath;
	}

	public String getSpellingCorrection(String pagraph) throws ClientProtocolException, IOException {
		System.out.println("Output" + "\t\t" + "Score" + "\t\t" + "Input");
		return checkDocSpelling(escapeString(pagraph)).replace("\\n", "\n");
	}

	private String readFile(String param) throws IOException {
		String s = FileUtils.readFileToString(new File(this.dataTemplatePath), "UTF-8");
		return s.replace("{{param}}", param);
	}

	private String checkDocSpelling(String doc) throws ClientProtocolException, IOException {
		String[] arr = doc.split("\\n");
		for (int i = 0; i < arr.length; i++)
			arr[i] = checkBlockSpelling(arr[i]);
		return String.join("\n", arr);
	}

	private String checkBlockSpelling(String block) throws ClientProtocolException, IOException {
		String[] arr = block.split("\\.");
		for (int i = 0; i < arr.length; i++)
			arr[i] = checkLineSpelling(arr[i]);
		return String.join(".", arr);
	}

	private String checkLineSpelling(String line) throws ClientProtocolException, IOException {
		String[] arr = line.split(" ");
		int pos = 2;
		while (pos < arr.length) {

			String s1, s2, s3;

			if (arr[pos].matches(".*\\d+.*"))
				s3 = "";
			else
				s3 = arr[pos];

			if (arr[pos - 1].matches(".*\\d+.*"))
				s2 = "";
			else
				s2 = arr[pos - 1];

			if (arr[pos - 2].matches(".*\\d+.*"))
				s1 = "";
			else
				s1 = arr[pos - 2];

			String s = s1 + " " + s2 + " " + s3;

			// System.out.println(s);

			if (s.trim().equals("")) {
				pos++;
				continue;
			}

			String correction = checkGramSpelling(s);

			// System.out.println("correction: " + correction);

			String[] arr2 = correction.split(" ");

			if (s1.equals("")) {
				if (s2.equals("")) {
					if (arr2.length > 0)
						arr[pos] = arr2[0];
				} else {
					if (arr2.length > 0)
						arr[pos - 1] = arr2[0];
					if (arr2.length > 1)
						arr[pos] = arr2[1];
				}
			}

			else if (s2.equals("")) {
				if (s3.equals("")) {
					if (arr2.length > 0)
						arr[pos - 2] = arr2[0];
				}
				if (arr2.length > 0)
					arr[pos - 2] = arr2[0];
				if (arr2.length > 1)
					arr[pos] = arr2[1];
			}

			else if (s3.equals("")) {
				if (arr2.length > 0)
					arr[pos - 2] = arr2[0];
				if (arr2.length > 1)
					arr[pos - 1] = arr2[1];
			}

			else {
				if (arr2.length > 0)
					arr[pos - 2] = arr2[0];
				if (arr2.length > 1)
					arr[pos - 1] = arr2[1];
				if (arr2.length > 2)
					arr[pos] = arr2[2];
			}
			pos++;
		}

		return String.join(" ", arr);
	}

	private String checkGramSpelling(String text) throws ClientProtocolException, IOException {
		// String encoding =
		// Base64.getEncoder().encodeToString(("elastic:changeme").getBytes());
		HttpPost httppost = new HttpPost("http://130.211.253.233:9200/doc/vbpl/_search");
		// httppost.setHeader("Authorization", "Basic " + encoding);

		StringEntity params = new StringEntity(readFile(text), Consts.UTF_8);
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
			System.out.println(correction.getString("text") + "\t" + String.format("%.8f", correction.getDouble("score")) + "\t" + simple_phrase.getString("text"));
			return correction.getString("text");
		}
		return text;
	}

	public String escapeString(String string) {
		StringWriter sw = new StringWriter();
		synchronized (sw.getBuffer()) {
			try {
				return escapeString(string, sw).toString();
			} catch (IOException ignored) {
				return "";
			}
		}
	}

	private Writer escapeString(String string, Writer w) throws IOException {
		if (string == null || string.length() == 0) {
			w.write("\"\"");
			return w;
		}

		char b;
		char c = 0;
		String hhhh;
		int i;
		int len = string.length();

		for (i = 0; i < len; i += 1) {
			b = c;
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				w.write('\\');
				w.write(c);
				break;
			case '/':
				if (b == '<') {
					w.write('\\');
				}
				w.write(c);
				break;
			case '\b':
				w.write("\\b");
				break;
			case '\t':
				w.write("\\t");
				break;
			case '\n':
				w.write("\\n");
				break;
			case '\f':
				w.write("\\f");
				break;
			case '\r':
				w.write("\\r");
				break;
			default:
				if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
					w.write("\\u");
					hhhh = Integer.toHexString(c);
					w.write("0000", 0, 4 - hhhh.length());
					w.write(hhhh);
				} else {
					w.write(c);
				}
			}
		}
		return w;
	}
}