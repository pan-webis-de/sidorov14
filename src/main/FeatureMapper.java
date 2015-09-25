package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeatureMapper {
	static private HashMap<String, Integer> translationTable = new HashMap<String, Integer>();
	
	static public HashMap<String, Integer> getTranslationTable()
	{
		return translationTable;
	}
	
	static public int[][] numericalizeNGrams(List<List<String>> ngrams)
	{
		int nextID = 1;
		
		int[][] numericNGrams = new int[ngrams.size()][];
		int currentNgram = 0;
		
		for (List<String> ngram : ngrams)
		{
			int[] transformedNGram = new int[ngram.size()];
			int currentTag = 0;
			for (String tag : ngram)
			{
				if (translationTable.get(tag) == null) {
					translationTable.put(tag, nextID);
					transformedNGram[currentTag++] = nextID;
					++nextID;
				} else {
					transformedNGram[currentTag++] = translationTable.get(tag);
				}
			}
			numericNGrams[currentNgram++] = transformedNGram;
		}
		
		return numericNGrams;
	}
	
	static public HashMap<List<Integer>, Integer> createProfile(List<List<Integer>> numericNgrams)
	{
		HashMap<List<Integer>, Integer> ngramCounts = new HashMap<List<Integer>, Integer>();
		
		for (List<Integer> ngram : numericNgrams)
		{
			Integer count = ngramCounts.get(ngram);
			if (count != null)
			{
				ngramCounts.put(ngram, count + 1);
			}
			else
			{
				ngramCounts.put(ngram, 1);
			}
		}
		
		return ngramCounts;
	}
}
