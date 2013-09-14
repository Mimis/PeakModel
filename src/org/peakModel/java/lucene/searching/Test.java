package org.peakModel.java.lucene.searching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.peakModel.java.peakModel.LanguageModel;
import org.peakModel.java.peakModel.NGram;
import org.peakModel.java.utils.Helper;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		String[] all = new String[12];
		String fileToRead = "/Users/mimis/Desktop/data.txt";
		
		int i=0;
		File file = new File(fileToRead);
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					if (line.length() > 0 && !line.equals("###########")){
						if(all[i]== null)
							all[i++] = line.trim().replaceAll("\\n", "");
						else
							all[i++] += "\t&\t" + line.trim().replaceAll("\\n", "");
					}else{
						i=0;
					}
				}
			} finally {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		
	for(String s:all)	
		System.out.println(s+" \\\\ ");
		
	}
}
