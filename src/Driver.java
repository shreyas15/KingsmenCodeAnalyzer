import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.regex.Pattern;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
import java.util.List;

public class Driver {
	
	public static void main(String[] args) throws IOException{
		
		String fileName = args[0];
		List <Token> tokens = new ArrayList <Token>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		int lineNumber = 0;
		while((eachLine = br.readLine()) != null){
			tokens.add(new Token(eachLine.trim(), ++lineNumber));
		}
		for(Token t:tokens){
			t.registerVariables();
		}
		
		br.close();
	}
}
