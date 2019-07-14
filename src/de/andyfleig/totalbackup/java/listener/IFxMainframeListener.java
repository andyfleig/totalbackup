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
import javafx.stage.Stage;

/**
 * JavaFX based Main-Window of TotalBackup.
 *
 * @author Andreas Fleig
 */
public interface IFxMainframeListener {
	public void startMainframe(Stage stage);

	public void startBackupTaskDialog(String taskName);

	public void startAboutDialog();

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

	/***
	 * Executes the BackupTask with the given Name immediately.
	 * @param taskName name of the BackupTask
	 */
	public void runBackupTaskWithName(String taskName);

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

	/**
	 * Quits TotalBackup.
	 */
	public void quitTotalBackup();

	/**
	 * Skips the next execution of the BackupTask with the given name.
	 *
	 * @param taskName name of the BackupTask to skip
	 */
	public void skipNextExecution(String taskName);

	/**
	 * Postpones the execution of the BackupTask with the given name by the given amount of minutes.
	 *
	 * @param taskName            name of the BackupTask to postpone
	 * @param minutesToPostponeBy number of minutes to postpone by
	 */
	public void postponeExecutionBy(String taskName, int minutesToPostponeBy);
}
