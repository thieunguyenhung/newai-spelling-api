package vn.newai.elastic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import vn.newai.model.Word;
import vn.newai.preprocessing.Preprocessing;

public class SpellingChecker {
	public static String checkSpelling(String doc, String dataTemplatePath, String elasticServerURLPath) {
		ArrayList<Word> listWords = Preprocessing.splitWord(doc);
		listWords = groupWord(listWords, dataTemplatePath, elasticServerURLPath);
		return rebuildDocument(listWords);
	}

	/**
	 * Group words in ArrayList to a String with 2 to 3 words and perform
	 * spelling check
	 * 
	 * @param listWords
	 */
	private static ArrayList<Word> groupWord(ArrayList<Word> listWords, String dataTemplatePath, String elasticServerURLPath) {
		ElasticSearch elasticSearch = new ElasticSearch(dataTemplatePath, elasticServerURLPath);
		for (int i = 0; i < listWords.size(); i++) {
			int i1 = i;
			Word w1 = listWords.get(i1);
			if (w1.getType() == Word.UN_CHECKABLE || w1.getType() == Word.EMPTY_LINE)
				continue;
			int i2 = i + 1;
			if (i2 < listWords.size()) {
				Word w2 = listWords.get(i2);
				if (w2.getType() == Word.UN_CHECKABLE || w2.getType() == Word.EMPTY_LINE) {
					i += 1;
					continue;
				}
				int i3 = i + 2;
				if (i3 < listWords.size()) {
					Word w3 = listWords.get(i3);
					if (w3.getType() == Word.UN_CHECKABLE || w3.getType() == Word.EMPTY_LINE) {
						String[] arrWord = elasticSearch.checkSpelling(w1.getText() + " " + w2.getText()).split(" ");
						if (arrWord.length >= 2) {
							listWords.get(i1).setText(arrWord[0]);
							listWords.get(i2).setText(arrWord[1]);
						}
					} else {
						String[] arrWord = elasticSearch.checkSpelling(w1.getText() + " " + w2.getText() + " " + w3.getText()).split(" ");
						if (arrWord.length >= 3) {
							listWords.get(i1).setText(arrWord[0]);
							listWords.get(i2).setText(arrWord[1]);
							listWords.get(i3).setText(arrWord[2]);
						}
					}
				} else {
					String[] arrWord = elasticSearch.checkSpelling(w1.getText() + " " + w2.getText()).split(" ");
					if (arrWord.length >= 2) {
						listWords.get(i1).setText(arrWord[0]);
						listWords.get(i2).setText(arrWord[1]);
					}
				}
			}
		}
		return listWords;
	}

	private static String rebuildDocument(ArrayList<Word> listWords) {
		String result = "";
		boolean quoteOpening = false;
		for (int i = 0; i < listWords.size(); i++) {
			result += listWords.get(i).getText();
			if (listWords.get(i).getText().matches("[\'\"]+")) {
				if (!quoteOpening) {
					quoteOpening = true;
					continue;
				} else {
					result += " ";
					quoteOpening = false;
					continue;
				}
			}
			if (listWords.get(i).getType() != Word.EMPTY_LINE && !listWords.get(i).getText().matches("[\\(\\[{<?!]+")) {
				int i2 = i + 1;
				if (i2 < listWords.size()) {
					if (listWords.get(i2).getText().matches("[\'\"]+") && quoteOpening)
						continue;
					else if (!listWords.get(i2).getText().matches("[.,:\\)\\]}>]+"))
						result += " ";
				}
			}
		}
		return result;
	}

	public static void main(String[] args) {
		try {
			String s = FileUtils.readFileToString(new File("/home/anonym/Documents/eclipseWS/newai-spelling-api/test.txt"), "UTF-8");
			String result = SpellingChecker.checkSpelling(s, "/home/anonym/Documents/eclipseWS/newai-spelling-api/src/main/webapp/conf/data_template.txt", "/home/anonym/Documents/eclipseWS/newai-spelling-api/src/main/webapp/conf/elasticsearch_url.txt");
			System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
