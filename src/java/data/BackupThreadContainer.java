package data;

//TODO: JavaDoc
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
