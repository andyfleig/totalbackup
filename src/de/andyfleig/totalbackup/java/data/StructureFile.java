/*
 * Copyright 2014 - 2019 Andreas Fleig (github AT andyfleig DOT de)
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
 * A file or dictionary within the backups index. It is meant to be used to hold information about all the files of a
 * hardlink backup to assess whether the file has changed since the last backup or not (so it has to be copied or just
 * linked).
 *
 * @author Andreas Fleig
 */
public class StructureFile implements Serializable {

	/**
	 * Version number for serialization.
	 */
	private static final long serialVersionUID = 7289482994627565945L;
	private String filePath;
	private long lastModified;
	private boolean isDirectory = false;
	private ArrayList<StructureFile> existingFiles;

	/**
	 * Creates a new StructureFile object with the given path and the last modification date of it.
	 *
	 * @param rootPath root path of the given path
	 * @param path     relative path of the file or directory
	 */
	public StructureFile(String rootPath, String path) {
		this.filePath = path;

		File tempFile = new File(rootPath + filePath);
		lastModified = tempFile.lastModified();
	}

	/**
	 * Adds the given file or directory to the list of sub-files of this StructureFile. Only valid if this StructureFile
	 * is a directory.
	 *
	 * @param file hinzuzufügende Datei
	 */
	public void addFile(StructureFile file) {
		if (!isDirectory) {
			existingFiles = new ArrayList<>();
			isDirectory = true;
		}
		existingFiles.add(file);
	}

	/**
	 * Returns the time of the last modification of the corresponding file.
	 *
	 * @return time of the last modification (unix time as long)
	 */
	public long getLastModifiedDate() {
		return lastModified;
	}

	/**
	 * Returns the path of the file or directory.
	 *
	 * @return path as string
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Returns the StructureFile with the given name from the specified sub-files of this StructureFile.
	 *
	 * @param name name of the StructureFile to return
	 * @return StructureFile or null if no file with the given name exists
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
