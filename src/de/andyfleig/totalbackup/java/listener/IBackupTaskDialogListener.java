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

	/**
	 * Prüft ob bereits ein BackupTask mit dem gegebenen Namen existiert.
	 * @param taskName zu prüfender Name
	 */
	public boolean backupTaskWithNameExisting(String taskName);

	/**
	 * Löscht den BackupTask mit dem gegebenen Namen.
	 * Existiert kein BackupTask mit dem gegebenen Namen, passiert nichts.
	 * @param taskName Name des BackupTasks
	 */
	public void deleteBackupTaskWithName(String taskName);

}
