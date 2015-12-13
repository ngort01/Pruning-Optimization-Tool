import java.io.IOException;

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
		FileReader reader = new FileReader();
		Optimizer optimizer = new Optimizer();
		//ParsedInput input = reader.readFile("C:/Users/Niko/Desktop/graph_decomposition_example_2.txt");
		ParsedInput input = reader.readFile(args[0]);
		input.calcScores();
		//input.print();
		optimizer.optimize(input, args[1]);
	}

}
