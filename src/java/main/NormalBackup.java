package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.*;

import javax.naming.directory.DirContext;

public class NormalBackup implements Backupable {

	/**
	 * Name des zu bearbeitenden BackupTasks
	 */
	private String taskName;
	/**
	 * Liste der Quellpfade
	 */
	private ArrayList<String> sourcePaths;
	/**
	 * Zielpfad
	 */
	private String destinationPath;
	/**
	 * Listener zur Interaktion mit dem Controller
	 */
	private IBackupListener listener;
	/**
	 * Zu bearbeitende Elemente
	 */
	private LinkedList<BackupElement> elementQueue;

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
		elementQueue = new LinkedList<BackupElement>();
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
		if (dir == null) {
			// listener.printOut(listener.getCurrentTask(),
			// ResourceBundle.getBundle("gui.messages").getString("Messages.BackupFolderCreationError"),
			// 0, true);
			return;
		}

		try {
			for (int i = 0; i < sourcePaths.size(); i++) {
				File sourceFile = new File(sourcePaths.get(i));

				String folder = dir + File.separator + sourceFile.getName();
				File f = new File(folder);

				if (f.mkdir()) {
					String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.FolderCreated");
					listener.printOut(outprint, false);
					listener.log(outprint, listener.getCurrentTask());
				} else {
					String outprint = ResourceBundle.getBundle("gui.messages")
							.getString("Messages.FolderCreationError");
					listener.printOut(outprint, true);
					listener.log(outprint, listener.getCurrentTask());
				}

				// Queueing:
				try {
					for (int j = 0; j < sourcePaths.size(); j++) {
						rekursivePreparation(new File(sourcePaths.get(j)), f);
					}
				} catch (BackupCanceledException e) {
					// TODO
				}
			}
			// Eigentlicher Backup-Vorgang:
			while (!elementQueue.isEmpty()) {
				BackupElement currentElement = elementQueue.pop();
				if (currentElement.isDirectory()) {
					(new File(currentElement.getDestPath())).mkdir();
				} else {
					BackupHelper.copyFile(new File(currentElement.getSourcePath()),
							new File(currentElement.getDestPath()), listener);
				}
			}

			String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.BackupComplete");
			listener.printOut(outprint, false);
			listener.log(outprint, listener.getCurrentTask());

		} catch (BackupCanceledException e) {
			String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
			listener.printOut(outprint, false);
			listener.log(outprint, listener.getCurrentTask());
		}
	}

	private void rekursivePreparation(File sourceFile, File backupDir) {
		File[] files = sourceFile.listFiles();

		if (files == null) {
			String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.UnknownErrorAt") + " "
					+ sourceFile.getPath();
			listener.printOut(outprint, true);
			listener.log(outprint, listener.getCurrentTask());

			return;
		}

		for (int i = 0; i < files.length; i++) {
			if (Thread.interrupted()) {
				throw new BackupCanceledException();
			}
			if (files[i].isDirectory()) {
				File newBackupDir = new File(backupDir.getAbsolutePath() + File.separator + files[i].getName());
				elementQueue.add(new BackupElement(files[i].getAbsolutePath(), newBackupDir.getAbsolutePath(), true,
						false));
				rekursivePreparation(files[i], newBackupDir);
			} else {
				File newFile = new File(backupDir.getAbsolutePath() + File.separator + files[i].getName());
				elementQueue
						.add(new BackupElement(files[i].getAbsolutePath(), newFile.getAbsolutePath(), false, false));
			}
		}
	}
}
