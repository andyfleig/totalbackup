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
package main;

import java.io.FileNotFoundException;
import java.io.IOException;

import data.BackupInfos;
import data.BackupTask;

public interface Backupable {

	/**
	 * Bereitet den Backup-Vorgang vor (sammelt zu bearbeitenden Dateien).
	 * 
	 * @param task
	 *            betreffender BackupTask
	 */
	public void runPreparation(BackupTask task);

	/**
	 * Führt das Backup aus.
	 * 
	 * @param task
	 *            auszuführender Backup-Tasks
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void runBackup(BackupTask task) throws FileNotFoundException, IOException;

	/**
	 * Gibt die BackupInfos zu diesem Backup zurück.
	 * 
	 * @return BackupInfos zu diesem Backup
	 */
	public BackupInfos getBackupInfos();

	/**
	 * Gibt zurück, ob dieses Backup gecanceled ist.
	 * 
	 * @return ob dieses Backup gecanceled ist
	 */
	public boolean isCanceled();

	/**
	 * Setzt dieses Backup auf gecanceled.
	 */
	public void cancel();
}
