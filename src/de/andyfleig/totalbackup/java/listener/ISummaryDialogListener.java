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

public interface ISummaryDialogListener {

	/**
	 * Löscht alle leeren Backup-Ordner (erzeugt z.B. durch das Abbrechen eines
	 * Backup-Vorgangs nach der Übersicht)
	 *
	 * @param task betreffender BackupTask
	 */
	public void deleteEmptyBackupFolders(BackupTask task);

	/**
	 * Gibt eine Meldung aus, dass das laufende Backup (in der Zusammenfassung)
	 * abgebrochen wurde.
	 *
	 * @param task entsprechender BackupTask
	 */
	public void outprintBackupCanceled(BackupTask task);

	/**
	 * Markes the given task as finished and thus as not currently running. Allows to reschedule.
	 * @param task task to mark as finished
	 * @param schedule whether the task should be rescheduled
	 */
	public void taskFinished(BackupTask task, boolean schedule);
}