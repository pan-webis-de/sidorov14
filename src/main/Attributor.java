package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import corpus.CorpusManager;
import weka.classifiers.trees.J48;

public class Attributor {

	public static void main(String[] args) throws IOException {

		if (args.length == 0) {
			System.out.println("Usage: FILE  [NgramSize] [FileLocation]");
			System.out.println("       CORP  [NgramSize] [CorpusLocation]");
			System.out.println("       PREP  [ProfileFolders]");
			System.out.println("       LEARN [TrainingData]");
			System.exit(0);
		}
		
		if (args[0].equals("FILE")) {
			SNExtractor extractor = new SNExtractor();
			List<List<String>> ngrams = extractor.processFile(args[2], Integer.parseInt(args[1]));
			System.out.println(ngrams);
		}
		
		if (args[0].equals("CORP")) {
			ExtractionManager ext = new ExtractionManager();
			ext.processCorpus(args[2], "Corpus/Processed", Integer.parseInt(args[1]));
		}
		
		if (args[0].equals("LEARN")) {	
			Learner l = new Learner();
			try {
				l.train(args[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (args[0].equals("CLASS")) {
			Learner l = new Learner();
			try {
				l.classify("Corpus/Processed/unknownVectors.txt");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (args[0].equals("PREP")) {
			SVMPreprocessor s = new SVMPreprocessor();
			File authorProfilesLocation = new File(args[1]);
			s.generateTrainingFile(authorProfilesLocation);
			s.generateClassificationFile(authorProfilesLocation);
		}
	}
}