import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FileCompactor {
	
	private static final Pattern COMMENT_RX = Pattern.compile("");

	public FileCompactor(String fileName) throws IOException{
		File input = new File(fileName);
		File output = new File("compacted" + "_" + fileName + ".js");
		
		Scanner reader = null;
		Writer writer = null;
		try{
			reader = new Scanner (input);
			writer = new FileWriter (output);
			String lineRead;
			
			while(!(lineRead = reader.nextLine()).isEmpty()){
				writer.write(lineRead + "\n");
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
		finally{
			reader.close();
			writer.close();
		}
	}
	
	public void removeComments(String fileName){
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		finally{
			
		}
	}
}
