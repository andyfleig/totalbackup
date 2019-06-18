package listener;

import data.BackupTask;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public interface IGUIControllerListener {
	/**
	 * Prüft ob der gegebene String teil der übergebenen Argumente ist.
	 *
	 * @param s zu prüfender String (gesuchtes Argument)
	 * @return ob der gegebene String teil der übergebenen Argumente ist
	 */
	public boolean argsContains(String s);

	/**
	 * Beendet das TotalBackup.
	 */
	public void quitTotalBackup();

	/**
	 * Serialisiert die Programm-Einstellungen (Backup-Tasks).
	 */
	public void saveProperties();

	/**
	 * Fügt einen Backup-Task hinzu.
	 *
	 * @param task hinzuzufügender Backup-Task
	 */
	public void addBackupTask(BackupTask task);

	/**
	 * Reschedules the given BackupTask according to its properties.
	 *
	 * @param task BackupTask to reschedule
	 */
	public void scheduleBackupTask(BackupTask task);

	/**
	 * Reschedules the given BackupTask to be executed immediately.
	 *
	 * @param task BackupTask to execute
	 */
	public void scheduleBackupTaskNow(BackupTask task);

	/**
	 * Gibt aus der Liste der BackupTasks den Task mit dem Namen taskName zurück. Wird kein entsprechender Eintrag
	 * gefunden, wird null zurückgegeben.
	 *
	 * @param taskName Name des gesuchten BackupTasks
	 */
	public BackupTask getBackupTaskWithName(String taskName);

	/**
	 * Deletes the BackupTask with the given taskName from the list of BackupTasks (if any).
	 *
	 * @param taskName name of the BackupTask to delete
	 */
	public void deleteBackupTaskWithName(String taskName);

	/**
	 * Löscht alle leeren Backup-Ordner (erzeugt z.B. durch das Abbrechen eines Backup-Vorgangs nach der Übersicht)
	 *
	 * @param task entsprechender BackupTask
	 */
	public void deleteEmptyBackupFolders(String path, BackupTask task);

	/**
	 * Markes the given task as finished and thus as not currently running. Allows to reschedule.
	 * @param task task to mark as finished
	 * @param schedule whether the task should be rescheduled
	 */
	public void taskFinished(BackupTask task, boolean schedule);
}
