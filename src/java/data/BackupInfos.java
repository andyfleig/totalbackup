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
 * Enthält Informationen über den Umfang eines auszuführenden BackupTasks (z.B.
 * Größe der zu kopierenden/ verlinkgenden Dateien oder Anzahl der zu
 * kopierenden/ verlinkenden Dateien oder Verzeichnissen).
 * 
 * @author Andreas Fleig
 *
 */
public class BackupInfos {
	/**
	 * Anzahl der zu sichernden Ordner
	 */
	private long numberOfDirectories = 0;
	/**
	 * Anzahl der zu kopierenden Dateien
	 */
	private long numberOfFilesToCopy = 0;
	/**
	 * Anzahl der zu verlinkenden Dateien
	 */
	private long numberOfFilesToLink = 0;

	/**
	 * Gesamtgröße der zu kopierenden Dateien
	 */
	private double sizeToCopy = 0;
	/**
	 * Gesamtgröße der zu verlinkenden Dateien
	 */
	private double sizeToLink = 0;

	/**
	 * Gibt die Anzahl der zu bearbeitenden Ordner zurück.
	 * 
	 * @return Anzahl der zu bearbeitenden Ordner
	 */
	public long getNumberOfDirectories() {
		return numberOfDirectories;
	}

	/**
	 * Erhöht die Anzahl der zu bearbeitenden Ordner um 1.
	 */
	public void increaseNumberOfDirectories() {
		numberOfDirectories++;
	}

	/**
	 * Gibt die Anzahl der zu kopierenden Dateien zurück.
	 * 
	 * @return Anzahl der zu kopierenden Dateien
	 */
	public long getNumberOfFilesToCopy() {
		return numberOfFilesToCopy;
	}

	/**
	 * Erhöht die Anzahl der zu kopierenden Ordner um 1.
	 */
	public void increaseNumberOfFilesToCopy() {
		numberOfFilesToCopy++;
	}

	/**
	 * Gibt die Anzahl der zu verlinkenden Dateien zurück.
	 * 
	 * @return Anzahl der zu verlinkenden Dateien
	 */
	public long getNumberOfFilesToLink() {
		return numberOfFilesToLink;
	}

	/**
	 * Erhöht die Anzahl der zu verlinkenden Ordner um 1.
	 */
	public void increaseNumberOfFilesToLink() {
		numberOfFilesToLink++;
	}

	/**
	 * Gibt die Gesamtgröße der zu kopierenden Dateien zurück.
	 * 
	 * @return Gesamtgröße der zu kopierenden Dateien
	 */
	public double getSizeToCopy() {
		return sizeToCopy;
	}

	/**
	 * Erhöht die Gesamtgröße der zu kopierenden Dateien um den gegebenen Wert.
	 * 
	 * @param sizeToIncreaseBy
	 *            Größe um die der Gesamtwert erhöht wird
	 */
	public void increaseSizeToCopyBy(double sizeToIncreaseBy) {
		sizeToCopy += sizeToIncreaseBy;
	}

	/**
	 * Gibt die Gesamtgröße der zu verlinkenden Dateien zurück.
	 * 
	 * @return Gesamtgröße der zu verlinkenden Dateien
	 */
	public double getSizeToLink() {
		return sizeToLink;
	}

	/**
	 * Erhöht die Gesamtgröße der zu verlinkenden Dateien um den gegebenen Wert.
	 * 
	 * @param sizeToIncreaseBy
	 *            Größe um die der Gesamtwert erhöht wird
	 */
	public void increaseSizeToLinkBy(double sizeToIncreaseBy) {
		sizeToLink += sizeToIncreaseBy;
	}

	/**
	 * Löscht alle Backup-Infos.
	 */
	public void clear() {
		numberOfDirectories = 0;
		numberOfFilesToCopy = 0;
		numberOfFilesToLink = 0;
		sizeToCopy = 0;
		sizeToLink = 0;
	}
}
