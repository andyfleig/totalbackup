package listener;

import data.BackupTask;

public interface IBackupListener {

	/**
	 * Gibt den gegebenen String auf der GUI aus. error bestimmt ob es sich um
	 * eine Fehlermeldung (rot) handelt oder nicht.
	 * 
	 * @param s
	 *            auszugebender String
	 * @param error
	 *            legt fest ob es sich um eine Fehlermeldung handelt oder nicht
	 * @param taskName
	 *            Name des entsprechenen Tasks
	 */
	public void printOut(String s, boolean error, final String taskName);

	/**
	 * Schreibt den gegebenen String in das log-File des gegebenen Tasks.
	 * 
	 * @param event
	 *            zu loggender String
	 * @param task
	 *            zugehöriger Task
	 */
	public void log(String event, BackupTask task);

	/**
	 * Gibt den gegebenen String auf dem Status-Textfeld auf der GUI aus.
	 * 
	 * @param status
	 *            auszugebender String
	 */
	public void setStatus(String status);

	/**
	 * Gibt zurück ob die erweiterte Ausgabe aktiviert ist.
	 * 
	 * @return Status der erweiterten Ausgabe
	 */
	public boolean advancedOutputIsEnabled();

	/**
	 * Fügt den gegebenen Task zur Liste der laufenden Backup-Tasks hinzu.
	 * 
	 * @param taskName
	 *            Name des hinzuzufügenden Backup-Tasks
	 */
	public void taskStarted(String taskName);

	/**
	 * Entfernt den gegebenen Task aus der Liste der laufenden Backup-Tasks.
	 * 
	 * @param task
	 *            der zu entfernenden Backup-Tasks
	 */
	public void taskFinished(BackupTask task);
}
