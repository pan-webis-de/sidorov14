package main;

import java.io.IOException;

public class TokenizerDemo {

	public static void main(String[] args) throws IOException 
	{
		SNExtractor extractor = new SNExtractor();
		extractor.extract_SN_Grams("I can, even now, remember the hour from which I dedicated myself to this great enterprise.", 3);

	}
}