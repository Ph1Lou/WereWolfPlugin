package io.github.ph1lou.pluginlg.savelg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class FileLG {
	
	public void createFile (File file) throws IOException {
		
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
	}

	public void save (File file, String text) {
		
		final FileWriter fw;
		
		try {
			createFile(file);
			
			fw = new FileWriter(file);
			
			fw.write(text);
			
			fw.flush();
			
			fw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public String loadContent(File file) {
		
		if(file.exists()) {
			
			try {
				
				final BufferedReader reader = new BufferedReader(new FileReader(file));
				final StringBuilder text = new StringBuilder();
				String line;
				
				while ((line=reader.readLine()) !=null) {
					text.append(line);
				}
				reader.close();
				
				return text.toString();
				
				
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
}

