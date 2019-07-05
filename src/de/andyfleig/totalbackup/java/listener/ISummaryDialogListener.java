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
 * Listener of the SummaryDialog.
 *
 * @author Andreas Fleig
 */
public interface ISummaryDialogListener {

	/**
	 * Deletes empty backup folders within the destination path.
	 *
	 * @param task corresponding BackupTask
	 */
	public void deleteEmptyBackupFolders(BackupTask task);

	/**
	 * Sets the status of the given BackupTask to canceled.
	 *
	 * @param task corresponding BackupTask
	 */
	public void setStatusToCanceled(BackupTask task);

	/**
	 * Marks the given task as finished and thus as not currently running. Allows to reschedule.
	 *
	 * @param task     task to mark as finished
	 * @param schedule whether the task should be rescheduled
	 */
	public void taskFinished(BackupTask task, boolean schedule);
}