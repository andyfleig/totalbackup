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

/**
 * Einthält Informationen zu einer Datei oder einem Verzeichnis. Es wird bei der
 * Preparation angelegt und später beim eigentlichen Backup-Vorgang benutzt.
 *
 * @author Andreas Fleig
 */
public class BackupElement {
	/**
	 * Legt den Quell-Pfad des Elements fest
	 */
	private String sourcePath;
	/**
	 * Legt den Ziel-Pfad des Elements fest
	 */
	private String destPath;
	/**
	 * Legt fest ob das Element ein Ordner ist
	 */
	private boolean isDirectory;
	/**
	 * Legt fest ob das Element verlinkt (true) oder kopiert (false) werden soll
	 */
	private boolean toLink = false;

	/**
	 * Erzeugt ein neues Backup-Element.
	 *
	 * @param path        Pfad des Elements
	 * @param isDirectory ob das Element ein Ordner ist
	 * @param toLink      ob das Element kopiert oder verlinkt werden soll (Achtung:
	 *                    Ordner können nicht verlinkt werden)
	 */
	public BackupElement(String sourcePath, String destPath, boolean isDirectory, boolean toLink) {
		this.sourcePath = sourcePath;
		this.destPath = destPath;
		this.isDirectory = isDirectory;
		if (!isDirectory) {
			this.toLink = toLink;
		}
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public String getDestPath() {
		return destPath;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public boolean toLink() {
		return toLink;
	}
}
