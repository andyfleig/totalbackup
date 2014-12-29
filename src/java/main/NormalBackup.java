package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.*;

public class NormalBackup implements Backupable {

	/**
	 * Name des zu bearbeitenden BackupTasks
	 */
	private String taskName;
	/**
	 * Liste der Quellen
	 */
	private ArrayList<Source> sources;
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
	 * Zeigt ob die Vorbereitungen bereits getroffen wurde. Erst dann kann
	 * runBackup() aufgerufen werden.
	 */
	private boolean preparationDone = false;

	/**
	 * Quelle an der aktuell "gearbeitet" wird (für das Filtern der zu queuenden
	 * Elemente).
	 */
	private Source currentSource;

	/**
	 * Backup-Objekt zur Datensicherung.
	 * 
	 * @param listener
	 *            Listener
	 * @param nameOfTask
	 *            Name des Backup-Tasks
	 * @param sources
	 *            Quellen
	 * @param destination
	 *            Zielpfad
	 */
	public NormalBackup(IBackupListener listener, String nameOfTask, ArrayList<Source> sources, String destination) {
		this.listener = listener;
		this.taskName = nameOfTask;
		this.sources = sources;
		this.destinationPath = destination;
		elementQueue = new LinkedList<BackupElement>();
	}

	@Override
	public void runPreparation() {
		File dir = BackupHelper.createBackupFolder(destinationPath, taskName, listener);
		if (dir == null) {
			String output = ResourceBundle.getBundle("gui.messages").getString("Messages.BackupFolderCreationError");
			listener.printOut(output, true);
			listener.log(output, listener.getCurrentTask());
			return;
		}

		try {
			for (int i = 0; i < sources.size(); i++) {
				// Für die Filterung:
				currentSource = sources.get(i);

				File sourceFile = new File(sources.get(i).getPath());

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

				String output = ResourceBundle.getBundle("gui.messages").getString("Messages.PreparationStarted");
				listener.printOut(output, false);
				listener.log(output, listener.getCurrentTask());

				// Queueing:
				try {
					for (int j = 0; j < sources.size(); j++) {
						rekursivePreparation(new File(sources.get(j).getPath()), f);
					}
				} catch (BackupCanceledException e) {
					String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
					listener.printOut(outprint, false);
					listener.log(outprint, listener.getCurrentTask());
				}
			}
		} catch (BackupCanceledException e) {
			String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
			listener.printOut(outprint, false);
			listener.log(outprint, listener.getCurrentTask());
		}
		String output = ResourceBundle.getBundle("gui.messages").getString("Messages.PreparationDone");
		listener.printOut(output, false);
		listener.log(output, listener.getCurrentTask());
		preparationDone = true;
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
		// Test ob die Vorbereitung durchgeführt wurden:
		if (!preparationDone) {
			System.out.println("Fehler: Vorbereitung muss zuerst ausgeführt werden!");
			return;
		}
		String output = ResourceBundle.getBundle("gui.messages").getString("Messages.startBackup");
		listener.printOut(output, false);
		listener.log(output, listener.getCurrentTask());

		try {
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
				// Filtern:
				ArrayList<String> filtersOfThisSource = currentSource.getFilter();
				boolean filterMatches = false;
				for (int j = 0; j < filtersOfThisSource.size(); j++) {
					if ((files[i].getAbsolutePath().equals(filtersOfThisSource.get(j)))) {
						filterMatches = true;
					}
				}
				if (!filterMatches) {
					// Queuen:
					File newBackupDir = new File(backupDir.getAbsolutePath() + File.separator + files[i].getName());
					elementQueue.add(new BackupElement(files[i].getAbsolutePath(), newBackupDir.getAbsolutePath(),
							true, false));
					listener.increaseNumberOfDirectories();
					rekursivePreparation(files[i], newBackupDir);
				}
			} else {
				// Filtern:
				ArrayList<String> filtersOfThisSource = currentSource.getFilter();
				boolean filterMatches = false;
				for (int j = 0; j < filtersOfThisSource.size(); j++) {
					if ((files[i].getAbsolutePath().equals(filtersOfThisSource.get(j)))) {
						filterMatches = true;
					}
				}
				if (!filterMatches) {
					// Queuen:
					File newFile = new File(backupDir.getAbsolutePath() + File.separator + files[i].getName());
					elementQueue.add(new BackupElement(files[i].getAbsolutePath(), newFile.getAbsolutePath(), false,
							false));
					listener.increaseNumberOfFiles();
					listener.increaseSizeToCopyBy(files[i].length());
				}
			}
		}
	}
}
