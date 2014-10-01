package gui;

import java.util.ArrayList;

import main.BackupTask;

public interface IMainframeListener {
	/**
	 * Startet alle Backup-Tasks.
	 */
	public void startAllBackups();
	
	public void removeBackupTask(BackupTask task);
	
	public ArrayList<BackupTask> getBackupTasks();
	
	public BackupTask getBackupTaskWithName(String name);
	
	public ArrayList<String> getBackupTaskNames();
	
	public void addBackupTask(BackupTask task);
}
