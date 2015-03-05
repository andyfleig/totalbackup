package main;

import java.io.FileNotFoundException;
import java.io.IOException;

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

}
