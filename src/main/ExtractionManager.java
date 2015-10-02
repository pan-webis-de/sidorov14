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
		
		// Do the same with the unknown texts
		// TODO: Fix duplication!
		int unknownTextCount = 1;
		TextInstance currentUnknownText = corpusManager.getUnknownText();
		while (currentUnknownText != null) {
			System.out.println("Processing unknown text " + unknownTextCount + " of " + corpusManager.getUnknownTextCount());
			String unknownText = null;
			try {
				unknownText = currentUnknownText.getFullText();
			} catch (IOException e) {
				System.err.println("Could not get unknown text. Skipping " + currentUnknownText.getTextSource() + " Cause: " + e.getMessage());
				currentUnknownText = corpusManager.getUnknownText();
				continue;
			}
			processText(unknownText, ngramSize, resultFolder, currentUnknownText.getTextSource().getName(),
					"UNKNOWN");
			currentUnknownText = corpusManager.getUnknownText();
			++unknownTextCount;
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
		File profileFile = new File(authorFolder, sourceFile);

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
				System.err.println("Failed to close file " + sourceFile);
				}
		}
	}
}
