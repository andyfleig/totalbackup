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

import data.BackupInfos;
import data.Source;
import gui.FxMainframe;
import gui.GuiController;
import gui.NextExecutionChooser;
import javafx.application.Platform;
import listener.IBackupListener;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import javax.swing.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import data.BackupTask;
import data.BackupThreadContainer;
import listener.IGUIControllerListener;
import listener.INextExecutionChooserListener;

/**
 * Main controller of TotalBackup.
 *
 * @author Andreas Fleig
 */
public class Controller {

	/**
	 * Call arguments.
	 */
	private String[] arguments;
	/**
	 * List of all BackupTasks.
	 */
	private ArrayList<BackupTask> backupTasks = new ArrayList<>();
	/**
	 * IBackupListener instance.
	 */
	private IBackupListener backupListener;
	/**
	 * List containing the names of all currently running BackupTasks.
	 */
	private ArrayList<String> runningBackupTasks = new ArrayList<>();

	private ScheduledThreadPoolExecutor threadExecutor = new ScheduledThreadPoolExecutor(3);
	/**
	 * List of all BackupThreads.
	 */
	private ArrayList<BackupThreadContainer> backupThreads;

	/**
	 * Defines how many seconds a backup to catch-up should be delayed.
	 */
	private static final int DELAY_FOR_MISSED_BACKUP = 2;

	private static final long NUMBER_OF_SECONDS_UNTIL_NEXT_EXECUTION_FOR_POPUP = 90;

	// size of one inode (in Byte):
	private static final double SIZE_OF_INODE = 4096;

	// Controller of TotalBackups GUI.
	private GuiController guiController;

	/**
	 * Creates a new controller with the given FxMainframe.
	 *
	 * @param fxMainframe current mainframe instance
	 */
	public Controller(FxMainframe fxMainframe) {
		guiController = new GuiController(new IGUIControllerListener() {
			@Override
			public boolean argsContains(String arg) {
				return Controller.this.argsContains(arg);
			}

			@Override
			public void quitTotalBackup() {
				Controller.this.quit();
			}

			@Override
			public void addBackupTask(BackupTask task) {
				Controller.this.addBackupTask(task);
			}

			@Override
			public void saveProperties() {
				saveSerialization();
			}

			@Override
			public void scheduleBackupTask(BackupTask task) {
				Controller.this.scheduleBackupTask(task);
			}

			@Override
			public void scheduleBackupTaskNow(BackupTask task) {
				Controller.this.scheduleBackupTaskNow(task);
			}

			@Override
			public BackupTask getBackupTaskWithName(String taskName) {
				for (BackupTask task : backupTasks) {
					if (task.getTaskName().equals(taskName)) {
						return task;
					}
				}
				return null;
			}

			@Override
			public void deleteBackupTaskWithName(String taskName) {
				ArrayList<BackupTask> tasksToDelete = new ArrayList<>();
				for (BackupTask backupTask : backupTasks) {
					if (backupTask.getTaskName().equals(taskName)) {
						tasksToDelete.add(backupTask);
					}
				}
				for (BackupTask backupTask : tasksToDelete) {
					Controller.this.removeBackupTask(backupTask);
				}
			}

			@Override
			public void deleteEmptyBackupFolders(BackupTask task) {
				Controller.this.deleteEmptyBackupFolders(task);
			}

			@Override
			public void taskFinished(BackupTask task, boolean schedule) {
				Controller.this.taskFinished(task, schedule);
			}

			@Override
			public boolean taskIsRunning(String taskName) {
				return runningBackupTasks.contains(taskName);
			}
		}, fxMainframe);
	}

	/**
	 * Starts and initializes the Controller.
	 */
	public void startController(String[] args) {
		this.arguments = args;
		// Restrict number of running instances of TotalBackup to 1 using a opened network socket
		try {
			new ServerSocket(2210);
		} catch (IOException e) {
			System.out.println("TotalBackup is already running");
			System.exit(1);
		}

		backupThreads = new ArrayList<>();

		backupListener = new IBackupListener() {
			@Override
			public void setStatus(final String msg, final boolean error, final String taskName) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						Controller.this.setStatus(msg, error, taskName);
					}

				});
			}

			@Override
			public void log(String msg, BackupTask task) {
				Controller.this.log(msg, task);

			}

			@Override
			public void taskStarted(String taskName) {
				Controller.this.taskStarted(taskName);

			}

			@Override
			public void taskFinished(BackupTask task) {
				Controller.this.taskFinished(task, true);

			}

			@Override
			public void deleteEmptyBackupFolders(BackupTask task) {
				Controller.this.deleteEmptyBackupFolders(task);

			}

		};
		// load serialized settings of TotalBackup (the BackupTasks)
		loadSerialization();

		// list of all missed BackupTasks to catch up
		ArrayList<BackupTask> missedBackupTaks = new ArrayList<>();
		// check whether backups has been missed since the last execution of TotalBackup
		for (BackupTask task : backupTasks) {
			if (task.getLocalDateTimeOfNextBackup() != null &&
					task.getLocalDateTimeOfNextBackup().isBefore(LocalDateTime.now())) {
				missedBackupTaks.add(task);
			}
		}

		guiController.initialize();

		// reschedule all backup tasks according to there auto-backup configuration (if any)
		scheduleBackupTasks();

		// catch up missed backups
		for (BackupTask task : missedBackupTaks) {
			// check whether it is worth catching those backups up (by checking the next regularly planned execution)
			if ((task.getLocalDateTimeOfNextBackup().minusMinutes(task.getProfitableTimeUntilNextExecution())).isAfter(
					LocalDateTime.now())) {
				String msg = "Catch up task" + " " + task.getTaskName();
				showTrayPopupMessage(msg);
				scheduleBackupTaskNow(task, true);
			}
		}
	}

	/**
	 * Creating and writing GSON-based serialization of the BackupTasks.
	 */
	private void saveSerialization() {
		Gson gson = new Gson();
		String settings = gson.toJson(backupTasks);
		try {
			PrintWriter out = new PrintWriter("./properties");
			out.println(settings);
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error: FileNotException while writing properties");
		}

	}

	/**
	 * Loads the GSON-based serialization of the BackupTasks and adds them to the local list of BackupTasks.
	 */
	private void loadSerialization() {
		String settings = "";
		File properties = new File("./properties");
		if (properties.exists()) {
			try {
				Scanner scanner = new Scanner(properties);
				settings = scanner.nextLine();
				scanner.close();
			} catch (FileNotFoundException e) {
				System.err.println("Error: FileNotFoundException while loading Gson-Properties");
			}
			Gson gson = new Gson();
			Type listOfBackupTasks = new TypeToken<ArrayList<BackupTask>>() {
			}.getType();
			if (settings != null) {
				try {
					backupTasks = gson.fromJson(settings, listOfBackupTasks);
				} catch (com.google.gson.JsonSyntaxException e) {
					System.err.println("Error: Could not read properties file! Seems like the syntax of the file is " +
							"not correct.");
					if (guiController.isQTTray()) {
						guiController.sendToQtTrayOverSocket(null, true);
					}
					guiController.destroyTrayProcess();
					System.exit(1);
				}
			}
			for (BackupTask backupTask : backupTasks) {
				guiController.addBackupTask(backupTask);
			}
		}
	}

	/**
	 * Starts the preparation of the given BackupTask.
	 *
	 * @param task BackupTask to start preparation for
	 */
	public void startPreparation(BackupTask task) {
		Thread backupThread = new Thread(new Runnable() {
			@Override
			public void run() {
				runPreparation(task);
			}
		});
		backupThreads.add(new BackupThreadContainer(backupThread, task.getTaskName()));
		backupThread.start();
	}

	/**
	 * Runs the actual preparation of the given BackupTask (runs the analysis).
	 *
	 * @param task corresponding BackupTask
	 */
	private void runPreparation(BackupTask task) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Backupable backup;
		// create the backup instance (Backupable) depending on the backup type
		if (task.getBackupMode() == 1) {
			// case: hardlink backup
			// check whether it is the first execution of the hardlink backup which means it has to be executed as
			// normal backup since there is no backup set yet to link to
			File[] files = new File(task.getDestinationPath()).listFiles();
			boolean backupSetFound = false;
			if (files.length > 0) {
				for (File file : files) {
					if (file.isDirectory()) {
						// split up name of the directory
						StringTokenizer tokenizer = new StringTokenizer(file.getName(), "_");
						// has to consist of exactly two parts (name of the BackupTask and date)
						if (tokenizer.countTokens() != 2) {
							continue;
						}
						if (!tokenizer.nextToken().equals(task.getTaskName())) {
							continue;
						}
						backupSetFound = true;
						break;
					}
				}
			}

			if (backupSetFound) {
				String output = "At least one Backup-Set found. Starting Hardlink Backup...";
				setStatus(output, false, task.getTaskName());
				log(output, task);
				backup = new HardlinkBackup(backupListener, task.getTaskName(), task.getSources(),
						task.getDestinationPath());
			} else {
				String output = "No Backup-Set found. Starting 'normal' Backup";
				setStatus(output, false, task.getTaskName());
				log(output, task);
				backup = new NormalBackup(backupListener, task.getTaskName(), task.getSources(),
						task.getDestinationPath());
			}
		} else {
			// case: normal backup
			backup = new NormalBackup(backupListener, task.getTaskName(), task.getSources(), task.getDestinationPath());
		}

		// show PreparingDialog if not in autostart-mode
		if (!task.autostartIsEnabled()) {
			guiController.showPreparingDialog(task, backup);
			try {
				// workaround to force JavaFX to show PreparingDialog
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Warning: Sleeping thread was interrupted.");
			}
		}

		// check whether sources and destination exist
		ArrayList<Source> sources = task.getSources();
		for (Source source : sources) {
			if (!(new File(source.getPath())).exists()) {
				String output = "Error: At least one of the source paths doesn't exists";
				setStatus(output, false, task.getTaskName());
				log(output, task);
				guiController.disposePreparingDialogIfNotNull();
				taskFinished(task, true);
				return;
			}
		}

		// DestinationVerification:
		String OS = System.getProperty("os.name").toLowerCase();
		if (!OS.contains("win") && task.destinationVerificationIsEnabled()) {
			File identifier = new File(task.getDestinationPath() + "/" + task.getTaskName() + ".id");
			if (!(new File(task.getDestinationPath())).exists() || !identifier.exists()) {
				// request to user: try to find path?
				int reply = JOptionPane.showConfirmDialog(null,
						"Could not find Destination. Should I try to find the right Destination?", null,
						JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					// user response: YES
					ArrayList<String> correctDest = searchForCorrectDestPath(task.getTaskName(),
							task.getDestinationPath());
					boolean successful = false;
					for (String dest : correctDest) {
						// request to user: possible path found ... is it correct?
						int reply2 = JOptionPane.showConfirmDialog(null,
								"Found possible Destination-Path:" + " " + dest + "  " + "s it correct?", null,
								JOptionPane.YES_NO_OPTION);
						if (reply2 == JOptionPane.YES_OPTION) {
							// user response: YES
							// request to user: set this path as new destination path?
							int reply3 = JOptionPane.showConfirmDialog(null,
									"Do you want to set this path as new Destination-Path (for future backups)?", null,
									JOptionPane.YES_NO_OPTION);
							if (reply3 == JOptionPane.YES_OPTION) {
								// user response: YES
								successful = true;
								task.setDestinationPath(dest);
							} else {
								// user response: NO
								successful = true;
								task.setRealDestinationPath(task.getDestinationPath());
								task.setDestinationPath(dest);
								saveSerialization();
							}
						}
					}
					if (!successful) {
						// DestinationVerification as not successful -> cancel backup
						cancelBackup(task, false);
						guiController.disposePreparingDialogIfNotNull();
						askForNextExecution(task);
						return;
					}

				} else {
					// user response: NO
					cancelBackup(task, false);
					guiController.disposePreparingDialogIfNotNull();
					askForNextExecution(task);
					return;
				}
			}
		}

		// check whether destination path exists
		if (!(new File(task.getDestinationPath())).exists()) {
			cancelBackup(task, false);
			guiController.disposePreparingDialogIfNotNull();
			askForNextExecution(task);
			return;
		}

		try {
			backup.runPreparation(task);
		} catch (BackupCanceledException ex) {
			String output = "Backup canceled by User";
			setStatus(output, false, task.getTaskName());
			log(output, task);
		}

		// TODO: problems with setPrepared when cancelling?
		task.setPrepared();

		guiController.disposePreparingDialogIfNotNull();

		// check whether enough free space is available
		File destDir = new File(task.getDestinationPath());
		double freeSize = destDir.getFreeSpace();
		BackupInfos backupInfos = backup.getBackupInfos();
		// ToDo: additional warning if there is not much more free space than necessary (e.g. 1%)?
		double sizeNeeded = backupInfos.getSizeToCopy() + SIZE_OF_INODE * backupInfos.getNumberOfFilesToCopy() +
				SIZE_OF_INODE * backupInfos.getNumberOfDirectories();
		if (freeSize <= sizeNeeded) {
			// not enough free space available
			JOptionPane.showMessageDialog(null, "There is not enough free space for this Backup.", "Error",
					JOptionPane.INFORMATION_MESSAGE);
			// cancel backup
			return;
		}
		boolean isCanceled = true;
		for (BackupThreadContainer container : backupThreads) {
			if (container.getTaskName().equals(task.getTaskName())) {
				isCanceled = false;
			}
		}
		// disposeDialog PreparingDialog:
		guiController.disposePreparingDialogIfNotNull();

		// show SummaryDialog if not canceled and not in auto-backup mode
		if (!isCanceled && !backup.isCanceled()) {
			if (!task.autostartIsEnabled()) {
				guiController.showSummaryDialog(task, backup);
				synchronized (task) {
					try {
						task.wait();
						if (!backup.isCanceled()) {
							startBackup(task, backup);
						}
					} catch (InterruptedException e) {
						System.out.println("Backup-Thread was unexpectedly canceled");
					}
				}
			} else {
				startBackup(task, backup);
			}
		}
	}

	/**
	 * Cancels the given BackupTask and reschedules if desired.
	 *
	 * @param task       BackupTask to cancel
	 * @param reschedule whether the BackupTask should be rescheduled (true) or not (false)
	 */
	private void cancelBackup(BackupTask task, boolean reschedule) {
		// ToDo: non-JavaFX thread?
		guiController.setStatusOfBackupTask(task.getTaskName(), false, "Canceling Backup...");

		BackupThreadContainer tmpContainer = null;
		for (BackupThreadContainer container : backupThreads) {
			if (container.getTaskName().equals(task.getTaskName())) {
				container.getBackupThread().interrupt();
				tmpContainer = container;
				break;
			}
		}
		if (tmpContainer != null) {
			backupThreads.remove(tmpContainer);
			taskFinished(task, reschedule);
		}
	}

	/**
	 * Request from user when the given BackupTask should be executed the next time.
	 *
	 * @param task given BackupTask
	 */
	public void askForNextExecution(BackupTask task) {
		NextExecutionChooser nec = new NextExecutionChooser(new INextExecutionChooserListener() {
			@Override
			public void skipBackup() {
				scheduleBackupTask(task);
			}

			@Override
			public void postponeBackupTo(LocalDateTime nextExecutionTime) {
				scheduleBackupTaskAt(task, nextExecutionTime);
			}

			@Override
			public void retry() {
				scheduleBackupTaskNow(task);
			}
		});
		nec.setModal(true);
		nec.setVisible(true);
	}

	/**
	 * Tries to find the correct destination path for the BackupTask with the given name and returns a list of possible
	 * candidates. Used for DestinationVerification.
	 *
	 * @param taskName      name of the corresponding BackupTask
	 * @param wrongDestPath wrong destination
	 * @return list of candidates for the correct destination path
	 */
	private ArrayList<String> searchForCorrectDestPath(String taskName, String wrongDestPath) {
		ArrayList<String> foundDestPaths = new ArrayList<>();
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.contains("win")) {
			String destSuffix = wrongDestPath.substring(3);
			File[] roots = File.listRoots();
			for (File root : roots) {
				File potentialDest = new File(root.getAbsolutePath() + destSuffix);
				if (potentialDest.exists()) {
					if (checkForIdentifier(taskName, potentialDest.getAbsolutePath())) {
						foundDestPaths.add(potentialDest.getAbsolutePath());
					}
				}
			}

		} else {
			throw new IllegalStateException("DestinationVerification is only supported under Windows");
		}
		return foundDestPaths;
	}

	/**
	 * Checks for a valid identifier-file for the BackupTask with the given name and the given destination.
	 *
	 * @param taskName BackupTask to check for
	 * @param dest     given destination path
	 * @return whether there is a valid identifier (true) or not (false)
	 */
	private boolean checkForIdentifier(String taskName, String dest) {
		File destFile = new File(dest);
		if (destFile.exists()) {
			File[] files = destFile.listFiles();
			if (files.length > 0) {
				for (File file : (new File(dest)).listFiles()) {
					if (file.getName().equals(taskName + ".id")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Starts the backup procedure for the given BackupTask in the given Backupable.
	 *
	 * @param task BackupTask to run
	 */
	public void startBackup(BackupTask task, Backupable backup) {
		// set correct destination-path (if necessary)
		if (task.getRealDestinationPath() != null) {
			task.setDestinationPath(task.getRealDestinationPath());
			task.setRealDestinationPath(null);
		}

		if (!task.isPrepared()) {
			return;
		}

		try {
			backup.runBackup(task);
		} catch (BackupCanceledException ex) {
			String output = "Backup canceled by User";
			setStatus(output, false, task.getTaskName());
			log(output, task);
		}
		// set correct destination-path (if necessary)
		// ToDo: why again (see above)?
		if (task.getRealDestinationPath() != null) {
			task.setDestinationPath(task.getRealDestinationPath());
			task.setRealDestinationPath(null);
		}

		// clean up old backups (if auto-clean is enabled)
		if (task.basicAutoCleanIsEnabled()) {
			try {
				while (this.calcNumberOfBackups(task) > task.getNumberOfBackupsToKeep()) {
					File toDelete = new File(task.getDestinationPath() + File.separator + findOldestBackup(
							new ArrayList<>(Arrays.asList((new File(task.getDestinationPath()).listFiles()))), task));

					String output = "Deleting Backup" + " " + toDelete.getAbsolutePath();
					guiController.setStatusOfBackupTask(task.getTaskName(), false, output);
					log(output, task);
					if (!BackupHelper.deleteDirectory(toDelete)) {
						System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
					}
					setStatus(toDelete.getAbsolutePath() + " deleted", false, task.getTaskName());
				}
			} catch (BackupCanceledException e) {
				String outprint = "Backup canceled by User";
				setStatus(outprint, false, task.getTaskName());
				log(outprint, task);
			}
		} else if (task.advancedAutoCleanIsEnabled()) {
			runAdvancedClean(task);
		}

		task = null;

	}

	/**
	 * Returns a list with the names of all BackupTasks.
	 *
	 * @return list of all BackupTask names
	 */
	public ArrayList<String> getBackupTaskNames() {
		ArrayList<String> backupTaskNames = new ArrayList<>();
		for (BackupTask backupTask : backupTasks) {
			backupTaskNames.add(backupTask.getTaskName());
		}
		return backupTaskNames;
	}

	/**
	 * Sets the status of the BackupTask with the given name to the given message. The error-flag indicates whether it
	 * is a error status and thus has to be highlighted.
	 *
	 * @param msg      message to set the status to
	 * @param error    whether it is an error status (true) or not (false)
	 * @param taskName name of the corresponding BackupTask
	 */
	private void setStatus(String msg, boolean error, String taskName) {
		// Avoid throwing IllegalStateException by running from a non-JavaFX thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				guiController.setStatusOfBackupTask(taskName, error, msg);
			}
		});

	}

	/**
	 * Sets the next execution status of the BackupTask with the given name to the given message. The error-flag
	 * indicates whether it is a error status and thus has to be highlighted.
	 *
	 * @param nextExecutionTime next execution time
	 * @param taskName          name of the corresponding BackupTask
	 */
	private void setNextExecutionStatus(LocalDateTime nextExecutionTime, String taskName) {
		// Avoid throwing IllegalStateException by running from a non-JavaFX thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				guiController.setNextExecutionTimeStatus(taskName, nextExecutionTime);
			}
		});

	}

	/**
	 * Logs the given message for the given BackupTask.
	 *
	 * @param msg  message to log
	 * @param task corresponding BackupTask
	 */
	private void log(String msg, BackupTask task) {
		// Log-Datei anlegen:
		if (task == null) {
			return;
		}
		File log = new File(task.getDestinationPath() + File.separator + task.getTaskName() + ".log");
		// Kontrollieren ob bereits eine log Datei existiert:
		if (!log.exists()) {
			try {
				log.createNewFile();
			} catch (IOException e) {
				System.out.println("Fehler: IO-Problem");
			}
		}
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(log, true));
			LocalDateTime dateDime = LocalDateTime.now();
			String timePattern = "dd.MM.yyyy HH:mm:ss";
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(timePattern);
			String output = dtf.format(dateDime) + ": " + msg;

			writer.append("\n" + output);
			writer.close();
		} catch (FileNotFoundException e) {
			System.err.println("Fehler: log Datei nicht gefunden");
		}
	}

	/**
	 * Returns the BackupTask with the given name. Returns null if no BackupTask with the given name exists.
	 *
	 * @param taskName name of the BackupTask to return
	 * @return BackupTask with the given name (if any), else null
	 */
	public BackupTask getBackupTaskWithName(String taskName) {
		for (BackupTask backupTask : backupTasks) {
			if (backupTask.getTaskName().equals(taskName)) {
				return backupTask;
			}
		}
		return null;
	}

	/**
	 * Adds the given name of a BackupTask to the list of BackupTasks.
	 *
	 * @param task BackupTask to add
	 */
	public void addBackupTask(BackupTask task) {
		backupTasks.add(task);
		guiController.addBackupTask(task);
	}

	/**
	 * Removes the given BackupTask from the list of BackupTasks.
	 *
	 * @param task BackupTask to remove
	 */
	public void removeBackupTask(BackupTask task) {
		backupTasks.remove(task);
		guiController.removeBackupTask(task.getTaskName());
	}

	/**
	 * Returns the number of existing backup sets for the given BackupTask.
	 *
	 * @param task BackupTask to find existing backup sets for
	 * @return list of existing backup sets
	 */
	private int calcNumberOfBackups(BackupTask task) {
		File dest = new File(task.getDestinationPath());
		return findBackupSets(dest, task).size();
	}

	/**
	 * Returns a list of all existing backup sets within the given directory.
	 *
	 * @param dir  directory to find backup sets in
	 * @param task corresponding BackupTask
	 * @return list of existing backup sets
	 */
	private ArrayList<File> findBackupSets(File dir, BackupTask task) {
		File[] files = dir.listFiles();
		ArrayList<File> foundBackupsets = new ArrayList<>();
		if (files.length > 0) {
			for (File file : files) {
				// ToDo: duplicate code (5 times)
				// split up name of the directory
				StringTokenizer tokenizer = new StringTokenizer(file.getName(), "_");
				// has to consist of exactly two parts (name of the BackupTask and date)
				if (tokenizer.countTokens() != 2) {
					continue;
				}
				if (!tokenizer.nextToken().equals(task.getTaskName())) {
					continue;
				}
				// analyze date token (second one)
				String backupDate = tokenizer.nextToken();

				try {
					SimpleDateFormat sdfToDate = new SimpleDateFormat(BackupHelper.BACKUP_FOLDER_NAME_PATTERN);
					sdfToDate.parse(backupDate);
					foundBackupsets.add(file);
				} catch (ParseException e) {
					// ToDo: handle case without valid date
				}
			}
		}

		return foundBackupsets;
	}

	/**
	 * Returns the path of the oldest existing backup set.
	 *
	 * @param directories directory of the backup sets
	 * @param task        corresponding BackupTask
	 * @return oldest backup set
	 */
	private String findOldestBackup(ArrayList<File> directories, BackupTask task) {

		Date oldestDate = null;
		String oldestBackupPath = null;
		Date foundDate;
		for (File directory : directories) {
			if (directory.isDirectory()) {
				// split up name of the directory
				StringTokenizer tokenizer = new StringTokenizer(directory.getName(), "_");
				// has to consist of exactly two parts (name of the BackupTask and date)
				if (tokenizer.countTokens() != 2) {
					continue;
				}
				if (!tokenizer.nextToken().equals(task.getTaskName())) {
					continue;
				}
				// analyze date token (second one)
				String backupDate = tokenizer.nextToken();

				try {
					SimpleDateFormat sdfToDate = new SimpleDateFormat(BackupHelper.BACKUP_FOLDER_NAME_PATTERN);
					foundDate = sdfToDate.parse(backupDate);
				} catch (ParseException e) {
					// no valid date
					continue;
				}
				if (oldestDate == null) {
					oldestDate = foundDate;
					oldestBackupPath = directory.getName();
				} else {
					if (oldestDate.compareTo(foundDate) > 0) {
						oldestDate = foundDate;
						oldestBackupPath = directory.getName();
					}
				}
			}
		}
		return oldestBackupPath;
	}

	/**
	 * Executes the auto-cleaning of the given BackupTask.
	 *
	 * @param task auto-cleaning of the task
	 */
	private void runAdvancedClean(BackupTask task) {
		LocalDateTime currentSystemTime = LocalDateTime.now();
		ArrayList<File> existingBackupSets = findBackupSets(new File(task.getDestinationPath()), task);

		if (existingBackupSets.size() == 0) {
			return;
		}
		// create buckes according to the number of defined rules
		ArrayList<File> bucket1 = new ArrayList<>();
		ArrayList<File> bucket2 = new ArrayList<>();
		ArrayList<File> bucket3 = new ArrayList<>();
		ArrayList<File> bucket4 = new ArrayList<>();
		ArrayList<File> bucket5 = new ArrayList<>();

		// create dates for those buckets
		LocalDateTime[] boundaries = new LocalDateTime[task.getNumberOfExtendedCleanRules() - 1];
		String[] boundaryStrings = task.getFormattedBoundaries();
		for (int i = 0; i < boundaries.length; i++) {
			StringTokenizer tokanizer = new StringTokenizer(boundaryStrings[i], "_");
			String threshold = tokanizer.nextToken();
			switch (tokanizer.nextToken()) {
				case "min":
					boundaries[i] = currentSystemTime.minusMinutes(Long.parseLong(threshold));
					break;
				case "h":
					boundaries[i] = currentSystemTime.minusHours(Long.parseLong(threshold));
					break;
				case "d":
					boundaries[i] = currentSystemTime.minusDays(Long.parseLong(threshold));
					break;
				case "m":
					boundaries[i] = currentSystemTime.minusDays(Long.parseLong(threshold) * 30);
					break;
				case "y":
					boundaries[i] = currentSystemTime.minusDays(Long.parseLong(threshold) * 256);
					break;
			}
		}

		// sort backup sets to the buckets
		for (File backupSet : existingBackupSets) {
			StringTokenizer tokenizer = new StringTokenizer(backupSet.getName(), "_");
			tokenizer.nextToken();
			String currentBackupSet = tokenizer.nextToken();
			Date dateOfCurrentBackupSet = new Date();
			try {
				SimpleDateFormat sdfOfCurrentBackupSet = new SimpleDateFormat(BackupHelper.BACKUP_FOLDER_NAME_PATTERN);
				dateOfCurrentBackupSet = sdfOfCurrentBackupSet.parse(currentBackupSet);
			} catch (ParseException e) {
				System.err.println("Error while parsing date");
			}
			// transform dateOfCurrentBackupSet to LocalTimeDate
			LocalDateTime ltmOfCurrentBackupSet = LocalDateTime.ofInstant(dateOfCurrentBackupSet.toInstant(),
					ZoneId.systemDefault());

			// find correct bucket and insert
			switch (task.getNumberOfExtendedCleanRules()) {
				case 1:
					bucket1.add(backupSet);
					break;
				case 2:
					if (ltmOfCurrentBackupSet.isAfter(boundaries[0])) {
						bucket1.add(backupSet);
					} else {
						bucket2.add(backupSet);
					}
					break;
				case 3:
					if (ltmOfCurrentBackupSet.isAfter(boundaries[0])) {
						bucket1.add(backupSet);
					} else if (ltmOfCurrentBackupSet.isAfter(boundaries[1])) {
						bucket2.add(backupSet);
					} else {
						bucket3.add(backupSet);
					}
					break;
				case 4:
					if (ltmOfCurrentBackupSet.isAfter(boundaries[0])) {
						bucket1.add(backupSet);
					} else if (ltmOfCurrentBackupSet.isAfter(boundaries[1])) {
						bucket2.add(backupSet);
					} else if (ltmOfCurrentBackupSet.isAfter(boundaries[2])) {
						bucket3.add(backupSet);
					} else {
						bucket4.add(backupSet);
					}
					break;
				case 5:
					if (ltmOfCurrentBackupSet.isAfter(boundaries[0])) {
						bucket1.add(backupSet);
					} else if (ltmOfCurrentBackupSet.isAfter(boundaries[1])) {
						bucket2.add(backupSet);
					} else if (ltmOfCurrentBackupSet.isAfter(boundaries[2])) {
						bucket3.add(backupSet);
					} else if (ltmOfCurrentBackupSet.isAfter(boundaries[3])) {
						bucket4.add(backupSet);
					} else {
						bucket5.add(backupSet);
					}
					break;
			}
		}

		// clear all the buckets according to their maximum size
		if (task.getBackupsToKeep().length > 0 && task.getBackupsToKeep()[0] != -1) {
			while (!bucket1.isEmpty() && bucket1.size() > Integer.valueOf(task.getBackupsToKeep()[0])) {
				if (!BackupHelper.deleteDirectory(
						new File(task.getDestinationPath() + "/" + findOldestBackup(bucket1, task)))) {
					System.err.println("Error: Directory could not be deleted");
					break;
				}
			}
		}

		if (task.getBackupsToKeep().length > 1 && task.getBackupsToKeep()[1] != -1) {
			while (!bucket2.isEmpty() && bucket2.size() > Integer.valueOf(task.getBackupsToKeep()[1])) {
				File oldestBackupSet = new File(task.getDestinationPath() + "/" + findOldestBackup(bucket2, task));
				if (!BackupHelper.deleteDirectory(oldestBackupSet)) {
					System.err.println("Error: Directory could not be deleted");
					break;
				}
				bucket2.remove(oldestBackupSet);
			}
		}

		if (task.getBackupsToKeep().length > 2 && task.getBackupsToKeep()[2] != -1) {
			while (!bucket3.isEmpty() && bucket3.size() > Integer.valueOf(task.getBackupsToKeep()[2])) {
				if (!BackupHelper.deleteDirectory(
						new File(task.getDestinationPath() + "/" + findOldestBackup(bucket3, task)))) {
					System.err.println("Error: Directory could not be deleted");
					break;
				}
			}
		}

		if (task.getBackupsToKeep().length > 3 && task.getBackupsToKeep()[3] != -1) {
			while (!bucket4.isEmpty() && bucket4.size() > Integer.valueOf(task.getBackupsToKeep()[3])) {
				if (!BackupHelper.deleteDirectory(
						new File(task.getDestinationPath() + "/" + findOldestBackup(bucket4, task)))) {
					System.err.println("Error: Directory could not be deleted");
					break;
				}
			}
		}

		if (task.getBackupsToKeep().length > 4 && task.getBackupsToKeep()[4] != -1) {
			while (!bucket4.isEmpty() && bucket5.size() > Integer.valueOf(task.getBackupsToKeep()[4])) {
				if (!BackupHelper.deleteDirectory(
						new File(task.getDestinationPath() + "/" + findOldestBackup(bucket5, task)))) {
					System.err.println("Error: Directory could not be deleted");
					break;
				}
			}
		}
		bucket1 = null;
		bucket2 = null;
		bucket3 = null;
		bucket4 = null;
		bucket5 = null;
	}

	/**
	 * Adds the BackupTask with the given name to the list of running BackupTasks.
	 *
	 * @param taskName name of the started BackupTask
	 */
	private void taskStarted(String taskName) {
		runningBackupTasks.add(taskName);
		System.out.println("Task started:" + taskName);
		setStatus("Started", false, taskName);
	}

	/**
	 * Deletes the given BackupTask from the list of running BackupTasks.
	 *
	 * @param task finished BackupTask
	 */
	private void taskFinished(BackupTask task, boolean schedule) {
		if (!runningBackupTasks.remove(task.getTaskName())) {
			System.err.println("Error: This task isn't running");
		}
		System.out.println("Task finished:" + task.getTaskName());
		setStatus("Finished", false, task.getTaskName());
		setNextExecutionStatus(null, task.getTaskName());
		if (schedule) {
			scheduleBackupTask(task);
		}
	}

	/**
	 * Schedules the given BackupTask according to its configuration.
	 *
	 * @param task BackupTask to schedule
	 */
	public void scheduleBackupTask(final BackupTask task) {
		LocalDateTime newestBackupTime = BackupHelper.getLocalDateTimeOfNewestBackupSet(task);
		LocalDateTime now = LocalDateTime.now().minusNanos(LocalDateTime.now().getNano());
		if (newestBackupTime != null && newestBackupTime.isEqual(now)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		scheduleBackupTaskStartingAt(task, LocalDateTime.now());
	}

	/**
	 * Schedules the given BackupTask to be executed immediately.
	 *
	 * @param task   BackupTask to schedule
	 * @param silent whether the BackupTask should be preformed without status-updates (true) or not (false)
	 */
	public void scheduleBackupTaskNow(final BackupTask task, boolean silent) {
		// checks whether this BackupTas is already running
		if (runningBackupTasks.contains(task.getTaskName())) {
			System.err.println("Error: Could not run BackupTask since it is already running.");
			return;
		}
		if (!silent) {
			setStatus("backup starts in 2 seconds", false, task.getTaskName());
		}

		scheduleBackupTaskAt(task, LocalDateTime.now().plusSeconds(DELAY_FOR_MISSED_BACKUP));
	}

	/**
	 * Schedules the given BackupTask to be executed immediately.
	 *
	 * @param task BackupTask to schedule
	 */
	public void scheduleBackupTaskNow(final BackupTask task) {
		scheduleBackupTaskNow(task, false);
	}

	/**
	 * Schedules the given BackupTask according to its configuration.
	 */
	public void scheduleBackupTasks() {
		// clear waiting "queue"
		for (BackupTask task : backupTasks) {
			if (task.getScheduledFuture() != null) {
				task.resetLocalDateTimeOfNextExecution();
				task.getScheduledFuture().cancel(false);
			}
			if (task.getPopupScheduledFuture() != null) {
				task.getPopupScheduledFuture().cancel(false);
			}
		}
		for (final BackupTask task : backupTasks) {
			scheduleBackupTask(task);
		}
	}

	/**
	 * Schedules the given BackupTask from the given time (as a starting point).
	 *
	 * @param task     BackupTask to schedule
	 * @param dateTime starting point for the scheduling
	 */
	private void scheduleBackupTaskStartingAt(final BackupTask task, LocalDateTime dateTime) {
		// checks whether this BackupTas is already running
		if (runningBackupTasks.contains(task.getTaskName())) {
			return;

		}
		LocalDateTime nextExecutionTime = null;
		int autoBackupMode = task.getAutoBackupMode();
		if (autoBackupMode == 0) {
			return;

		} else if (autoBackupMode == 1) {
			nextExecutionTime = calcTimeFromWeekdaysStartingFrom(task.getBackupWeekdays(), task.getBackupStartTime(),
					dateTime);

		} else if (autoBackupMode == 2) {
			nextExecutionTime = calcTimeFromDaysInMonthStartingFrom(task.getBackupDaysInMonth(),
					task.getBackupStartTime(), dateTime);

		} else if (autoBackupMode == 3) {
			nextExecutionTime = calcTimeFromIntervalStartingFrom(task.getIntervalTime(), task.getIntervalUnit(),
					dateTime);

		}
		scheduleBackupTaskAt(task, nextExecutionTime);
	}

	/**
	 * Schedules the given BackupTask to the given time.
	 *
	 * @param task              BackupTask to schedule
	 * @param nextExecutionTime time for the next execution
	 */
	private void scheduleBackupTaskAt(final BackupTask task, LocalDateTime nextExecutionTime) {
		// checks whether this BackupTas is already running
		if (runningBackupTasks.contains(task.getTaskName())) {
			return;
		}

		task.resetLocalDateTimeOfNextExecution();
		guiController.setNextExecutionTimeStatus(task.getTaskName(), nextExecutionTime);
		// scheduling:
		// for the backup itself
		Runnable backup = new Runnable() {
			public void run() {
				Thread backupThread = new Thread(new Runnable() {
					@Override
					public void run() {
						if (task.autostartIsEnabled()) {
							startPreparation(task);
						} else {
							task.setAutostart(true);
							startPreparation(task);
							task.setAutostart(false);
						}
					}
				});
				BackupThreadContainer newContainer = new BackupThreadContainer(backupThread, task.getTaskName());
				backupThreads.add(newContainer);
				backupThread.start();
			}
		};
		// for the popup
		Runnable popup = new Runnable() {
			public void run() {
				showTrayPopupMessage("BackupTask " + task.getTaskName() + " starts in one minute.");
			}
		};
		// schedule task
		task.setScheduledFuture(
				threadExecutor.schedule(backup, LocalDateTime.now().until(nextExecutionTime, ChronoUnit.SECONDS),
						TimeUnit.SECONDS));
		// schedule tray icon popup
		// check whether the backup execution is far enough in the future to show a "one minute warning"
		if (LocalDateTime.now().until(nextExecutionTime, ChronoUnit.SECONDS) >
				NUMBER_OF_SECONDS_UNTIL_NEXT_EXECUTION_FOR_POPUP) {
			task.setPopupScheduledFuture(threadExecutor.schedule(popup,
					LocalDateTime.now().minusMinutes(1).until(nextExecutionTime, ChronoUnit.SECONDS),
					TimeUnit.SECONDS));
		}

		// save next execution time within the BackupTask to be able to catch up missed backups after restart of
		// TotalBackup
		task.setLocalDateTimeOfNextBackup(nextExecutionTime);
	}

	/**
	 * Calculates the next execution time from the given weekday rules.
	 *
	 * @param weekdays weekdays to perform backup on
	 * @param time     backup-time
	 * @param startAt  start-time of the calculation
	 * @return time of the next execution (null if something went wrong)
	 */
	private LocalDateTime calcTimeFromWeekdaysStartingFrom(boolean[] weekdays, LocalTime time, LocalDateTime startAt) {
		DayOfWeek currentWeekday = startAt.getDayOfWeek();
		int weekdayNumber = currentWeekday.getValue() - 1;
		// today?
		if (weekdays[weekdayNumber]) {
			// check the exact time if a backup has to be performed today
			if (startAt.toLocalTime().isBefore(time)) {
				return time.atDate(startAt.toLocalDate());
			}
		}

		int wNumber = weekdayNumber + 1;
		int daysFromTodayCounter = 1;
		while (wNumber < 7) {
			if (weekdays[wNumber]) {
				LocalDate date = startAt.toLocalDate().plusDays(daysFromTodayCounter);
				return time.atDate(date);
			}
			wNumber++;
			daysFromTodayCounter++;
		}
		wNumber = 0;
		while (wNumber < 7) {
			if (weekdays[wNumber]) {
				if (daysFromTodayCounter > 7) {
					// TODO: Debugging-Ausgabe raus
					System.err.println("Error: Weekday more than 7 days in future is not possible");
				}
				LocalDate date = startAt.toLocalDate().plusDays(daysFromTodayCounter);
				return time.atDate(date);
			}
			wNumber++;
			daysFromTodayCounter++;
		}
		return null;
	}

	/**
	 * Deletes the scheduled execution of the given BackupTask.
	 *
	 * @param task BackupTask to delete scheduled execution
	 */
	public void removeBackupTaskScheduling(BackupTask task) {
		if (task.getScheduledFuture() != null) {
			task.getScheduledFuture().cancel(false);
			task.resetLocalDateTimeOfNextExecution();
		}
		if (task.getPopupScheduledFuture() != null) {
			task.getPopupScheduledFuture().cancel(false);
		}
	}

	/**
	 * Calculates the next execution time from the given days in month rules.
	 *
	 * @param daysInMonth days in month to perform backup on
	 * @param time        backup-time
	 * @param startAt     start-time of the calculation
	 * @return time of the next execution (null if something went wrong)
	 */
	private LocalDateTime calcTimeFromDaysInMonthStartingFrom(boolean[] daysInMonth, LocalTime time,
			LocalDateTime startAt) {
		int currentDayInMonth = startAt.getDayOfMonth() - 1;
		// today
		if (daysInMonth[currentDayInMonth]) {
			// check the exact time if a backup has to be performed today
			if (startAt.toLocalTime().isBefore(time)) {
				return time.atDate(startAt.toLocalDate());
			}
		}

		int mNumber = currentDayInMonth + 1;
		int daysFromTodayCounter = 1;
		while (mNumber <= (startAt.toLocalDate().lengthOfMonth() - 1)) {
			if (daysInMonth[mNumber]) {
				LocalDate date = startAt.toLocalDate().withDayOfMonth(daysFromTodayCounter);
				return time.atDate(date);
			}
			mNumber++;
			daysFromTodayCounter++;
		}
		mNumber = 0;
		daysFromTodayCounter = 0;
		while (mNumber <= (startAt.toLocalDate().lengthOfMonth() - 1)) {
			if (daysInMonth[mNumber]) {
				LocalDate date = startAt.toLocalDate().withDayOfMonth(daysFromTodayCounter);
				date = date.plusMonths(1);
				return time.atDate(date);
			}
			mNumber++;
			daysFromTodayCounter++;
		}
		return null;
	}

	/**
	 * Calculates the next execution time from the given interval rules.
	 *
	 * @param interval     interval value
	 * @param intervalUnit unit of the interval
	 * @param startAt      start-time of the calculation
	 * @return time of the next execution (null if something went wrong)
	 */
	private LocalDateTime calcTimeFromIntervalStartingFrom(int interval, String intervalUnit, LocalDateTime startAt) {
		switch (intervalUnit) {
			case "min":
				return startAt.plusMinutes(interval);
			case "h":
				return startAt.plusHours(interval);
			case "d":
				return startAt.plusDays(interval);
			case "m":
				return startAt.plusMonths(interval);
		}
		return null;
	}

	/**
	 * Deletes empty backup folders within the destination path.
	 *
	 * @param task corresponding BackupTask
	 */
	private void deleteEmptyBackupFolders(BackupTask task) {
		String path = task.getDestinationPath();
		File currentDest = new File(path);
		File[] backupFolders = currentDest.listFiles();
		if (backupFolders.length > 0) {
			for (File backupFolder : backupFolders) {
				if (!backupFolder.isDirectory()) {
					continue;
				}
				boolean deleteThisDir = true;
				File[] filesInBackupFolder = backupFolder.listFiles();
				for (File aFilesInBackupFolder : filesInBackupFolder) {
					if (aFilesInBackupFolder.isDirectory() && aFilesInBackupFolder.listFiles().length != 0) {
						deleteThisDir = false;
						break;
					}
				}
				if (deleteThisDir) {
					BackupHelper.deleteDirectory(backupFolder);
				}
			}
		}

		String outprint = "Deleted Backup-Directory";
		backupListener.setStatus(outprint, false, task.getTaskName());
		backupListener.log(outprint, task);
	}

	/**
	 * Shows the given message as popup-message.
	 *
	 * @param msg message to show
	 */
	private void showTrayPopupMessage(String msg) {
		if (guiController.isQTTray()) {
			guiController.sendToQtTrayOverSocket(msg, false);
		} else {
			guiController.showTrayPopupMessage(msg);
		}
	}

	/**
	 * Checks whether the given argument is part of the execution arguments of TotalBackup.
	 *
	 * @param arg argument to check
	 * @return whether it is part of the execution arguments (true) or not (false)
	 */
	public boolean argsContains(String arg) {
		for (String string : arguments) {
			if (string.equals(arg)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the BackupTask with the given name is currently running.
	 *
	 * @param taskName name of the BackupTask
	 * @return whether the corresponding task is running (true) or not (false)
	 */
	public boolean backupTaskIsRunning(String taskName) {
		return runningBackupTasks.contains(taskName);
	}

	/**
	 * Cancels all running tasks without further confirmation.
	 */
	private void cancelAllRunningTasksImmediately() {
		for (String taskName : runningBackupTasks) {
			cancelBackup(getBackupTaskWithName(taskName), true);
		}
	}

	/**
	 * Quits TotalBackup.
	 */
	private void quit() {
		int reply = JOptionPane.showConfirmDialog(null,
				"Really want to quit?\\nAll runningn Backups will be canceled!\\nNo scheduled Backups will be executed!",
				"Quit", JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			saveSerialization();
			cancelAllRunningTasksImmediately();
			if (guiController.isQTTray()) {
				guiController.sendToQtTrayOverSocket(null, true);
			}
			guiController.destroyTrayProcess();
			System.exit(0);
		}
	}
}
