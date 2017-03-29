import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
	
	String line = "";
	String objName = "";
	int lineNumber = 0;
	int columnNumber = 0;
	String ifOrElse = "";
	
	private static final Pattern VAR_BOUNDARY = Pattern.compile("(let|var)(.*?)\\=(.*?)\\;");
	private static final Pattern VAR_BOUNDARY2 = Pattern.compile("(let|var)(.*?)\\;");
	private static final Pattern IF_BOUNDARY = Pattern.compile("(if)[\\s]*[\\(]*(.*?){1,}[\\)]{1,}[\\s]{0,}");
	private static final Pattern IF_NOT = Pattern.compile("(if)[\\s]*[\\(]*(.*?){1,}[\\)]{1,}[\\s]{0,}[\\{]");
	private static final Pattern ELSE_BOUNDARY = Pattern.compile("(else)[\\s]*(.*?){1,}[\\s]{0,}");
	private static final Pattern ELSE_NOT = Pattern.compile("(else)[\\s]*(.*?){1,}[\\s]{0,}[\\{]");
	
	public static Map<String, Integer> varNames = new HashMap<String, Integer>(); 
	
	 public Map<String, Integer> getMap() {
	       return varNames;
	 }
	
	public Token (){
		this.line = "";
		this.objName = "";
		this.lineNumber = 0;
		this.columnNumber = 0;
	}
	
	public Token (String line, int lineNum){
		this.line = line;
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
			System.out.println(this.line + " : " + "possibly a one line if"  + " at line " + this.lineNumber);
		else if(this.ifOrElse.equals("else"))
			System.out.println(this.line + " : " + "possibly a one line else"  + " at line " + this.lineNumber);
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

	
	public void findIfElse() {
		Matcher matcher1 = IF_BOUNDARY.matcher(this.line);
		Matcher matcher2 = ELSE_BOUNDARY.matcher(this.line);
		Matcher matcher3 = IF_NOT.matcher(this.line);
		Matcher matcher4 = ELSE_NOT.matcher(this.line);
		
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


	
}

