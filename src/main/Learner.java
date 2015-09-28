package main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

public class Learner {
	public void train (String authorProfiles, String authorName) throws Exception
	{
		CSVLoader loader = new CSVLoader();
		String[] options = new String[1];
		options[0] = "-H";
		loader.setOptions(options);
		loader.setSource(new File(authorProfiles));
		loader.getStructure();
	    Instances data = loader.getDataSet();
	    
	    System.out.println(data.attribute(0));
	    
		/*DataSource source = new DataSource(loader);
        System.out.println(source.getStructure());
        Instances data = source.getDataSet();*/

        // setting class attribute if the data format does not provide this information
        // For example, the XRFF format saves the class attribute information as well
        if (data.classIndex() == -1)
        	data.setClassIndex(0);

        //initialize svm classifier
        LibSVM svm = new LibSVM();
        svm.buildClassifier(data);
	}
}
