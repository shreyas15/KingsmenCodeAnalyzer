import java.io.IOException;
import java.util.regex.Matcher;


/**
 * @author shreyas s bhat
 *
 */


public class myLogicStatement extends Context{
	

	public myLogicStatement (String line, String fileName, int lineNum){
		this._line = line;
		this._fileName = fileName;
		this._lineNumber = lineNum;
	}
	
	
	/***
	  *
	  * void findIfElse( )
	  *
	  * Summary of the findIfElse function:
	  *
	  *    The findIfElse function, scans the input file
	  *    and finds all single line if and else usages. 
	  *
	  * Parameters   : None
	  *
	  * Return Value : None
	  * 
	  */
	
	public void findIfElse() throws IOException {
		
		String multiLine = getMultiLineString(this._fileName, this._line);
		Matcher matcher1 = IF_RX.matcher(multiLine);
		Matcher matcher2 = ELSE_BOUNDARY.matcher(multiLine);
		Matcher matcher3 = IF_NOT_RX.matcher(multiLine);
		Matcher matcher4 = ELSE_NOT.matcher(multiLine);
		
		if(matcher1.find()){
			if (!matcher3.find()){
				this._ifOrElse = "if";
				getIfElseReport();
			}
		}
		else if(matcher2.find()){
			if (!matcher4.find()){
				this._ifOrElse = "else";
				getIfElseReport();
			}
		}
		else return;
	}
	
	
	/***
	  *
	  * void getIfElseReport( )
	  *
	  * Summary of the getIfElseReport function:
	  *
	  *    The getIfElseReport function, reports 
	  *    where single line if / else have been used.
	  *
	  * Parameters   : None
	  *
	  * Return Value : None
	  * 
	  */
	
	private void getIfElseReport() {
		if (this._ifOrElse.equals("if"))
			System.out.println(this._line.trim() + " : " + "is a one line if"  + " at line " + this._lineNumber);
		else if(this._ifOrElse.equals("else"))
			System.out.println(this._line.trim() + " : " + "is a one line else"  + " at line " + this._lineNumber);
		else return;
	}

	
}
