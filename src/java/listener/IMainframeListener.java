package listener;

import java.util.ArrayList;

import main.Backupable;
import data.BackupTask;

public interface IMainframeListener {

	/**
	 * Startet das Vorbereiten eines bestimmten Backup-Tasks.
	 * 
	 * @param task
	 *            vorzubereitender Backup-Task
	 */
	public Backupable startPreparation(BackupTask task);

	/**
	 * Startet einen bestimmten Backup-Task.
	 * 
	 * @param task
	 *            zu startender Backup-Task
	 */
	// TODO: JavaDoc
	public void startBackupTask(BackupTask task, Backupable backup);

	/**
	 * Löscht einen Backup-Task.
	 * 
	 * @param task
	 *            zu löschender Backup-Task.
	 */
	public void removeBackupTask(BackupTask task);

	/**
	 * Gibt alle Backup-Tasks zurück.
	 * 
	 * @return Liste aller Backup-Tasks
	 */
	public ArrayList<BackupTask> getBackupTasks();

	/**
	 * Liefert den Backup-Task mit gegebenem Namen zurück. Exisitert kein Backup
	 * mit dem angegebenen Namen so wird null zurückgeliefert.
	 * 
	 * @param name
	 *            Name des "gesuchten" Backup-Tasks
	 * @return den gesuchten Backup-Task oder null
	 */
	public BackupTask getBackupTaskWithName(String name);

	/**
	 * Gibt eine Liste mit allen Namen aller Backup-Tasks zurück.
	 * 
	 * @return Liste der Namen aller Backup-Tasks
	 */
	public ArrayList<String> getBackupTaskNames();

	/**
	 * Fügt einen Backup-Task hinzu.
	 * 
	 * @param task
	 *            hinzuzufügender Backup-Task
	 */
	public void addBackupTask(BackupTask task);

	/**
	 * Löscht alle leeren Backup-Ordner (erzeugt z.B. durch das Abbrechen eines
	 * Backup-Vorgangs nach der Übersicht)
	 * 
	 * @param task
	 *            entsprechernder BackupTask
	 */
	public void deleteEmptyBackupFolders(String path, BackupTask task);

	/**
	 * Gibt eine Meldung aus, dass das laufende Backup (in der Zusammenfassung)
	 * abgebrochen wurde.
	 * 
	 * @param task
	 *            entpsrechender BackupTask
	 */
	public void outprintBackupCanceled(BackupTask task);

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
	public void printOut(String s, boolean error, String taskName);

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

	// TODO: JavaDoc
	public void scheduleBackupTasks();

	public void removeBackupTaskScheduling(BackupTask task);
}
