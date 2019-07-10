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
package main;

import data.BackupInfos;
import data.BackupTask;

/**
 * Interface for a backup type (e.g. normal or hardlink).
 *
 * @author Andreas Fleig
 */
public interface Backupable {

	/**
	 * Executes the preparation of the given BackupTask (runs the analysis).
	 *
	 * @param task corresponding BackupTask
	 */
	public void runPreparation(BackupTask task);

	/**
	 * Executes the given BackupTask.
	 *
	 * @param task BackupTask to run
	 */
	public void runBackup(BackupTask task);

	/**
	 * Returns the BackupInfos of this Backupable.
	 *
	 * @return BackupInfos
	 */
	public BackupInfos getBackupInfos();

	/**
	 * Returns whether this Backupable was canceled.
	 *
	 * @return whether it was cenceled (true) or not (false)
	 */
	public boolean isCanceled();

	/**
	 * Cancel this Backupable.
	 */
	public void cancel();
}
