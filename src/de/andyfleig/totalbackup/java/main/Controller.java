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

import data.BackupInfos;
import data.Source;
import gui.FxMainframe;
import gui.GuiController;
import gui.NextExecutionChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import listener.IBackupListener;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
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
import listener.INECListener;

/**
 * Controller zur Steuerung der Anwendung.
 *
 * @author Andreas Fleig
 */
public class Controller {

	/**
	 * Kommandozeilenargumente welche aus der Main übergeben werden.
	 */
	private String[] arguments;
	/**
	 * Liste aller erstellten Backup-Tasks.
	 */
	private ArrayList<BackupTask> backupTasks = new ArrayList<BackupTask>();
	/**
	 * Aktuelle IBackupListener-Instanz.
	 */
	private IBackupListener backupListener;
	/**
	 * Alle laufenden Backup-Taks
	 */
	private ArrayList<String> runningBackupTasks = new ArrayList<String>();
	/**
	 * Timer für die AutoBackup-Funktion.
	 */
	private ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(3);

	private ArrayList<BackupThreadContainer> backupThreads;

	/**
	 * Gibt an um wie viele Sekunden das nachzuholenden Backup (von now an) verzögert werden soll.
	 */
	private static final int DELAY_FOR_MISSED_BACKUP = 2;

	private static final long NUMBER_OF_SECONDS_UNTIL_NEXT_EXECUTION_FOR_POPUP = 90;

	// Größe einer Inode (in Byte):
	private static final double SIZE_OF_INODE = 4096;

	private GuiController guiController;

	public Controller(FxMainframe fxMainframe) {
		guiController = new GuiController(new IGUIControllerListener() {
			@Override
			public boolean argsContains(String s) {
				return Controller.this.argsContains(s);
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
				savePropertiesGson();
			}

			@Override
			public void scheduleBackupTask(BackupTask task) {
				Controller.this.scheduleBackupTask(task);
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
			public int deleteBackupTaskWithName(String taskName) {
				for (int i = 0; i < backupTasks.size(); i++) {
					if (backupTasks.get(i).getTaskName().equals(taskName)) {
						Controller.this.removeBackupTask(backupTasks.get(i));
						return 1;
					}
				}
				return 0;
			}

			@Override
			public void deleteEmptyBackupFolders(String path, BackupTask task) {
				Controller.this.deleteEmptyBackupFolders(path, task);
			}
		}, fxMainframe);
	}

	public void startController(String[] args) {
	}

	/**
	 * Startet und initialisiert den Controller.
	 * ToDo: Rename
	 */
	public void startController2(String[] args) {
		this.arguments = args;
		// Dafür sorgen dass nur eine Instanz des Programms laufen kann:
		try {
			new ServerSocket(2210);
		} catch (IOException e) {
			// Programm läuft schon:
			System.out.println("TotalBackup is already running");
			System.exit(1);
		}

		backupThreads = new ArrayList<BackupThreadContainer>();

		try {
			java.awt.EventQueue.invokeAndWait(new Runnable() {
				public void run() {

					//GuiController guiController = new GuiController();

					/*
					mainframe = new Mainframe(new IMainframeListener() {

						@Override
						public void startPreparation(BackupTask task) {
							Controller.this.startPreparation(task);
						}

						@Override
						public void startBackupTask(BackupTask task, Backupable backup) {
							Controller.this.startBackup(task, backup);

						}

						@Override
						public void removeBackupTask(BackupTask task) {
							Controller.this.removeBackupTask(task);
						}

						@Override
						public ArrayList<BackupTask> getBackupTasks() {
							return backupTasks;
						}

						@Override
						public BackupTask getBackupTaskWithName(String name) {
							return Controller.this.getBackupTaskWithName(name);
						}

						@Override
						public ArrayList<String> getBackupTaskNames() {
							return Controller.this.getBackupTaskNames();
						}

						@Override
						public void addBackupTask(BackupTask task) {
							Controller.this.addBackupTask(task);
						}

						@Override
						public void deleteEmptyBackupFolders(String path, BackupTask task) {
							Controller.this.deleteEmptyBackupFolders(path, task);
						}

						@Override
						public void outprintBackupCanceled(BackupTask task) {
							String outprint = ResourceBundle.getBundle("messages").getString("Messages.BackupCanceled");
							backupListener.printOut(outprint, false, task.getTaskName());
							backupListener.log(outprint, task);
						}

						@Override
						public void printOut(String s, boolean error, String taskName) {
							Controller.this.printOut(s, error, taskName);
						}

						@Override
						public void log(String event, BackupTask task) {
							Controller.this.log(event, task);
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
						public void scheduleBackupTask(BackupTask task) {
							Controller.this.scheduleBackupTask(task);
						}

						@Override
						public void removeBackupTaskScheduling(BackupTask task) {
							Controller.this.removeBackupTaskScheduling(task);
						}

						@Override
						public ArrayList<String> getRunningBackupTasks() {
							return runningBackupTasks;
						}

						@Override
						public void scheduleBackupTaskNow(BackupTask task) {
							Controller.this.scheduleBackupTaskNow(task);
						}

						@Override
						public void removeBackupTaskFromRunningTasks(BackupTask task, boolean schedule) {
							Controller.this.taskFinished(task, schedule);
						}

						@Override
						public void scheduleBackupTaskAt(BackupTask task, LocalDateTime time) {
							task.resetLocalDateTimeOfNextExecution();
							Controller.this.scheduleBackupTaskAt(task, time);
						}

						@Override
						public void rescheduleBackupTask(BackupTask task) {
							LocalDateTime nextExecution = task.getLocalDateTimeOfNextBackup();
							task.resetLocalDateTimeOfNextExecution();
							Controller.this.scheduleBackupTaskStartingAt(task, nextExecution.plusSeconds(1));
						}

						@Override
						public boolean argsContains(String s) {
							return Controller.this.argsContains(s);
						}

						@Override
						public boolean isBackupTaskRunning(String s) {
							return Controller.this.isBackupTaskRunning(s);
						}

						@Override
						public void saveProperties() {
							savePropertiesGson();
						}

						@Override
						public void cancelBackup(BackupTask task, boolean reschedule) {
							Controller.this.cancelBackup(task, reschedule);
						}

						@Override
						public void quitTotalBackup() {
							quit();
						}
					});*/
				}
			});

			loadSerializationGson();
			// Liste aller versäumten BackupTasks:
			ArrayList<BackupTask> missedBackupTaks = new ArrayList<>();
			// Prüfen ob Backups versäumt wurden:
			for (BackupTask task : backupTasks) {
				if (task.getLocalDateTimeOfNextBackup() != null &&
						task.getLocalDateTimeOfNextBackup().isBefore(LocalDateTime.now())) {
					// Dieses Backup wurde versäumt
					missedBackupTaks.add(task);
				}
			}
			// Alle Tasks werden neu geschedulet:
			scheduleBackupTasks();

			// Versäumte Backups nachholen:
			for (BackupTask task : missedBackupTaks) {
				// Prüfen ob es sich noch lohnt das Backup nachzuholen (anhand
				// von profitableTimeUntilNextExecution des Tasks):
				if ((task.getLocalDateTimeOfNextBackup().minusMinutes(
						task.getProfitableTimeUntilNextExecution())).isAfter(LocalDateTime.now())) {
					String msg = ResourceBundle.getBundle("messages").getString("Messages.popup.catchUp1") + " " +
							task.getTaskName() + " " +
							ResourceBundle.getBundle("messages").getString("Messages.popup.catchUp2");
					showTrayPopupMessage(msg);
					scheduleBackupTaskNow(task);
				}
			}
		} catch (InterruptedException e) {
			System.err.println("Error: InterruptedException while starting Controller");
		} catch (InvocationTargetException ex) {
			System.err.println("Error: InvocationTargetException while starting Controller");
		}
		guiController.initialize();
	}

	private void loadSerializationGson() {
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
				guiController.addBackupTask(backupTask.getTaskName());
			}
		}
	}

	/**
	 * Startet die Backup-Vorbereitung (in eigenem Thread).
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


	private void runPreparation(BackupTask task) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Testen ob Quell- und Zielpfad(e) existieren:
		ArrayList<Source> sources = task.getSources();
		for (Source source : sources) {
			if (!(new File(source.getPath())).exists()) {
				String output = ResourceBundle.getBundle("messages").getString("GUI.Mainframe.errorSourceDontExists");
				printOut(output, false, task.getTaskName());
				log(output, task);
				guiController.disposePreparingDialogIfNotNull();
				taskFinished(task, true);
				return;
			}
		}

		// DestinationVerification:
		String OS = System.getProperty("os.name").toLowerCase();
		if (!OS.contains("win") && task.getDestinationVerification()) {
			File identifier = new File(task.getDestinationPath() + "/" + task.getTaskName() + ".id");
			if (!(new File(task.getDestinationPath())).exists() || !identifier.exists()) {
				// Abfrage: Pfad suchen?:
				int reply = JOptionPane.showConfirmDialog(null,
						ResourceBundle.getBundle("messages").getString("Messages.SearchForCorrectDestPath"), null,
						JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					// ja:
					ArrayList<String> correctDest = searchForCorrectDestPath(task.getTaskName(),
							task.getDestinationPath());
					boolean successful = false;
					for (String dest : correctDest) {
						int reply2 = JOptionPane.showConfirmDialog(null,
								ResourceBundle.getBundle("messages").getString("Messages.FoundDestCorrect1") + " " +
										dest + "  " +
										ResourceBundle.getBundle("messages").getString("Messages.FoundDestCorrect2"),
								null, JOptionPane.YES_NO_OPTION);
						if (reply2 == JOptionPane.YES_OPTION) {
							int reply3 = JOptionPane.showConfirmDialog(null,
									ResourceBundle.getBundle("messages").getString("Messages.SetNewPathAsDest"), null,
									JOptionPane.YES_NO_OPTION);
							if (reply3 == JOptionPane.YES_OPTION) {
								successful = true;
								task.setDestinationPath(dest);
							} else {
								successful = true;
								task.setRealDestinationPath(task.getDestinationPath());
								task.setDestinationPath(dest);
								savePropertiesGson();
							}
						}
					}
					if (!successful) {
						cancelBackup(task, false);
						guiController.disposePreparingDialogIfNotNull();
						askForNextExecution(task);
						return;
					}

				} else {
					// nein:
					cancelBackup(task, false);
					guiController.disposePreparingDialogIfNotNull();
					askForNextExecution(task);
					return;
				}
			}
		}

		// Prüfen ob der Zielpfad existiert:
		if (!(new File(task.getDestinationPath())).exists()) {
			cancelBackup(task, false);
			guiController.disposePreparingDialogIfNotNull();
			askForNextExecution(task);
			return;
		}
		// Listener anlegen:
		backupListener = new IBackupListener() {

			@Override
			public void printOut(final String s, final boolean error, final String taskName) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						Controller.this.printOut(s, error, taskName);
					}

				});
			}

			@Override
			public void setStatus(final String status) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						guiController.setStatusOfBackupTask(task.getTaskName(), true, status);
					}


				});
			}

			@Override
			public void log(String event, BackupTask task) {
				Controller.this.log(event, task);

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
			public void deleteEmptyBackupFolders(String path, BackupTask task) {
				Controller.this.deleteEmptyBackupFolders(path, task);

			}

		};
		Backupable backup;
		// Backup-Objekt in Abhängigkeit des Backup-Modus erstellen:
		if (task.getBackupMode() == 1) {
			// Prüfen ob bereits ein "normales" Backup erstellt wurde oder ob es
			// sich um die erste Ausführung handelt:
			File[] files = new File(task.getDestinationPath()).listFiles();
			boolean backupSetFound = false;
			for (File file : files) {
				if (file.isDirectory()) {
					// Namen des Ordners "zerlegen":
					StringTokenizer tokenizer = new StringTokenizer(file.getName(), "_");
					// Es wird geprüft ob der Name aus genau 2 Tokens besteht:
					if (tokenizer.countTokens() != 2) {
						continue;
					}
					// Erster Token muss dem TaskName entsprechen:
					if (!tokenizer.nextToken().equals(task.getTaskName())) {
						continue;
					}
					backupSetFound = true;
					break;
				}
			}
			if (backupSetFound) {
				String output = ResourceBundle.getBundle("messages").getString("Messages.startHardlinkBackup");
				printOut(output, false, task.getTaskName());
				log(output, task);
				backup = new HardlinkBackup(backupListener, task.getTaskName(), task.getSources(),
						task.getDestinationPath());
			} else {
				String output = ResourceBundle.getBundle("messages").getString("Messages.startNormalBackup");
				printOut(output, false, task.getTaskName());
				log(output, task);
				backup = new NormalBackup(backupListener, task.getTaskName(), task.getSources(),
						task.getDestinationPath());
			}
		} else {
			backup = new NormalBackup(backupListener, task.getTaskName(), task.getSources(), task.getDestinationPath());
		}

		try {
			backup.runPreparation(task);
		} catch (BackupCanceledException ex) {
			String output = ResourceBundle.getBundle("messages").getString("Messages.CanceledByUser");
			printOut(output, false, task.getTaskName());
			log(output, task);
		}

		// TODO: Probleme mit setPrepared bei abbruch?
		task.setPrepared(true);

		guiController.disposePreparingDialogIfNotNull();

		// TODO: nichts zu tun -> Meldung
		// Prüfen ob ausreichend freier Speicherplatz verfügbar ist:
		File destDir = new File(task.getDestinationPath());
		double freeSize = destDir.getFreeSpace();
		BackupInfos backupInfos = backup.getBackupInfos();
		// TODO: Zusätzliche Warnung wenn knapp (z.B. 1%)
		double sizeNeeded = backupInfos.getSizeToCopy() + SIZE_OF_INODE * backupInfos.getNumberOfFilesToCopy() +
				SIZE_OF_INODE * backupInfos.getNumberOfDirectories();
		if (freeSize <= sizeNeeded) {
			// Es steht nicht ausreichend Speicherplatz zur Verfügung:
			JOptionPane.showMessageDialog(null,
					ResourceBundle.getBundle("messages").getString("GUI.Mainframe.errNotEnoughSpace"),
					ResourceBundle.getBundle("messages").getString("GUI.errMsg"), JOptionPane.INFORMATION_MESSAGE);
			// Backup abbrechen:
			return;
		}
		boolean isCanceled = true;
		for (BackupThreadContainer container : backupThreads) {
			if (container.getTaskName().equals(task.getTaskName())) {
				isCanceled = false;
			}
		}
		if (!isCanceled) {
			if (!task.getAutostart()) {
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
	 * Serialisiert die Programm-Einstellungen (Backup-Tasks) mit Gson.
	 */
	private void savePropertiesGson() {
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
	 * Serialisiert die Programm-Einstellungen (Backup-Taks)
	 *
	 * @deprecated
	 */
	private void saveProperties() {
		File properties = new File("./properties.ser");
		if (!properties.exists()) {
			try {
				properties.createNewFile();
			} catch (IOException ex) {
				System.err.println("Error: IOException in Mainframe in saveProperties while creating properties file");
			}
		}

		OutputStream fos = null;
		ObjectOutputStream o = null;

		try {
			fos = new FileOutputStream(properties);
			o = new ObjectOutputStream(fos);

			o.writeObject(backupTasks);
		} catch (IOException ex) {
			System.out.println("Error: IOException in Mainframe in saveProperties while creating FileOutputStream, " +
					"ObjectOutputStream and writin out Object");
		} finally {
			if (o != null) {
				try {
					o.close();
				} catch (IOException ex) {
					System.err.println(
							"Error: IOException in Mainframe in saveProperties while closing ObjectOutputStream");
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex) {
					System.err.println(
							"Error: IOException in Mainframe in saveProperties while closing FileOutputStream");
				}
			}
		}
	}

	/**
	 * Bricht den gegebenen BackupTask ab und reschedult den BackupTask wenn rescheduling true ist.
	 *
	 * @param task       abzubrechender BackupTask
	 * @param reschedule gibt an, ob der BackupTask rescheduled werden soll
	 */
	private void cancelBackup(BackupTask task, boolean reschedule) {
		guiController.setStatusOfBackupTask(task.getTaskName(), false,
				ResourceBundle.getBundle("messages").getString("Messages.CancelingBackup"));

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
	 * Erfragt beim Benutzer wann das abgebrochene Backup das nächste mal ausgeführt werden soll.
	 */
	public void askForNextExecution(BackupTask task) {
		NextExecutionChooser nec = new NextExecutionChooser(new INECListener() {
			@Override
			public void skipBackup() {
				rescheduleBackupTask(task);
			}

			@Override
			public void postponeBackup(LocalDateTime nextExecutionTime) {
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
	 * Sucht nach dem "richtigen" Zielpfad. Gibt eine Liste möglicher Kandidaten zurück.
	 *
	 * @param taskName      Name des BackupTasks
	 * @param wrongDestPath "falscher" Zielpfad
	 * @return Liste möglicher "richiger" Zielpfade
	 */
	public ArrayList<String> searchForCorrectDestPath(String taskName, String wrongDestPath) {
		ArrayList<String> foundDestPaths = new ArrayList<String>();
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

	private boolean checkForIdentifier(String taskName, String dest) {
		for (File file : (new File(dest)).listFiles()) {
			if (file.getName().equals(taskName + ".id")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Startet den Backup-Vorgang eines bestimmten Backup-Tasks.
	 *
	 * @param task Backup-Task welcher ausgeführt werden soll
	 */
	public void startBackup(BackupTask task, Backupable backup) {
		// "Richtigen" Zielpfad setzten (wenn nötig):
		if (task.getRealDestinationPath() != null) {
			task.setDestinationPath(task.getRealDestinationPath());
			task.setRealDestinationPath(null);
		}

		if (!task.isPrepered()) {
			return;
		}

		try {
			backup.runBackup(task);
		} catch (IOException e) {
			System.err.println("Fehler beim einlesen der Datei(en)");
		} catch (BackupCanceledException ex) {
			String output = ResourceBundle.getBundle("messages").getString("Messages.CanceledByUser");
			printOut(output, false, task.getTaskName());
			log(output, task);
		}
		// "Richtigen" Zielpfad setzten (wenn nötig):
		if (task.getRealDestinationPath() != null) {
			task.setDestinationPath(task.getRealDestinationPath());
			task.setRealDestinationPath(null);
		}

		// alte Backups aufräumen (wenn gewünscht):
		if (task.simpleAutoCleanIsEnabled()) {
			try {
				while (this.calcNumberOfBackups(task) > task.getNumberOfBackupsToKeep()) {
					File toDelete = new File(task.getDestinationPath() + File.separator + findOldestBackup(
							new ArrayList<File>(Arrays.asList((new File(task.getDestinationPath()).listFiles()))),
							task));

					String output = ResourceBundle.getBundle("messages").getString("Messages.deleting") + " " +
							toDelete.getAbsolutePath();
					guiController.setStatusOfBackupTask(task.getTaskName(), false, output);
					log(output, task);
					if (!BackupHelper.deleteDirectory(toDelete)) {
						System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
					}
					printOut(toDelete.getAbsolutePath() + " " +
									ResourceBundle.getBundle("messages").getString("Messages.deleted"), false,
							task.getTaskName());
				}
			} catch (BackupCanceledException e) {
				String outprint = ResourceBundle.getBundle("messages").getString("Messages.CanceledByUser");
				printOut(outprint, false, task.getTaskName());
				log(outprint, task);
			}
		} else if (task.extendedAutoCleanIsEnabled()) {
			runExtendedClean(task);
		}

		task = null;

	}

	/**
	 * Gibt eine Liste mit allen Namen aller Backup-Tasks zurück.
	 *
	 * @return Liste der Namen aller Backup-Tasks
	 */
	public ArrayList<String> getBackupTaskNames() {
		ArrayList<String> backupTaskNames = new ArrayList<String>();
		for (BackupTask backupTask : backupTasks) {
			backupTaskNames.add(backupTask.getTaskName());
		}
		return backupTaskNames;
	}

	/**
	 * Gibt den gegebenen String auf der GUI aus. error bestimmt ob es sich um eine Fehlermeldung (rot) handelt oder
	 * nicht.
	 *
	 * @param s     auszugebender String
	 * @param error legt fest ob es sich um eine Fehlermeldung handelt oder nicht
	 */
	private void printOut(String s, boolean error, String taskName) {
		guiController.setStatusOfBackupTask(taskName, error, s);
	}

	/**
	 * Schreibt den gegebenen String in das log-File des gegebenen Tasks.
	 *
	 * @param event zu loggender String
	 * @param task  zugehöriger Task
	 */
	private void log(String event, BackupTask task) {
		// Log-Datei anlegen:
		if (task == null) {
			return;
		}
		File log = new File(task.getDestinationPath() + File.separator + task.getTaskName() + ".log");
		// Kontrollieren ob bereits eine log Datei exisitert:
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
			String output = dtf.format(dateDime) + ": " + event;

			writer.append("\n" + output);
			writer.close();
		} catch (FileNotFoundException e) {
			System.err.println("Fehler: log Datei nicht gefunden");
		}
	}

	/**
	 * Liefert den Backup-Task mit gegebenem Namen zurück. Exisitert kein Backup mit dem angegebenen Namen so wird null
	 * zurückgeliefert.
	 *
	 * @param name Name des "gesuchten" Backup-Tasks
	 * @return den gesuchten Backup-Task oder null
	 */
	public BackupTask getBackupTaskWithName(String name) {
		for (BackupTask backupTask : backupTasks) {
			if (backupTask.getTaskName().equals(name)) {
				return backupTask;
			}
		}
		return null;
	}

	/**
	 * Fügt einen Backup-Task hinzu.
	 *
	 * @param task hinzuzufügender Backup-Task
	 */
	public void addBackupTask(BackupTask task) {
		backupTasks.add(task);
		guiController.addBackupTask(task.getTaskName());
	}

	/**
	 * Löscht einen Backup-Task.
	 *
	 * @param task zu löschender Backup-Task.
	 */
	public void removeBackupTask(BackupTask task) {
		backupTasks.remove(task);
		guiController.removeBackupTask(task.getTaskName());
	}

	/**
	 * Gibt die Anzahl der Backup-Sätze zum gegebenen Backup-Task zurück.
	 *
	 * @param task betreffender BackupTask
	 * @return Anzahl der Backup-Sätze zum gegebenen Backup-Task
	 */
	private int calcNumberOfBackups(BackupTask task) {
		File dest = new File(task.getDestinationPath());
		return findBackupSets(dest, task).size();
	}

	/**
	 * Gibt eine Liste aller Backupsätze im gegebenen Verzeichnis zurück.
	 *
	 * @param dir  zu durchsuchendes Verzeichnis
	 * @param task betreffender BackupTask
	 * @return Liste aller gefundenen Backupsätze
	 */
	private ArrayList<File> findBackupSets(File dir, BackupTask task) {
		File[] files = dir.listFiles();
		ArrayList<File> foundBackupsets = new ArrayList<File>();
		for (File file : files) {
			// Namen des Ordners "zerlegen":
			StringTokenizer tokenizer = new StringTokenizer(file.getName(), "_");
			// Es wird geprüft ob der Name aus genau 2 Tokens besteht:
			if (tokenizer.countTokens() != 2) {
				continue;
			}
			// Erster Token muss dem TaskName entsprechen:
			if (!tokenizer.nextToken().equals(task.getTaskName())) {
				continue;
			}
			// Zweiter Token muss analysiert werden:
			String backupDate = tokenizer.nextToken();

			try {
				SimpleDateFormat sdfToDate = new SimpleDateFormat(BackupHelper.BACKUP_FOLDER_NAME_PATTERN);
				sdfToDate.parse(backupDate);
				foundBackupsets.add(file);
			} catch (ParseException e) {
				// Offenbar kein gültiges Datum
			}
		}
		return foundBackupsets;
	}

	/**
	 * Gibt den Pfad des ältesten Backup-Satzes zurück.
	 *
	 * @param directories Ordner unter denen der älteste Backupsatz gefunden werden soll
	 * @param task        betreffender BackupTask
	 * @return Pfad des ältesten Backup-Satzes
	 */
	private String findOldestBackup(ArrayList<File> directories, BackupTask task) {

		Date oldestDate = null;
		String oldestBackupPath = null;
		Date foundDate;
		for (File directory : directories) {
			if (directory.isDirectory()) {
				// Namen des Ordners "zerlegen":
				StringTokenizer tokenizer = new StringTokenizer(directory.getName(), "_");
				// Es wird geprüft ob der Name aus genau 2 Tokens besteht:
				if (tokenizer.countTokens() != 2) {
					continue;
				}
				// Erster Token muss dem TaskName entsprechen:
				if (!tokenizer.nextToken().equals(task.getTaskName())) {
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
	 * Führt für den gegebenen BackupTask das erweiterte CleanUp durch.
	 *
	 * @param task BackupTask für den das CleanUp durchgeführt werden soll
	 */
	private void runExtendedClean(BackupTask task) {
		// aktuelle SystemZeit:
		LocalDateTime currentSystemTime = LocalDateTime.now();

		// Liste aller existenten Backupsätze anlegen:
		ArrayList<File> backupSets = findBackupSets(new File(task.getDestinationPath()), task);

		if (backupSets.size() == 0) {
			return;
		}
		// Buckets anlegen (Anzahl = Menge der Regeln):
		ArrayList<File> bucket1 = new ArrayList<File>();
		ArrayList<File> bucket2 = new ArrayList<File>();
		ArrayList<File> bucket3 = new ArrayList<File>();
		ArrayList<File> bucket4 = new ArrayList<File>();
		ArrayList<File> bucket5 = new ArrayList<File>();

		// Dates für die Bucket-Grenzen erstellen:
		LocalDateTime[] boundaries = new LocalDateTime[task.getNumberOfExtendedCleanRules() - 1];
		String[] boundaryStrings = task.getBoundaries();
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

		// Bäckupsätze in die Buckets sortieren
		for (File backupSet : backupSets) {
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
			// dateOfCurrentBackupSet in LocalTimeDate umwandeln:
			LocalDateTime ltmOfCurrentBackupSet = LocalDateTime.ofInstant(dateOfCurrentBackupSet.toInstant(),
					ZoneId.systemDefault());

			// Richtiges Bucket finden und einfügen:
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

		// Alle Buckets der maximalgröße Entsprechend "ausmisten":
		// Kontrolle auf Wert "all":
		if (task.getBackupsToKeep().length > 0 && task.getBackupsToKeep()[0] != -1) {
			while (!bucket1.isEmpty() && bucket1.size() > Integer.valueOf(task.getBackupsToKeep()[0])) {
				if (!BackupHelper.deleteDirectory(
						new File(task.getDestinationPath() + "/" + findOldestBackup(bucket1, task)))) {
					System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
					break;
				}
			}
		}

		if (task.getBackupsToKeep().length > 1 && task.getBackupsToKeep()[1] != -1) {
			while (!bucket2.isEmpty() && bucket2.size() > Integer.valueOf(task.getBackupsToKeep()[1])) {
				File oldestBackupSet = new File(task.getDestinationPath() + "/" + findOldestBackup(bucket2, task));
				if (!BackupHelper.deleteDirectory(oldestBackupSet)) {
					System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
					break;
				}
				bucket2.remove(oldestBackupSet);
			}
		}

		if (task.getBackupsToKeep().length > 2 && task.getBackupsToKeep()[2] != -1) {
			while (!bucket3.isEmpty() && bucket3.size() > Integer.valueOf(task.getBackupsToKeep()[2])) {
				if (!BackupHelper.deleteDirectory(
						new File(task.getDestinationPath() + "/" + findOldestBackup(bucket3, task)))) {
					System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
					break;
				}
			}
		}

		if (task.getBackupsToKeep().length > 3 && task.getBackupsToKeep()[3] != -1) {
			while (!bucket4.isEmpty() && bucket4.size() > Integer.valueOf(task.getBackupsToKeep()[3])) {
				if (!BackupHelper.deleteDirectory(
						new File(task.getDestinationPath() + "/" + findOldestBackup(bucket4, task)))) {
					System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
					break;
				}
			}
		}

		if (task.getBackupsToKeep().length > 4 && task.getBackupsToKeep()[4] != -1) {
			while (!bucket4.isEmpty() && bucket5.size() > Integer.valueOf(task.getBackupsToKeep()[4])) {
				if (!BackupHelper.deleteDirectory(
						new File(task.getDestinationPath() + "/" + findOldestBackup(bucket5, task)))) {
					System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
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
	 * Fügt den gegebenen Task zur Liste der laufenden Backup-Tasks hinzu.
	 *
	 * @param taskName Name des hinzuzufügenden Backup-Tasks
	 */
	private void taskStarted(String taskName) {
		runningBackupTasks.add(taskName);
		System.out.println("Task started:" + taskName);
	}

	/**
	 * Entfernt den gegebenen Task aus der Liste der laufenden Backup-Tasks.
	 *
	 * @param task Der zu entfernenden Backup-Task
	 */
	private void taskFinished(BackupTask task, boolean schedule) {
		if (!runningBackupTasks.remove(task.getTaskName())) {
			System.err.println("Error: This task isn't running");
		}
		System.out.println("Task finished:" + task.getTaskName());
		if (schedule) {
			scheduleBackupTask(task);
		}
	}

	/**
	 * Reschedulet den gegebenen Task.
	 *
	 * @param task Task der gereschedulet werden soll
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
	 * Reschedult den gegebenen Task von einem bestimmten Zeitpunkt aus.
	 *
	 * @param task     zu schedulender Task
	 * @param dateTime Zeitpunkt von dem aus geschedult werden soll
	 */
	private void scheduleBackupTaskStartingAt(final BackupTask task, LocalDateTime dateTime) {
		// Kontrollieren ob dieser Task bereits läuft:
		if (runningBackupTasks.contains(task.getTaskName())) {
			return;
		}
		LocalDateTime nextExecutionTime = null;
		int autoBackupMode = task.getAutoBackupMode();
		if (autoBackupMode == 0) {
			return;
		} else if (autoBackupMode == 1) {
			nextExecutionTime = calcTimeFromWeekdaysStartingFrom(task.getBackupWeekdays(), task.getStartTime(),
					dateTime);
		} else if (autoBackupMode == 2) {
			nextExecutionTime = calcTimeFromDaysInMonthStartingFrom(task.getBackupDaysInMonth(), task.getStartTime(),
					dateTime);
		} else if (autoBackupMode == 3) {
			nextExecutionTime = calcTimeFromIntervalStartingFrom(task.getIntervalTime(), task.getIntervalUnit(),
					dateTime);
		}
		scheduleBackupTaskAt(task, nextExecutionTime);
	}

	/**
	 * Schedulet den gegebenen Task auf jetzt (+ Verzögerung).
	 *
	 * @param task zu schedulender Task
	 */
	public void scheduleBackupTaskNow(final BackupTask task) {
		// Kontrollieren ob dieser Task bereits läuft:
		if (runningBackupTasks.contains(task.getTaskName())) {
			return;
		}
		printOut(ResourceBundle.getBundle("messages").getString("Messages.scheduleBackupNow"), false,
				task.getTaskName());
		scheduleBackupTaskAt(task, LocalDateTime.now().plusSeconds(DELAY_FOR_MISSED_BACKUP));
	}

	/**
	 * Schedulet den gegebenen Task auf die gegebene Zeit.
	 *
	 * @param task              zu schedulenden Task
	 * @param nextExecutionTime Zeit auf die der Task geschedulet wird
	 */
	private void scheduleBackupTaskAt(final BackupTask task, LocalDateTime nextExecutionTime) {
		task.resetLocalDateTimeOfNextExecution();
		guiController.setStatusOfBackupTask(task.getTaskName(), false, "Next Execution: " + nextExecutionTime.toString());
		// scheduling:
		// Für das Backup:
		Runnable backup = new Runnable() {
			public void run() {
				taskStarted(task.getTaskName());
				Thread backupThread = new Thread(new Runnable() {
					@Override
					public void run() {
						if (task.getAutostart()) {
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
		// Für das Popup:
		Runnable popup = new Runnable() {
			public void run() {
				showTrayPopupMessage(ResourceBundle.getBundle("messages").getString("Messages.popup.backupTask") + " " +
						task.getTaskName() + " " +
						ResourceBundle.getBundle("messages").getString("Messages.popup.startsInOneMinute"));
			}
		};
		// Task (mit timer) schedulen:
		task.setScheduledFuture(timer.schedule(backup, LocalDateTime.now().until(nextExecutionTime, ChronoUnit.SECONDS),
				TimeUnit.SECONDS));
		// Tray-Icon-Popup (mit timer) schedulen:
		// Prüfen ob das Backup weit genug in der Zukunft liegt um 1min vorher
		// ein Popup anzuzeigen:
		if (LocalDateTime.now().until(nextExecutionTime, ChronoUnit.SECONDS) >
				NUMBER_OF_SECONDS_UNTIL_NEXT_EXECUTION_FOR_POPUP) {
			task.setPopupScheduledFuture(timer.schedule(popup,
					LocalDateTime.now().minusMinutes(1).until(nextExecutionTime, ChronoUnit.SECONDS),
					TimeUnit.SECONDS));
		}

		// Nächsten Ausführungszeitpunkt (als LocalDateTime) im Task sichern (um
		// Backups nachholen zu können):
		task.setLocalDateTimeOfNextBackup(nextExecutionTime);
	}

	/**
	 * Reschedulet alle Tasks, dabei dürfen laufende Backups weiterlaufen.
	 */
	public void scheduleBackupTasks() {
		// Alle aus dem Timer werfen, die geplant aber noch nicht gestartet sind
		for (BackupTask task : backupTasks) {
			if (task.getScheduledFuture() != null) {
				task.resetLocalDateTimeOfNextExecution();
				task.getScheduledFuture().cancel(false);
			}
			if (task.getPopupScheduledFuture() != null) {
				task.getPopupScheduledFuture().cancel(false);
			}
		}
		// Einstellungen aus dem Backup-Task holen und im LocalTime Objekte
		// "umwandeln" (für jeden Task)
		for (final BackupTask task : backupTasks) {
			scheduleBackupTask(task);
		}
	}

	/**
	 * Berechnet (als LocalDateTime) den nächsten Ausführungszeitpunkt aus den gegebenen weekdays ab dem gegebenen
	 * Zeitpunkt.
	 *
	 * @param weekdays Wochentage an denen gesichert werden soll
	 * @param time     Zeit zu der gesichert werden soll
	 * @param startAt  Zeitpunkt von dem aus gerchnet werden soll
	 * @return nächster Ausführungszeitpunkt oder null im Fehlerfall
	 */
	private LocalDateTime calcTimeFromWeekdaysStartingFrom(boolean[] weekdays, LocalTime time, LocalDateTime startAt) {
		DayOfWeek currentWeekday = startAt.getDayOfWeek();
		int weekdayNumber = currentWeekday.getValue() - 1;
		// Heute?:
		if (weekdays[weekdayNumber]) {
			// Heute istBackup-Tag, also muss die Zeit betrachet werden:
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
	 * Löscht die geplante Ausführung des gegebenen Tasks.
	 *
	 * @param task entsprechender Task
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
	 * Berechnet (als LocalDateTime) den nächsten Ausführungszeitpunkt aus den gegebenen daysInMonth.
	 *
	 * @param daysInMonth Tage im Monat an denen gesichert werden soll
	 * @param time        Zeit zu der gesichert werden soll
	 * @param startAt     Zeitpunkt von dem aus gerchnet werden soll
	 * @return nächster Ausführungszeitpunkt oder null im Fehlerfall
	 */
	private LocalDateTime calcTimeFromDaysInMonthStartingFrom(boolean[] daysInMonth, LocalTime time,
			LocalDateTime startAt) {
		int currentDayInMonth = startAt.getDayOfMonth() - 1;
		// Heute?:
		if (daysInMonth[currentDayInMonth]) {
			// Heute istBackup-Tag, also muss die Zeit betrachet werden:
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
	 * Berechnet (als LocalDateTime) den nächsten Ausführungszeitpunkt aus dem gegebenen Intervall.
	 *
	 * @param interval     gegebenes Intervall
	 * @param intervalUnit Einheit des Intervals (min, h, d, m)
	 * @param startAt      Zeitpunkt von dem aus gerchnet werden soll
	 * @return nächster Ausführungszeitpunkt oder null im Fehlerfall
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
	 * Löscht einen leeren Backup-Ordner.
	 *
	 * @param task BackupTask zu dem der Ordner gehört
	 */
	private void deleteEmptyBackupFolders(String path2, BackupTask task) {
		String path = task.getDestinationPath();
		File currentDest = new File(path);
		File[] backupFolders = currentDest.listFiles();
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
		String outprint = ResourceBundle.getBundle("messages").getString("Messages.deletedBackupFolder");
		backupListener.printOut(outprint, false, task.getTaskName());
		backupListener.log(outprint, task);
	}

	/**
	 * Gibt den gegebenen String als Tray-Popup-Message aus.
	 *
	 * @param msg anzuzeigender String
	 */
	private void showTrayPopupMessage(String msg) {
		if (guiController.isQTTray()) {
			guiController.sendToQtTrayOverSocket(msg, false);
		} else {
			guiController.showTrayPopupMessage(msg);
		}
	}

	/**
	 * Prüft ob der gegebene String teil der übergebenen Argumente ist.
	 *
	 * @param s zu prüfender String (gesuchtes Argument)
	 * @return ob der gegebene String teil der übergebenen Argumente ist
	 */
	public boolean argsContains(String s) {
		for (String string : arguments) {
			if (string.equals(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Prüft ob ein BackupTask mit dem gegebenen Namen gerade ausgeführt wird.
	 *
	 * @param s Name des BackupTasks
	 * @return ob ein BackupTask mit dem gegebenen Namen gerade ausgeführt wird
	 */
	public boolean isBackupTaskRunning(String s) {
		return runningBackupTasks.contains(s);
	}

	/**
	 * Reschedult den gegebenen BackupTask.
	 *
	 * @param task zu reschedulender BackupTask
	 */
	public void rescheduleBackupTask(BackupTask task) {
		LocalDateTime nextExecution = task.getLocalDateTimeOfNextBackup();
		task.resetLocalDateTimeOfNextExecution();
		Controller.this.scheduleBackupTaskStartingAt(task, nextExecution.plusSeconds(1));
	}

	/**
	 * Bricht (ohne Nachfrage!) alle laufenden Backups ab.
	 */
	private void cancelAllRunningTasks() {
		for (String taskName : runningBackupTasks) {
			cancelBackup(getBackupTaskWithName(taskName), true);
		}
	}

	/**
	 * Beendet das Programm.
	 */
	private void quit() {
		int reply = JOptionPane.showConfirmDialog(null,
				ResourceBundle.getBundle("messages").getString("Messages.ReallyQuit"),
				ResourceBundle.getBundle("messages").getString("Messages.Quit"), JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			savePropertiesGson();
			cancelAllRunningTasks();
			if (guiController.isQTTray()) {
				guiController.sendToQtTrayOverSocket(null, true);
			}
			guiController.destroyTrayProcess();
			System.exit(0);
		}
	}
}
