package org.peakModel.java.peakModel.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class testPython {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		
        ProcessBuilder builder = new ProcessBuilder("python2.7", "/Users/mimis/Development/EclipseProject/PeakModel/src/org/peakModel/java/peakModel/tests/parsy.py", 
        		"four scores and seven years ago", "years ago");

        builder.redirectErrorStream(true);
        Process p = builder.start();
        InputStream stdout = p.getInputStream();
        BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));

        String line;
        while ((line = reader.readLine ()) != null) {
            System.out.println ("Stdout: " + line);
        }

        
	}

}
