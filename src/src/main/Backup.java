package main;

import gui.Mainframe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.text.*;

public class Backup {
	File sourceFile;
	File destinationFile;
	Controller controller;
	
	public Backup(Controller c, String source, String destination){
		this.controller = c;
		this.sourceFile = new File(source);
		this.destinationFile = new File(destination);
	}
	
	public void runBackup() throws FileNotFoundException, IOException {
		// Ordnername mit Datum festlegen:
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setTimeZone(TimeZone.getDefault());
		String backupDir = destinationFile.getAbsolutePath() + "/" + df.format(date);
		
		File dir = new File(backupDir);
		// Backup-Ordner anlegen:
		if (dir.mkdir()) {
			System.out.println("Backup-Ordner erfolgreich erstellt!");
		} else {
			System.out.println("Fehler beim erstellen des Backup-Ordners");
		}
		
		String folder = dir + "/" + sourceFile.getName();
		File f = new File(folder);
		
		if (f.mkdir()) {
			System.out.println("Ordner erfolgreich erstellt!");
		} else {
			System.out.println("Fehler beim erstellen des Ordners");
		}
		// Eigentlicher Kopiervorgang:
		copyDirectory(sourceFile, f);
	}
	
	/**
	 * Kopiert ein Verzeichnis rekursiv.
	 * @param source Quellverzeichnis
	 * @param destination Zielverzeichnis
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void copyDirectory(File source, File destination) throws FileNotFoundException, IOException {
		File[] files = source.listFiles();File newFile = null;
		
		destination.mkdirs();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				newFile = new File(destination.getAbsolutePath() + System.getProperty("file.separator") + files[i].getName());
				if (files[i].isDirectory()) {
					copyDirectory(files[i], newFile);
				} else {
					copyFile(files[i], newFile);
				}
			}
		}
	}
	
	public void copyFile(File source, File destination) throws FileNotFoundException, IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destination, true));
		int bytes = 0;
		while ((bytes = in.read()) != -1) {
			out.write(bytes);
		}
		in.close();
		out.close();
		
		String fileWithPath = source.getPath() + "/" + source.getName();
		controller.printOut(fileWithPath);
	}
}
