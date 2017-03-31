import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.awt.BufferCapabilities;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Context {
	
	String line;
	String objName;
	int lineNumber;
	int columnNumber;
	String ifOrElse;
	String fileName;
	
	/***
	 * 
	 * REGEX for various pattern detection.
	 * 
	 */
	//general Identifiers.
	private static final Pattern IDENTIFIER_RX = Pattern.compile("([a-zA-Z_$][a-zA-Z0-9_$]*)"); 
	//variable with assignment
	private static final Pattern VAR_ASN_RX = Pattern.compile("(let|var)(.*?)\\=(.*?)\\;");
	//variable declared
	private static final Pattern VAR_DEC_RX = Pattern.compile("(let|var)(.*?)\\;");
	//typical function declaration
	private static final Pattern FUNCTION_RX = Pattern.compile("([a-zA-Z_$][a-zA-Z0-9_$]*)[\\(][\\s]{0,}[\\)]");
	//Anonymous functions like: var doSomethingFunction = function () { ... };
	private static final Pattern FUNC_BOUNDARY1 = Pattern.compile("(var)[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\=][\\s]{0,}(function)[\\s]{0,}[\\(][\\s]{0,}[\\)]"); 
	//function expressions with names like: var tool = {"doSomething": function () { ... }};
	private static final Pattern FUNC_BOUNDARY2 = Pattern.compile("(var)[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\=][\\s]{0,}[\\{]{0,}[\\s]{0,}[\\\"]{0,}[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\\"]{0,}[\\s]{0,}[\\:][\\s]{0,}(function)[\\s]{0,}[\\(][\\s]{0,}[\\)][\\s]{0,}[\\{]"); 
	//function declarations like: let doSomething = function() {
	private static final Pattern FUNC_BOUNDARY3 = Pattern.compile("(let)[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\(][\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\)][\\s]{0,}[\\{]");  
	//function calls like: mustang.getEngineType();
	private static final Pattern FUNC_BOUNDARY4 = Pattern.compile("[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\:][\\s]{0,}(function)[\\s]{0,}[\\(][\\s]{0,}[\\)][\\s]{0,}[\\{]"); 
	//single line IF
	private static final Pattern IF_RX = Pattern.compile("[\\t]{0,}[\\s]{0,}(if)[\\s]{0,}[\\(]{1,}[\\!]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\)]{1,}[\\n]{0,}[\\t]{0,}");
	//if with trailing curly bracket
	private static final Pattern IF_NOT_RX = Pattern.compile("[\\t]{0,}[\\s]{0,}(if)[\\s]{0,}[\\(]{1,}[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\)]{1,}[\\n]{0,}[\\t]{0,}[\\{]");
	//single line else
	private static final Pattern ELSE_BOUNDARY = Pattern.compile("[\\t]{0,}[\\}]{0,}[\\t]{0,}[\\s]{0,}(else)[\\s]{0,}[\\n]{0,}[\\t]{0,}");
	//else with trailing curly bracket
	private static final Pattern ELSE_NOT = Pattern.compile("[\\t]{0,}[\\}]{0,}[\\t]{0,}[\\s]{0,}(else)[\\s]{0,}[\\n]{0,}[\\t]{0,}([a-zA-Z_$][a-zA-Z0-9_$])*[\\s]{0,}[\\n]{0,}[\\t]{0,}[\\{]");
	
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
	
	private void getIfElseReport() {
		if (this.ifOrElse.equals("if"))
			System.out.println(this.line.trim() + " : " + "possibly a one line if"  + " at line " + this.lineNumber);
		else if(this.ifOrElse.equals("else"))
			System.out.println(this.line.trim() + " : " + "possibly a one line else"  + " at line " + this.lineNumber);
		else return;
	}

	private String getMultiLineString(String fileName2, String line2) throws IOException {
		
		BufferedReader bufferreader = new BufferedReader(new FileReader(fileName2));
		StringBuilder stringbuilder = new StringBuilder();
		String line;
		boolean fromLine = false;
		
		try{
			while((line = bufferreader.readLine()) != null){
				if (line.contains(line2)){
					fromLine = true;
				}
				int count = 2;
				while (line != null && fromLine == true && count > 0){
					stringbuilder.append(line);
					stringbuilder.append("\n");
					line = bufferreader.readLine();
					count -= 1;
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

	public void registerFunctions() {
		//Matcher match0 = FUNCTION_RX.matcher(this.line);
		Matcher match1 = FUNC_BOUNDARY1.matcher(this.line);
		Matcher match2 = FUNC_BOUNDARY2.matcher(this.line);
		Matcher match3 = FUNC_BOUNDARY3.matcher(this.line);
		Matcher match4 = FUNC_BOUNDARY4.matcher(this.line);
		String splitter = "";
		
		if(match1.find()){
			splitter = line.split("\\=")[0].split("var")[1].trim();
			this.objName = splitter;
			int counter = funcNames.containsKey(this.objName)? funcNames.get(this.objName) : 0;
			funcNames.put(this.objName, counter + 1);
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
			splitter = line.split("\\=")[0].split("let")[1].trim();
			this.objName = splitter;
			int counter = funcNames.containsKey(this.objName)? funcNames.get(this.objName) : 0;
			funcNames.put(this.objName, counter + 1);
		}
//		else if(match0.find()){
//			splitter = match0.group().split("\\(")[0].trim();
//			this.objName = splitter;
//			int counter = funcNames.containsKey(this.objName)? funcNames.get(this.objName) : 0;
//			funcNames.put(this.objName, counter + 1);
//		}
		else
			return;
	}
	
	//...*** static methods
	
	public static void getUndefFunctions(String fileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		int lineNumber = 0;
		while((eachLine = br.readLine()) != null){
			lineNumber += 1;
			Matcher matcher = FUNCTION_RX.matcher(eachLine);
			while (matcher.find()){
				String found = matcher.group().split("\\(")[0].trim();
				boolean flag = Context.funcNames.containsKey(found)? true : false;
				if (!flag && (!found.equals("function"))){
					int counter = Context.undecFuncNames.containsKey(found) ? Context.undecFuncNames.get(found) : 0;
					Context.undecFuncNames.put(found, counter + 1);
					System.out.println(found + " : " + "is possibly an undeclared function"  + " at line " + lineNumber);
				}
				else{
					int counter = Context.funcNames.get(found) != null ? Context.funcNames.get(found) : 0;
					Context.funcNames.put(found, counter + 1);
				}
			}
		}
		br.close();
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

	public static boolean findBracketBalance(String fileName) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		int lineNumber = 0;
		Stack <Character> stack = new Stack <Character> ();
		try{
			while((eachLine = br.readLine()) != null){
				lineNumber += 1;
				for (int i = 0; i < eachLine.length(); i++){
			        char current = eachLine.charAt(i);
			        if (current == '{'){
			            stack.push(current);
			            openBrackets.add(new Context(eachLine, fileName, "{", lineNumber, i));
			        }


			        if (current == '}'){
			        	closedBrackets.add(new Context(eachLine, fileName, "}", lineNumber, i));
			            if (stack.isEmpty()){
			            	System.out.println("Extra } at line " + lineNumber + " and position " + i);
			            	return false;
			            }

			            char last = stack.peek();	
			            if (current == '}' && last == '{')
			                stack.pop();
			            else 
			                return false;
			        }
			    }
			}
		}
		finally{
			br.close();
		}
		return stack.isEmpty();	
	}
}

