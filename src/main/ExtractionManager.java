package main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import corpus.CorpusManager;
import corpus.TextInstance;

public class ExtractionManager {
	SNExtractor extractor = new SNExtractor();
	
	public void processCorpus(String corpusFolder, String resultFolder, int ngramSize)
	{
		CorpusManager corpusManager = null;
		try {
			corpusManager = new CorpusManager(corpusFolder);
		} catch (IOException e) {
			System.err.println("Could not initialize corpus manager.");
			System.exit(-1);
		}
		
		
		
		TextInstance currentTextInstance = corpusManager.getNextText();
		
		while (currentTextInstance != null)
		{
			String currentText = null;
			try {
				 currentText = currentTextInstance.getFullText();
			} catch (IOException e) {
				System.err.println("Could not get text. Skipping " + currentTextInstance.getTextSource());
				currentTextInstance = corpusManager.getNextText();
				continue;
			}
			
			
			currentTextInstance = corpusManager.getNextText();
		}
	}
	
	private void processText(String text, int ngramSize)
	{
		List<List<String>> ngrams = extractor.extract_SN_Grams(text, ngramSize);
		List<List<Integer>> numericNGrams = FeatureMapper.numericalizeNGrams(ngrams);
		HashMap<List<Integer>, Integer> profile = FeatureMapper.createProfile(numericNGrams);
		// ProfileWriter.writeProfile(file.toFile(), outputDir, profile);
	}
}
