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

import java.time.LocalDateTime;

/**
 * Listener of the INextExecutionChooserDialog.
 *
 * @author Andreas Fleig
 */
public interface INextExecutionChooserListener {
	/**
	 * Skips this execution of the BackupTask. The BackupTask will be rescheduled accorting to its configuration.
	 */
	public void skipBackup();

	/**
	 * Postpones this BackupTask to the given time.
	 *
	 * @param nextExecutionTime time to postpone the execution of the BackupTask to
	 */
	public void postponeBackupTo(LocalDateTime nextExecutionTime);

	/**
	 * Reschedules the BackupTask to be executed immediately.
	 */
	public void retry();
}
