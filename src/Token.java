import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
	
	String line = "";
	int lineNumber = 0;
	int columnNumber = 0;
	String objName = "";
	
	private static final Pattern VAR_BOUNDARY = Pattern.compile("(let|var)(.*?)\\=(.*?)\\;");
	private static final Pattern VAR_BOUNDARY2 = Pattern.compile("(let|var)(.*?)\\;");
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
	
	public void getReport(){

		boolean flag = varNames.containsKey(this.objName) ? (varNames.get(this.objName) == 2) ? true : false : false;
		if (flag)
			System.out.println(this.objName + " : " + "is an unused variable"  + " at line " + this.lineNumber);
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
	
	
}

