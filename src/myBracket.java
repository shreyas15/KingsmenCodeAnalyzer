import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * @author shreyas s bhat
 * 
 */



public class myBracket extends Context{

	

	public static Map<String, Integer> bracketCount = new HashMap<String, Integer>(); 
	public static List <myBracket> openBrackets = new ArrayList <myBracket>();
	public static List <myBracket> closedBrackets = new ArrayList <myBracket>();

	public myBracket (String line, String fileName, String objName, int lineNum, int colNum){
		this._line = line;
		this._fileName = fileName;
		this._objName = objName;
		this._lineNumber = lineNum;
		this._columnNumber = colNum;
	}
	
	public myBracket (String fileName){
		this._fileName = fileName; 
	}

	/***
	  *
	  * void findBracketBalance( )
	  *
	  * Summary of the findBracketBalance function:
	  *
	  *    The findBracketBalance function, scans the input file
	  *    and reports all extra or missing brackets in the file
	  *
	  * Parameters   : fileName: the input file name
	  *
	  * Return Value : true : brackets are balanced
	  * 			   false: brackets are un-balanced.
	  *
	  * Description:
	  *
	  *    This function utilizes the Stack to keep track of brackets. 
	  *
	  */
	
	public boolean findBracketBalance() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(this._fileName));
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
			            openBrackets.add(new myBracket(eachLine, this._fileName, "{", lineNumber, i));
			            continue;
			        }

			        if (current == '}'){
			        	closedBrackets.add(new myBracket(eachLine, _fileName, "}", lineNumber, i));
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
			Iterator<myBracket> iterator = openBrackets.iterator();
			while (!stack.isEmpty()){
				System.out.println("Missing closing bracket corresponding to '{' at line " + iterator.next()._lineNumber);
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
