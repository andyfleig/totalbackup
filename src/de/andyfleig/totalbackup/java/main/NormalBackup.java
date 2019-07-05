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
 * Implementation of Backupable for normal backups.
 *
 * @author Andreas Fleig
 */
public class NormalBackup implements Backupable {

	private String taskName;
	private ArrayList<Source> sources;
	private String destinationPath;
	private IBackupListener listener;
	/**
	 * List of BackupElements to process as part of this backup.
	 */
	private LinkedList<BackupElement> elementQueue;
	private boolean preparationDone = false;
	private boolean isCanceled;
	private BackupInfos backupInfos = new BackupInfos();
	private Source currentSource;

	/**
	 * Creates a new normal backup Backupable object.
	 *
	 * @param listener    corresponding IBackupListener instance
	 * @param nameOfTask  name of the BackupTask
	 * @param sources     list of sources
	 * @param destination destination path
	 */
	public NormalBackup(IBackupListener listener, String nameOfTask, ArrayList<Source> sources, String destination) {
		this.listener = listener;
		this.taskName = nameOfTask;
		this.sources = sources;
		this.destinationPath = destination;
		elementQueue = new LinkedList<>();
	}

	@Override
	public void runPreparation(BackupTask task) {
		File dir = BackupHelper.createBackupFolder(destinationPath, taskName, listener, task);
		if (dir == null) {
			String output = "Error while creating Backup-Folder\\!";
			listener.setStatus(output, true, task.getTaskName());
			listener.log(output, task);
			return;
		}

		try {
			for (int i = 0; i < sources.size(); i++) {
				// for filtering
				currentSource = sources.get(i);

				File sourceFile = new File(sources.get(i).getPath());

				// handling for windows special case where the source path is the root of a volume (e.g. C:/)
				String folder;
				File f;
				if (!sourceFile.isDirectory()) {
					f = dir;
				} else {
					if (sourceFile.getAbsolutePath().contains(":\\") && sourceFile.getAbsolutePath().length() == 3 &&
							sourceFile.getName().equals("")) {
						// name is created from the volume letter in this special case
						folder = dir.getAbsolutePath() + File.separator + sourceFile.getAbsolutePath().charAt(0);
					} else {
						folder = dir.getAbsolutePath() + File.separator + sourceFile.getName();
					}

					f = new File(folder);

					if (f.mkdir()) {
						String outprint = "Backup-Folder created";
						listener.setStatus(outprint, false, task.getTaskName());
						listener.log(outprint, task);
					} else {
						String outprint = "Error while creating Folder";
						listener.setStatus(outprint, true, task.getTaskName());
						listener.log(outprint, task);
					}
				}

				String output = "Analyzing source...";
				listener.setStatus(output, false, task.getTaskName());
				listener.log(output, task);

				// queueing:
				try {
					for (Source source : sources) {
						rekursivePreparation(new File(source.getPath()), f, task);
					}
				} catch (BackupCanceledException e) {
					String outprint = "Backup canceled by User";
					listener.setStatus(outprint, false, task.getTaskName());
					listener.log(outprint, task);
					isCanceled = true;
				}
			}
		} catch (BackupCanceledException e) {
			String outprint = "Backup canceled by User";
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
		String output = "Backup-Task started";
		listener.setStatus(output, false, task.getTaskName());
		listener.log(output, task);

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

			String outprint = "Backup done";
			listener.setStatus(outprint, false, task.getTaskName());
			listener.log(outprint, task);
			listener.taskFinished(task);
		} catch (BackupCanceledException e) {
			String outprint = "Backup canceled by User";
			listener.setStatus(outprint, false, task.getTaskName());
			listener.log(outprint, task);
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
				for (Filter filter : filtersOfThisSource) {
					if (filter.getMode() == 0 && file.getAbsolutePath().equals(filter.getPath())) {
						filterMatches = true;
					}
				}
				if (!filterMatches) {
					File newFile = new File(backupDir.getAbsolutePath() + File.separator + file.getName());
					elementQueue.add(
							new BackupElement(file.getAbsolutePath(), newFile.getAbsolutePath(), false, false));
					backupInfos.increaseNumberOfFilesToCopy();
					backupInfos.increaseSizeToCopyBy(file.length());
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
