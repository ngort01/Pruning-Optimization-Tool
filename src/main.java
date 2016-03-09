import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import de.ifgi.importer.FileReader;
import de.ifgi.importer.ParsedInput;
import de.ifgi.optimizer.Optimizer;

public class main {
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Properties customWeights = new Properties();
		String inputFile, outputFile;
		
		if (args.length == 0) {
			System.err.println("Please supply an input and output file! Run with '-help' to get further information.");
			System.exit(1);
		} else if (args.length == 1 && args[0].equals("-help")) {
			printHelp();
			System.exit(1);
		} else if (args.length == 2) {
			inputFile = args[0];
			outputFile = args[1];
		} else if (args.length == 3) {
			inputFile = args[0];
			outputFile = args[1];
			         
			InputStream stream = new FileInputStream(new File(args[2]));
			try {
				customWeights.load(stream);
			} catch (Exception e) {
				System.err.println("Failed to load custom Weights! Using defaults! ");
				System.err.println(e);
			}
			
		}
		
		FileReader reader = new FileReader();
		//ParsedInput input = reader.readFile("./dist/propositions/b1p22.txt");
		ParsedInput input = reader.readFile(args[0]);
		input.calcScores(customWeights);
		//Optimizer optimizer = new Optimizer(input, "test.txt");
		Optimizer optimizer = new Optimizer(input, args[1]);
		//input.print();
		optimizer.optimize();
	}
	
	private static void printHelp() {
		System.out.println("Arguments:");
		System.out.println("	inputFile.txt (mandatory)		path to textfile containing a qualitative description of spatial obects and their relations");
		System.out.println("	outputFile.txt (mandatory)		path and and name of the file that will hold the output");
		System.out.println("	weights.properties (optional)		properties file with custom weights for relations");
		System.out.println("");
		System.out.println("Options:");
		System.out.println("	-help		prints this help");
		System.out.println("");
		System.out.println("Visit https://github.com/ngort01/Pruning-Optimization-Tool for more information");
	}

}
