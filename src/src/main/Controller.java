package main;

import gui.Mainframe;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;

public class Controller {

	private Mainframe mainframe;
	private int numberOfSources = 0;
	private ProgramState programState = ProgramState.getInstance(this);
	private static final String PROPERTIES_PATH = "./properties.properties";

	/**
	 * Startet und initialisiert den Controller.
	 */
	public void startController() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				mainframe = new Mainframe(Controller.this);
				mainframe.frmTotalbackup.setVisible(true);
				loadProps();
			}
		});
	}

	/**
	 * Läd die Programmeinstellungen aus der Properties-Datei und wendet sie an.
	 */
	public void loadProps() {
		programState.loadProperties(PROPERTIES_PATH);
	}

	/**
	 * Speichert die Programmeinstellungen in die Properties-Datei.
	 */
	public void saveProbs() {
		programState.saveProperties(PROPERTIES_PATH);
	}

	/**
	 * Startet ein "normales" Backup.
	 * @param source Quellpfad
	 * @param destination Zielpfad
	 */
	public void startBackup(String source, String destination) {
		Backup backup = new Backup(this, source, destination);
		try {
			backup.runBackup();
		} catch (FileNotFoundException ex) {
			System.out.println("Datei existiert nicht!");
		} catch (IOException ex) {
			System.out.println("IO-Fehler!");
		}
	}

	/**
	 * Gibt den übergebenen String auf dem Output-Panel aus.
	 * @param s auszugebender String
	 */
	public void printOut(String s) {
		mainframe.addToOutput(s);
	}
	
	/**
	 * Gibt den Zielpfad als String zurück.
	 * @return Zielpfad
	 */
	public String getDestinationPath() {
		return mainframe.getDestPath();
	}
	
	/**
	 * Setzt den Zielpfad auf den übergebenen String.
	 * @param path zu setzender Zielpfad
	 */
	public void setDestinationPath(String path) {
		mainframe.setDestPath(path);
	}
	
	/**
	 * Gibt Anzahl der eingetragenen Quellverzeichnisse zurück.
	 * @return Anzahl der Quellverzeichnisse.
	 */
	public int getNumberOfSources() {
		return numberOfSources;
	}
	
	/**
	 * Legt die Anzahl der Quellverzeichnisse auf den übergebenen Wert.
	 * @param number zu setzende Anzahl der Quellverzeichnisse
	 */
	public void setNumberOfSources(int number) {
		this.numberOfSources = number;
	}
	
	/**
	 * Gibt alle Quellpfade als ArrayList zurück.
	 * @return Quellpfade
	 */
	public ArrayList<String> getSourcePaths() {
		return mainframe.getSourcePaths();
	}
	
	/**
	 * Legt die Quellpfade auf die übergebenen Pfade fest. Alte Quellpfade werden dabei überschrieben.
	 * @param sourcePaths zu setzende Quellpfade
	 */
	public void setSourcePaths(ArrayList<String> sourcePaths) {
		mainframe.setSourcePaths(sourcePaths);
		numberOfSources = sourcePaths.size();
	}
}
