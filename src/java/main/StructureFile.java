package main;

import java.util.ArrayList;
import java.io.File;
import java.io.Serializable;

public class StructureFile implements Serializable {

	/**
	 * Versionsnummer für die Seriallisierung.
	 */
	private static final long serialVersionUID = 7289482994627565945L;
	private String rootPath;
	private String filePath;
	private long lastModified;
	private boolean isDirectory = false;
	private ArrayList<StructureFile> existingFiles;

	/**
	 * Erzeugt ein StructureFile-Objekt, liest das letzte Modifizierungsdatum
	 * aus und schreibt dieses.
	 * 
	 * @param root
	 *            Pfad des Wurzelverzeichnisses
	 */
	public StructureFile(String root, String path) {
		this.rootPath = root;
		this.filePath = path;

		File tempFile = new File(filePath);
		lastModified = tempFile.lastModified();
		tempFile = null;
	}

	/**
	 * Fügt zum StruktureFile eine Datei hinzu.
	 * @param file hinzuzufügende Datei
	 */
	public void addFile(StructureFile file) {
		if (isDirectory == false) {
			existingFiles = new ArrayList<StructureFile>();
			isDirectory = true;
		}
		existingFiles.add(file);
	}

	/**
	 * Gibt den Zeitpunkt der letzten Bearbeitung der Datei zurück.
	 * @return Zeitpunkt der letzten Bearbeitung (als long in ms seit 1.1.1970)
	 */
	public long getLastModifiedDate() {
		return lastModified;
	}

	public String getFilePath() {
		return rootPath;
	}

	/**
	 * Gibt die gesuchte Datei zurück, oder null wenn die gesuchte Datei nicht
	 * exisitert.
	 * 
	 * @param name
	 *            Name der zu suchenden Datei
	 * @return Datei oder null
	 */
	public StructureFile getStructureFile(String name) {
		for (int i = 0; i < existingFiles.size(); i++) {
			if (existingFiles.get(i).getFilePath().endsWith(name)) {
				return existingFiles.get(i);
			}
		}
		return null;
	}
}
