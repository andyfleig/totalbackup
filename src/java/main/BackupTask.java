package main;

import java.io.Serializable;

import java.util.ArrayList;

/**
 * Eine Backup-Aufgabe.
 * 
 * @author andy
 *
 */
public class BackupTask implements Serializable {

	/**
	 * Versionsnummer für die Seriallisierung.
	 */
	private static final long serialVersionUID = 1L;
	private String taskName;
	private ArrayList<String> sourcePaths;
	private String destinationPath;
	private int backupMode;

	/**
	 * Erzeugt einen BackupTask
	 * 
	 * @param name
	 *            Name des Backup-Tasks
	 */
	public BackupTask(String name) {
		this.taskName = name;
		sourcePaths = new ArrayList<String>();
	}

	/**
	 * Gibt den Namen des Tasks zurück.
	 * 
	 * @return Task-Name
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * Gitb alle Quellpfade zurück.
	 * 
	 * @return alle Quellpfade
	 */
	public ArrayList<String> getSourcePaths() {
		return sourcePaths;
	}

	/**
	 * Fügt der Listen der zu sichernden Quellpfade einen Pfad hinzu.
	 * 
	 * @param path
	 *            hinzuzufügender Pfad
	 */
	public void addSourcePath(String path) {
		sourcePaths.add(path.trim());
	}

	/**
	 * Legt den Zielpfad auf den übergebenen Pfad fest.
	 * 
	 * @param path
	 *            festzulegender Pfad
	 */
	public void setDestinationPath(String path) {
		this.destinationPath = path;
	}

	/**
	 * Legt alle Quellpfade auf die übergebenen Quellpfade fest. Achtung, alle
	 * existierenden Quellpfade werden überschrieben!
	 * 
	 * @param sourcePaths
	 *            festzulegende Quellpfade
	 */
	public void setSourcePaths(ArrayList<String> sourcePaths) {
		this.sourcePaths = sourcePaths;
	}

	/**
	 * Gibt den Zielpfad zurück.
	 * 
	 * @return Zielpfad
	 */
	public String getDestinationPath() {
		return this.destinationPath;
	}

	/**
	 * Löscht den gegebenen Pfad aus der Liste der zu sichernden Quellpfade.
	 * 
	 * @param path
	 *            zu löschender Pfad
	 */
	public void deletePath(String path) {
		int index = getIndexOfPath(path);
		if (index != -1) {
			sourcePaths.remove(index);
		}

	}

	/**
	 * Sucht den Index zum gegebenen Pfad.
	 * 
	 * @param path
	 *            Pfad zu welchem der Index gesucht wird
	 * @return Index, -1 falls der gesuchte Pfad nicht gefunden wurde
	 */
	private int getIndexOfPath(String path) {
		for (int i = 0; i < sourcePaths.size(); i++) {
			if (sourcePaths.get(i).equals(path)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gibt den Namen des Backup-Tasks als String zurück. Wird für das korrekte
	 * Anzeigen des Namens in der Liste (GUI) benötigt.
	 */
	@Override
	public String toString() {
		return taskName;
	}

	/**
	 * Löscht alle Quell- und Zielpfade.
	 */
	public void resetPaths() {
		sourcePaths.clear();
		destinationPath = null;
	}
	
	/**
	 * Legt den Backup-Modus fest.
	 * @param mode festzulegender Backup-Modus
	 */
	public void setBackupMode(int mode) {
		backupMode = mode;
	}
	
	/**
	 * Gibt den gewählten Backup-Modus zurück. 0 = normal, 1 = hardlink.
	 * @return gewälter Backup-Modus
	 */
	public int getBackupMode() {
		return backupMode;
	}
}
