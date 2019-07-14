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
 * Listener for instances of Backupable (NormalBackup or HardlinkBackup).
 */
public interface IBackupListener {

	/**
	 * Sets the status of the BackupTask with the given name to the given message. The error-flag indicates whether it
	 * is a error status and thus has to be highlighted.
	 *
	 * @param msg      message to set the status to
	 * @param error    whether it is an error status (true) or not (false)
	 * @param taskName name of the corresponding BackupTask
	 */
	public void setStatus(String msg, boolean error, final String taskName);

	/**
	 * Logs the given message for the given BackupTask.
	 *
	 * @param msg  message to log
	 * @param task corresponding BackupTask
	 */
	public void log(String msg, BackupTask task);

	/**
	 * Adds the BackupTask with the given name to the list of running BackupTasks.
	 *
	 * @param taskName name of the started BackupTask
	 */
	public void taskStarted(String taskName);

	/**
	 * Deletes the given BackupTask from the list of running BackupTasks.
	 *
	 * @param task finished BackupTask
	 */
	public void taskFinished(BackupTask task);

	/**
	 * Deletes empty backup folders within the destination path.
	 *
	 * @param task corresponding BackupTask
	 */
	public void deleteEmptyBackupFolders(BackupTask task);
}
