package gui;

import java.util.ArrayList;

import main.BackupTask;

public interface IMainframeListener {

	/**
	 * Startet das Vorbereiten eines bestimmten Backup-Tasks.
	 * 
	 * @param task
	 *            vorzubereitender Backup-Task
	 */
	public void startPreparation(BackupTask task);

	/**
	 * Startet einen bestimmten Backup-Task.
	 * 
	 * @param task
	 *            zu startender Backup-Task
	 */
	public void startBackupTask(BackupTask task);

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
	 * Löscht alle Backup-Infos.
	 */
	public void clearBackupInfos();

	/**
	 * Gibt die Anzahl der zu kopierenden Ordner zurück.
	 * 
	 * @return Anzahl der zu kopierenden Ordner
	 */
	public long getNumberOfDirectories();

	/**
	 * Gibt die Anzahl der zu kopierenden Dateien zurück.
	 * 
	 * @return Anzahl der zu kopierenden Dateien
	 */
	public long getNumberOfFiles();

	/**
	 * Gibt die Gesamtgröße der zu kopierenden Dateien zurück.
	 * 
	 * @return Gesamtgröße der zu kopierenden Dateien
	 */
	public double getSizeToCopy();

	/**
	 * Gibt die Gesamtgröße der zu verlinkenden Dateien zurück.
	 * 
	 * @return Gesamtgröße der zu verlinkenden Dateien
	 */
	public double getSizeToLink();

	/**
	 * Löscht alle leeren Backup-Ordner (erzeugt z.B. durch das Abbrechen eines
	 * Backup-Vorgangs nach der Übersicht)
	 */
	public void deleteEmptyBackupFolders(String path);

	/**
	 * Gibt eine Meldung aus, dass das laufende Backup (in der Zusammenfassung)
	 * abgebrochen wurde.
	 */
	public void outprintBackupCanceled();
}
