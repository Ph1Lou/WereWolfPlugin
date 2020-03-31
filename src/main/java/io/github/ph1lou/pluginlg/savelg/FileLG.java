package io.github.ph1lou.pluginlg.savelg;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class FileLG {
	
	public void createFile (File file) throws IOException {
		
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
			System.out.println("[pluginLG] Create "+file.getName());
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

	public void copy(InputStream source , String destination) {

		System.out.println("[pluginLG] Copying ->" + source + "\n\tto ->" + destination);
		try {
			createFile(new File(destination));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(source!=null){
			try {
				Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String loadContent(File file) {
		
		if(file.exists()) {
			
			try {

				final BufferedReader reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), StandardCharsets.UTF_8));
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

