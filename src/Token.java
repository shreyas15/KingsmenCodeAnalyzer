import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.awt.BufferCapabilities;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
	
	String line;
	String objName;
	int lineNumber;
	int columnNumber;
	String ifOrElse;
	String fileName;
	
	private static final Pattern VAR_BOUNDARY = Pattern.compile("(let|var)(.*?)\\=(.*?)\\;");
	private static final Pattern VAR_BOUNDARY2 = Pattern.compile("(let|var)(.*?)\\;");
	private static final Pattern FUNC_BOUNDARY1 = Pattern.compile("(var)[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\=][\\s]{0,}(function)[\\s]{0,}[\\(][\\s]{0,}[\\)]");
	private static final Pattern FUNC_BOUNDARY2 = Pattern.compile("(var)[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\=][\\s]{0,}[\\{][\\s]{0,}[\\\"][\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\\"][\\s]{0,}[\\:][\\s]{0,}(function)[\\s]{0,}[\\(][\\s]{0,}[\\)][\\s]{0,}[\\{]");
	private static final Pattern FUNC_BOUNDARY3 = Pattern.compile("(set)[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\(][\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\)][\\s]{0,}[\\{]");
	private static final Pattern FUNC_BOUNDARY4 = Pattern.compile("([a-zA-Z_$][a-zA-Z0-9_$]*)[\\.]([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\(][\\s]{0,}[\\)][\\s]{0,}[\\;]");
	private static final Pattern IF_BOUNDARY = Pattern.compile("[\\t]{0,}[\\s]{0,}(if)[\\s]{0,}[\\(]{1,}[\\!]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\)]{1,}[\\n]{0,}[\\t]{0,}");
	private static final Pattern IF_NOT = Pattern.compile("[\\t]{0,}[\\s]{0,}(if)[\\s]{0,}[\\(]{1,}[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\)]{1,}[\\n]{0,}[\\t]{0,}[\\{]");
	private static final Pattern ELSE_BOUNDARY = Pattern.compile("[\\t]{0,}[\\}]{0,}[\\t]{0,}[\\s]{0,}(else)[\\s]{0,}[\\n]{0,}[\\t]{0,}");
	private static final Pattern ELSE_NOT = Pattern.compile("[\\t]{0,}[\\}]{0,}[\\t]{0,}[\\s]{0,}(else)[\\s]{0,}[\\n]{0,}[\\t]{0,}([a-zA-Z_$][a-zA-Z0-9_$])*[\\s]{0,}[\\n]{0,}[\\t]{0,}[\\{]");
	
	public static Map<String, Integer> varNames = new HashMap<String, Integer>(); 
	public static Map<String, Integer> funcNames = new HashMap<String, Integer>(); 
	
	public Map<String, Integer> getMap() {
	       return varNames;
	 }
	
	public Token (){
		this.line = "";
		this.objName = "";
		this.lineNumber = 0;
		this.columnNumber = 0;
		this.ifOrElse = "";
		this.fileName = "";
	}
	
	public Token (String line, int lineNum){
		this.line = line;
		this.objName = "";
		this.lineNumber = lineNum;
		this.columnNumber = 0;
	}
	
	public Token (String line, String fileName, int lineNum){
		this.line = line;
		this.fileName = fileName;
		this.objName = "";
		this.lineNumber = lineNum;
		this.columnNumber = 0;
	}
	
	public void getVarReport(){
		boolean flag = varNames.containsKey(this.objName) ? (varNames.get(this.objName) == 2) ? true : false : false;
		if (flag)
			System.out.println(this.objName + " : " + "is an unused variable"  + " at line " + this.lineNumber);
		else return;
	}
	
	public void getIfElseReport() {
		if (this.ifOrElse.equals("if"))
			System.out.println(this.line.trim() + " : " + "possibly a one line if"  + " at line " + this.lineNumber);
		else if(this.ifOrElse.equals("else"))
			System.out.println(this.line.trim() + " : " + "possibly a one line else"  + " at line " + this.lineNumber);
		else return;
	}
	
	public void getFuncReport() {
		boolean flag = funcNames.containsKey(this.objName) ? (funcNames.get(this.objName) == 2) ? true : false : false;
		if (flag)
			System.out.println(this.objName + " : " + "is an undeclared function"  + " at line " + this.lineNumber);
		else return;		
	}

	public void registerVariables(){
		Matcher matcher = VAR_BOUNDARY.matcher(this.line);
		Matcher matcher2 = VAR_BOUNDARY2.matcher(this.line);
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
		Matcher matcher1 = IF_BOUNDARY.matcher(multiLine);
		Matcher matcher2 = ELSE_BOUNDARY.matcher(multiLine);
		Matcher matcher3 = IF_NOT.matcher(multiLine);
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

	
	public void registerFunctions() {
		// TODO Auto-generated method stub
		
	}

}

