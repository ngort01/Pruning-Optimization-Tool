import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Reads a file containing spatial objects and relations
 * @author Niko
 *
 */
public class FileReader {

	private ParsedInput input;
	private boolean isObject = false;
	private boolean isRelation = false;

	public FileReader() {
		this.input = new ParsedInput();
	}

	public ParsedInput readFile(String filePath) {
		System.out.println(filePath);
		File file = new File(filePath);

		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();

				// case: objects
				if (this.isObject && line.matches(".*\\s.*")) { // ".*\\s.*" = two strings seperated by whitespace
					String[] object = line.split("\\s");
					if (object[1].equalsIgnoreCase("point")) {
						this.input.addPoint(object[0]);
					} else if (object[1].equalsIgnoreCase("circle")) {
						this.input.addCircle(object[0]);
					}
					// case: relations
				} else if (this.isRelation && !line.matches("^$")) { // "^$" = empty string
					this.input.addRelation(line);
				}

				// check for objects or relations
				if (line.contains("#Objects")) {
					this.isObject = true;
					this.isRelation = false;
				} else if (line.contains("#Relations")) {
					this.isObject = false;
					this.isRelation = true;
				}

			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return this.input;
	}

}
