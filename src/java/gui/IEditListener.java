package gui;

import java.util.ArrayList;

import main.BackupTask;

public interface IEditListener {
	public BackupTask getBackupTaskWithName(String name);
	
	public void removeBackupTask(BackupTask task);
	
	public ArrayList<String> getBackupTaskNames();
	
	public void addBackupTask(BackupTask task);
}
