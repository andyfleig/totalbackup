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
package listener;

import data.BackupTask;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public interface IBackupTaskDialogListener {

	/**
	 * Fügt einen Backup-Task hinzu.
	 *
	 * @param task hinzuzufügender Backup-Task
	 */
	public void addBackupTask(BackupTask task);

	/**
	 * Serialisiert die Programm-Einstellungen (Backup-Taks)
	 */
	public void saveProperties();

	/**
	 * Rescheduled den gegebenen BackupTask.
	 */
	public void scheduleBackupTask(BackupTask task);

	/**
	 * Prüft ob bereits ein BackupTask mit dem gegebenen Namen existiert.
	 *
	 * @param taskName zu prüfender Name
	 */
	public boolean backupTaskWithNameExisting(String taskName);

	/**
	 * Löscht den BackupTask mit dem gegebenen Namen. Existiert kein BackupTask mit dem gegebenen Namen, passiert
	 * nichts.
	 *
	 * @param taskName Name des BackupTasks
	 */
	public void deleteBackupTaskWithName(String taskName);

}
