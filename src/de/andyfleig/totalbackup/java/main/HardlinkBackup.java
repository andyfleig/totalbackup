/*
 * Copyright 2014 - 2016 Andreas Fleig (andy DOT fleig AT gmail DOT com)
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.FilenameFilter;

import listener.IBackupListener;
import data.BackupElement;
import data.BackupInfos;
import data.BackupTask;
import data.Filter;
import data.Source;
import data.StructureFile;

/**
 * Ein Hardlink-Backup Objekt. Implementierung von Backupable.
 *
 * @author Andreas Fleig
 */
public class HardlinkBackup implements Backupable {

	/**
	 * Liste der Quellen
	 */
	private ArrayList<Source> sources;
	/**
	 * Name des zu bearbeitenden BackupTasks
	 */
	private String taskName;
	/**
	 * Zielpfad
	 */
	private String destinationPath;
	/**
	 * Listener zur Interaktion mit dem Controller
	 */
	private IBackupListener listener;
	/**
	 * Index für diesen Backup-Task
	 */
	private StructureFile directoryStructure;
	/**
	 * aktuellster Backup-Satz
	 */
	private String newestBackupPath;
	/**
	 * Zu bearbeitende Elemente
	 */
	private LinkedList<BackupElement> elementQueue;
	/**
	 * Zeigt ob die Vorbereitungen bereits getroffen wurde. Erst dann kann runBackup() aufgerufen werden.
	 */
	private boolean preparationDone = false;
	/**
	 * Aktuelles Backup-Directory.
	 */
	private File backupDir;
	/**
	 * Root-Verzeichnis der Quelle.
	 */
	private String sourceRootDir;
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
	public HardlinkBackup(IBackupListener listener, String nameOfTask, ArrayList<Source> sources, String destination) {
		this.listener = listener;
		this.taskName = nameOfTask;
		this.sources = sources;
		this.destinationPath = destination;
		elementQueue = new LinkedList<>();
	}

	@Override
	public void runPreparation(BackupTask task) {

		// Kontrollieren ob für jeden Backup-Satz ein Index vorhanden ist:
		File dest = new File(destinationPath);

		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		};

		File[] destFolders = dest.listFiles(filter);

		// Prüfen bei welchen Ordnern es sich um Backup-Sätze handelt und den
		// aktuellsten Backup-Satz finden:
		if (destFolders.length > 0) {
			for (File destFolder1 : destFolders) {
				boolean indexExists = false;
				if (destFolder1.isDirectory()) {
					// Namen des Ordners "zerlegen":
					StringTokenizer tokenizer = new StringTokenizer(destFolder1.getName(), "_");
					// Es wird geprüft ob der Name aus genau 2 Tokens besteht:
					if (tokenizer.countTokens() != 2) {
						continue;
					}
					// Erster Token muss dem TaskName entsprechen:
					if (!tokenizer.nextToken().equals(taskName)) {
						continue;
					}
					// Es handelt sich wohl um einen Backup-Satz
					File[] destFolder = destFolder1.listFiles();
					for (File aDestFolder : destFolder) {
						if (!aDestFolder.isDirectory() && aDestFolder.getName().contains(taskName) &&
								aDestFolder.getName().contains(".ser")) {
							// Ab hier wird davon ausgegangen, dass ein index-file
							// exisitert.
							indexExists = true;
							break;
						}
					}
				}
				// Falls kein index gefunden wurde, wird ein index angelegt:
				if (!indexExists) {
					String outprint = ResourceBundle.getBundle("messages").getString("Messages.noValidIndexIndexing");
					listener.printOut(outprint, false, task.getTaskName());
					listener.log(outprint, task);

					createIndex(destFolder1, task);

					// Indizierung wurde abgebrochen:
					if (directoryStructure == null) {
						throw new BackupCanceledException();
					}

					outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexCreated");
					listener.printOut(outprint, false, task.getTaskName());
					listener.log(outprint, task);

					outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexSaving");
					listener.printOut(outprint, false, task.getTaskName());
					listener.log(outprint, task);

					serializeIndex(taskName, destFolder1.getAbsolutePath());
					outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexSaved");
					listener.printOut(outprint, false, task.getTaskName());
					listener.log(outprint, task);
				}
			}
		}


		// Herausfinden welcher Backup-Satz der Neuste ist und diesen laden:
		// Neusten Backup-Ordner finden:
		newestBackupPath = findNewestBackup(destinationPath);
		if (newestBackupPath == null) {
			String outprint = ResourceBundle.getBundle("messages").getString("Messages.noValidIndexCanceled");
			listener.printOut(outprint, true, task.getTaskName());
			listener.log(outprint, task);
			return;
		}

		// Index dieses backups einlesen:
		File index = new File(
				destinationPath + File.separator + newestBackupPath + File.separator + "index_" + taskName + ".ser");

		// Pfad prüfen:
		if (!index.exists()) {
			String outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexNotFound");
			listener.printOut(outprint, true, task.getTaskName());
			listener.log(outprint, task);
			return;
		}

		if (!loadSerialization(index)) {
			String outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexCorrupted");
			listener.printOut(outprint, false, task.getTaskName());
			listener.log(outprint, task);

			createIndex(index, task);

			// Indizierung wurde abgebrochen:
			if (directoryStructure == null) {
				throw new BackupCanceledException();
			}

			outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexCreated");
			listener.printOut(outprint, false, task.getTaskName());
			listener.log(outprint, task);

			outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexSaving");
			listener.printOut(outprint, false, task.getTaskName());
			listener.log(outprint, task);

			serializeIndex(taskName, index.getAbsolutePath());

			outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexSaved");
			listener.printOut(outprint, false, task.getTaskName());
			listener.log(outprint, task);

			// Index erneut laden:
			if (!loadSerialization(index)) {
				outprint = ResourceBundle.getBundle("messages").getString("Messages.FatalErrorIndexing");
				listener.printOut(outprint, true, task.getTaskName());
				listener.log(outprint, task);
				return;
			}
		}
		// Eigentliches Hardlink Backup:
		// Backup-Ordner anlegen:
		backupDir = BackupHelper.createBackupFolder(destinationPath, taskName, listener, task);

		String outprint = ResourceBundle.getBundle("messages").getString("Messages.PreparationStarted");
		listener.printOut(outprint, false, task.getTaskName());
		listener.log(outprint, task);

		if (backupDir == null) {
			outprint = ResourceBundle.getBundle("messages").getString("Messages.BackupFolderCreationError");
			listener.printOut(outprint, true, task.getTaskName());
			return;
		}

		try {
			for (int i = 0; i < sources.size(); i++) {
				if (Thread.interrupted()) {
					throw new BackupCanceledException();
				}
				// Für die Filterung:
				currentSource = sources.get(i);

				File sourceFile = new File(sources.get(i).getPath());

				// Sonderbehandlung für Windows, wenn der SourcePath das
				// root-dir eines Volume (z.B. C:/) ist:
				String folder;
				File f;
				if (!sourceFile.isDirectory()) {
					f = backupDir;
				} else {
					if (sourceFile.getAbsolutePath().contains(":\\") && sourceFile.getAbsolutePath().length() == 3 &&
							sourceFile.getName().equals("")) {
						// In diesem Sonderfall ergibt sich der Name nur aus dem
						// Laufwerksbuchstaben:
						folder = backupDir + File.separator + sourceFile.getAbsolutePath().charAt(0);
					} else {
						folder = backupDir + File.separator + sourceFile.getName();
					}

					f = new File(folder);

					if (f.mkdir()) {
						outprint = ResourceBundle.getBundle("messages").getString("Messages.FolderCreated");
						listener.printOut(outprint, false, task.getTaskName());
						listener.log(outprint, task);
					} else {
						outprint = ResourceBundle.getBundle("messages").getString("Messages.FolderCreationError");
						listener.printOut(outprint, true, task.getTaskName());
						listener.log(outprint, task);
					}
				}

				// Queueing:
				try {
					for (Source source : sources) {
						// Sonderfall: Wenn der Backup-Root == Root des Systems
						// ist:
						if (sourceFile.getName().length() == 0) {
							sourceRootDir = "";
						} else {
							sourceRootDir = sourceFile.getAbsolutePath().substring(0,
									source.getPath().length() - sourceFile.getName().length());
						}
						rekursivePreparation(new File(source.getPath()), f, task);
					}
				} catch (BackupCanceledException e) {
					outprint = ResourceBundle.getBundle("messages").getString("Messages.CanceledByUser");
					listener.printOut(outprint, false, task.getTaskName());
					listener.log(outprint, task);
					isCanceled = true;
				}
			}
		} catch (BackupCanceledException e) {
			outprint = ResourceBundle.getBundle("messages").getString("Messages.CanceledByUser");
			listener.printOut(outprint, false, task.getTaskName());
			listener.log(outprint, task);
			isCanceled = true;
		}
		if (!isCanceled) {
			String output = ResourceBundle.getBundle("messages").getString("Messages.PreparationDone");
			listener.printOut(output, false, task.getTaskName());
			listener.log(output, task);
			preparationDone = true;
		} else {
			listener.deleteEmptyBackupFolders(task.getDestinationPath(), task);
		}
	}

	@Override
	public void runBackup(BackupTask task) {
		// Test ob die Vorbereitung durchgeführt wurden:
		if (!preparationDone) {
			System.out.println("Fehler: Vorbereitung muss zuerst ausgeführt werden!");
			return;
		}

		String outprint = ResourceBundle.getBundle("messages").getString("Messages.startBackup");
		listener.printOut(outprint, false, task.getTaskName());
		listener.log(outprint, task);
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
					if (currentElement.toLink()) {
						BackupHelper.hardlinkFile(new File(currentElement.getSourcePath()),
								new File(currentElement.getDestPath()), listener, task);
					} else {
						try {
							BackupHelper.copyFile(new File(currentElement.getSourcePath()),
									new File(currentElement.getDestPath()), listener, task);
						} catch (IOException e) {
							String msg = ResourceBundle.getBundle("messages").getString("GUI.errCopyIOExMsg1") +
									currentElement.getSourcePath() +
									ResourceBundle.getBundle("messages").getString("GUI.errCopyIOExMsg2");
							listener.printOut(msg, true, task.getTaskName());
							listener.log(msg, task);
						}
					}
				}
			}

			// Index des Backup-Satzes erzeugen und serialisiert:
			createIndex(backupDir, task);

			// Indizierung wurde abgebrochen:
			if (directoryStructure == null) {
				throw new BackupCanceledException();
			}

			outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexCreated");
			listener.printOut(outprint, false, task.getTaskName());
			listener.log(outprint, task);
			outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexSaving");
			listener.printOut(outprint, false, task.getTaskName());
			listener.log(outprint, task);

			serializeIndex(taskName, backupDir.getAbsolutePath());

			outprint = ResourceBundle.getBundle("messages").getString("Messages.BackupComplete");
			listener.printOut(outprint, false, task.getTaskName());
			listener.log(outprint, task);
			listener.taskFinished(task);
		} catch (BackupCanceledException e) {
			outprint = ResourceBundle.getBundle("messages").getString("Messages.CanceledByUser");
			listener.printOut(outprint, false, task.getTaskName());
			listener.log(outprint, task);
		}
	}

	/**
	 * Rekursive Mathode zur Vorbereitung ("analyse") eines Hardlink Backups.
	 *
	 * @param sourceFile Quell-Verzeichnis
	 * @param backupDir  Ziel-Verzeichnis
	 * @param task       betreffender BackupTask
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
			String outprint = ResourceBundle.getBundle("messages").getString("Messages.UnknownErrorAt") + " " +
					sourceFile.getPath();
			listener.printOut(outprint, true, task.getTaskName());
			listener.log(outprint, task);

			return;
		}
		for (File file : files) {
			if (Thread.interrupted()) {
				throw new BackupCanceledException();
			}
			if (file.isDirectory()) {
				// Filtern:
				ArrayList<Filter> filtersOfThisSource = currentSource.getFilter();
				boolean filterMatches = false;
				for (Filter aFiltersOfThisSource : filtersOfThisSource) {
					if ((file.getAbsolutePath().equals(aFiltersOfThisSource.getPath()))) {
						filterMatches = true;
					}
				}
				if (!filterMatches) {
					File newBackupDir = new File(backupDir.getAbsolutePath() + File.separator + file.getName());
					elementQueue.add(
							new BackupElement(file.getAbsolutePath(), newBackupDir.getAbsolutePath(), true, false));
					backupInfos.increaseNumberOfDirectories();
					rekursivePreparation(file, newBackupDir, task);
				}

			} else {
				// Filtern:
				ArrayList<Filter> filtersOfThisSource = currentSource.getFilter();
				boolean filterMatches = false;
				for (Filter aFiltersOfThisSource : filtersOfThisSource) {
					if (aFiltersOfThisSource.getMode() == 0 &&
							(file.getAbsolutePath().equals(aFiltersOfThisSource.getPath()))) {
						filterMatches = true;
					}
				}
				if (!filterMatches) {
					// Herausfinden ob zu kopieren oder zu verlinken:
					// Entsprechendes StrucutreFile aus dem Index:

					StructureFile fileInIndex = getStructureFileFromIndex(file, sourceRootDir);

					File newFile = new File(backupDir.getAbsolutePath() + File.separator + file.getName());

					if (fileInIndex == null) {
						// Befindet die Datei sich nicht im Index, wird sie
						// kopiert (nicht verlinkt)
						// Es handelt sich also um eine neue Datei (bisher nicht
						// im Backup)
						elementQueue.add(
								new BackupElement(file.getAbsolutePath(), newFile.getAbsolutePath(), false, false));
						backupInfos.increaseNumberOfFilesToCopy();
						backupInfos.increaseSizeToCopyBy(file.length());
						continue;
					}
					if (file.lastModified() > fileInIndex.getLastModifiedDate()) {
						// Datei liegt in einer älteren Version im Backup vor
						// Datei zu kopieren:
						elementQueue.add(
								new BackupElement(file.getAbsolutePath(), newFile.getAbsolutePath(), false, false));
						backupInfos.increaseNumberOfFilesToCopy();
						backupInfos.increaseSizeToCopyBy(file.length());
					} else {
						// Datei liegt in der aktuellen Version vor

						// Test ob die Datei im Backup-Satz vorhanden ist:
						File fileToLinkFrom = new File(
								destinationPath + File.separator + newestBackupPath + fileInIndex.getFilePath());
						if (fileToLinkFrom.exists()) {
							// Filterung:
							filterMatches = false;
							for (Filter aFiltersOfThisSource : filtersOfThisSource) {
								if (aFiltersOfThisSource.getMode() == 1 &&
										(file.getAbsolutePath().equals(aFiltersOfThisSource.getPath()))) {
									filterMatches = true;
								}
							}
							if (!filterMatches) {
								// Datei verlinken:
								elementQueue.add(
										new BackupElement(fileToLinkFrom.getAbsolutePath(), newFile.getAbsolutePath(),
												false, true));
								backupInfos.increaseNumberOfFilesToLink();
								backupInfos.increaseSizeToLinkBy(file.length());
							} else {
								// Überprüfung der MD5 Summe:
								// Gleiche MD5 heißt verlinken, unterschiedliche
								// MD5 heißt kopieren:
								String md5OfSourceFile = BackupHelper.calcMD5(file);
								String md5OfFileToLinkFrom = BackupHelper.calcMD5(fileToLinkFrom);
								if (md5OfSourceFile != null && md5OfSourceFile.equals(md5OfFileToLinkFrom)) {
									// Datei verlinken:
									elementQueue.add(new BackupElement(fileToLinkFrom.getAbsolutePath(),
											newFile.getAbsolutePath(), false, true));
									backupInfos.increaseNumberOfFilesToLink();
									backupInfos.increaseSizeToLinkBy(file.length());
								} else {
									// Datei zu kopieren:
									elementQueue.add(
											new BackupElement(file.getAbsolutePath(), newFile.getAbsolutePath(), false,
													false));
									backupInfos.increaseNumberOfFilesToCopy();
									backupInfos.increaseSizeToCopyBy(file.length());
								}
							}

						} else {
							// File existiert im Backup-Satz nicht (aber im
							// Index)
							String outprint = ResourceBundle.getBundle("messages").getString("Messages.BadIndex");
							listener.printOut(outprint, false, task.getTaskName());
							listener.log(outprint, task);

							outprint = ResourceBundle.getBundle("messages").getString("Messages.DeletingIndex");
							listener.printOut(outprint, false, task.getTaskName());
							listener.log(outprint, task);

							// Root-Pfad des Index "sichern":
							String rootPathForIndex = file.getAbsolutePath();

							// Ungültiger Index wird gelöscht:
							File badIndex = new File(file.getAbsolutePath() + directoryStructure.getFilePath());
							badIndex.delete();

							outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexDeleted");
							listener.printOut(outprint, false, task.getTaskName());
							listener.log(outprint, task);

							// Neu indizieren:
							outprint = ResourceBundle.getBundle("messages").getString("Messages.Indexing");
							listener.printOut(outprint, false, task.getTaskName());
							listener.log(outprint, task);
							createIndex(new File(rootPathForIndex), task);

							// Indizierung wurde abgebrochen:
							if (directoryStructure == null) {
								throw new BackupCanceledException();
							}

							outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexCreated");
							listener.printOut(outprint, false, task.getTaskName());
							listener.log(outprint, task);
							outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexSaving");
							listener.printOut(outprint, false, task.getTaskName());
							listener.log(outprint, task);

							serializeIndex(taskName, rootPathForIndex);

							outprint = ResourceBundle.getBundle("messages").getString("Messages.IndexSaved");
							listener.printOut(outprint, false, task.getTaskName());
							listener.log(outprint, task);
							// Datei zu kopieren:
							elementQueue.add(
									new BackupElement(file.getAbsolutePath(), newFile.getAbsolutePath(), false, false));
							backupInfos.increaseNumberOfFilesToCopy();
							backupInfos.increaseSizeToCopyBy(file.length());
						}
					}
				}
			}
		}
	}

	/**
	 * Gibt die Datei (als StructureFile) aus dem Index zurück, falls diese dort vorhanden ist. Ist die Datei nicht im
	 * Index wird null zurückgegeben.
	 *
	 * @param file           Datei für welche das StrucutreFile zurückgegeben werden soll
	 * @param sourceRootPath Root-Pfad der Quelle
	 * @return Gefundenes StructureFile oder null
	 */
	private StructureFile getStructureFileFromIndex(File file, String sourceRootPath) {

		// Namen der Datei "zerlegen":
		StringTokenizer tokenizerOfFile = new StringTokenizer(file.getAbsolutePath().substring(sourceRootPath.length()),
				File.separator);
		StructureFile currentStructureFile = directoryStructure;
		StructureFile tmp;

		while (tokenizerOfFile.hasMoreTokens()) {

			tmp = currentStructureFile.getStructureFile(tokenizerOfFile.nextToken());

			if (tmp != null) {
				currentStructureFile = tmp;
			} else {
				return null;
			}
		}
		return currentStructureFile;
	}

	/**
	 * Erzeugt den Index.
	 *
	 * @param root Root-File zur Indizierung
	 * @param task betreffender BackupTask
	 */
	private void createIndex(File root, BackupTask task) {
		if (root.isDirectory()) {
			// Verzeichnisstruktur-Objekt erzeugen:
			try {
				directoryStructure = recCalcDirStruct(root.getAbsolutePath(), root.getAbsolutePath());
			} catch (BackupCanceledException e) {
				directoryStructure = null;
				String output = ResourceBundle.getBundle("messages").getString("Messages.IndexingCanceled");
				listener.printOut(output, false, task.getTaskName());
				listener.log(output, task);
			}
		}
	}

	/**
	 * Serialisiert den Index.
	 *
	 * @param taskName      Name des Tasks des zu serialisierenden Index
	 * @param backupSetPath Pfad zum Backup-Satz
	 */
	private void serializeIndex(String taskName, String backupSetPath) {

		// Verzeichnisstruktur speichern:
		// File anlegen:
		File index = new File(backupSetPath + File.separator + "index_" + taskName + ".ser");
		// Prüfen ob bereits ein Index existiert:
		if (!index.exists()) {
			try {
				index.createNewFile();
			} catch (IOException ex) {
				System.err.println("Error: IOException in HardlinkBackup while creating new File");
			}
		}
		// OutputStreams anlegen:
		OutputStream fos = null;
		ObjectOutputStream o = null;

		try {
			fos = new FileOutputStream(index);
			o = new ObjectOutputStream(fos);

			o.writeObject(this.directoryStructure);
		} catch (IOException ex) {
			System.out.println("Error: IOException in HardlinkBackup in serializeIndex while creating " +
					"FileOutputStream, ObjectOutputStream and writing Object");
		} finally {
			if (o != null) {
				try {
					o.close();
				} catch (IOException ex) {
					System.err.println(
							"Error: IOException in HardlinkBackup in serializeIndex while closing ObjectOutputStream");
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex) {
					System.err.println(
							"Error: IOException in HardlinkBackup in serializeIndex while closing FileOutputStream");
				}
			}
		}
	}

	/**
	 * Lädt einen seriallisierten Index. Gibt bei Erfolg TRUE und sonst FALSE zurück;
	 *
	 * @param index zu ladender Index
	 */
	private boolean loadSerialization(File index) {

		boolean result = true;

		ObjectInputStream ois = null;
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(index);
			ois = new ObjectInputStream(fis);

			directoryStructure = (StructureFile) ois.readObject();
		} catch (IOException e) {
			System.err.println("Error: IOException in HardlinkBackup in loadSerialization while creating " +
					"FileInputStream, ObjectInputStream and reading Object");
			result = false;
		} catch (ClassNotFoundException e) {
			System.err.println("Error: ClassNotFoundException in HardlinkBackup in loadSerialization while creating " +
					"FileInputStream, ObjectInputStream and reading Object");
			result = false;
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					System.err.println(
							"Error: IOException in HardlinkBackup in loadSerialization while closing ObjectInputStream");
					result = false;
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					System.err.println(
							"Error: IOException in HardlinkBackup in loadSerialization while closing FileInputStream");
					result = false;
				}
			}
		}
		return result;
	}

	/**
	 * Rekursive Methode zur Berechnung der Verzeichnisstruktur.
	 *
	 * @param rootPath root Pfad des Backups
	 * @param path     Pfad der aktuell zu analysierenden Datei (relativer Pfad)
	 * @return StructureFile für die Verzeichnisstruktur
	 */
	private StructureFile recCalcDirStruct(String rootPath, String path) {
		if (Thread.interrupted()) {
			throw new BackupCanceledException();
		}

		File[] files = new File(path).listFiles();

		StructureFile sFile;
		// Sonderbehandlung für Windows, wenn der SourcePath das
		// root-dir eines Volume (z.B. C:/) ist:
		String nameOfBackupDir = path.substring(rootPath.length());
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.contains("win") && nameOfBackupDir.length() == 2) {
			sFile = new StructureFile(rootPath, nameOfBackupDir.substring(1) + ":");
		} else {
			sFile = new StructureFile(rootPath, nameOfBackupDir);
		}
		if (files.length > 0) {
			for (File file : files) {
				StructureFile newFile;
				if (file.isDirectory()) {
					newFile = recCalcDirStruct(rootPath, file.getAbsolutePath());
				} else {
					newFile = new StructureFile(rootPath, file.getAbsolutePath().substring(rootPath.length()));
				}
				sFile.addFile(newFile);
			}
		}

		return sFile;
	}

	/**
	 * Gibt den Pfad (als String) zum aktuellsten Backup-Satz zurück.
	 *
	 * @param rootPath Ordner in dem nach Backup-Sätzen gesucht werden soll
	 * @return Pfad zum aktuellsten Backup-Satz
	 */
	private String findNewestBackup(String rootPath) {
		File root = new File(rootPath);
		File[] directories = root.listFiles();

		Date newestDate = null;
		String newestBackupPath = null;
		Date foundDate;
		if (directories.length > 0) {
			for (File directory : directories) {
				if (directory.isDirectory()) {
					// Namen des Ordners "zerlegen":
					StringTokenizer tokenizer = new StringTokenizer(directory.getName(), "_");
					// Es wird geprüft ob der Name aus genau 2 Tokens besteht:
					if (tokenizer.countTokens() != 2) {
						continue;
					}
					// Erster Token muss dem TaskName entsprechen:
					if (!tokenizer.nextToken().equals(taskName)) {
						continue;
					}
					// Zweiter Token muss analysiert werden:
					String backupDate = tokenizer.nextToken();

					try {
						SimpleDateFormat sdfToDate = new SimpleDateFormat(BackupHelper.BACKUP_FOLDER_NAME_PATTERN);
						foundDate = sdfToDate.parse(backupDate);
					} catch (ParseException e) {
						// Offenbar kein gültiges Datum
						continue;
					}
					if (newestDate == null) {
						newestDate = foundDate;
						newestBackupPath = directory.getName();
					} else {
						if (newestDate.compareTo(foundDate) < 0) {
							newestDate = foundDate;
							newestBackupPath = directory.getName();
						}
					}
				}
			}
		}
		return newestBackupPath;
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
