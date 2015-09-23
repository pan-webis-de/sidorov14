package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeatureMapper {
	static public List<List<Integer>> numericalizeNGrams(List<List<String>> ngrams)
	{
		HashMap<String, Integer> translationTable = new HashMap<String, Integer>();
		int nextID = 1;
		
		ArrayList<List<Integer>> numericNGrams = new ArrayList<List<Integer>>();
		
		for (List<String> ngram : ngrams)
		{
			ArrayList<Integer> transformedNGram = new ArrayList<Integer>();
			for (String tag : ngram)
			{
				if (translationTable.get(tag) == null) {
					translationTable.put(tag, nextID);
					transformedNGram.add(nextID);
					++nextID;
				} else {
					transformedNGram.add(translationTable.get(tag));
				}
			}
			numericNGrams.add(transformedNGram);
		}
		
		return numericNGrams;
	}
}
