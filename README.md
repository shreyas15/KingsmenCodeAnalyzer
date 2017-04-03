# KingsmenCodeAnalyzer
Coding Challenge

The program has been written using Java. It is a command line program that accepts as a command line argument which is the name of a JavaScript file. The code is parsed the fsdollowing items are searched for and warned for anomalies: 
1) Declared variables that are not used in the code and report:
o	The name of the variable
o	The line number the variable is declared on 

2) One line if/else statements that don’t have curly brackets and report:
o	If it is an ‘if’ or an ‘else’ block
o	The line number the if/else block is on

3) Function calls that have not been declared and report:

o	The line number the function call happens on
o	The name of the function called

4) Find any missing/extra curly brackets throughout the code and report:
    o	If it is a missing or an extra curly bracket
	If it is a missing curly bracket
    •	The line number and statement that started the opening curly bracket
	If it is an extra curly bracket
    •	The line number the extra bracket is on
    

## Installation

Anybody who wants to download and play with the code:

    your_git_directory% git clone https://github.com/shreyas15/KingsmenCodeAnalyzer.git
    
The code can then me compiled and executed using command line arguments or using an IDE such as Eclipse.

The program takes one argument from the command line - the name of the javascript file. 

For command line execution: 
1) From the Command Prompt, navigate to the directory containing .java files, say C:\KingsmenCodeAnalyzer\ , by typing the cd command below.

        C:\Users\username>cd c:\KingsmenCodeAnalyzer\

2) type the javac command below to compile it.

        \>javac src\*.java

3) Use java command to execute your program. From the Command Prompt, type the java command below.

        C:\KingsmenCodeAnalyzer\src\>java Driver <javascript_file_name.js>


## Usage

This would primarily be used for source code analysis and automated testing of source code for the purpose of debugging and best programming practices. 

## License

MIT License
Copyright (c) 2017 Shreyas S. Bhat

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
