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

import java.util.ArrayList;

import data.BackupTask;

public interface IEditDialogListener {
	/**
	 * Liefert den Backup-Task mit gegebenem Namen zurück. Existiert kein Backup mit dem angegebenen Namen so wird null
	 * zurückgeliefert.
	 *
	 * @param name Name des "gesuchten" Backup-Tasks
	 * @return den gesuchten Backup-Task oder null
	 */
	public BackupTask getBackupTaskWithName(String name);

	/**
	 * Löscht einen Backup-Task.
	 *
	 * @param task zu löschender Backup-Task.
	 */
	public void removeBackupTask(BackupTask task);

	/**
	 * Gibt eine Liste mit allen Namen aller Backup-Tasks zurück.
	 *
	 * @return Liste der Namen aller Backup-Tasks
	 */
	public ArrayList<String> getBackupTaskNames();

	/**
	 * Fügt einen Backup-Task hinzu.
	 *
	 * @param task hinzuzufügender Backup-Task
	 */
	public void addBackupTask(BackupTask task);

	/**
	 * Serialisiert die Programm-Einstellungen (Backup-Task)
	 */
	public void saveProperties();

	/**
	 * Rescheduled den gegebenen BackupTask.
	 */
	public void scheduleBackupTask(BackupTask task);

	/**
	 * Prüft ob ein BackupTask mit dem gegebenen Namen gerade ausgeführt wird.
	 *
	 * @param s Name des BackupTasks
	 * @return ob ein BackupTask mit dem gegebenen Namen gerade ausgeführt wird
	 */
	public boolean isBackupTaskRunning(String s);
}
