/*
 * Copyright 2014, 2015 Andreas Fleig (andy DOT fleig AT gmail DOT com)
 * 
 * All rights reserved.
 * 
 * This file is part of TotalBackup.
 *
 * TotalBackup is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TotalBackup is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TotalBackup.  If not, see <http://www.gnu.org/licenses/>.
 */
package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.*;

import listener.IBackupListener;
import data.BackupElement;
import data.BackupInfos;
import data.BackupTask;
import data.Filter;
import data.Source;

/**
 * Ein Normal-Backup Objekt. Implementierung von Backupable.
 *
 * @author Andreas Fleig
 */
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
	 * Zeigt ob die Vorbereitungen bereits getroffen wurde. Erst dann kann runBackup() aufgerufen werden.
	 */
	private boolean preparationDone = false;
	/**
	 * Informationen (der Vorbereitung) über dieses Backup.
	 */
	private BackupInfos backupInfos = new BackupInfos();

	/**
	 * Quelle an der aktuell "gearbeitet" wird (für das Filtern der zu queuenden Elemente).
	 */
	private Source currentSource;

	/**
	 * Gibt an ob dieses Backup gecanceled ist.
	 */
	private boolean isCanceled;

	/**
	 * Backup-Objekt zur Datensicherung.
	 *
	 * @param listener    Listener
	 * @param nameOfTask  Name des Backup-Tasks
	 * @param sources     Quellen
	 * @param destination Zielpfad
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
			String output = ResourceBundle.getBundle("messages").getString("Messages.BackupFolderCreationError");
			listener.printOut(output, true, task.getTaskName());
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
				File f;
				if (!sourceFile.isDirectory()) {
					f = dir;
				} else {
					if (sourceFile.getAbsolutePath().contains(
							":\\") && sourceFile.getAbsolutePath().length() == 3 && sourceFile.getName().equals("")) {
						// In diesem Sonderfall ergibt sich der Name nur aus dem
						// Laufwerksbuchstaben:
						folder = dir.getAbsolutePath() + File.separator + sourceFile.getAbsolutePath().charAt(0);
					} else {
						folder = dir.getAbsolutePath() + File.separator + sourceFile.getName();
					}

					f = new File(folder);

					if (f.mkdir()) {
						String outprint = ResourceBundle.getBundle("messages").getString("Messages.FolderCreated");
						listener.printOut(outprint, false, task.getTaskName());
						listener.log(outprint, task);
					} else {
						String outprint = ResourceBundle.getBundle("messages").getString(
								"Messages.FolderCreationError");
						listener.printOut(outprint, true, task.getTaskName());
						listener.log(outprint, task);
					}
				}

				String output = ResourceBundle.getBundle("messages").getString("Messages.PreparationStarted");
				listener.printOut(output, false, task.getTaskName());
				listener.log(output, task);

				// Queueing:
				try {
					for (int j = 0; j < sources.size(); j++) {
						rekursivePreparation(new File(sources.get(j).getPath()), f, task);
					}
				} catch (BackupCanceledException e) {
					String outprint = ResourceBundle.getBundle("messages").getString("Messages.CanceledByUser");
					listener.printOut(outprint, false, task.getTaskName());
					listener.log(outprint, task);
					isCanceled = true;
				}
			}
		} catch (BackupCanceledException e) {
			String outprint = ResourceBundle.getBundle("messages").getString("Messages.CanceledByUser");
			listener.printOut(outprint, false, task.getTaskName());
			listener.log(outprint, task);
			isCanceled = true;
		}
		// TODO: Warum hier noch nicht gecanceled?
		if (!isCanceled) {
			String output = ResourceBundle.getBundle("messages").getString("Messages.PreparationDone");
			listener.printOut(output, false, task.getTaskName());
			listener.log(output, task);
			preparationDone = true;
		} else {
			listener.deleteEmptyBackupFolders(task.getDestinationPath(), task);
		}
	}

	/**
	 * Startet den Backup-Vorgang.
	 *
	 * @param task Backup-Tasks welcher ausgeführt wird
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void runBackup(BackupTask task) throws FileNotFoundException {
		// Test ob die Vorbereitung durchgeführt wurden:
		if (!preparationDone) {
			System.out.println("Fehler: Vorbereitung muss zuerst ausgeführt werden!");
			return;
		}
		String output = ResourceBundle.getBundle("messages").getString("Messages.startBackup");
		listener.printOut(output, false, task.getTaskName());
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
						String msg = ResourceBundle.getBundle("messages").getString(
								"GUI.errCopyIOExMsg1") + currentElement.getSourcePath() + ResourceBundle.getBundle(
								"messages").getString("GUI.errCopyIOExMsg2");
						listener.printOut(msg, true, task.getTaskName());
						listener.log(msg, task);
					}

				}
			}

			String outprint = ResourceBundle.getBundle("messages").getString("Messages.BackupComplete");
			listener.printOut(outprint, false, task.getTaskName());
			listener.log(outprint, task);
			listener.taskFinished(task);
		} catch (BackupCanceledException e) {
			String outprint = ResourceBundle.getBundle("messages").getString("Messages.CanceledByUser");
			listener.printOut(outprint, false, task.getTaskName());
			listener.log(outprint, task);
		}
	}

	/**
	 * Führt die rekursive Backup-Vorbereitung durch.
	 *
	 * @param sourceFile Quellverzeichnis
	 * @param backupDir  Backup-Verzeichnis (im Zielverzeichnis)
	 * @param task       zugehöriger BackupTask
	 */
	private void rekursivePreparation(File sourceFile, File backupDir, BackupTask task) {

		if (Thread.interrupted()) {
			throw new BackupCanceledException();
		}
		File[] files;
		if (sourceFile.isDirectory()) {
			files = sourceFile.listFiles();
		} else {
			files = new File[1];
			files[0] = sourceFile;
		}
		if (files == null) {
			String outprint = ResourceBundle.getBundle("messages").getString(
					"Messages.UnknownErrorAt") + " " + sourceFile.getPath();
			listener.printOut(outprint, true, task.getTaskName());
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
					elementQueue.add(
							new BackupElement(files[i].getAbsolutePath(), newBackupDir.getAbsolutePath(), true, false));
					backupInfos.increaseNumberOfDirectories();
					rekursivePreparation(files[i], newBackupDir, task);
				}
			} else {
				// Filtern:
				ArrayList<Filter> filtersOfThisSource = currentSource.getFilter();
				boolean filterMatches = false;
				for (int j = 0; j < filtersOfThisSource.size(); j++) {
					if (filtersOfThisSource.get(j).getMode() == 0 && files[i].getAbsolutePath().equals(
							filtersOfThisSource.get(j).getPath())) {
						filterMatches = true;
					}
				}
				if (!filterMatches) {
					// Queuen:
					File newFile = new File(backupDir.getAbsolutePath() + File.separator + files[i].getName());
					elementQueue.add(
							new BackupElement(files[i].getAbsolutePath(), newFile.getAbsolutePath(), false, false));
					backupInfos.increaseNumberOfFilesToCopy();
					backupInfos.increaseSizeToCopyBy(files[i].length());
				}
			}
		}
	}

	@Override
	public BackupInfos getBackupInfos() {
		return backupInfos;
	}

	@Override
	public boolean isCanceled() {
		return isCanceled;
	}

	@Override
	public void cancel() {
		isCanceled = true;
	}
}
