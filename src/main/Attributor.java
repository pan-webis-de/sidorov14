package main;

import java.io.IOException;
import java.util.List;
import weka.classifiers.trees.J48;

public class Attributor {

	public static void main(String[] args) throws IOException {
		System.out.println(args);
		SNExtractor extractor = new SNExtractor();
		List<List<String>> ngrams = extractor.extract_SN_Grams(
				"I can, even now, remember the hour from which I dedicated myself to this great enterprise.", 3);
		List<List<Integer>> numericNGrams = FeatureMapper.numericalizeNGrams(ngrams);

		System.out.println(numericNGrams);

		String[] options = new String[1];
		options[0] = "-U"; // unpruned tree
		J48 tree = new J48(); // new instance of tree
		// tree.setOptions(options); // set the options
		// tree.buildClassifier(data); // build classifier
	}
}