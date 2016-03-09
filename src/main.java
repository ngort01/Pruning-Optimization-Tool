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
		//ParsedInput input = reader.readFile("./dist/propositions/b1p22.txt");
		ParsedInput input = reader.readFile(args[0]);
		input.calcScores();
		//Optimizer optimizer = new Optimizer(input, "test.txt");
		Optimizer optimizer = new Optimizer(input, args[1]);
		//input.print();
		optimizer.optimize();
	}

}
