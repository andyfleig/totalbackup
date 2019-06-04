package listener;

import data.BackupTask;
import gui.FxMainframe;
import gui.SummaryDialog;
import javafx.stage.Stage;
import main.Backupable;

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

	/**
	 * Löscht den BackupTask mit dem gegebenen Namen.
	 * Existiert kein BackupTask mit dem gegebenen Namen, passiert nichts.
	 * @param taskName Name des BackupTasks
	 */
	public void deleteBackupTaskWithName(String taskName);

	/**
	 * Löscht alle leeren Backup-Ordner (erzeugt z.B. durch das Abbrechen eines Backup-Vorgangs nach der Übersicht)
	 *
	 * @param task entsprechernder BackupTask
	 */
	public void deleteEmptyBackupFolders(String path, BackupTask task);
}
