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
	
	static public List<List<Integer>> numericalizeNGrams(List<List<String>> ngrams)
	{
		int nextID = 1;
		
		List<List<Integer>> numericNGrams = new ArrayList<List<Integer>>();
		int currentNgram = 0;
		
		for (List<String> ngram : ngrams)
		{
			List<Integer> transformedNGram = new ArrayList<Integer>();
			int currentTag = 0;
			for (String tag : ngram)
			{
				if (translationTable.get(tag) == null) {
					translationTable.put(tag, nextID);
					transformedNGram.add(nextID++);
				} else {
					transformedNGram.add(translationTable.get(tag));
				}
			}
			numericNGrams.add(transformedNGram);
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
