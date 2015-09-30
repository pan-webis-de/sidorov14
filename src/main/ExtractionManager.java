package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import corpus.CorpusManager;
import corpus.TextInstance;

public class ExtractionManager {
	SNExtractor extractor = new SNExtractor();

	// Set to store all seen ngrams in
	Set<List<Integer>> uniqueNgrams = new HashSet<List<Integer>>();

	public void processCorpus(String corpusFolder, String resultFolder, int ngramSize) {
		CorpusManager corpusManager = null;
		try {
			corpusManager = new CorpusManager(corpusFolder);
		} catch (IOException e) {
			System.err.println("Could not initialize corpus manager: " + e.getMessage());
			System.exit(-1);
		}

		TextInstance currentTextInstance = corpusManager.getNextText();
		int textCount = 1;

		while (currentTextInstance != null) {
			String currentText = null;
			try {
				currentText = currentTextInstance.getFullText();
			} catch (IOException e) {
				System.err.println("Could not get text. Skipping " + currentTextInstance.getTextSource() + " Cause: " + e.getMessage());
				currentTextInstance = corpusManager.getNextText();
				continue;
			}

			System.out.print("Processing text " + textCount + " of " + corpusManager.getTextCount() + " ... ");
			processText(currentText, ngramSize, resultFolder, currentTextInstance.getTextSource().getName(),
					currentTextInstance.getTrueAuthor());
			System.out.println("Done.");

			currentTextInstance = corpusManager.getNextText();
			++textCount;
		}

		ProfileWriter.writeTranslationTable(FeatureMapper.getTranslationTable(), new File(resultFolder));
		ProfileWriter.writeSeenUniqueNGrams(uniqueNgrams, new File(resultFolder));
	}

	private void processText(String text, int ngramSize, String resultFolder, String sourceFile, String author) {
		List<List<String>> ngrams = extractor.extract_SN_Grams(text, ngramSize);
		List<List<Integer>> numericNGrams = FeatureMapper.numericalizeNGrams(ngrams);
		HashMap<List<Integer>, Integer> profile = FeatureMapper.createProfile(numericNGrams);
		writeProfile(profile, resultFolder, sourceFile, author);

		// Store the seen ngrams
		for (List<Integer> seenNgram : profile.keySet()) {
			uniqueNgrams.add(seenNgram);
		}
	}

	private void writeProfile(HashMap<List<Integer>, Integer> profile, String resultFolder, String sourceFile,
			String author) {
		File authorFolder = new File(resultFolder, author);
		authorFolder.mkdirs();
		File profileFile = new File(authorFolder, (sourceFile + "_profile"));

		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(profileFile), "utf-8"));

			// Actually write data
			for (Map.Entry<List<Integer>, Integer> entry : profile.entrySet()) {
				List<Integer> ngram = entry.getKey();
				Integer ngramCount = entry.getValue();

				String ngramString = ngram.toString();

				writer.write("[" + ngramString.substring(1, ngramString.length() - 1) + "] -> " + ngramCount + "\n");
			}
		} catch (IOException ex) {
			System.err.println("Failed to write profile for " + sourceFile);
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				/* ignore */}
		}
	}

	public static void main(String[] args) {
		// ExtractionManager ex = new ExtractionManager();
		// ex.processCorpus("Corpus/NEW CORPORA/C10", "Corpus/Processed", 3);
		//ex.processCorpus("Corpus/NEW CORPORA/pan12A", "Corpus/Processed", 3);
		
		SVMPreprocessor prep = new SVMPreprocessor();
		try {
			prep.generateTrainingFile(new File("Corpus/Processed"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
