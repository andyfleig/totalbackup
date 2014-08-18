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
	 * Erzeugt ein StructureFile-Objekt, liest das letzte Modifizierungsdatum aus und schreibt dieses.
	 * @param root Pfad des Wurzelverzeichnisses
	 */
	public StructureFile(String root, String path) {
		this.rootPath = root;
		this.filePath = path;
		
		File tempFile = new File(filePath);
		lastModified = tempFile.lastModified();
		tempFile = null;
	}
	
	public void addFile(StructureFile file) {
		if (isDirectory == false) {
			existingFiles = new ArrayList<StructureFile>();
			isDirectory = true;
		}
		existingFiles.add(file);
	}
}
