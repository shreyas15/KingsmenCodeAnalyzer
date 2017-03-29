import java.io.BufferedReader;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;

public class Driver {
	
	private static final Pattern IDENTIFIER_RX = Pattern.compile("([a-zA-Z_$][a-zA-Z0-9_$]*)"); 
	
	public static void main(String[] args) throws IOException{
		
		String fileName = args[0];
		findUnusedVariables(fileName);
	}
	
	static void findUnusedVariables(String fileName) throws IOException{
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
		updateMap(fileName);
		for (Token t: tokens){
			t.getReport();
		}
		br.close();
	}
	
	static void updateMap(String fileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		while((eachLine = br.readLine()) != null){
			Matcher matcher = IDENTIFIER_RX.matcher(eachLine);
			while (matcher.find()){
				String found = matcher.group().trim();
				int counter = Token.varNames.containsKey(found)? Token.varNames.get(found) : 0;
				Token.varNames.put(found, counter + 1);
			}
		}
		br.close();
	}
}
