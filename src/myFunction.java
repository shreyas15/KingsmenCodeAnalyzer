import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;


/**
 * @author shreyas s bhat
 *
 */


public class myFunction extends Context {
	

	public static Map<String, Integer> funcNames = new HashMap<String, Integer>(); 
	public static Map<String, Integer> undecFuncNames = new HashMap<String, Integer>(); 
	
	
	public Map<String, Integer> getDeclaredFunctions() {
	       return funcNames;
	 }
	
	public Map<String, Integer> getUndeclaredFunctions() {
	       return undecFuncNames;
	 }

	public myFunction (String line, int lineNum){
		this._line = line;
		this._lineNumber = lineNum;
	}

	
	
	/***
	  *
	  * void registerFunctions( )
	  *
	  * Summary of the registerFunctions function:
	  *
	  *    The registerFunctions function, scans the input file
	  *    and registers all types of function declarations. 
	  *    Also calls isFunctMissingBracket function to determine if the have a bracket missing. 
	  *
	  * Parameters   : fileName: input file name
	  *
	  * Return Value : None
	  *
	  */
	
	public void registerFunctions(String fileName) throws IOException {
		Matcher match0 = DEC_FUNC_RX.matcher(this._line);
		Matcher match1 = FUNC_BOUNDARY1.matcher(this._line);
		Matcher match2 = FUNC_BOUNDARY2.matcher(this._line);
		Matcher match3 = FUNC_BOUNDARY3.matcher(this._line);
		Matcher match4 = FUNC_BOUNDARY4.matcher(this._line);
		String splitter = "";
		
		if(match1.find()){
			if (this._line.contains("var"))
				splitter = _line.split("\\=")[0].split("var")[1].trim();
			else
				splitter = _line.split("\\=")[0].split("let")[1].trim();
			this._objName = splitter;
			int counter = funcNames.containsKey(this._objName)? funcNames.get(this._objName) : 0;
			funcNames.put(this._objName, counter + 1);
			if (isFunctMissingBracket(fileName, this._line))
				System.out.println(this._line + " : Expected '{' after " + this._objName + " (...) " + "at line " + this._lineNumber);
		}
		else if(match2.find()){
			splitter = _line.split("\"",3)[1].trim();
			this._objName = splitter;
			int counter = funcNames.containsKey(this._objName)? funcNames.get(this._objName) : 0;
			funcNames.put(this._objName, counter + 1);
		}
		else if(match4.find()){
			splitter = _line.split(":")[0].trim();
			this._objName = splitter;
			int counter = funcNames.containsKey(this._objName)? funcNames.get(this._objName) : 0;
			funcNames.put(this._objName, counter + 1);
		}
		else if(match3.find()){
			if (this._line.contains("let"))
				splitter = _line.split("\\=")[0].split("let")[1].trim();
			else
				splitter = _line.split("\\=")[0].split("var")[1].trim();
			this._objName = splitter;
			int counter = funcNames.containsKey(this._objName)? funcNames.get(this._objName) : 0;
			funcNames.put(this._objName, counter + 1);
			if (isFunctMissingBracket(fileName, this._line))
				System.out.println(this._line + " : Expected '{' after " +  " \'=>\' " + "at line " + this._lineNumber );
		}
		else if(match0.find()){
			splitter = match0.group().split("\\(")[0].split("function")[1].trim();
			this._objName = splitter;
			int counter = funcNames.containsKey(this._objName)? funcNames.get(this._objName) : 0;
			funcNames.put(this._objName, counter + 1);
			if (isFunctMissingBracket(fileName, this._line))
					System.out.println(this._line + " : Expected '{' after " + this._objName + " (...) " + "at line " + this._lineNumber );
		}
		else
			return;
	}
		

	/***
	  *
	  * void isFunctMissingBracket( )
	  *
	  * Summary of the isFunctMissingBracket function:
	  *
	  *    The isFunctMissingBracket function, scans the current line
	  *    and the line following after it to see if it is missing a '{' 
	  *    and reports true or false.
	  *
	  * Parameters   : fileName: The input fileName
	  * 			   currentLine: the line bearing function definition
	  *
	  * Return Value : true / false 
	  *
	  */
	
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


	/***
	  *
	  * void getUndefFunctions( )
	  *
	  * Summary of the getUndefFunctions function:
	  *
	  *    The getUndefFunctions function, scans the input file
	  *    and finds all function calls to determine if the function
	  *    was used before it was declared. 
	  *
	  * Parameters   : fileName: input file name
	  *
	  * Return Value : None
	  *
	  */
	
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
				boolean flag = funcNames.containsKey(found1)? true : false;
				if (!flag && (!found1.equals("function")) && (!found1.equals("if")) && (!found1.equals("return"))){
					int counter = undecFuncNames.containsKey(found1) ? undecFuncNames.get(found1) : 0;
					undecFuncNames.put(found1, counter + 1);
					System.out.println(found1 + " : " + "was used before it was defined"  + " at line " + lineNumber);
				}
				else{
					int counter = funcNames.get(found1) != null ? funcNames.get(found1) : 0;
					funcNames.put(found1, counter + 1);
				}
			}
		}
		br.close();
	}
	
	
}
