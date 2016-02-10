/*
 * Copyright 2014 - 2016 Andreas Fleig (andy DOT fleig AT gmail DOT com)
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

public interface IBackupListener {

	/**
	 * Gibt den gegebenen String auf der GUI aus. error bestimmt ob es sich um
	 * eine Fehlermeldung (rot) handelt oder nicht.
	 *
	 * @param s        auszugebender String
	 * @param error    legt fest ob es sich um eine Fehlermeldung handelt oder nicht
	 * @param taskName Name des entsprechenen Tasks
	 */
	public void printOut(String s, boolean error, final String taskName);

	/**
	 * Schreibt den gegebenen String in das log-File des gegebenen Tasks.
	 *
	 * @param event zu loggender String
	 * @param task  zugehöriger Task
	 */
	public void log(String event, BackupTask task);

	/**
	 * Gibt den gegebenen String auf dem Status-Textfeld auf der GUI aus.
	 *
	 * @param status auszugebender String
	 */
	public void setStatus(String status);

	/**
	 * Fügt den gegebenen Task zur Liste der laufenden Backup-Tasks hinzu.
	 *
	 * @param taskName Name des hinzuzufügenden Backup-Tasks
	 */
	public void taskStarted(String taskName);

	/**
	 * Entfernt den gegebenen Task aus der Liste der laufenden Backup-Tasks.
	 *
	 * @param task der zu entfernenden Backup-Tasks
	 */
	public void taskFinished(BackupTask task);

	/**
	 * Löscht alle leeren Backup-Ordner (erzeugt z.B. durch das Abbrechen eines
	 * Backup-Vorgangs nach der Übersicht)
	 *
	 * @param task entsprechernder BackupTask
	 */
	public void deleteEmptyBackupFolders(String path, BackupTask task);
}
