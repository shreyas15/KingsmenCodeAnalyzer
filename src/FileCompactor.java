import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */

/**
 * @author shreyas
 *
 */
public class FileCompactor {
	
	//single line comments
	private static final Pattern SL_COMMENT_RX = Pattern.compile("^(\\/\\*(.*)\\*\\/)|(\\/\\/(.*)$)");
	
	private String fileName;

	public FileCompactor(String fileName){
		this.fileName = fileName;
	}
	
	public void removeComments() throws IOException{
		File input = new File(fileName);
		File output = new File("compacted" + "_" + fileName);
		String newLine= "";
		Scanner reader = null;
		Writer writer = null;
		try {
			reader = new Scanner (input);
			writer = new FileWriter (output);

			while(reader.hasNextLine()) {
				newLine = reader.nextLine().toString().replaceAll("(\\/\\*(.*)\\*\\/)|(\\/\\/(.*)$)", "$1");
				newLine = newLine.replaceAll("(\\/\\*(.*)\\*\\/)|(\\/\\/(.*)$)", "$3");
				writer.write(newLine + "\n");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		finally{
			reader.close();
			writer.close();
		}
	}

	public boolean isOneLineComment(String currentLine) throws IOException{
		Matcher slComMatcher = SL_COMMENT_RX.matcher(currentLine);
		if (slComMatcher.matches())
			return true;
		return false;
	}

}