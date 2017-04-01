import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.regex.Pattern;
//import java.util.regex.Matcher;
import java.util.List;

public class Driver {
	
	public static void main(String[] args) throws IOException{
		
		String fileName = args[0];
		System.out.println("================================");
		System.out.println("            WARNINGS            ");
		System.out.println("================================\n");
		//System.out.println("------Unused Variables------\n");
		findUnusedVariables(fileName);
		
		//System.out.println("\n------One Line if/else------\n");
		findOneLineIfElse(fileName);
		
		//System.out.println("\n------Undeclared Function Calls------\n");
		findUndeclaredFuncts(fileName);
		
		//System.out.println("\n------Missing/Extra Brackets------\n");
		if (!Context.findBracketBalance(fileName)){	
			;
		}
		else System.out.println("Brackets balanced");
	}
	

	private static void findUndeclaredFuncts(String fileName) throws IOException {
		List <Context> functTokens = new ArrayList <Context>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		int lineNumber = 0;
		while((eachLine = br.readLine()) != null){
			functTokens.add(new Context(eachLine.trim(), ++lineNumber));
		}
		for(Context t:functTokens){
			t.registerFunctions(fileName);
		}
		Context.getUndefFunctions(fileName);
		br.close();
	}

	private static void findOneLineIfElse(String fileName) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		List <Context> logicalTokens = new ArrayList <Context>();
		String eachLine = "";
		int lineNumber = 0;
		try{
			while((eachLine = br.readLine()) != null){
				lineNumber += 1;
				if (eachLine.contains("if") || eachLine.contains("else"))
					logicalTokens.add(new Context(eachLine, fileName, lineNumber));
			}
			for(Context t : logicalTokens){
				t.findIfElse();
			}
		}
		finally{
			br.close();
		}
	}

	private static void findUnusedVariables(String fileName) throws IOException{
		List <Context> tokens = new ArrayList <Context>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		int lineNumber = 0;
		while((eachLine = br.readLine()) != null){
			tokens.add(new Context(eachLine.trim(), ++lineNumber));
		}
		for(Context t:tokens){
			t.registerVariables();
		}
		Context.updateVarMap(fileName);
		for (Context t: tokens){
			t.getVarReport();
		}
		br.close();
	}

}
