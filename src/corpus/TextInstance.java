package corpus;

import java.io.File;

public class TextInstance {
	private String trueAuthor;
	private String fullText;
	private File textSource;
	private boolean hasText;
	
	public TextInstance(String author, File newTextSource)
	{
		trueAuthor = author;
		fullText = "";
		textSource = newTextSource;
		hasText = false;
	}
	
	public String getFullText() {
		return fullText;
	}
	
	public String getTrueAuthor() {
		return trueAuthor;
	}
	
	public boolean hasText()
	{
		return hasText;
	}
}
