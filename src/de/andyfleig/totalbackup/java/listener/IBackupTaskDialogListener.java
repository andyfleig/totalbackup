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
 * Listener of the BackupTaskDialog.
 *
 * @author Andreas Fleig
 */
public interface IBackupTaskDialogListener {

	/**
	 * Adds the given name of a BackupTask to the list of BackupTasks.
	 *
	 * @param task BackupTask to add
	 */
	public void addBackupTask(BackupTask task);

	/**
	 * Serializes all the BackupTasks.
	 */
	public void saveProperties();

	/**
	 * Schedules the given BackupTask according to its configuration.
	 *
	 * @param task BackupTask to schedule
	 */
	public void scheduleBackupTask(BackupTask task);

	/**
	 * Returns whether a BackupTask with the given name already exists.
	 *
	 * @param taskName name for the BackupTask to check
	 * @return whether the name already exists (true) or not (false)
	 */
	public boolean backupTaskWithNameExisting(String taskName);

	/**
	 * Deletes the BackupTask with the given taskName from the list of BackupTasks (if any).
	 *
	 * @param taskName name of the BackupTask to delete
	 */
	public void deleteBackupTaskWithName(String taskName);

}
