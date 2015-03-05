package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.*;

import listener.IBackupListener;
import data.BackupElement;
import data.BackupTask;
import data.Filter;
import data.Source;

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
	public void runPreparation(BackupTask task) {
		File dir = BackupHelper.createBackupFolder(destinationPath, taskName, listener, task);
		if (dir == null) {
			String output = ResourceBundle.getBundle("gui.messages").getString("Messages.BackupFolderCreationError");
			listener.printOut(output, true);
			listener.log(output, task);
			return;
		}

		try {
			for (int i = 0; i < sources.size(); i++) {
				// Für die Filterung:
				currentSource = sources.get(i);

				File sourceFile = new File(sources.get(i).getPath());

				// Sonderbehandlung für Windows, wenn der SourcePath das
				// root-dir eines Volume (z.B. C:/) ist:
				String folder;
				if (sourceFile.getAbsolutePath().contains(":\\") && sourceFile.getAbsolutePath().length() == 3
						&& sourceFile.getName().equals("")) {
					// In diesem Sonderfall ergibt sich der Name nur aus dem
					// Laufwerksbuchstaben:
					String test = sourceFile.getAbsolutePath();
					folder = dir + File.separator + sourceFile.getAbsolutePath().charAt(0);
				} else {
					folder = dir + File.separator + sourceFile.getName();
				}

				File f = new File(folder);

				if (f.mkdir()) {
					String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.FolderCreated");
					listener.printOut(outprint, false);
					listener.log(outprint, task);
				} else {
					String outprint = ResourceBundle.getBundle("gui.messages")
							.getString("Messages.FolderCreationError");
					listener.printOut(outprint, true);
					listener.log(outprint, task);
				}

				String output = ResourceBundle.getBundle("gui.messages").getString("Messages.PreparationStarted");
				listener.printOut(output, false);
				listener.log(output, task);

				// Queueing:
				try {
					for (int j = 0; j < sources.size(); j++) {
						rekursivePreparation(new File(sources.get(j).getPath()), f, task);
					}
				} catch (BackupCanceledException e) {
					String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
					listener.printOut(outprint, false);
					listener.log(outprint, task);
				}
			}
		} catch (BackupCanceledException e) {
			String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
			listener.printOut(outprint, false);
			listener.log(outprint, task);
		}
		String output = ResourceBundle.getBundle("gui.messages").getString("Messages.PreparationDone");
		listener.printOut(output, false);
		listener.log(output, task);
		preparationDone = true;
	}

	/**
	 * Startet den Backup-Vorgang.
	 * 
	 * @param task
	 *            Backup-Tasks welcher ausgeführt wird
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void runBackup(BackupTask task) throws FileNotFoundException {
		// Test ob die Vorbereitung durchgeführt wurden:
		if (!preparationDone) {
			System.out.println("Fehler: Vorbereitung muss zuerst ausgeführt werden!");
			return;
		}
		String output = ResourceBundle.getBundle("gui.messages").getString("Messages.startBackup");
		listener.printOut(output, false);
		listener.log(output, task);

		try {
			// Eigentlicher Backup-Vorgang:
			while (!elementQueue.isEmpty()) {
				if (Thread.interrupted()) {
					throw new BackupCanceledException();
				}
				BackupElement currentElement = elementQueue.pop();
				if (currentElement.isDirectory()) {
					(new File(currentElement.getDestPath())).mkdir();
				} else {
					try {
						BackupHelper.copyFile(new File(currentElement.getSourcePath()),
								new File(currentElement.getDestPath()), listener, task);
					} catch (IOException e) {
						String msg = ResourceBundle.getBundle("gui.messages").getString("GUI.errCopyIOExMsg1")
								+ currentElement.getSourcePath()
								+ ResourceBundle.getBundle("gui.messages").getString("GUI.errCopyIOExMsg2");
						listener.printOut(msg, true);
						listener.log(msg, task);
					}

				}
			}

			String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.BackupComplete");
			listener.printOut(outprint, false);
			listener.log(outprint, task);
			listener.taskFinished(taskName);
		} catch (BackupCanceledException e) {
			String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
			listener.printOut(outprint, false);
			listener.log(outprint, task);
		}
	}

	// TODO: JavaDoc
	private void rekursivePreparation(File sourceFile, File backupDir, BackupTask task) {

		if (Thread.interrupted()) {
			throw new BackupCanceledException();
		}
		File[] files = sourceFile.listFiles();

		if (files == null) {
			String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.UnknownErrorAt") + " "
					+ sourceFile.getPath();
			listener.printOut(outprint, true);
			listener.log(outprint, task);

			return;
		}

		for (int i = 0; i < files.length; i++) {
			if (Thread.interrupted()) {
				throw new BackupCanceledException();
			}
			if (files[i].isDirectory()) {
				// Filtern:
				ArrayList<Filter> filtersOfThisSource = currentSource.getFilter();
				boolean filterMatches = false;
				for (int j = 0; j < filtersOfThisSource.size(); j++) {
					if ((files[i].getAbsolutePath().equals(filtersOfThisSource.get(j).getPath()))) {
						filterMatches = true;
					}
				}
				if (!filterMatches) {
					// Queuen:
					File newBackupDir = new File(backupDir.getAbsolutePath() + File.separator + files[i].getName());
					elementQueue.add(new BackupElement(files[i].getAbsolutePath(), newBackupDir.getAbsolutePath(),
							true, false));
					listener.increaseNumberOfDirectories();
					rekursivePreparation(files[i], newBackupDir, task);
				}
			} else {
				// Filtern:
				ArrayList<Filter> filtersOfThisSource = currentSource.getFilter();
				boolean filterMatches = false;
				for (int j = 0; j < filtersOfThisSource.size(); j++) {
					if (filtersOfThisSource.get(j).getMode() == 0
							&& files[i].getAbsolutePath().equals(filtersOfThisSource.get(j).getPath())) {
						filterMatches = true;
					}
				}
				if (!filterMatches) {
					// Queuen:
					File newFile = new File(backupDir.getAbsolutePath() + File.separator + files[i].getName());
					elementQueue.add(new BackupElement(files[i].getAbsolutePath(), newFile.getAbsolutePath(), false,
							false));
					listener.increaseNumberOfFilesToCopy();
					listener.increaseSizeToCopyBy(files[i].length());
				}
			}
		}
	}
}
