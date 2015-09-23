package main;

import java.io.IOException;

public class Attributor {

	public static void main(String[] args) throws IOException 
	{
		System.out.println(args);
		SNExtractor extractor = new SNExtractor();
		extractor.extract_SN_Grams("I can, even now, remember the hour from which I dedicated myself to this great enterprise.", 3);
		
	}
}