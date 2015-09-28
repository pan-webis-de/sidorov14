package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class ProfileWriter {
	/**
	 * Extract the Author name. The file name "12AtrainA1.txt" contains that
	 * info in "A1": This is the first training text of author A.
	 * 
	 * @param file
	 * @return
	 */
	static private String getAuthorFromFile(File file) {
		// Get file name without extension
		String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());

		// Use regex to get author and text number
		Pattern authorPattern = Pattern.compile("([0-9]*[A-Z]train)([A-Z]*)([0-9]*)");
		Matcher authorMatcher = authorPattern.matcher(fileNameWithOutExt);
		if (authorMatcher.find()) {
			// Group 0 is whole string, 1 an irrelevant part, 2 is the author, 3
			// the number of the text
			return authorMatcher.group(2);
		} else {
			return "";
		}
	}

	static private String getProfileFileName(File file) {
		String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
		return (fileNameWithOutExt + "_profile.txt");
	}

	static public void writeProfile(File filename, File targetDir, HashMap<List<Integer>, Integer> profile) {
		String author = getAuthorFromFile(filename);
		File authorFolder = new File(targetDir, author);
		authorFolder.mkdirs();
		File profileFile = new File(authorFolder, getProfileFileName(filename));

		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(profileFile), "utf-8"));

			// Actually write data
			for (Map.Entry<List<Integer>, Integer> entry : profile.entrySet()) {
				List<Integer> ngram = entry.getKey();
				Integer ngramCount = entry.getValue();

				String ngramString = ngram.toString();

				writer.write(ngramString.substring(1, ngramString.length() - 1) + ", " + ngramCount + "\n");
			}
		} catch (IOException ex) {
			System.err.println("Failed to write profile for " + filename);
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				/* ignore */}
		}
	}

	static public void writeTranslationTable(HashMap<String, Integer> translationTable, File targetDir) {
		File tableFile = new File(targetDir, "translationTable");

		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tableFile), "utf-8"));
			for (Map.Entry<String, Integer> entry : translationTable.entrySet()) {
				writer.write(entry.getKey() + ", " + entry.getValue() + "\n");
			}
		} catch (IOException ex) {
			System.err.println("Failed to write Translation table.");
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				/* ignore */}
		}

	}
}
