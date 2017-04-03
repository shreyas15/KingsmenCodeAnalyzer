import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author shreyas s bhat
 *
 */


public class Driver {
	
	public static void main(String[] args) throws IOException{
		
		FileCompactor fc = new FileCompactor(args[0]);
		fc.removeComments();
		String fileName = "compacted" + "_" + args[0];
		
		System.out.println("================================");
		System.out.println("            WARNINGS            ");
		System.out.println("================================\n");
		//System.out.println("------Unused Variables------\n");
		printUnusedVariables(fileName);
		
		//System.out.println("\n------One Line if/else------\n");
		findOneLineIfElse(fileName);
		
		//System.out.println("\n------Undeclared Function Calls------\n");
		findUndeclaredFuncts(fileName);
		
		//System.out.println("\n------Missing/Extra Brackets------\n");
		myBracket mb = new myBracket(fileName);
		mb.findBracketBalance();
	}
	

	private static void findUndeclaredFuncts(String fileName) throws IOException {
		List <myFunction> functTokens = new ArrayList <myFunction>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		int lineNumber = 0;
		try{
			while((eachLine = br.readLine()) != null){
				functTokens.add(new myFunction(eachLine.trim(), ++lineNumber));
			}
			for(myFunction t : functTokens){
				t.registerFunctions(fileName);
			}
			myFunction.getUndefFunctions(fileName);
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
		finally{
			br.close();
		}
	}

	private static void findOneLineIfElse(String fileName) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		List <myLogicStatement> logicalTokens = new ArrayList <myLogicStatement>();
		String eachLine = "";
		int lineNumber = 0;
		try{
			while((eachLine = br.readLine()) != null){
				lineNumber += 1;
				if (eachLine.contains("if") || eachLine.contains("else"))
					logicalTokens.add(new myLogicStatement(eachLine, fileName, lineNumber));
			}
			for(myLogicStatement t : logicalTokens){
				t.findIfElse();
			}
		}
		catch( Exception e){
			System.out.println(e.getMessage());
		}
		finally{
			br.close();
		}
	}

	private static void printUnusedVariables(String fileName) throws IOException{
		List <myVariable> variables = new ArrayList <myVariable>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		int lineNumber = 0;
		try{
			while((eachLine = br.readLine()) != null){
				variables.add(new myVariable(eachLine.trim(), ++lineNumber));
			}
			for(myVariable t : variables){
				t.registerVariables();
			}
			myVariable.updateVarMap(fileName);
			for (myVariable t: variables){
				t.getVarReport();
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
		finally{
			br.close();
		}
	}

}
