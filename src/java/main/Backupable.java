package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public interface Backupable {
	
	
	public void runBackup(String taskName) throws FileNotFoundException, IOException;

}
