package org.sanjose.util;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestDotMatrixPrinter {
	public static String strLine;
	public static String strLine1="";


	public static void main(String[] args) {
	try{
	// Open the file that is the first 
	// command line parameter
	FileInputStream fstream = new FileInputStream("c:\test.txt");
	// Get the object of DataInputStream
	DataInputStream in = new DataInputStream(fstream);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	//Read File Line By Line
	while ((strLine = br.readLine()) != null) {
	// Print the content on the console
	strLine1 = strLine1 + strLine;
	}
	//Close the input stream
	in.close();
	}catch (Exception e){//Catch exception if any
	System.err.println("Error: " + e.getMessage());
	}

	try {
	FileWriter out = new FileWriter("lpt1");
	out.write(strLine1);
	out.write(0x0D); // CR
	out.close();
	}
	catch (IOException e) {
	e.printStackTrace();
	}
	}
}

// class that opens the printer as a file  
// and writes "Hello World" to it  

/*import java.io.*;  
public class lpt {  
    public static void main (String[] argv) {  
        try {  
            FileOutputStream os = new FileOutputStream("LPT1");  
            //wrap stream in "friendly" PrintStream  
            PrintStream ps = new PrintStream(os);  

            //print text here  
            ps.println("Hello world!");  

            //form feed -- this is important  
            //Without the form feed, the text will simply sit  
            // in print buffer until something else gets printed.  
            ps.print("\f");  
            //flush buffer and close  
            ps.close();  
        } catch (Exception e) {  
            System.out.println("Exception occurred: " + e);  
        }  
    }  
} */ 