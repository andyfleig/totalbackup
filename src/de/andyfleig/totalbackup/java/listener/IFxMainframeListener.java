package listener;

import data.BackupTask;
import gui.FxMainframe;
import javafx.stage.Stage;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public interface IFxMainframeListener {
	public void startMainframe(Stage stage);
	public void startBackupTaskDialog(String taskName);
	public void startAboutDialog();
	/**
	 * Serialisiert die Programm-Einstellungen (Backup-Tasks).
	 */
	public void saveProperties();
}
