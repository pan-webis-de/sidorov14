package corpus;

import java.io.File;
import java.nio.file.Path;

public class CorpusManager {
	private Path corpusLocation;
	private String metaFile = "meta-file.json";
	private String groundTruthFile = "ground-truth.json";
	
	public CorpusManager(Path location) {
		corpusLocation = location;
	}
	
	
}
