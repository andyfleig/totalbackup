package main;

import gui.About;
import gui.Mainframe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.*;
import java.text.*;

public class Backup {
	File sourceFile;
	File destinationFile;
	
	public Backup(String source, String destination){
		this.sourceFile = new File(source);
		this.destinationFile = new File(destination);
	}
	
	public void runBackup() throws FileNotFoundException, IOException {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setTimeZone(TimeZone.getDefault());
		
		
		String backupDir = destinationFile.getAbsolutePath() + "/" + df.format(date);
		
		File dir = new File(backupDir);
		
		if (dir.mkdir()) {
			System.out.println("Ordner erfolgreich erstellt!");
		} else {
			System.out.println("Fehler beim erstellen des Ordners");
		}
	}
}
