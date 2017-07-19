package vn.newai.preprocessing;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.newai.model.Word;

public class Preprocessing {
	private static final String SPECIAL_CHAR_REGEX = "[!@#$%^&*\\(\\)_\\-+=\\[\\]\\{\\}\\\\|;:,.<>/?'\"]+";
	/*-private static final String ACRONYM_REGEX = "[A-ZÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ]+[\\-]*[A-ZÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ]+";*/
	private static final String ACRONYM_REGEX = "([A-ZÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ][A-Z0-9ÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ]+)([\\-]*)([A-ZÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ][A-Z0-9ÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ]*)*";
	private static final String VN_TEXT_REGEX = "[a-zA-ZáàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ]+";

	public static ArrayList<Word> splitWord(String doc) {
		ArrayList<Word> listWords = new ArrayList<>();
		int idCounter = 0;
		String[] arrParagraph = doc.split("\n");
		for (String paragraph : arrParagraph) {
			if (!paragraph.isEmpty()) {
				String[] arrWord = paragraph.split(" ");
				for (String word : arrWord) {
					if (word.matches(ACRONYM_REGEX))
						listWords.add(new Word(idCounter, word.trim(), Word.UN_CHECKABLE));
					/*-Special char + text*/
					else if (word.matches(SPECIAL_CHAR_REGEX + VN_TEXT_REGEX)) {
						Matcher matcher = Pattern.compile("(" + SPECIAL_CHAR_REGEX + ")(" + VN_TEXT_REGEX + ")").matcher(word);
						if (matcher.find()) {
							listWords.add(new Word(idCounter, matcher.group(1), Word.UN_CHECKABLE));
							idCounter += 1;
							listWords.add(new Word(idCounter, matcher.group(2), Word.CHECKABLE));
						}
					}
					/*-text + special char*/
					else if (word.matches(VN_TEXT_REGEX + SPECIAL_CHAR_REGEX)) {
						Matcher matcher = Pattern.compile("(" + VN_TEXT_REGEX + ")(" + SPECIAL_CHAR_REGEX + ")").matcher(word);
						if (matcher.find()) {
							listWords.add(new Word(idCounter, matcher.group(1), Word.CHECKABLE));
							idCounter += 1;
							listWords.add(new Word(idCounter, matcher.group(2), Word.UN_CHECKABLE));
						}
					}
					/*-'text' or "text"*/
					else if (word.matches("[\'\"]+" + VN_TEXT_REGEX + "[\'\"]+")) {
						Matcher matcher = Pattern.compile("([\'|\"]+)(" + VN_TEXT_REGEX + ")([\'|\"]+)").matcher(word);
						if (matcher.find()) {
							listWords.add(new Word(idCounter, matcher.group(1), Word.UN_CHECKABLE));
							idCounter += 1;
							listWords.add(new Word(idCounter, matcher.group(2), Word.CHECKABLE));
							idCounter += 1;
							listWords.add(new Word(idCounter, matcher.group(3), Word.UN_CHECKABLE));
						}
					}
					/*-Signed integer with special char*/
					else if (word.matches("([+|-]*\\d+)(" + SPECIAL_CHAR_REGEX + ")")) {
						Matcher matcher = Pattern.compile("([+|-]*\\d+)(" + SPECIAL_CHAR_REGEX + ")").matcher(word);
						if (matcher.find())
							listWords.add(new Word(idCounter, matcher.group(0), Word.UN_CHECKABLE));
					}
					/*-Signed double by [.] with special char*/
					else if (word.matches("([+|-]*[0-9]+.[0-9]+)(" + SPECIAL_CHAR_REGEX + ")")) {
						Matcher matcher = Pattern.compile("([+|-]*[0-9]+.[0-9]+)(" + SPECIAL_CHAR_REGEX + ")").matcher(word);
						if (matcher.find())
							listWords.add(new Word(idCounter, matcher.group(0), Word.UN_CHECKABLE));
					}
					/*-Signed double by [,] with special char*/
					else if (word.matches("([+|-]*[0-9]+,[0-9]+)(" + SPECIAL_CHAR_REGEX + ")")) {
						Matcher matcher = Pattern.compile("([+|-]*[0-9]+,[0-9]+)(" + SPECIAL_CHAR_REGEX + ")").matcher(word);
						if (matcher.find())
							listWords.add(new Word(idCounter, matcher.group(0), Word.UN_CHECKABLE));
					} else
						listWords.add(classifyWord(idCounter, word));
					idCounter += 1;
				}
			} else {
				listWords.add(new Word(idCounter, "\n\n", Word.EMPTY_LINE));
				idCounter += 1;
			}
		}
		/*-for (Word w : listWords) {
			System.out.println(w);
		}*/
		return listWords;
	}

	public static Word classifyWord(int id, String text) {
		text = text.trim();
		Word word = new Word(id, text);
		/*-Signed integer*/
		if (text.matches("[+|-]*\\d+"))
			word.setType(Word.UN_CHECKABLE);
		/*-Signed double by [.]*/
		else if (text.matches("[+|-]*[0-9]+.[0-9]+"))
			word.setType(Word.UN_CHECKABLE);
		/*-Signed double by [,]*/
		else if (text.matches("[+|-]*[0-9]+,[0-9]+"))
			word.setType(Word.UN_CHECKABLE);
		/*-Special char*/
		else if (text.matches(SPECIAL_CHAR_REGEX))
			word.setType(Word.UN_CHECKABLE);
		/*-Normal text*/
		else
			word.setType(Word.CHECKABLE);
		return word;
	}
}
