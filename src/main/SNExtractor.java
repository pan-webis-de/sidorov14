package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.CoreMap;

public class SNExtractor {

	private Properties props;
	private StanfordCoreNLP pipeline;

	public SNExtractor() {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		// NER, parsing, and coreference resolution
		props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		pipeline = new StanfordCoreNLP(props);
	}

	public List<List<String>> extract_SN_Grams(String text, int nGramSize) {
		// create an empty Annotation, run annotator and extract sentences
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		List<List<String>> ngrams = new ArrayList<List<String>>();
		
		// Iterate over all sentences and get the SN grams from the dependency
		// graph.
		for (CoreMap sentence : sentences) {
			// this is the Stanford dependency graph of the current sentence
			// TODO: Test CollapsedDependenciesAnnotation and BasicDependenciesAnnotation
			// TODO: Also note that paper doesn't mention what to use.
			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);

			Collection<IndexedWord> rootNodes = dependencies.getRoots();

			if (rootNodes.isEmpty()) {
				System.err.println("ERROR: Could not get any roots in sentence: '" + sentence.toString() + "'");
				continue; // skip sentence
			}

			if (rootNodes.size() > 1) {
				System.err.println("ERROR:More than one root in sentence: '" + sentence.toString() + "'");
				continue; // skip sentence
			}

			// Unique set of all paths from the root to the leaves of the semantic tree
			Set<ArrayList<String>> relationPaths = getRelationPaths(dependencies, rootNodes);

			// We have all paths, now chop them into appropriate n-grams.
			// Map N = 2 -> [[aux, sub], [sub, npos], ...]
			ngrams.addAll(generateNgrams(nGramSize, relationPaths));			
		}
		return ngrams;
	}
	
	private List<List<String>> generateNgrams(int length, Set<ArrayList<String>> relationPaths)
	{
		List<List<String>> ngrams = new ArrayList<List<String>>(); 
		for (ArrayList<String> path : relationPaths) {
			List<List<String>> subsequences = getSubsequences(path, length);
			if (ngrams.isEmpty()) {
				ngrams = subsequences;
			} else {
				ngrams.addAll(subsequences);
			}
		}
		return ngrams;
	}
	
	private Set<ArrayList<String>> getRelationPaths(SemanticGraph dependencies, Collection<IndexedWord> rootNodes)
	{
		Set<ArrayList<String>> relationPaths = new ArraySet<ArrayList<String>>();

		for (IndexedWord root : rootNodes) {
			Set<IndexedWord> leaves = dependencies.getLeafVertices();

			for (IndexedWord leaf : leaves) {
				List<IndexedWord> path = dependencies.getShortestDirectedPathNodes(root, leaf);
				ArrayList<String> currentRelationPath = new ArrayList<String>();
				currentRelationPath.add("root");
				for (IndexedWord w : path) {
					List<SemanticGraphEdge> incomingEdges = dependencies.getIncomingEdgesSorted(w);
					if (!incomingEdges.isEmpty()) {
						// Only use nodes with one incoming edge. Two or more aren't handled.
						currentRelationPath.add(incomingEdges.get(0).getRelation().toString());
					}
				}
				relationPaths.add(currentRelationPath);
			}
		}
		
		return relationPaths;
	}

	private List<List<String>> getSubsequences(List<String> source, int length) {
		assert(length >= 2);

		if (source.size() < 2) {
			return new ArrayList<List<String>>();
		} else if (source.size() == length) {
			ArrayList<List<String>> trivialSubsequence = new ArrayList<List<String>>();
			trivialSubsequence.add(source);
			return trivialSubsequence;
		} else {
			ArrayList<List<String>> subsequences = new ArrayList<List<String>>();
			for (int i = length; i <= source.size(); ++i) {
				List<String> subsequence = source.subList(i - length, i);
				subsequences.add(subsequence);
			}
			return subsequences;
		}
	}
}
