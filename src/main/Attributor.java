package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import weka.classifiers.trees.J48;

public class Attributor {

	public static void main(String[] args) throws IOException {

		if (args.length == 0) {
			System.out.println("Usage: FILE [NgramSize] [FileLocation]");
			System.out.println("       CORP [NgramSize] [CorpusLocation]");
			System.out.println("       TEST [NgramSize]");
			System.exit(0);
		}
		
		if (args[0].equals("FILE")) {
			SNExtractor extractor = new SNExtractor();
			List<List<String>> ngrams = extractor.processFile(args[2], Integer.parseInt(args[1]));
			System.out.println(ngrams);
		}
		
		if (args[0].equals("CORP")) {
			processCorpus(args);
		}

		if (args[0].equals("TEST")) {
			SNExtractor extractor = new SNExtractor();
			
			List<List<String>> two_grams = extractor.extract_SN_Grams(
					"I can, even now, remember the hour from which I dedicated myself to this great enterprise.", 2);
			int[][] numeric2Grams = FeatureMapper.numericalizeNGrams(two_grams);
			
			List<List<String>> three_grams = extractor.extract_SN_Grams(
					"I can, even now, remember the hour from which I dedicated myself to this great enterprise.", 3);
			int[][] numeric3Grams = FeatureMapper.numericalizeNGrams(three_grams);

			System.out.println(Arrays.deepToString(numeric2Grams));
			System.out.println(Arrays.deepToString(numeric3Grams));

			String[] options = new String[1];
			options[0] = "-U"; // unpruned tree
			J48 tree = new J48(); // new instance of tree
			try {
				tree.setOptions(options);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// tree.buildClassifier(data); // build classifier
		}
	}
	
	/**
	 * 
	 * @param args Expects 1 as ngram size and 2 as directory with txt files.
	 */
	private static void processCorpus(String[] args) {
		int NGramLength = Integer.parseInt(args[1]);
		SNExtractor extractor = new SNExtractor();
		
		File corpusLocation = new File(args[2]);
		ArrayList<Path> files = new ArrayList<Path>();
		try {
			Iterator<Path> fileIter = Files.list(corpusLocation.toPath()).iterator();
			while (fileIter.hasNext())
			{
				files.add(fileIter.next());
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("Found " + files.size() + " files.");
		
		for (Path file : files) {
			if (file.toFile().isFile() && (file.toFile().getName().endsWith("txt") || file.toFile().getName().endsWith("TXT")))
			{
				System.out.println("Processing file " + file);
				// Get Ngrams
				List<List<String>> ngrams = extractor.processFile(file.toFile().getAbsolutePath(), NGramLength);
				int[][] numericNGrams = FeatureMapper.numericalizeNGrams(ngrams);
				
				// TODO Write [filename_Xgrams].txt containing numeric ngrams
				HashMap<int[], Integer> profile = FeatureMapper.createProfile(numericNGrams);
				System.out.println(profile.toString());
			}
			else
			{
				System.out.println("Skipping path " + file.toFile().getAbsolutePath());
			}
		}
		
		// TODO Write full translation table
	}
	
}