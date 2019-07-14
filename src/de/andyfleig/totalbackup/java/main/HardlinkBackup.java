/*
 * Copyright 2014 - 2019 Andreas Fleig (github AT andyfleig DOT de)
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
 * Implementation of Backupable for hardlink backups.
 *
 * @author Andreas Fleig
 */
public class HardlinkBackup implements Backupable {

	private String taskName;
	private ArrayList<Source> sources;
	private String destinationPath;
	private IBackupListener listener;
	/**
	 * Index for this BackupTask (used to decide whether a file has to be linked or copied).
	 */
	private StructureFile directoryStructure;
	/**
	 * Path of the most recent backup set.
	 */
	private String newestBackupPath;
	/**
	 * List of BackupElements to process as part of this backup.
	 */
	private LinkedList<BackupElement> elementQueue;
	private boolean preparationDone = false;
	private boolean isCanceled;
	private BackupInfos backupInfos = new BackupInfos();
	private File backupDir;
	private String sourceRootDir;
	private Source currentSource;


	/**
	 * Creates a new hardlink backup Backupable object.
	 *
	 * @param listener    corresponding IBackupListener instance
	 * @param nameOfTask  name of the BackupTask
	 * @param sources     list of sources
	 * @param destination destination path
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
		// checks whether there is an index for every existing backup set
		File dest = new File(destinationPath);

		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		};
		File[] destFolders = dest.listFiles(filter);

		// find backup sets to locate the most recent one
		if (destFolders.length > 0) {
			for (File destFolder1 : destFolders) {
				boolean indexExists = false;
				if (destFolder1.isDirectory()) {
					// split up name of the directory
					StringTokenizer tokenizer = new StringTokenizer(destFolder1.getName(), "_");
					// has to consist of exactly two parts (name of the BackupTask and date)
					if (tokenizer.countTokens() != 2) {
						continue;
					}
					if (!tokenizer.nextToken().equals(taskName)) {
						continue;
					}
					// found an existing backup set
					File[] destFolder = destFolder1.listFiles();
					for (File aDestFolder : destFolder) {
						if (!aDestFolder.isDirectory() && aDestFolder.getName().contains(taskName) &&
								aDestFolder.getName().contains(".ser")) {
							// corresponding index existing
							indexExists = true;
							break;
						}
					}
				}
				// create index if no index exists
				if (!indexExists) {
					String outprint = "No valid Index found. Indexing Backup...";
					listener.setStatus(outprint, false, task.getTaskName());
					listener.log(outprint, task);

					createIndex(destFolder1, task);

					// indexing was cancelled
					if (directoryStructure == null) {
						throw new BackupCanceledException();
					}

					outprint = "Index created";
					listener.setStatus(outprint, false, task.getTaskName());
					listener.log(outprint, task);

					outprint = "Saving Index...";
					listener.setStatus(outprint, false, task.getTaskName());
					listener.log(outprint, task);

					serializeIndex(taskName, destFolder1.getAbsolutePath());
					outprint = "Index saved";
					listener.setStatus(outprint, false, task.getTaskName());
					listener.log(outprint, task);
				}
			}
		}

		// find most recent backup set and load it
		newestBackupPath = findMostRecentBackup(destinationPath);
		if (newestBackupPath == null) {
			String outprint = "Error: No valid Index found\nTask canceled";
			listener.setStatus(outprint, true, task.getTaskName());
			listener.log(outprint, task);
			return;
		}

		// read index of this backup set
		File index = new File(
				destinationPath + File.separator + newestBackupPath + File.separator + "index_" + taskName + ".ser");

		// check path
		if (!index.exists()) {
			String outprint = "Error: Index not found\nBackup canceled";
			listener.setStatus(outprint, true, task.getTaskName());
			listener.log(outprint, task);
			return;
		}

		if (!loadSerialization(index)) {
			String outprint = "Existing Index is corrupted!\nIndexing Backup...";
			listener.setStatus(outprint, false, task.getTaskName());
			listener.log(outprint, task);

			createIndex(index, task);

			// indexing was cancelled
			if (directoryStructure == null) {
				throw new BackupCanceledException();
			}

			outprint = "Index created";
			listener.setStatus(outprint, false, task.getTaskName());
			listener.log(outprint, task);

			outprint = "Saving Index...";
			listener.setStatus(outprint, false, task.getTaskName());
			listener.log(outprint, task);

			serializeIndex(taskName, index.getAbsolutePath());

			outprint = "Index saved";
			listener.setStatus(outprint, false, task.getTaskName());
			listener.log(outprint, task);

			// reload index
			if (!loadSerialization(index)) {
				outprint = "Fatal Error while loading Index!";
				listener.setStatus(outprint, true, task.getTaskName());
				listener.log(outprint, task);
				return;
			}
		}
		// actual backup process starts here
		// create backup folder
		backupDir = BackupHelper.createBackupFolder(destinationPath, taskName, listener, task);

		String outprint = "Analyzing source...";
		listener.setStatus(outprint, false, task.getTaskName());
		listener.log(outprint, task);

		if (backupDir == null) {
			outprint = "Error while creating Backup-Folder!";
			listener.setStatus(outprint, true, task.getTaskName());
			return;
		}

		try {
			for (int i = 0; i < sources.size(); i++) {
				if (Thread.interrupted()) {
					throw new BackupCanceledException();
				}
				// for filtering
				currentSource = sources.get(i);

				File sourceFile = new File(sources.get(i).getPath());

				// handling for windows special case where the source path is the root of a volume (e.g. C:/)
				String folder;
				File f;
				if (!sourceFile.isDirectory()) {
					f = backupDir;
				} else {
					if (sourceFile.getAbsolutePath().contains(":\\") && sourceFile.getAbsolutePath().length() == 3 &&
							sourceFile.getName().equals("")) {
						// name is created from the volume letter in this special case
						folder = backupDir + File.separator + sourceFile.getAbsolutePath().charAt(0);
					} else {
						folder = backupDir + File.separator + sourceFile.getName();
					}

					f = new File(folder);

					if (f.mkdir()) {
						outprint = "Folder created";
						listener.setStatus(outprint, false, task.getTaskName());
						listener.log(outprint, task);
					} else {
						outprint = "Error while creating Folder";
						listener.setStatus(outprint, true, task.getTaskName());
						listener.log(outprint, task);
					}
				}

				// queueing:
				try {
					for (Source source : sources) {
						// special case when backup root is system root
						if (sourceFile.getName().length() == 0) {
							sourceRootDir = "";
						} else {
							sourceRootDir = sourceFile.getAbsolutePath().substring(0,
									source.getPath().length() - sourceFile.getName().length());
						}
						rekursivePreparation(new File(source.getPath()), f, task);
					}
				} catch (BackupCanceledException e) {
					outprint = "Backup canceled by User";
					listener.setStatus(outprint, false, task.getTaskName());
					listener.log(outprint, task);
					isCanceled = true;
				}
			}
		} catch (BackupCanceledException e) {
			outprint = "ackup canceled by User";
			listener.setStatus(outprint, false, task.getTaskName());
			listener.log(outprint, task);
			isCanceled = true;
		}
		if (!isCanceled) {
			String output = "Finished analyzing source";
			listener.setStatus(output, false, task.getTaskName());
			listener.log(output, task);
			preparationDone = true;
		} else {
			listener.deleteEmptyBackupFolders(task);
		}
	}

	@Override
	public void runBackup(BackupTask task) {
		if (!preparationDone) {
			System.out.println("Error: Tying to run backup without previously running the preparation.");
			return;
		}

		String outprint = "Backup-Task started";
		listener.setStatus(outprint, false, task.getTaskName());
		listener.log(outprint, task);
		listener.taskStarted(task.getTaskName());
		try {
			// actual backup process starts here
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
							String msg = "Error: Could not read " + currentElement.getSourcePath() +
									". Therefore this file will be ignored!";
							listener.setStatus(msg, true, task.getTaskName());
							listener.log(msg, task);
						}
					}
				}
			}

			// create and serialize the index of the backup set
			createIndex(backupDir, task);

			// indexing was cancelled
			if (directoryStructure == null) {
				throw new BackupCanceledException();
			}

			outprint = "Index created";
			listener.setStatus(outprint, false, task.getTaskName());
			listener.log(outprint, task);
			outprint = "Saving Index...";
			listener.setStatus(outprint, false, task.getTaskName());
			listener.log(outprint, task);

			serializeIndex(taskName, backupDir.getAbsolutePath());

			outprint = "Backup done";
			listener.setStatus(outprint, false, task.getTaskName());
			listener.log(outprint, task);
			listener.taskFinished(task);
		} catch (BackupCanceledException e) {
			outprint = "Backup canceled by User";
			listener.setStatus(outprint, false, task.getTaskName());
			listener.log(outprint, task);
			listener.taskFinished(task);
		}
	}

	/**
	 * Recursive method for the actual preparation of the backup.
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
			String outprint = "Unknown error at " + sourceFile.getPath();
			listener.setStatus(outprint, true, task.getTaskName());
			listener.log(outprint, task);

			return;
		}
		for (File file : files) {
			if (Thread.interrupted()) {
				throw new BackupCanceledException();
			}
			if (file.isDirectory()) {
				// filtering
				ArrayList<Filter> filtersOfThisSource = currentSource.getFilters();
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
				// filtering
				ArrayList<Filter> filtersOfThisSource = currentSource.getFilters();
				boolean filterMatches = false;
				for (Filter aFiltersOfThisSource : filtersOfThisSource) {
					if (aFiltersOfThisSource.getMode() == 0 &&
							(file.getAbsolutePath().equals(aFiltersOfThisSource.getPath()))) {
						filterMatches = true;
					}
				}
				if (!filterMatches) {
					// find out whether the file has to be copied or linked

					StructureFile fileInIndex = getStructureFileFromIndex(file, sourceRootDir);

					File newFile = new File(backupDir.getAbsolutePath() + File.separator + file.getName());

					if (fileInIndex == null) {
						// no corresponding file in index -> copy it
						elementQueue.add(
								new BackupElement(file.getAbsolutePath(), newFile.getAbsolutePath(), false, false));
						backupInfos.increaseNumberOfFilesToCopy();
						backupInfos.increaseSizeToCopyBy(file.length());
						continue;
					}
					if (file.lastModified() > fileInIndex.getLastModifiedDate()) {
						// file is already part of an older backup set
						elementQueue.add(
								new BackupElement(file.getAbsolutePath(), newFile.getAbsolutePath(), false, false));
						backupInfos.increaseNumberOfFilesToCopy();
						backupInfos.increaseSizeToCopyBy(file.length());
					} else {
						// file is already part of the most recent backup set

						File fileToLinkFrom = new File(
								destinationPath + File.separator + newestBackupPath + fileInIndex.getFilePath());
						if (fileToLinkFrom.exists()) {
							// filtering
							filterMatches = false;
							for (Filter aFiltersOfThisSource : filtersOfThisSource) {
								if (aFiltersOfThisSource.getMode() == 1 &&
										(file.getAbsolutePath().equals(aFiltersOfThisSource.getPath()))) {
									filterMatches = true;
								}
							}
							if (!filterMatches) {
								// link file
								elementQueue.add(
										new BackupElement(fileToLinkFrom.getAbsolutePath(), newFile.getAbsolutePath(),
												false, true));
								backupInfos.increaseNumberOfFilesToLink();
								backupInfos.increaseSizeToLinkBy(file.length());
							} else {
								// check MD5 sum
								// equal MD5 means linking, different MD5 means copying
								String md5OfSourceFile = BackupHelper.calcMD5(file);
								String md5OfFileToLinkFrom = BackupHelper.calcMD5(fileToLinkFrom);
								if (md5OfSourceFile != null && md5OfSourceFile.equals(md5OfFileToLinkFrom)) {
									// file to link
									elementQueue.add(new BackupElement(fileToLinkFrom.getAbsolutePath(),
											newFile.getAbsolutePath(), false, true));
									backupInfos.increaseNumberOfFilesToLink();
									backupInfos.increaseSizeToLinkBy(file.length());
								} else {
									// file to copy
									elementQueue.add(
											new BackupElement(file.getAbsolutePath(), newFile.getAbsolutePath(), false,
													false));
									backupInfos.increaseNumberOfFilesToCopy();
									backupInfos.increaseSizeToCopyBy(file.length());
								}
							}

						} else {
							// file is not existing within the backup set but listed in the index
							String outprint = "Existing Index is invalid";
							listener.setStatus(outprint, false, task.getTaskName());
							listener.log(outprint, task);

							outprint = "Deleting Index...";
							listener.setStatus(outprint, false, task.getTaskName());
							listener.log(outprint, task);

							// save root path of the index
							String rootPathForIndex = file.getAbsolutePath();

							// delete invalid index
							File badIndex = new File(file.getAbsolutePath() + directoryStructure.getFilePath());
							badIndex.delete();

							outprint = "Index deleted";
							listener.setStatus(outprint, false, task.getTaskName());
							listener.log(outprint, task);

							// recreate index
							outprint = "Indexing...";
							listener.setStatus(outprint, false, task.getTaskName());
							listener.log(outprint, task);
							createIndex(new File(rootPathForIndex), task);

							// indexing was cancelled
							if (directoryStructure == null) {
								throw new BackupCanceledException();
							}

							outprint = "Index created";
							listener.setStatus(outprint, false, task.getTaskName());
							listener.log(outprint, task);
							outprint = "Saving Index...";
							listener.setStatus(outprint, false, task.getTaskName());
							listener.log(outprint, task);

							serializeIndex(taskName, rootPathForIndex);

							outprint = "Index saved";
							listener.setStatus(outprint, false, task.getTaskName());
							listener.log(outprint, task);
							// file to copy
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
	 * Returns the corresponding StructureFile of the index for the given file. Returns null if file is not existing in
	 * the index.
	 *
	 * @param file           file to find the StrcutureFile from the index for
	 * @param sourceRootPath root path of the source
	 * @return corresponding StructureFile or null
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
	 * Creates the index.
	 *
	 * @param root root file for the indexing
	 * @param task corresponding BackupTask
	 */
	private void createIndex(File root, BackupTask task) {
		if (root.isDirectory()) {
			// Verzeichnisstruktur-Objekt erzeugen:
			try {
				directoryStructure = recCalcDirStruct(root.getAbsolutePath(), root.getAbsolutePath());
			} catch (BackupCanceledException e) {
				directoryStructure = null;
				String output = "Indexing canceled";
				listener.setStatus(output, false, task.getTaskName());
				listener.log(output, task);
			}
		}
	}

	/**
	 * Serializes the index.
	 *
	 * @param taskName      name of the corresponding BackupTask
	 * @param backupSetPath path of the corresponding backup set
	 */
	private void serializeIndex(String taskName, String backupSetPath) {

		// save file structure
		// create file
		File index = new File(backupSetPath + File.separator + "index_" + taskName + ".ser");
		// check for existing index
		if (!index.exists()) {
			try {
				index.createNewFile();
			} catch (IOException ex) {
				System.err.println("Error: IOException in HardlinkBackup while creating new File");
			}
		}
		// create OutputStreams
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
	 * Loads the serialized index. Returns true if successful and false otherwise.
	 *
	 * @param index index file to load
	 * @return true if successful and false otherwise
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
	 * Recursive method to calculate the directory structure
	 */
	private StructureFile recCalcDirStruct(String rootPath, String path) {
		if (Thread.interrupted()) {
			throw new BackupCanceledException();
		}

		File[] files = new File(path).listFiles();

		StructureFile sFile;
		// handling for windows special case where the source path is the root of a volume (e.g. C:/)
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
	 * Returns the path of the most recent backup set at the given root path.
	 *
	 * @param rootPath path to search for backups sets
	 * @return most recent backup task
	 */
	private String findMostRecentBackup(String rootPath) {
		File root = new File(rootPath);
		File[] directories = root.listFiles();

		Date newestDate = null;
		String newestBackupPath = null;
		Date foundDate;
		if (directories.length > 0) {
			for (File directory : directories) {
				if (directory.isDirectory()) {
					// split up name of the directory
					StringTokenizer tokenizer = new StringTokenizer(directory.getName(), "_");
					// has to consist of exactly two parts (name of the BackupTask and date)
					if (tokenizer.countTokens() != 2) {
						continue;
					}
					if (!tokenizer.nextToken().equals(taskName)) {
						continue;
					}
					// analyze date token (second one)
					String backupDate = tokenizer.nextToken();

					try {
						SimpleDateFormat sdfToDate = new SimpleDateFormat(BackupHelper.DATE_TIME_PATTERN_NAMING);
						foundDate = sdfToDate.parse(backupDate);
					} catch (ParseException e) {
						// no valid date
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
