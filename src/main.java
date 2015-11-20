
public class main {
	
	private static FileReader reader = new FileReader();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		reader = new FileReader();
		ParsedInput input = reader.readFile("C:/Users/Niko/Desktop/example1.txt");
		input.print();
	}

}
