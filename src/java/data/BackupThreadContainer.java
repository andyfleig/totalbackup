package data;

/**
 * Container für Backup-Threads. Besteht aus einem Thread und dem Namen des
 * zugehörigen BackupTask.
 * 
 * @author andy
 *
 */
public class BackupThreadContainer {
	private Thread backupThread;
	private String nameOfBackupTask;

	public BackupThreadContainer(Thread thread, String taskName) {
		this.backupThread = thread;
		this.nameOfBackupTask = taskName;
	}

	public Thread getBackupThread() {
		return backupThread;
	}

	public String getTaskName() {
		return nameOfBackupTask;
	}
}
