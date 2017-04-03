/***
 * 
 * This is the Super class for all other classes except the Driver.
 * It defines the context of what we will analyze/ parse.  
 * Contains fields like the objectName, line, lineNumber, ifOrElse flag. 
 * Contains Regular Expressions for various patterns and tokens in the context. 
 * 
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author shreyas s bhat
 *
 */

public class Context {
	
	protected String _line;
	protected String _objName;
	protected String _ifOrElse;
	protected String _fileName;
	protected int _lineNumber;
	protected int _columnNumber;
	
	/***
	 * 
	 * REGEX for various pattern detection. 
	 * The reason why some of the regex are long is because the file is parsed
	 * as one single string with various escape characters. 
	 * 
	 */
	
	//single line comments
	protected static final Pattern SL_COMMENT_RX = Pattern.compile("^(\\/\\*(.*)\\*\\/)|(\\/\\/(.*)$)");
	//general Identifiers.
	protected static final Pattern IDENTIFIER_RX = Pattern.compile("([a-zA-Z_$][a-zA-Z0-9_$]*)"); 
	//variable with assignment
	protected static final Pattern VAR_ASN_RX = Pattern.compile("(let|var)(.*?)\\=(.*?)\\;");
	//variable declared
	protected static final Pattern VAR_DEC_RX = Pattern.compile("(let|var)(.*?)\\;");
	//function declaration for bracket check like function name ( )
	protected static final Pattern DEC_FUNC_RX = Pattern.compile("(function)[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\(]([\\s\\S]*?)[\\)]");
	//any function call with parameters
	protected static final Pattern FUNCTION_RX = Pattern.compile("[a-zA-Z_$][a-zA-Z0-9_$]{1,}[\\s]*[\\(]");
	//any function call without parameters
	protected static final Pattern FUNCTION2_RX = Pattern.compile("([a-zA-Z_$][a-zA-Z0-9_$]{1,}[\\s]{0,}[\\(]{1,}[\\s]{0,}[\\)])");
	//Anonymous functions like: var doSomethingFunction = function ();
	protected static final Pattern FUNC_BOUNDARY1 = Pattern.compile("(let|var)[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\=][\\s]{0,}(function)[\\s]{0,}[\\(]([\\s\\S]*?)[\\)]"); 
	//function expressions with names like: var tool = {"doSomething": function () { ... }};
	protected static final Pattern FUNC_BOUNDARY2 = Pattern.compile("(var|let)[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\=][\\s]{0,}[\\{]{0,}[\\s]{0,}[\\\"]{0,}[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\\"]{0,}[\\s]{0,}[\\:][\\s]{0,}(function)[\\s]{0,}[\\(]([\\s\\S]*?)[\\)][\\s]{0,}[\\{]"); 
	//function declarations like: let doSomething = () => {
	protected static final Pattern FUNC_BOUNDARY3 = Pattern.compile("(let|var)[\\s]{0,}[a-zA-Z_$][a-zA-Z0-9_$]{1,}[\\s]{0,}[\\=][\\s]{0,}[\\(][\\s]{0,}([\\s\\S]*?)[\\s]{0,}[\\)][\\s]{0,}[\\=]{1,}[\\>]");  
	//function calls like: mustang.getEngineType();
	protected static final Pattern FUNC_BOUNDARY4 = Pattern.compile("[\\s]{0,}([a-zA-Z_$][a-zA-Z0-9_$]*)[\\s]{0,}[\\:][\\s]{0,}(function)[\\s]{0,}[\\(][\\s]{0,}[\\)][\\s]{0,}[\\{]"); 
	//single line IF
	protected static final Pattern IF_RX = Pattern.compile("[\\t]{0,}[\\s]{0,}(if)[\\s]{0,}[\\(]{1,}[\\!]{0,}([\\s\\S]*?)[\\)]{1,}[\\s]{0,}[\\n]{0,}[\\t]{0,}");
	//if with trailing curly bracket
	protected static final Pattern IF_NOT_RX = Pattern.compile("[\\t]{0,}[\\s]{0,}(if)[\\s]{0,}[\\(]{1,}[\\!]{0,}([\\s\\S]*?)[\\)]{1,}[\\s]{0,}[\\n]{0,}[\\t]{0,}[\\{]");
	//single line else
	protected static final Pattern ELSE_BOUNDARY = Pattern.compile("[\\t]{0,}[\\}]{0,}[\\t]{0,}[\\s]{0,}(else)[\\s]{0,}[\\n]{0,}[\\t]{0,}");
	//else with trailing curly bracket
	protected static final Pattern ELSE_NOT = Pattern.compile("[\\t]{0,}[\\}]{0,}[\\t]{0,}[\\s]{0,}(else)[\\s]{0,}[\\n]{0,}[\\t]{0,}([\\s\\S]*?)[\\s]{0,}[\\n]{0,}[\\t]{0,}[\\{]");
	

	public Context (){
		this._line = "";
		this._objName = "";
		this._lineNumber = 0;
		this._columnNumber = 0;
		this._ifOrElse = "";
		this._fileName = "";
	}
	
	public Context (String line, int lineNum){
		this._line = line;
		this._objName = "";
		this._lineNumber = lineNum;
		this._columnNumber = 0;
	}
	
	public Context (String line, String fileName, int lineNum){
		this._line = line;
		this._fileName = fileName;
		this._objName = "";
		this._lineNumber = lineNum;
		this._columnNumber = 0;
	}
	
	public Context (String line, String fileName, String objName, int lineNum, int colNum){
		this._line = line;
		this._fileName = fileName;
		this._objName = objName;
		this._lineNumber = lineNum;
		this._columnNumber = colNum;
	}
	

	/***
	 * ********** Additional context features ***********
	 */
	
	/***
	  *
	  * void getMultiLineString( String fileName2, String line2 )
	  *
	  * Summary of the getMultiLineString function:
	  *
	  *    The getMultiLineString function, takes a line
	  *    integers from highest to lowest
	  *
	  * Parameters   : fileName2: The input file name
	  * 			   line2	: The starting line from which you need to aggregate non-empty lines
	  *
	  * Return Value : String : Return an aggregated string value
	  *
	  * Description:
	  *
	  *    This function utilizes the StringBuilder to create a new string. 
	  *    fromLine is used to flag the starting line from which we aggregate our string from.
	  *
	  */
	
	//...*** private methods
	
	
	protected static String getMultiLineString(String fileName2, String line2) throws IOException {
		
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


	}