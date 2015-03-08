package listener;

import data.BackupTask;

public interface ISummaryDialogListener {

	/**
	 * Startet den Backup-Vorgang.
	 */
	public void startBackup();

	/**
	 * Gibt den Namen des Backup-Tasks zurück.
	 * 
	 * @return Name des Backup-Tasks
	 */
	public String getTaskName();

	/**
	 * Löscht alle leeren Backup-Ordner (erzeugt z.B. durch das Abbrechen eines
	 * Backup-Vorgangs nach der Übersicht)
	 * 
	 * @param task
	 *            betreffender BackupTask
	 */
	public void deleteEmptyBackupFolders(BackupTask task);

	/**
	 * Gibt eine Meldung aus, dass das laufende Backup (in der Zusammenfassung)
	 * abgebrochen wurde.
	 * 
	 * @param task
	 *            entsprechender BackupTask
	 */
	public void outprintBackupCanceled(BackupTask task);

	/**
	 * Entfernt den gegebenen Task aus der Liste der laufenden Backup-Tasks.
	 * 
	 * @param task
	 *            der zu entfernenden Backup-Tasks
	 */
	public void taskFinished(BackupTask task);
}