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



public class myVariable extends Context{
	
	protected static Map<String, Integer> varNames = new HashMap<String, Integer>(); 
	
	public myVariable(String line, int lineNumber) {
		_line = line;
		_lineNumber = lineNumber;
	}


	public Map<String, Integer> getVariableMap() {
	       return varNames;
	 }
	
	/***
	  *
	  * void getVarReport( )
	  *
	  * Summary of the getVarReport function:
	  *
	  *    The getVarReport function, simply reports the unused variables from the varNames HashMap. 
	  *
	  * Parameters   : None
	  *
	  * Return Value : None 
	  *
	  */

	public void getVarReport(){
		boolean flag = varNames.containsKey(this._objName) ? (varNames.get(this._objName) == 2) ? true : false : false;
		if (flag)
			System.out.println(this._objName + " : " + "is an unused variable"  + " at line " + this._lineNumber);
		else return;
	}
	
	
	/***
	  *
	  * void registerVariables( )
	  *
	  * Summary of the registerVariables function:
	  *
	  *    The registerVariables function, scans the input file
	  *    and registers all the variables declarations when they are declared in the varNames hashmap.
	  *
	  * Parameters   : None
	  *
	  * Return Value : None
	  *
	  */
	
	public void registerVariables(){
		Matcher matcher = VAR_ASN_RX.matcher(this._line);
		Matcher matcher2 = VAR_DEC_RX.matcher(this._line);
		String splitter = "";
		
		if (matcher.matches()){
			splitter = _line.split("\\=")[0];
			if (splitter.contains("let"))
			{
				this._objName = splitter.split("(let)")[1].trim();
			}
			else if (splitter.contains("var")){
				this._objName = splitter.split("(var)")[1].trim();
			}
			int counter = varNames.containsKey(this._objName)? varNames.get(this._objName) : 0;
			varNames.put(this._objName, counter + 1);
		}
		else if (matcher2.matches()){
			splitter = _line.split("\\;")[0];
			if (splitter.contains("let"))
			{
				this._objName = splitter.split("(let)")[1].trim();
			}
			else if (splitter.contains("var")){
				this._objName = splitter.split("(var)")[1].trim();
			}
			int counter = varNames.containsKey(this._objName)? varNames.get(this._objName) : 0;
			varNames.put(this._objName, counter + 1);
		}
	}
	

	/***
	  *
	  * void updateVarMap( )
	  *
	  * Summary of the updateVarMap function:
	  *
	  *    The updateVarMap function, scans the input file
	  *    and updates varNames when a variable has been used.
	  *    Current implementation doesn't consider scope of the variable. 
	  *    Unused variables will have a count of 2 which the getVarReport checks and reports.
	  *
	  * Parameters   : fileName: the input fileName
	  *
	  * Return Value : None
	  *
	  */
	
	public static void updateVarMap(String fileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String eachLine = "";
		while((eachLine = br.readLine()) != null){
			Matcher matcher = IDENTIFIER_RX.matcher(eachLine);
			while (matcher.find()){
				String found = matcher.group().trim();
				int counter = varNames.containsKey(found)? varNames.get(found) : 0;
				varNames.put(found, counter + 1);
			}
		}
		br.close();
	}
	
}
