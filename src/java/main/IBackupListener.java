package main;

public interface IBackupListener {
	
	public void printOut(BackupTask task, String s, int level);
	
	public BackupTask getCurrentTask();

}
