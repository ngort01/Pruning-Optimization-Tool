package de.ifgi.importer;
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
	private boolean isTest = false;

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
				if (isObject && line.matches(".*\\s.*")) { // ".*\\s.*" = two strings separated by whitespace
					String[] object = line.split("\\s");
					if (object[1].equalsIgnoreCase("point")) {
						input.addPoint(object[0]);
					} else if (object[1].equalsIgnoreCase("line")) {
						input.addLine(object[0]);
					} else if (object[1].equalsIgnoreCase("circle")) {
						input.addCircle(object[0]);
					}
					// case: relations
				} else if (isRelation && line.matches(".*\\(.*")) { // "^$" = empty string
					String[] object = line.split("\\(");
					object[1] = object[1].replaceAll("\\)", "");
					input.addRelation(object);
				} else if (isTest && line.matches(".*\\(.*")) {
					String[] object = line.split("\\(");
					input.addTest(object[0]);
				}

				// check for objects or relations
				if (line.contains("#Objects")) {
					isObject = true;
					isRelation = false;
					isTest = false;
				} else if (line.contains("#Relations")) {
					isObject = false;
					isRelation = true;
					isTest = false;
				} else if (line.contains("#Tests")) {
					isObject = false;
					isRelation = false;
					isTest = true;
				}

			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return this.input;
	}

}
