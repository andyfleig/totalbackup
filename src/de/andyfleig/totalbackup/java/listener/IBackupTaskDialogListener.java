package listener;

import data.BackupTask;

import java.io.File;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public interface IBackupTaskDialogListener {

	/**
	 * Fügt einen Backup-Task hinzu.
	 *
	 * @param task hinzuzufügender Backup-Task
	 */
	public void addBackupTask(BackupTask task);

	/**
	 * Seriallisiert die Programm-Einstellungen (Backup-Taks)
	 */
	public void saveProperties();

	/**
	 * Rescheduled den gegebenen BackupTask.
	 */
	public void scheduleBackupTask(BackupTask task);

}
