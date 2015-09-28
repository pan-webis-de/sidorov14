package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SVMPreprocessor {
	private List<Integer> parseNgram(String ngram)
	{
		ngram = ngram.trim();
		ngram = ngram.substring(1, ngram.length() - 1);
		List<String> values = Arrays.asList(ngram.split(","));
		List<Integer> ngramValues = new ArrayList<Integer>();
		
		for (String entry : values)
		{
			ngramValues.add(Integer.parseInt(entry.trim()));
		}
		
		return ngramValues;
	}
	
	private Set<List<Integer>> getSeenNgrams(File seenNgramFile) throws IOException
	{
		// Open the file
		FileInputStream fstream = new FileInputStream(seenNgramFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		Set<List<Integer>> seenNgrams = new HashSet<List<Integer>>();

		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
			seenNgrams.add(parseNgram(strLine));
		}

		//Close the input stream
		br.close();
		return seenNgrams;
	}
	
	public void generateAuthorVectorFile(File authorFolder, File seenNgramFile, String authorName)
	{
		// Get author files
		ArrayList<Path> files = new ArrayList<Path>();
		try {
			Iterator<Path> fileIter = Files.list(authorFolder.toPath()).iterator();
			while (fileIter.hasNext())
			{
				files.add(fileIter.next());
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("Transforming " + files.size() + " files by author " + authorName);
		
		Set<List<Integer>> seenNgrams = null;
		try {
			seenNgrams = getSeenNgrams(seenNgramFile);
		} catch (IOException e1) {
			System.err.println("Could not read see nngram file");
			e1.printStackTrace();
			System.exit(-1);
		}
		
		// Open file for all resulting vectors
		File vectorFile = new File(authorFolder.getParentFile(), authorName + "_profileVectors.txt");

		Writer vectorWriter = null;
		try {
			vectorWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(vectorFile), "utf-8"));
		} catch (Exception ex) {
			System.err.println("Failed to open vector file for author " + authorName);
			System.exit(-1);
		}
		
		// Read each author file
		for (Path fileToRead : files) {
			// build hash map to count ngrams into
			HashMap<List<Integer>, Integer> profileVector = new HashMap<List<Integer>, Integer>();
			for (List<Integer> ngram : seenNgrams)
			{
				profileVector.put(ngram, 0);
			}
			
			try {
				FileInputStream fstream = new FileInputStream(fileToRead.toFile());
				BufferedReader reader = new BufferedReader(new InputStreamReader(fstream));	
				String strLine;
		
				// Transform the file into a vector of how many times each ngram has been seen
				// e.g. sngrams A...D have been seen and this contains A, C, D, once then we write
				// 1, 0, 1, 1 -> A for that file.
				while ((strLine = reader.readLine()) != null)   {
					// Each line has the format [ngram] -> count
					List<String> splitLine = Arrays.asList(strLine.split("->"));
					if (splitLine.size() != 2)
					{
						System.err.println("SVM Preprocessing encountered malformatted line: " + strLine);
						continue;
					}
					List<Integer> ngram = parseNgram(splitLine.get(0));
					int count = Integer.parseInt(splitLine.get(1).trim());
					
					if (profileVector.get(ngram) == null)
					{
						System.err.println("Programming error: Ngram " + ngram.toString() + " not in seen ngrams.");
						System.exit(-1);
					} else {
						profileVector.put(ngram, profileVector.get(ngram) + count);
					}
				}
				reader.close();
			} catch (IOException e)
			{
				System.err.println("Could not transform " + fileToRead.toString() + ", skipping.");
				continue;
			}
			
			// Write to profile file
			for (Map.Entry<List<Integer>, Integer> entry : profileVector.entrySet())
			{
				try
				{
					vectorWriter.write(entry.getValue() + ", ");
				} catch (Exception e) {
					System.err.println("Failed to write vector file for author " + authorName);
					System.exit(-1);
				}
			}
			
			try {
				vectorWriter.write("\n");
			} catch (IOException e) {
				System.err.println("Failed to terminate vector with newline for author " + authorName);
				System.exit(-1);
			}
		}
		
		try{
		vectorWriter.close();
		} catch (IOException e)
		{
		}
	}
}
