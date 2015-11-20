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
				if (this.isObject && line.matches(".*\\s.*")) { // ".*\\s.*" = two strings separated by whitespace
					String[] object = line.split("\\s");
					if (object[1].equalsIgnoreCase("point")) {
						this.input.addPoint(object[0]);
					} else if (object[1].equalsIgnoreCase("line")) {
						this.input.addLine(object[0]);
					} else if (object[1].equalsIgnoreCase("circle")) {
						this.input.addCircle(object[0]);
					}
					// case: relations
				} else if (this.isRelation && !line.matches("^$")) { // "^$" = empty string
					String[] object = line.split("\\(");
					object[1] = object[1].replaceAll("\\)", "");
					this.input.addRelation(object);
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
