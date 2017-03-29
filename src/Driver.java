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
	
	private static final Pattern IDENTIFIER_RX = Pattern.compile("([a-zA-Z_$][a-zA-Z0-9_$]*)"); //regex to find Identifiers.
	
	public static void main(String[] args) throws IOException{
		
		String fileName = args[0];
		findUnusedVariables(fileName);

		findOneLineIfElse(fileName);
		
	}
	
	private static void findOneLineIfElse(String fileName) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		List <Token> logicalTokens = new ArrayList <Token>();
		String eachLine = "";
		int lineNumber = 0;
		try{
			while((eachLine = br.readLine()) != null){
				lineNumber += 1;
				if (eachLine.contains("if") || eachLine.contains("else"))
					logicalTokens.add(new Token(eachLine, fileName, lineNumber));
			}
			for(Token t : logicalTokens){
				t.findIfElse();
			}
		}
		finally{
			br.close();
		}
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
			t.getVarReport();
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
