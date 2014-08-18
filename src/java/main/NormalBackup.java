package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.text.*;

public class NormalBackup implements Backupable {
	
	private ArrayList<String> sourcePaths;
	private String destinationPath;
	private Controller controller;
	
	
	/**
	 * Backup-Objekt zur Datensicherung.
	 * @param c Controller
	 * @param source Quellpfade
	 * @param destination Zielpfad
	 */
	public NormalBackup(Controller c, ArrayList<String> sources, String destination) {
		this.controller = c;
		this.sourcePaths = sources;
		this.destinationPath = destination;
	}
	
	/**
	 * Startet den Backup-Vorgang.
	 * @param taskName Name des Backup-Tasks welcher ausgeführt wird
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void runBackup(String taskName) throws FileNotFoundException, IOException {
		// Ordnername mit Datum festlegen:
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH-mm");
		df.setTimeZone(TimeZone.getDefault());
		
		File destinationFile = new File(destinationPath);
		String backupDir = destinationFile.getAbsolutePath() + "/" + taskName + "_" + df.format(date);
		
		File dir = new File(backupDir);
		// Backup-Ordner anlegen:
		if (dir.mkdir()) {
			System.out.println("Backup-Ordner erfolgreich erstellt!");
		} else {
			System.out.println("Fehler beim erstellen des Backup-Ordners");
			return;
		}
		
		for (int i = 0; i < sourcePaths.size(); i++) {
			File sourceFile = new File(sourcePaths.get(i));
			
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
	}
	
	/**
	 * Kopiert ein Verzeichnis rekursiv.
	 * @param source Quellverzeichnis
	 * @param destination Zielverzeichnis
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void copyDirectory(File source, File destination) throws FileNotFoundException, IOException {
		File[] files = source.listFiles();
		File newFile = null;
		
		destination.mkdirs();
		
		String output = "Verzeichnis " + destination.getAbsolutePath() + " erstellt";
		controller.printOut(output);
		
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
	
	/**
	 * Kopiet eine Datei vom angegebenen Quellpfad zum angegebenen Zielpfad.
	 * @param source Quellpfad
	 * @param destination Zielpfad
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void copyFile(File source, File destination) throws FileNotFoundException, IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destination, true));
		int bytes = 0;
		while ((bytes = in.read()) != -1) {
			out.write(bytes);
		}
		in.close();
		out.close();
		
		String output = "Datei " + source.getPath() + "/" + source.getName() + " nach " + destination.getPath() + "/" + destination.getName() + " kopiert";
		controller.printOut(output);
	}
}
