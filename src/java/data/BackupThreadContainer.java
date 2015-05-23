/*
 * Copyright 2014, 2015 Andreas Fleig (andy DOT fleig AT gmail DOT com)
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
package data;

/**
 * Container für Backup-Threads. Besteht aus einem Thread und dem Namen des
 * zugehörigen BackupTask.
 * 
 * @author Andreas Fleig
 *
 */
public class BackupThreadContainer {
	private Thread backupThread;
	private String nameOfBackupTask;

	public BackupThreadContainer(Thread thread, String taskName) {
		this.backupThread = thread;
		this.nameOfBackupTask = taskName;
	}

	public Thread getBackupThread() {
		return backupThread;
	}

	public String getTaskName() {
		return nameOfBackupTask;
	}
}
