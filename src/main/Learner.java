package main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import corpus.CorpusManager;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

public class Learner {
	public void train (String authorProfiles) throws Exception
	{
		CSVLoader trainLoader = new CSVLoader();
		trainLoader.setSource(new File(authorProfiles));
		trainLoader.getStructure();
	    Instances trainSet = trainLoader.getDataSet();

        // setting class attribute if the data format does not provide this information
        // For example, the XRFF format saves the class attribute information as well
        if (trainSet.classIndex() == -1)
        	trainSet.setClassIndex(0);

        //initialize svm classifier
        LibSVM svm = new LibSVM();
        svm.buildClassifier(trainSet);
        
        J48 tree = new J48();
    	tree.buildClassifier(trainSet);
    	
    	/**************************************************/
    	
    	CSVLoader testLoader = new CSVLoader();
		testLoader.setSource(new File("Corpus/Processed/unknownVectors.txt"));
		testLoader.getStructure();
	    Instances testSet = testLoader.getDataSet();
	    
	    if (testSet.classIndex() == -1)
	    	testSet.setClassIndex(0);
        
        weka.core.SerializationHelper.write("svm.model", svm);
        weka.core.SerializationHelper.write("j48.model", tree);
        
        /*Evaluation eval = new Evaluation(trainSet);
        eval.crossValidateModel(svm, trainSet, 2, new Random(1));
        System.out.println(eval.toSummaryString("\nResults\n\n", false));
        eval.crossValidateModel(tree, trainSet, 2, new Random(1));
        System.out.println(eval.toSummaryString("\nResults\n\n", false));*/

        CorpusManager cm = new CorpusManager("Corpus/NEW CORPORA/pan12B/");
        
        for (int i = 0; i < testSet.numInstances(); ++i)
	    {
	    	double prediction = svm.classifyInstance(testSet.instance(i));
	    	String predictedAuthor = trainSet.classAttribute().value((int) prediction);
	    	String classifiedText = testSet.instance(i).stringValue(0);
	    	System.out.println(classifiedText + " -> " + predictedAuthor + " (is " + cm.getAuthorTextMapping().get(classifiedText + ".txt") + ")");
	    }
        
        // svm.classifyInstance(instance);
	}
	
	public void classify(String unknownFile) throws Exception
	{
		LibSVM svm = null;
		try {
			svm = (LibSVM) weka.core.SerializationHelper.read("svm.model");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		CSVLoader loader = new CSVLoader();
		String[] options = new String[1];
		options[0] = "-H";
		loader.setOptions(options);
		loader.setSource(new File(unknownFile));
		loader.getStructure();
	    Instances data = loader.getDataSet();
	    
	    if (data.classIndex() == -1)
        	data.setClassIndex(0);
	    
	    for (int i = 0; i < data.numInstances(); ++i)
	    {
	    	double prediction = svm.classifyInstance(data.instance(i));
	    	System.out.println(data.instance(i).stringValue(0) + " -> " + data.classAttribute().value((int) prediction));
	    }
	}
}
