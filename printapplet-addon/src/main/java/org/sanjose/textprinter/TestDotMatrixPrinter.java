package org.sanjose.textprinter;


import java.io.*;

public class TestDotMatrixPrinter {
	public static String strLine;
	public static String strLine1 = "";
	private static TextPrinter textPrinter = new ESCPPrinter();


	public static void main(String[] args) {
		try {
			// Open the file that is the first
			// command line parameter
			//FileInputStream fstream = new FileInputStream("c:\\test.txt");
			FileInputStream fstream = new FileInputStream("c:\\dev\\ComprobanteTxt.txt");
			//FileInputStream fstream = new FileInputStream("/tmp/ComprobanteTxt.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			//Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				strLine = textPrinter.convert(strLine);
				strLine1 = strLine1 + strLine + "\r\n";
				//textPrinter.addToBuffer(strLine1);

			}
			//Close the input stream
			in.close();
		} catch (Exception e) {//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		try {
			FileWriter out = new FileWriter("lpt1");
			//FileWriter out = new FileWriter(new File("/tmp/out"));
			//FileWriter out = new FileWriter(new File("c:\\dev\\out.txt"));
			out.write(strLine1);
			out.write("\f");
			//out.write(0x0D); // CR
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}