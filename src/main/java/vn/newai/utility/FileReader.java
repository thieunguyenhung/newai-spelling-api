package vn.newai.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileReader {
	/**
	 * Read a text file
	 * 
	 * @param filePath
	 *            path to file, must include file extension
	 * @throws IOException
	 */
	public static String readTextFile(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
		String line;
		StringBuilder builder = new StringBuilder();
		while ((line = br.readLine()) != null) {
			builder.append(line);
		}
		br.close();
		return builder.toString();
	}
}
