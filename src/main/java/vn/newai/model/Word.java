package vn.newai.model;

public class Word {
	/**
	 * For number, special character and other kind that makes no sense with
	 * spell checking
	 */
	public static final int UN_CHECKABLE = 0;
	/** For text */
	public static final int CHECKABLE = 1;
	/** For and empty line */
	public static final int EMPTY_LINE = 2;
	private int id, type;
	private String text;

	public Word(int id, String text) {
		this.id = id;
		this.text = text;
		this.type = UN_CHECKABLE;
	}

	public Word(int id, String text, int type) {
		this.id = id;
		this.text = text;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.id + "\t" + this.text + "\t\t" + this.type;
	}
}
