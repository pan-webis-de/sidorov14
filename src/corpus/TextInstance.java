package corpus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class TextInstance {
	private String trueAuthor;
	private File textSource;
	private boolean hasText;
	
	public TextInstance(String author, File newTextSource)
	{
		trueAuthor = author;
		textSource = newTextSource;
		hasText = false;
	}
	
	public String getFullText() throws FileNotFoundException, IOException {
		List<String> fileContents = IOUtils.readLines(new FileInputStream(textSource));
		StringBuilder fullFile = new StringBuilder();
		for (String line : fileContents)
		{
			fullFile.append(line);
			fullFile.append("\n");
		}
		return fullFile.toString();
	}
	
	public File getTextSource() {
		return textSource;
	}
	
	public String getTrueAuthor() {
		return trueAuthor;
	}
	
	public boolean hasText()
	{
		return hasText;
	}
}
