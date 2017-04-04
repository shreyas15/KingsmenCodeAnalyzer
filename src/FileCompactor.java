/***
 * 
 * This class has methods used to clean the file before it is parsed. 
 * Currently used to remove all single line comments from the file. 
 * 
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * @author shreyas s bhat
 *
 */


public class FileCompactor extends Context{
	
	private String fileName;

	public FileCompactor(String fileName){
		this.fileName = fileName;
	}
	
	/***
	  *
	  * void removeComments( )
	  *
	  * Summary of the removeComments function:
	  *
	  *    The removeComments function, scans the input file
	  *    and removes all single line comments.
	  *
	  * Parameters   : None
	  *
	  * Return Value : None : Saves a new file to the original file location. 
	  *
	  * Description:
	  *
	  *    This function utilizes the Scanner and Writer classes to create a new usable file
	  *    free of single line comments that might otherwise interfere in parsing the code. 
	  *    The new output file has a composite name : <"compacted" + "_" + fileName>
	  *
	  */
	
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
				newLine = newLine.replaceAll("(\\<\\!\\-\\-(.*)\\-\\-\\>)", "");
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

	/***
	  *
	  * boolean isOneLineComment(String currentLine)
	  *
	  * Summary of the isOneLineComment function:
	  *
	  *    The isOneLineComment function, takes a string to check if it a single line comment or not.
	  *
	  * Parameters   : currentLine - line to be checked if single line or not.
	  *
	  * Return Value : true / false
	  *
	  */
	
	public boolean isOneLineComment(String currentLine) throws IOException{
		Matcher slComMatcher = SL_COMMENT_RX.matcher(currentLine);
		if (slComMatcher.matches())
			return true;
		return false;
	}
	
}