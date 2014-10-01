package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.*;

public class NormalBackup implements Backupable {

	private String taskName;
	private ArrayList<String> sourcePaths;
	private String destinationPath;
	private IBackupListener listener;

	/**
	 * Backup-Objekt zur Datensicherung.
	 * 
	 * @param c
	 *            Controller
	 * @param source
	 *            Quellpfade
	 * @param destination
	 *            Zielpfad
	 */
	public NormalBackup(IBackupListener listener, String nameOfTask, ArrayList<String> sources, String destination) {
		this.listener = listener;
		this.taskName = nameOfTask;
		this.sourcePaths = sources;
		this.destinationPath = destination;
	}

	/**
	 * Startet den Backup-Vorgang.
	 * 
	 * @param taskName
	 *            Name des Backup-Tasks welcher ausgeführt wird
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void runBackup(String taskName) throws FileNotFoundException, IOException {

		File dir = BackupHelper.createBackupFolder(destinationPath, taskName, listener);

		for (int i = 0; i < sourcePaths.size(); i++) {
			File sourceFile = new File(sourcePaths.get(i));

			String folder = dir + System.getProperty("file.separator") + sourceFile.getName();
			File f = new File(folder);

			if (f.mkdir()) {
				listener.printOut(listener.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.FolderCreated"), 1);
			} else {
				listener.printOut(listener.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.FolderCreationError"), 1);
			}
			// Eigentlicher Kopiervorgang:
			BackupHelper.copyDirectory(sourceFile, f, listener);
		}
		listener.printOut(listener.getCurrentTask(),
				ResourceBundle.getBundle("gui.messages").getString("Messages.BackupComplete"), 1);
	}
}
