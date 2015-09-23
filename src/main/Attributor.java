package main;

import java.io.IOException;
import java.util.List;
import weka.core.Instances;

public class Attributor {

	public static void main(String[] args) throws IOException 
	{
		System.out.println(args);
		SNExtractor extractor = new SNExtractor();
		List<List<String>> ngrams = extractor.extract_SN_Grams("I can, even now, remember the hour from which I dedicated myself to this great enterprise.", 3);
		
		System.out.println(FeatureMapper.numericalizeNGrams(ngrams));
	}
}