/*
 * Copyright 2014, 2015 Andreas Fleig (andy DOT fleig AT gmail DOT com)
 * 
 * All rights reserved.
 * 
 * This file is part of TotalBackup.
 *
 * TotalBackup is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TotalBackup is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TotalBackup.  If not, see <http://www.gnu.org/licenses/>.
 */
package data;

import java.util.ArrayList;
import java.io.File;
import java.io.Serializable;

/**
 * Eine Datei oder ein Verzeichnis für die Struktur innerhalb des Index.
 *
 * @author Andreas Fleig
 */
public class StructureFile implements Serializable {

	/**
	 * Versionsnummer für die Seriallisierung.
	 */
	private static final long serialVersionUID = 7289482994627565945L;
	private String filePath;
	private long lastModified;
	private boolean isDirectory = false;
	private ArrayList<StructureFile> existingFiles;

	/**
	 * Erzeugt ein StructureFile-Objekt, liest das letzte Modifizierungsdatum aus und schreibt dieses.
	 *
	 * @param rootPath Pfad des Wurzelverzeichnisses
	 * @param path     relativer Pfad der Datei
	 */
	public StructureFile(String rootPath, String path) {
		this.filePath = path;

		File tempFile = new File(rootPath + filePath);
		lastModified = tempFile.lastModified();
		tempFile = null;
	}

	/**
	 * Fügt zum StruktureFile eine Datei hinzu.
	 *
	 * @param file hinzuzufügende Datei
	 */
	public void addFile(StructureFile file) {
		if (!isDirectory) {
			existingFiles = new ArrayList<StructureFile>();
			isDirectory = true;
		}
		existingFiles.add(file);
	}

	/**
	 * Gibt den Zeitpunkt der letzten Bearbeitung der Datei zurück.
	 *
	 * @return Zeitpunkt der letzten Bearbeitung (als long in ms seit 1.1.1970)
	 */
	public long getLastModifiedDate() {
		return lastModified;
	}

	/**
	 * Gibt den Datei-Pfad zurück.
	 *
	 * @return Datei-Pfad
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Gibt die gesuchte Datei zurück, oder null wenn die gesuchte Datei nicht exisitert.
	 *
	 * @param name Name der zu suchenden Datei
	 * @return Datei oder null
	 */
	public StructureFile getStructureFile(String name) {
		if (existingFiles == null) {
			return null;
		}
		for (StructureFile existingFile : existingFiles) {
			String test = existingFile.getFilePath();
			if (test.endsWith(name)) {
				return existingFile;
			}
		}
		return null;
	}
}
