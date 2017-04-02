/**
 * 
 */

/**
 * @author shreyas
 *
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Context {
	
	String line;
	String objName;
	String ifOrElse;
	String fileName;
	int lineNumber;
	int columnNumber;
	
	/***
	 * 
	 * REGEX for various pattern detection. 
	 * The reason why some of the regex are long is because the file is parsed
	 * as one single string with various escape characters. 
	 * 
	 */
	
	//general Identifiers.
	private static final Pattern IDENTIFIER_RX = Pattern.compile("([a-zA-Z_$][a-zA-Z0-9_$]*)"); 
	//variable with assignment
	private static final Pattern VAR_ASN_RX = Pattern.compile("(let|var)(.*?)\\=(.*?)\\;");
	//variable declared
	private static final Pattern VAR_DEC_RX = Pattern.compile("(let|var)(.*?)\\;");
	//function declaration for bracket check like function name ( )
	private static final Pattern DEC_FUNC_RX = Pattern.compile("(function)[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\(]([\\s\\S]*?)[\\)]");
	//any function call with parameters
	private static final Pattern FUNCTION_RX = Pattern.compile("[a-zA-Z_$][a-zA-Z0-9_$]{1,}[\\s]*[\\(]");
	//any function call without parameters
	private static final Pattern FUNCTION2_RX = Pattern.compile("([a-zA-Z_$][a-zA-Z0-9_$]{1,}[\\s]{0,}[\\(]{1,}[\\s]{0,}[\\)])");
	//Anonymous functions like: var doSomethingFunction = function ();
	private static final Pattern FUNC_BOUNDARY1 = Pattern.compile("(let|var)[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\=][\\s]{0,}(function)[\\s]{0,}[\\(]([\\s\\S]*?)[\\)]"); 
	//function expressions with names like: var tool = {"doSomething": function () { ... }};
	private static final Pattern FUNC_BOUNDARY2 = Pattern.compile("(var|let)[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\=][\\s]{0,}[\\{]{0,}[\\s]{0,}[\\\"]{0,}[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\\"]{0,}[\\s]{0,}[\\:][\\s]{0,}(function)[\\s]{0,}[\\(]([\\s\\S]*?)[\\)][\\s]{0,}[\\{]"); 
	//function declarations like: let doSomething = () => {
	private static final Pattern FUNC_BOUNDARY3 = Pattern.compile("(let|var)[\\s]{0,}[a-zA-Z_$][a-zA-Z0-9_$]{1,}[\\s]{0,}[\\=][\\s]{0,}[\\(][\\s]{0,}([\\s\\S]*?)[\\s]{0,}[\\)][\\s]{0,}[\\=]{1,}[\\>]");  
	//function calls like: mustang.getEngineType();
	private static final Pattern FUNC_BOUNDARY4 = Pattern.compile("[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\:][\\s]{0,}(function)[\\s]{0,}[\\(][\\s]{0,}[\\)][\\s]{0,}[\\{]"); 
	//single line IF
	private static final Pattern IF_RX = Pattern.compile("[\\t]{0,}[\\s]{0,}(if)[\\s]{0,}[\\(]{1,}[\\!]{0,}([\\s\\S]*?)[\\)]{1,}[\\s]{0,}[\\n]{0,}[\\t]{0,}");
	//if with trailing curly bracket
	private static final Pattern IF_NOT_RX = Pattern.compile("[\\t]{0,}[\\s]{0,}(if)[\\s]{0,}[\\(]{1,}[\\!]{0,}([\\s\\S]*?)[\\)]{1,}[\\s]{0,}[\\n]{0,}[\\t]{0,}[\\{]");
	//single line else
	private static final Pattern ELSE_BOUNDARY = Pattern.compile("[\\t]{0,}[\\}]{0,}[\\t]{0,}[\\s]{0,}(else)[\\s]{0,}[\\n]{0,}[\\t]{0,}");
	//else with trailing curly bracket
	private static final Pattern ELSE_NOT = Pattern.compile("[\\t]{0,}[\\}]{0,}[\\t]{0,}[\\s]{0,}(else)[\\s]{0,}[\\n]{0,}[\\t]{0,}([\\s\\S]*?)[\\s]{0,}[\\n]{0,}[\\t]{0,}[\\{]");
	
	/***
	 * 
	 * Hash Maps
	 */
	public static Map<String, Integer> varNames = new HashMap<String, Integer>(); 
	public static Map<String, Integer> funcNames = new HashMap<String, Integer>(); 
	public static Map<String, Integer> undecFuncNames = new HashMap<String, Integer>(); 
	public static Map<String, Integer> bracketCount = new HashMap<String, Integer>(); 
	public static List <Context> openBrackets = new ArrayList <Context>();
	public static List <Context> closedBrackets = new ArrayList <Context>();

	public Context (){
		this.line = "";
		this.objName = "";
		this.lineNumber = 0;
		this.columnNumber = 0;
		this.ifOrElse = "";
		this.fileName = "";
	}
	
	public Context (String line, int lineNum){
		this.line = line;
		this.objName = "";
		this.lineNumber = lineNum;
		this.columnNumber = 0;
	}
	
	public Context (String line, String fileName, int lineNum){
		this.line = line;
		this.fileName = fileName;
		this.objName = "";
		this.lineNumber = lineNum;
		this.columnNumber = 0;
	}
	
	public Context (String line, String fileName, String objName, int lineNum, int colNum){
		this.line = line;
		this.fileName = fileName;
		this.objName = objName;
		this.lineNumber = lineNum;
		this.columnNumber = colNum;
	}
	
	/***
	 * 
	 * Getter methods
	 */
	public Map<String, Integer> getVariableMap() {
	       return varNames;
	 }
	
	public Map<String, Integer> getDeclaredFunctions() {
	       return funcNames;
	 }
	
	public Map<String, Integer> getUndeclaredFunctions() {
	       return undecFuncNames;
	 }
	
	/***
	 * 
	 * Methods to perform operations on the context. 
	 */
	
	//...*** private methods
	
	private static String getMultiLineString(String fileName2, String line2) throws IOException {
		
		BufferedReader bufferreader = new BufferedReader(new FileReader(fileName2));
		StringBuilder stringbuilder = new StringBuilder();
		String line;
		boolean fromLine = false;
		
		try{
			while((line = bufferreader.readLine()) != null){
				if (line.contains(line2)){
					fromLine = true;
				}
				int count = 1;
				while (line != null && fromLine == true && count > 0){
					if (!line.isEmpty()){
						stringbuilder.append(line);
						stringbuilder.append("\n");
						count -= 1;
					}
					line = bufferreader.readLine();
				}
				if (fromLine){
					return stringbuilder.toString();
				}
			}
			return "";
		}
		finally{
			bufferreader.close();
		}
	}
	
	//...*** public methods

	public void getVarReport(){
		boolean flag = varNames.containsKey(this.objName) ? (varNames.get(this.objName) == 2) ? true : false : false;
		if (flag)
			System.out.println(this.objName + " : " + "is an unused variable"  + " at line " + this.lineNumber);
		else return;
	}
	

	public void registerVariables(){
		Matcher matcher = VAR_ASN_RX.matcher(this.line);
		Matcher matcher2 = VAR_DEC_RX.matcher(this.line);
		String splitter = "";
		
		if (matcher.matches()){
			splitter = line.split("\\=")[0];
			if (splitter.contains("let"))
			{
				this.objName = splitter.split("(let)")[1].trim();
			}
			else if (splitter.contains("var")){
				this.objName = splitter.split("(var)")[1].trim();
			}
			int counter = varNames.containsKey(this.objName)? varNames.get(this.objName) : 0;
			varNames.put(this.objName, counter + 1);
		}
		else if (matcher2.matches()){
			splitter = line.split("\\;")[0];
			if (splitter.contains("let"))
			{
				this.objName = splitter.split("(let)")[1].trim();
			}
			else if (splitter.contains("var")){
				this.objName = splitter.split("(var)")[1].trim();
			}
			int counter = varNames.containsKey(this.objName)? varNames.get(this.objName) : 0;
			varNames.put(this.objName, counter + 1);
		}
	}
	
	public static void updateVarMap(String fileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		while((eachLine = br.readLine()) != null){
			Matcher matcher = IDENTIFIER_RX.matcher(eachLine);
			while (matcher.find()){
				String found = matcher.group().trim();
				int counter = Context.varNames.containsKey(found)? Context.varNames.get(found) : 0;
				Context.varNames.put(found, counter + 1);
			}
		}
		br.close();
	}
	

	public void findIfElse() throws IOException {
		
		String multiLine = getMultiLineString(this.fileName, this.line);
		Matcher matcher1 = IF_RX.matcher(multiLine);
		Matcher matcher2 = ELSE_BOUNDARY.matcher(multiLine);
		Matcher matcher3 = IF_NOT_RX.matcher(multiLine);
		Matcher matcher4 = ELSE_NOT.matcher(multiLine);
		
		if(matcher1.find()){
			if (!matcher3.find()){
				this.ifOrElse = "if";
				getIfElseReport();
			}
		}
		else if(matcher2.find()){
			if (!matcher4.find()){
				this.ifOrElse = "else";
				getIfElseReport();
			}
		}
		else return;
	}
	
	private void getIfElseReport() {
		if (this.ifOrElse.equals("if"))
			System.out.println(this.line.trim() + " : " + "possibly a one line if"  + " at line " + this.lineNumber);
		else if(this.ifOrElse.equals("else"))
			System.out.println(this.line.trim() + " : " + "possibly a one line else"  + " at line " + this.lineNumber);
		else return;
	}
	

	public void registerFunctions(String fileName) throws IOException {
		Matcher match0 = DEC_FUNC_RX.matcher(this.line);
		Matcher match1 = FUNC_BOUNDARY1.matcher(this.line);
		Matcher match2 = FUNC_BOUNDARY2.matcher(this.line);
		Matcher match3 = FUNC_BOUNDARY3.matcher(this.line);
		Matcher match4 = FUNC_BOUNDARY4.matcher(this.line);
		String splitter = "";
		
		if(match1.find()){
			if (this.line.contains("var"))
				splitter = line.split("\\=")[0].split("var")[1].trim();
			else
				splitter = line.split("\\=")[0].split("let")[1].trim();
			this.objName = splitter;
			int counter = funcNames.containsKey(this.objName)? funcNames.get(this.objName) : 0;
			funcNames.put(this.objName, counter + 1);
			if (isFunctMissingBracket(fileName, this.line))
				System.out.println(this.line + " : Expected '{' after " + this.objName + " (...) " + "at line " + this.lineNumber);
		}
		else if(match2.find()){
			splitter = line.split("\"",3)[1].trim();
			this.objName = splitter;
			int counter = funcNames.containsKey(this.objName)? funcNames.get(this.objName) : 0;
			funcNames.put(this.objName, counter + 1);
		}
		else if(match4.find()){
			splitter = line.split(":")[0].trim();
			this.objName = splitter;
			int counter = funcNames.containsKey(this.objName)? funcNames.get(this.objName) : 0;
			funcNames.put(this.objName, counter + 1);
		}
		else if(match3.find()){
			if (this.line.contains("let"))
				splitter = line.split("\\=")[0].split("let")[1].trim();
			else
				splitter = line.split("\\=")[0].split("var")[1].trim();
			this.objName = splitter;
			int counter = funcNames.containsKey(this.objName)? funcNames.get(this.objName) : 0;
			funcNames.put(this.objName, counter + 1);
			if (isFunctMissingBracket(fileName, this.line))
				System.out.println(this.line + " : Expected '{' after " +  " \'=>\' " + "at line " + this.lineNumber );
		}
		else if(match0.find()){
			splitter = match0.group().split("\\(")[0].split("function")[1].trim();
			this.objName = splitter;
			int counter = funcNames.containsKey(this.objName)? funcNames.get(this.objName) : 0;
			funcNames.put(this.objName, counter + 1);
			if (isFunctMissingBracket(fileName, this.line))
					System.out.println(this.line + " : Expected '{' after " + this.objName + " (...) " + "at line " + this.lineNumber );
		}
		else
			return;
	}
	
	
	//...*** static methods
	
	private boolean isFunctMissingBracket(String fileName, String currentLine) throws IOException {
		if (currentLine.split("\\)",2)[1] == null || currentLine.split("\\)",2)[1].isEmpty()){
			if (!(getMultiLineString(fileName, currentLine)).contains("{")){
				// read further lines and see if there is a {. else report missing bracket.
				return true;
			}
		}
		if (currentLine.split("\\)",2)[1].isEmpty())
			return true; 
		
		if(currentLine.split("\\)")[1].contains("{") || currentLine.split("\\)")[1].contains(";")){
			//check for a closing bracket in the following lines
			return false;
		}
		
		if (currentLine.split("\\>",2)[1] == null || currentLine.split("\\>",2)[1].isEmpty()){
			if (!(getMultiLineString(fileName, currentLine)).contains("{")){
				// read further lines and see if there is a {. else report missing bracket.
				return true;
			}
		}
		if (currentLine.split("\\>",2)[1].isEmpty())
			return true; 
		
		if(currentLine.split("\\>")[1].contains("{") || currentLine.split("\\>")[1].contains(";")){
			//check for a closing bracket in the following lines
			return false;
		}
		return false;
	}

	public static void getUndefFunctions(String fileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		int lineNumber = 0;
		while((eachLine = br.readLine()) != null){
			lineNumber += 1;
			Matcher matcher1 = FUNCTION_RX.matcher(eachLine);
			
			while (matcher1.find()){
				String found1 = matcher1.group().split("\\(")[0].trim();
				if (found1.isEmpty()) continue;
				boolean flag = Context.funcNames.containsKey(found1)? true : false;
				if (!flag && (!found1.equals("function")) && (!found1.equals("if"))){
					int counter = Context.undecFuncNames.containsKey(found1) ? Context.undecFuncNames.get(found1) : 0;
					Context.undecFuncNames.put(found1, counter + 1);
					System.out.println(found1 + " : " + "was used before it was defined"  + " at line " + lineNumber);
				}
				else{
					int counter = Context.funcNames.get(found1) != null ? Context.funcNames.get(found1) : 0;
					Context.funcNames.put(found1, counter + 1);
				}
			}
		}
		br.close();
	}
	

	public static boolean findBracketBalance(String fileName) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		int lineNumber = 0;
		boolean balanced = true;
		Stack <Character> stack = new Stack <Character> ();
		
		try{
			while((eachLine = br.readLine()) != null){
				lineNumber += 1;
				 
				for (int i = 0; i < eachLine.length(); i++){
			        char current = eachLine.charAt(i);
			        if (current == '{'){
			            stack.push(current);
			            openBrackets.add(new Context(eachLine, fileName, "{", lineNumber, i));
			            continue;
			        }

			        if (current == '}'){
			        	closedBrackets.add(new Context(eachLine, fileName, "}", lineNumber, i));
			            if (stack.isEmpty()){
			            	System.out.println("Found extra '}' at line " + lineNumber);
			            	balanced = false;
			            	continue;
			            }

			            char last = stack.peek();	
			            if (current == '}' && last == '{'){
			                stack.pop();
			                openBrackets.remove(openBrackets.size() - 1);
			                continue;
			            }
			        }
			    }
			}
			Iterator<Context> iterator = openBrackets.iterator();
			while (!stack.isEmpty()){
				System.out.println("Missing closing bracket corresponding to '{' at line " + iterator.next().lineNumber);
				stack.pop();
				balanced = false;
            }
			
		}
		finally{
			br.close();
		}
		if (balanced) return stack.isEmpty(); 
		else
			return false;
	}
}