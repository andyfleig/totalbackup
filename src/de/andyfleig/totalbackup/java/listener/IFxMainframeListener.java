package listener;

import data.BackupTask;
import javafx.stage.Stage;

/**
 * JavaFX based Main-Windows of TotalBackp.
 *
 * @author Andreas Fleig
 */
public interface IFxMainframeListener {
	public void startMainframe(Stage stage);
	public void startBackupTaskDialog(String taskName);
	public void startAboutDialog();
	/**
	 * Serializes all the BackupTasks.
	 */
	public void saveProperties();

	/**
	 * Deletes the BackupTask with the given name.
	 * No action will be performed if no BackupTask with the given name exists.
	 * @param taskName name of the BackupTask to delete
	 */
	public void deleteBackupTaskWithName(String taskName);

	/**
	 * Deletes all empty Backup-Folders (e.g. created but not removed when a backup was canceled)
	 *
	 * @param task corresponding BackupTask
	 */
	public void deleteEmptyBackupFolders(String path, BackupTask task);

	/***
	 * Executes the BackupTask with the given Name immediately.
	 * @param taskName
	 */
	public void runBackupTaskWithName(String taskName);
}
