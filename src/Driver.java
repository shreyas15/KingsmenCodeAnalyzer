import java.io.BufferedReader;
//import java.io.FileNotFoundException;
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
	private static final Pattern FUNCTION_RX = Pattern.compile("([a-zA-Z_$][a-zA-Z0-9_$]*)[\\(][\\s]{0,}[\\)]"); //regex to find function names.
	
	public static void main(String[] args) throws IOException{
		
		String fileName = args[0];
		System.out.println("================================");
		System.out.println("            WARNINGS            ");
		System.out.println("================================\n");
		System.out.println("------Unused Variables------\n");
		findUnusedVariables(fileName);
		
		System.out.println("\n------One Line if/else------\n");
		findOneLineIfElse(fileName);
		
		System.out.println("\n------Undeclared Function Calls------\n");
		findUndeclaredFuncts(fileName);
	}
	
	private static void findUndeclaredFuncts(String fileName) throws IOException {
		List <Token> functTokens = new ArrayList <Token>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		int lineNumber = 0;
		while((eachLine = br.readLine()) != null){
			functTokens.add(new Token(eachLine.trim(), ++lineNumber));
		}
		for(Token t:functTokens){
			t.registerFunctions();
		}
		updateFuncMap(fileName);
		br.close();
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
		updateVarMap(fileName);
		for (Token t: tokens){
			t.getVarReport();
		}
		br.close();
	}
	
	static void updateVarMap(String fileName) throws IOException{
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
	
	static void updateFuncMap(String fileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		int lineNumber = 0;
		while((eachLine = br.readLine()) != null){
			lineNumber += 1;
			Matcher matcher = FUNCTION_RX.matcher(eachLine);
			while (matcher.find()){
				String found = matcher.group().split("\\(")[0].trim();
				boolean flag = Token.funcNames.containsKey(found)? true : false;
				if (!flag && (!found.equals("function"))){
					int counter = Token.undefNames.containsKey(found) ? Token.undefNames.get(found) : 0;
					Token.undefNames.put(found, counter + 1);
					System.out.println(found + " : " + "is possibly an undeclared function"  + " at line " + lineNumber);
				}
				else{
					int counter = Token.funcNames.get(found) != null ? Token.funcNames.get(found) : 0;
					Token.funcNames.put(found, counter + 1);
				}
			}
		}
		br.close();
	}


}
