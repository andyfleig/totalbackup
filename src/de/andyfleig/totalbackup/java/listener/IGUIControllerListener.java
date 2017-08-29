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
	 * Rescheduled den gegebenen BackupTask.
	 */
	public void scheduleBackupTask(BackupTask task);

	/**
	 * Gibt aus der Liste der BackupTasks den Task mit dem Namen taskName zurück. Wird kein entsprechender Eintrag
	 * gefunden, wird null zurückgegeben.
	 *
	 * @param taskName Name des gesuchten BackupTasks
	 */
	public BackupTask getBackupTaskWithName(String taskName);

	/**
	 * Löscht aus der Liste der BackupTasks den Task mit dem Namen taskName. Gibt bei erfolg 1 zurück, sonst 0;
	 *
	 * @param taskName Name des gesuchten BackupTasks
	 */
	public int deleteBackupTaskWithName(String taskName);
}
