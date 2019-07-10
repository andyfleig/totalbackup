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
public interface IGUIControllerListener {
	/**
	 * Checks whether the given argument is part of the execution arguments of TotalBackup.
	 *
	 * @param arg argument to check
	 * @return whether it is part of the execution arguments (true) or not (false)
	 */
	public boolean argsContains(String arg);

	/**
	 * Quits TotalBackup.
	 */
	public void quitTotalBackup();

	/**
	 * Serializes all the BackupTasks.
	 */
	public void saveProperties();

	/**
	 * Adds the given name of a BackupTask to the list of BackupTasks.
	 *
	 * @param task BackupTask to add
	 */
	public void addBackupTask(BackupTask task);

	/**
	 * Schedules the given BackupTask according to its configuration.
	 *
	 * @param task BackupTask to schedule
	 */
	public void scheduleBackupTask(BackupTask task);

	/**
	 * Schedules the given BackupTask to be executed immediately.
	 *
	 * @param task BackupTask to schedule
	 */
	public void scheduleBackupTaskNow(BackupTask task);

	/**
	 * Returns the BackupTask with the given name. Returns null if no BackupTask with the given name exists.
	 *
	 * @param taskName name of the BackupTask to return
	 * @return BackupTask with the given name (if any), else null
	 */
	public BackupTask getBackupTaskWithName(String taskName);

	/**
	 * Deletes the BackupTask with the given taskName from the list of BackupTasks (if any).
	 *
	 * @param taskName name of the BackupTask to delete
	 */
	public void deleteBackupTaskWithName(String taskName);

	/**
	 * Deletes empty backup folders within the destination path.
	 *
	 * @param task corresponding BackupTask
	 */
	public void deleteEmptyBackupFolders(BackupTask task);

	/**
	 * Marks the given task as finished and thus as not currently running. Allows to reschedule.
	 *
	 * @param task     task to mark as finished
	 * @param schedule whether the task should be rescheduled
	 */
	public void taskFinished(BackupTask task, boolean schedule);

	/**
	 * Returns whether the given BackupTask is currently running.
	 *
	 * @param taskName name of the BackupTask to check
	 * @return whether the BackupTask is currently running
	 */
	public boolean taskIsRunning(String taskName);

	/**
	 * Cancel the BackupTask with the given name if running.
	 *
	 * @param taskName name of the backup to cancel
	 */
	public void cancelBackupTaskWithName(String taskName);
}
