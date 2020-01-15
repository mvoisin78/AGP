package persistence.lucene;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {
	
	public Boolean createFile(String name) {
		Boolean isCreated = false;
		try {
			File fileToCreate = new File(name);
			
			if (fileToCreate.createNewFile()) {
				isCreated = true;
			} else {
				isCreated = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			isCreated = false;
		}
		
		return isCreated;
	}
	
	public Boolean deleteFile(String name) {
		Boolean isDeleted = false;
		File fileToDelete = new File(name);
		if (fileToDelete.delete()) { 
			isDeleted = true;
		} else {
			isDeleted = false;
		} 
		
		return isDeleted;
	}
	
	public Boolean writeInFile(String name, String textToWrite) {
		Boolean isModified = false;
		
		if(textToWrite.length() > 0) {
			try {
				FileWriter myWriter = new FileWriter(name);
				myWriter.write(textToWrite);
				myWriter.close();
				
				isModified = true;
			} catch (IOException e) {
				isModified = false;
				e.printStackTrace();
			}
		}
		
		return isModified;
	}
}
