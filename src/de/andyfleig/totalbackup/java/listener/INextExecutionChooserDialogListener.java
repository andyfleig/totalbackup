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

/**
 * Listener of the INextExecutionChooserDialog.
 *
 * @author Andreas Fleig
 */
public interface INextExecutionChooserDialogListener {
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
