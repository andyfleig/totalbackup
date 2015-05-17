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
package listener;

import data.BackupTask;

public interface ISummaryDialogListener {

	/**
	 * Startet den Backup-Vorgang.
	 */
	public void startBackup();

	/**
	 * Gibt den Namen des Backup-Tasks zurück.
	 * 
	 * @return Name des Backup-Tasks
	 */
	public String getTaskName();

	/**
	 * Löscht alle leeren Backup-Ordner (erzeugt z.B. durch das Abbrechen eines
	 * Backup-Vorgangs nach der Übersicht)
	 * 
	 * @param task
	 *            betreffender BackupTask
	 */
	public void deleteEmptyBackupFolders(BackupTask task);

	/**
	 * Gibt eine Meldung aus, dass das laufende Backup (in der Zusammenfassung)
	 * abgebrochen wurde.
	 * 
	 * @param task
	 *            entsprechender BackupTask
	 */
	public void outprintBackupCanceled(BackupTask task);

	/**
	 * Entfernt den gegebenen Task aus der Liste der laufenden Backup-Tasks.
	 * 
	 * @param task
	 *            der zu entfernenden Backup-Tasks
	 */
	public void taskFinished(BackupTask task);
}