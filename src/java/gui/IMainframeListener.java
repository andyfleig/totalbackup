package gui;

import java.util.ArrayList;

import main.BackupTask;

public interface IMainframeListener {
	/**
	 * Startet alle Backup-Tasks.
	 */
	public void startAllBackups();
	
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
}
