import de.ifgi.importer.FileReader;
import de.ifgi.importer.ParsedInput;
import de.ifgi.optimizer.Optimizer;

public class main {

	public static void main(String[] args) {
		FileReader reader = new FileReader();
		Optimizer optimizer = new Optimizer();
		ParsedInput input = reader.readFile("C:/Users/Niko/Desktop/graph_decomposition_example_2.txt");
		input.calcScores();
		//input.print();
		optimizer.optimize(input);
	}

}
