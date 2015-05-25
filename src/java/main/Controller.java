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

import gui.Mainframe;
import listener.IBackupListener;
import listener.IMainframeListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import javax.swing.SwingUtilities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import data.BackupTask;

/**
 * Controller zur Steuerung der Anwendung.
 * 
 * @author Andreas Fleig
 *
 */
public class Controller {

	/**
	 * Aktuelle Mainframe-Instanz.
	 */
	private Mainframe mainframe;
	/**
	 * Liste aller erstellten Backup-Tasks.
	 */
	private ArrayList<BackupTask> backupTasks = new ArrayList<BackupTask>();
	/**
	 * Namen-Muster für die benennung der Backup-Ordner.
	 */
	private static final String BACKUP_FOLDER_NAME_PATTERN = "dd-MM-yyyy-HH-mm-ss";
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

	/**
	 * Gibt an um wie viele Sekunden das nachzuholenden Backup (von now an)
	 * verzögert werden soll.
	 */
	private static final int DELAY_FOR_MISSED_BACKUP = 2;

	/**
	 * Startet und initialisiert den Controller.
	 */
	public void startController() {
		try {
			java.awt.EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					mainframe = new Mainframe(new IMainframeListener() {

						@Override
						public Backupable startPreparation(BackupTask task) {
							return Controller.this.startPreparation(task);
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
							String outprint = ResourceBundle.getBundle("gui.messages").getString(
									"Messages.BackupCanceled");
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
							Controller.this.taskFinished(task);

						}

						@Override
						public void scheduleBackupTasks() {
							Controller.this.scheduleBackupTasks();
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
						public void removeBackupTaskFromRunningTasks(BackupTask task) {
							taskFinished(task);
						}
					});
				}
			});
			loadSerializationGson();
			// Liste aller versäumten BackupTasks:
			ArrayList<BackupTask> missedBackupTaks = new ArrayList<>();
			// Prüfen ob Backups versäumt wurden:
			for (BackupTask task : backupTasks) {
				if (task.getLocalDateTimeOfNextBackup() != null
						&& task.getLocalDateTimeOfNextBackup().isBefore(LocalDateTime.now())) {
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
				if ((task.getLocalDateTimeOfNextBackup().minusMinutes(task.getProfitableTimeUntilNextExecution()))
						.isAfter(LocalDateTime.now())) {
					scheduleBackupTaskNow(task);
				}
			}
		} catch (InterruptedException e) {
			System.err.println(e);
		} catch (InvocationTargetException ex) {
			System.err.println(ex);
		}
	}

	/**
	 * Lädt die serialisierten Einstellungen.
	 */
	private void loadSerialization() {
		// Prüfen ob bereits Einstellungen gespeichert wurden:
		File file = new File("./properties.ser");
		if (file.exists()) {
			ObjectInputStream ois = null;
			FileInputStream fis = null;

			File properties = new File("./properties.ser");
			try {
				fis = new FileInputStream(properties);
				ois = new ObjectInputStream(fis);

				backupTasks = (ArrayList<BackupTask>) ois.readObject();
			} catch (IOException e) {
				System.err.println(e);
			} catch (ClassNotFoundException e) {
				System.err.println(e);
			} finally {
				if (ois != null)
					try {
						ois.close();
					} catch (IOException e) {
						System.err.println(e);
					}
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
						System.err.println(e);
					}
			}
			for (int i = 0; i < backupTasks.size(); i++) {
				mainframe.addBackupTaskToList(backupTasks.get(i));
			}
		}
	}

	private void loadSerializationGson() {
		String settings = new String();
		File properties = new File("./properties");
		if (properties.exists()) {
			try {
				Scanner scanner = new Scanner(properties);
				settings = scanner.nextLine().toString();
				scanner.close();
			} catch (FileNotFoundException e) {
				System.err.println("Error: FileNotFoundException while loading Gson-Properties");
			}
			Gson gson = new Gson();
			Type listOfBackupTasks = new TypeToken<ArrayList<BackupTask>>() {
			}.getType();
			if (settings != null) {
				backupTasks = gson.fromJson(settings, listOfBackupTasks);
			}
			for (int i = 0; i < backupTasks.size(); i++) {
				mainframe.addBackupTaskToList(backupTasks.get(i));
			}
		}
	}

	/**
	 * Startet alle Backup-Tasks.
	 */
	public void startAllBackups() {
		// TODO: unnötig?
		for (int i = 0; i < backupTasks.size(); i++) {
			// startBackup(backupTasks.get(i));
		}
	}

	/**
	 * Startet die Backup-Vorbereitung.
	 */
	public Backupable startPreparation(BackupTask task) {
		mainframe.setButtonsToBackupRunning(false);

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
						Controller.this.setStatus(status);
					}

				});
			}

			@Override
			public void log(String event, BackupTask task) {
				Controller.this.log(event, task);

			}

			@Override
			public boolean advancedOutputIsEnabled() {
				return Controller.this.advancedOutputIsEnabled();
			}

			@Override
			public void taskStarted(String taskName) {
				Controller.this.taskStarted(taskName);

			}

			@Override
			public void taskFinished(BackupTask task) {
				Controller.this.taskFinished(task);

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
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					// Namen des Ordners "zerlegen":
					StringTokenizer tokenizer = new StringTokenizer(files[i].getName(), "_");
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
				String output = ResourceBundle.getBundle("gui.messages").getString("Messages.startHardlinkBackup");
				printOut(output, false, task.getTaskName());
				log(output, task);
				backup = new HardlinkBackup(backupListener, task.getTaskName(), task.getSources(),
						task.getDestinationPath());
			} else {
				String output = ResourceBundle.getBundle("gui.messages").getString("Messages.startNormalBackup");
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
			String output = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
			printOut(output, false, task.getTaskName());
			log(output, task);
		}

		// TODO: In Mainframe (außerhalb des Threads)?
		mainframe.setButtonsToBackupRunning(true);
		// TODO: Probleme mit setPrepared bei abbruch?
		task.setPrepared(true);
		return backup;
	}

	/**
	 * Startet den Backup-Vorgang eines bestimmten Backup-Tasks.
	 * 
	 * @param task
	 *            Backup-Task welcher ausgeführt werden soll
	 */
	public void startBackup(BackupTask task, Backupable backup) {
		mainframe.setButtonsToBackupRunning(false);

		if (!task.isPrepered()) {
			mainframe.setButtonsToBackupRunning(true);
			return;
		}

		try {
			backup.runBackup(task);
		} catch (IOException e) {
			System.err.println("Fehler beim einlesen der Datei(en)");
		} catch (BackupCanceledException ex) {
			String output = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
			printOut(output, false, task.getTaskName());
			log(output, task);
		}
		// alte Backups aufräumen (wenn gewünscht):
		if (task.simpleAutoCleanIsEnabled()) {
			try {
				while (this.calcNumberOfBackups(task) > task.getNumberOfBackupsToKeep()) {
					File toDelete = new File(task.getDestinationPath()
							+ File.separator
							+ findOldestBackup(
									new ArrayList<File>(
											Arrays.asList((new File(task.getDestinationPath()).listFiles()))), task));

					String output = ResourceBundle.getBundle("gui.messages").getString("Messages.deleting") + " "
							+ toDelete.getAbsolutePath();
					setStatus(output);
					log(output, task);
					if (!BackupHelper.deleteDirectory(toDelete)) {
						System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
					}
					printOut(
							toDelete.getAbsolutePath() + " "
									+ ResourceBundle.getBundle("gui.messages").getString("Messages.deleted"), false,
							task.getTaskName());
				}
			} catch (BackupCanceledException e) {
				String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
				printOut(outprint, false, task.getTaskName());
				log(outprint, task);
			}
		} else if (task.extendedAutoCleanIsEnabled()) {
			runExtendedClean(task);
		}

		task = null;
		mainframe.setButtonsToBackupRunning(true);

	}

	/**
	 * Gibt eine Liste mit allen Namen aller Backup-Tasks zurück.
	 * 
	 * @return Liste der Namen aller Backup-Tasks
	 */
	public ArrayList<String> getBackupTaskNames() {
		ArrayList<String> backupTaskNames = new ArrayList<String>();
		for (int i = 0; i < backupTasks.size(); i++) {
			backupTaskNames.add(backupTasks.get(i).getTaskName());
		}
		return backupTaskNames;
	}

	/**
	 * Gibt den gegebenen String auf der GUI aus. error bestimmt ob es sich um
	 * eine Fehlermeldung (rot) handelt oder nicht.
	 * 
	 * @param s
	 *            auszugebender String
	 * @param error
	 *            legt fest ob es sich um eine Fehlermeldung handelt oder nicht
	 */
	private void printOut(String s, boolean error, String taskName) {
		mainframe.addToOutput(s, error, taskName);
	}

	/**
	 * Gibt den gegebenen String auf dem Status-Textfeld auf der GUI aus.
	 * 
	 * @param status
	 *            auszugebender String
	 */
	private void setStatus(String status) {
		mainframe.setStatus(status);
	}

	/**
	 * Schreibt den gegebenen String in das log-File des gegebenen Tasks.
	 * 
	 * @param event
	 *            zu loggender String
	 * @param task
	 *            zugehöriger Task
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
	 * Liefert den Backup-Task mit gegebenem Namen zurück. Exisitert kein Backup
	 * mit dem angegebenen Namen so wird null zurückgeliefert.
	 * 
	 * @param name
	 *            Name des "gesuchten" Backup-Tasks
	 * @return den gesuchten Backup-Task oder null
	 */
	public BackupTask getBackupTaskWithName(String name) {
		for (int i = 0; i < backupTasks.size(); i++) {
			if (backupTasks.get(i).getTaskName().equals(name)) {
				return backupTasks.get(i);
			}
		}
		return null;
	}

	/**
	 * Fügt einen Backup-Task hinzu.
	 * 
	 * @param task
	 *            hinzuzufügender Backup-Task
	 */
	public void addBackupTask(BackupTask task) {
		backupTasks.add(task);
		mainframe.addBackupTaskToList(task);
	}

	/**
	 * Löscht einen Backup-Task.
	 * 
	 * @param task
	 *            zu löschender Backup-Task.
	 */
	public void removeBackupTask(BackupTask task) {
		backupTasks.remove(task);
		mainframe.removeBackupTaskFromList(task);
	}

	/**
	 * Gibt zurück ob die erweiterte Ausgabe aktiviert ist.
	 * 
	 * @return Status der erweiterten Ausgabe
	 */
	public boolean advancedOutputIsEnabled() {
		return mainframe.advancedOutputIsEnabled();
	}

	/**
	 * Gibt die Anzahl der Backup-Sätze zum gegebenen Backup-Task zurück.
	 * 
	 * @param task
	 *            betreffender BackupTask
	 * @return Anzahl der Backup-Sätze zum gegebenen Backup-Task
	 */
	private int calcNumberOfBackups(BackupTask task) {
		File dest = new File(task.getDestinationPath());
		return findBackupSets(dest, task).size();
	}

	/**
	 * Gibt eine Liste aller Backupsätze im gegebenen Verzeichnis zurück.
	 * 
	 * @param dir
	 *            zu durchsuchendes Verzeichnis
	 * @param task
	 *            betreffender BackupTask
	 * @return Liste aller gefundenen Backupsätze
	 */
	private ArrayList<File> findBackupSets(File dir, BackupTask task) {
		File[] files = dir.listFiles();
		ArrayList<File> foundBackupsets = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			// Namen des Ordners "zerlegen":
			StringTokenizer tokenizer = new StringTokenizer(files[i].getName(), "_");
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
				SimpleDateFormat sdfToDate = new SimpleDateFormat(BACKUP_FOLDER_NAME_PATTERN);
				sdfToDate.parse(backupDate);
				foundBackupsets.add(files[i]);
			} catch (ParseException e) {
				// Offenbar kein gültiges Datum
				continue;
			}
		}
		return foundBackupsets;
	}

	/**
	 * Gibt den Pfad des ältesten Backup-Satzes zurück.
	 * 
	 * @param root
	 *            Ordner in dem der älteste Backupsatz gefunden werden soll
	 * @param task
	 *            betreffender BackupTask
	 * @return Pfad des ältesten Backup-Satzes
	 */
	private String findOldestBackup(ArrayList<File> directories, BackupTask task) {

		Date oldestDate = null;
		String oldestBackupPath = null;
		Date foundDate;
		for (int i = 0; i < directories.size(); i++) {
			if (directories.get(i).isDirectory()) {
				// Namen des Ordners "zerlegen":
				StringTokenizer tokenizer = new StringTokenizer(directories.get(i).getName(), "_");
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
					SimpleDateFormat sdfToDate = new SimpleDateFormat(BACKUP_FOLDER_NAME_PATTERN);
					foundDate = sdfToDate.parse(backupDate);
				} catch (ParseException e) {
					// Offenbar kein gültiges Datum
					continue;
				}
				if (oldestDate == null) {
					oldestDate = foundDate;
					oldestBackupPath = directories.get(i).getName();
				} else {
					if (oldestDate.compareTo(foundDate) > 0) {
						oldestDate = foundDate;
						oldestBackupPath = directories.get(i).getName();
					}
				}
			}
		}
		return oldestBackupPath;
	}

	/**
	 * Führt für den gegebenen BackupTask das erweiterte CleanUp durch.
	 * 
	 * @param task
	 *            BackupTask für den das CleanUp durchgeführt werden soll
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
			DateFormat formatter;
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
				SimpleDateFormat sdfOfCurrentBackupSet = new SimpleDateFormat(BACKUP_FOLDER_NAME_PATTERN);
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
		if (task.getBackupsToKeep().length > 0 && !task.getBackupsToKeep()[0].equals("all")) {
			while (!bucket1.isEmpty() && bucket1.size() > Integer.valueOf(task.getBackupsToKeep()[0])) {
				if (!BackupHelper.deleteDirectory(new File(task.getDestinationPath() + "/"
						+ findOldestBackup(bucket1, task)))) {
					System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
					break;
				}
			}
		}

		if (task.getBackupsToKeep().length > 1 && !task.getBackupsToKeep()[1].equals("all")) {
			while (!bucket2.isEmpty() && bucket2.size() > Integer.valueOf(task.getBackupsToKeep()[1])) {
				File oldestBackupSet = new File(task.getDestinationPath() + "/" + findOldestBackup(bucket2, task));
				if (!BackupHelper.deleteDirectory(oldestBackupSet)) {
					System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
					break;
				}
				bucket2.remove(oldestBackupSet);
			}
		}

		if (task.getBackupsToKeep().length > 2 && !task.getBackupsToKeep()[2].equals("all")) {
			while (!bucket3.isEmpty() && bucket3.size() > Integer.valueOf(task.getBackupsToKeep()[2])) {
				if (!BackupHelper.deleteDirectory(new File(task.getDestinationPath() + "/"
						+ findOldestBackup(bucket3, task)))) {
					System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
					break;
				}
			}
		}

		if (task.getBackupsToKeep().length > 3 && !task.getBackupsToKeep()[3].equals("all")) {
			while (!bucket4.isEmpty() && bucket4.size() > Integer.valueOf(task.getBackupsToKeep()[3])) {
				if (!BackupHelper.deleteDirectory(new File(task.getDestinationPath() + "/"
						+ findOldestBackup(bucket4, task)))) {
					System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
					break;
				}
			}
		}

		if (task.getBackupsToKeep().length > 4 && !task.getBackupsToKeep()[4].equals("all")) {
			while (!bucket4.isEmpty() && bucket5.size() > Integer.valueOf(task.getBackupsToKeep()[4])) {
				if (!BackupHelper.deleteDirectory(new File(task.getDestinationPath() + "/"
						+ findOldestBackup(bucket5, task)))) {
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
	 * @param taskName
	 *            Name des hinzuzufügenden Backup-Tasks
	 */
	private void taskStarted(String taskName) {
		runningBackupTasks.add(taskName);
		System.out.println("Task started:" + taskName);
	}

	/**
	 * Entfernt den gegebenen Task aus der Liste der laufenden Backup-Tasks.
	 * 
	 * @param task
	 *            Der zu entfernenden Backup-Task
	 */
	private void taskFinished(BackupTask task) {
		if (!runningBackupTasks.remove(task.getTaskName())) {
			System.err.println("Error: This task isn't running");
		}
		System.out.println("Task finished:" + task.getTaskName());
		scheduleBackupTask(task);
	}

	/**
	 * Reschedulet den gegebenen Task.
	 * 
	 * @param task
	 *            Task der gereschedulet werden soll
	 */
	public void scheduleBackupTask(final BackupTask task) {
		// Kontrollieren ob dieser Task bereits läuft:
		if (runningBackupTasks.contains(task.getTaskName())) {
			return;
		}
		LocalDateTime nextExecutionTime = null;
		int autoBackupMode = task.getAutoBackupMode();
		if (autoBackupMode == 0) {
			return;
		} else if (autoBackupMode == 1) {
			nextExecutionTime = calcTimeFromWeekdays(task.getBackupWeekdays(), task.getStartTime());
		} else if (autoBackupMode == 2) {
			nextExecutionTime = calcTimeFromDaysInMonth(task.getBackupDaysInMonth(), task.getStartTime());
		} else if (autoBackupMode == 3) {
			nextExecutionTime = calcTimeFromInterval(task.getIntervalTime(), task.getIntervalUnit());
		}
		// TODO: Debugging-Ausgabe raus:
		System.out.println("Nächste Ausführung von " + task.getTaskName() + ": " + nextExecutionTime.toString());

		scheduleBackup(task, nextExecutionTime);
	}

	/**
	 * Schedulet den gegebenen Task auf jetzt (+ Verzögerung).
	 * 
	 * @param task
	 *            zu schedulender Task
	 */
	public void scheduleBackupTaskNow(final BackupTask task) {
		// Kontrollieren ob dieser Task bereits läuft:
		if (runningBackupTasks.contains(task.getTaskName())) {
			return;
		}
		printOut(ResourceBundle.getBundle("gui.messages").getString("Messages.scheduleBackupNow"), false,
				task.getTaskName());

		scheduleBackup(task, LocalDateTime.now().plusSeconds(DELAY_FOR_MISSED_BACKUP));
	}

	/**
	 * Schedulet den gegebenen Task auf die gegebene Zeit.
	 * 
	 * @param task
	 *            zu schedulenden Task
	 * @param nextExecutionTime
	 *            Zeit auf die der Task geschedulet wird
	 */
	private void scheduleBackup(final BackupTask task, LocalDateTime nextExecutionTime) {
		// Autostart für diesen Task aktivieren:
		task.setAutostart(true);
		// scheduling:
		Runnable backup = new Runnable() {
			public void run() {
				taskStarted(task.getTaskName());
				mainframe.prepareBackup(task);
			}
		};
		task.setScheduledFuture(timer.schedule(backup, LocalDateTime.now().until(nextExecutionTime, ChronoUnit.MILLIS),
				TimeUnit.MILLISECONDS));
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
		}
		// Einstellungen aus dem Backup-Task holen und im LocalTime Objekte
		// "umwandeln" (für jeden Task)
		for (final BackupTask task : backupTasks) {
			scheduleBackupTask(task);
		}
	}

	/**
	 * Berechnet (als LocalDateTime) den nächsten Ausführungszeitpunkt aus den
	 * gegebenen weekdays.
	 * 
	 * @param weekdays
	 *            Wochentage an denen gesichert werden soll
	 * @param time
	 *            Zeit zu der gesichert werden soll
	 * @return nächster Ausführungszeitpunkt oder null im Fehlerfall
	 */
	private LocalDateTime calcTimeFromWeekdays(boolean[] weekdays, LocalTime time) {
		DayOfWeek currentWeekday = LocalDate.now().getDayOfWeek();
		int weekdayNumber = currentWeekday.getValue() - 1;
		// Heute?:
		if (weekdays[weekdayNumber]) {
			// Heute istBackup-Tag, also muss die Zeit betrachet werden:
			if (LocalTime.now().isBefore(time)) {
				LocalDateTime result = time.atDate(LocalDate.now());
				return result;
			}
		}

		int wNumber = weekdayNumber + 1;
		int daysFromTodayCounter = 1;
		while (wNumber < 7) {
			if (weekdays[wNumber]) {
				LocalDate date = LocalDate.now().plusDays(daysFromTodayCounter);
				LocalDateTime result = time.atDate(date);
				return result;
			}
			wNumber++;
			daysFromTodayCounter++;
		}
		wNumber = 0;
		while (wNumber < 7) {
			if (weekdays[wNumber]) {
				if (daysFromTodayCounter > 7) {
					// TODO: raus
					System.err.println("Error: Weekday more than 7 days in future is not possible");
				}
				LocalDate date = LocalDate.now().plusDays(daysFromTodayCounter);
				LocalDateTime result = time.atDate(date);
				return result;
			}
			wNumber++;
			daysFromTodayCounter++;
		}
		return null;
	}

	/**
	 * Löscht die geplante ausführung des gegebenen Tasks.
	 * 
	 * @param task
	 *            entsprechender Task
	 */
	public void removeBackupTaskScheduling(BackupTask task) {
		if (task.getScheduledFuture() != null) {
			task.getScheduledFuture().cancel(false);
			task.resetLocalDateTimeOfNextExecution();
		}
	}

	/**
	 * Berechnet (als LocalDateTime) den nächsten Ausführungszeitpunkt aus den
	 * gegebenen daysInMonth.
	 * 
	 * @param daysInMonth
	 *            Tage im Monat an denen gesichert werden soll
	 * @param time
	 *            Zeit zu der gesichert werden soll
	 * @return nächster Ausführungszeitpunkt oder null im Fehlerfall
	 */
	private LocalDateTime calcTimeFromDaysInMonth(boolean[] daysInMonth, LocalTime time) {
		int currentDayInMonth = LocalDate.now().getDayOfMonth() - 1;
		// Heute?:
		if (daysInMonth[currentDayInMonth]) {
			// Heute istBackup-Tag, also muss die Zeit betrachet werden:
			if (LocalTime.now().isBefore(time)) {
				LocalDateTime result = time.atDate(LocalDate.now());
				return result;
			}
		}

		int mNumber = currentDayInMonth + 1;
		int daysFromTodayCounter = 1;
		while (mNumber <= (LocalDate.now().lengthOfMonth() - 1)) {
			if (daysInMonth[mNumber]) {
				LocalDate date = LocalDate.now().withDayOfMonth(daysFromTodayCounter);
				LocalDateTime result = time.atDate(date);
				return result;
			}
			mNumber++;
			daysFromTodayCounter++;
		}
		mNumber = 0;
		daysFromTodayCounter = 0;
		while (mNumber <= (LocalDate.now().lengthOfMonth() - 1)) {
			if (daysInMonth[mNumber]) {
				LocalDate date = LocalDate.now().withDayOfMonth(daysFromTodayCounter);
				date = date.plusMonths(1);
				LocalDateTime result = time.atDate(date);
				return result;
			}
			mNumber++;
			daysFromTodayCounter++;
		}
		return null;
	}

	/**
	 * Berechnet (als LocalDateTime) den nächsten Ausführungszeitpunkt aus dem
	 * gegebenen Intervall.
	 * 
	 * @param interval
	 *            gegebenes Intervall
	 * @param intervalUnit
	 *            Einheit des Intervals (min, h, d, m)
	 * @return nächster Ausführungszeitpunkt oder null im Fehlerfall
	 */
	private LocalDateTime calcTimeFromInterval(int interval, String intervalUnit) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		switch (intervalUnit) {
		case "min":
			return currentDateTime.plusMinutes(interval);
		case "h":
			return currentDateTime.plusHours(interval);
		case "d":
			return currentDateTime.plusDays(interval);
		case "m":
			return currentDateTime.plusMonths(interval);
		}
		return null;
	}

	/**
	 * Löscht einen leeren Backup-Ordner.
	 * 
	 * @param path
	 *            Pfad an dem sich der Ordner befindet
	 * @param task
	 *            BackupTask zu dem der Ordner gehört
	 */
	private void deleteEmptyBackupFolders(String path, BackupTask task) {
		File currentDest = new File(path);
		File[] backupFolders = currentDest.listFiles();
		for (int i = 0; i < backupFolders.length; i++) {
			if (!backupFolders[i].isDirectory()) {
				continue;
			}
			boolean deleteThisDir = true;
			File[] filesInBackupFolder = backupFolders[i].listFiles();
			for (int j = 0; j < filesInBackupFolder.length; j++) {
				if (filesInBackupFolder[j].isDirectory() && filesInBackupFolder[j].listFiles().length != 0) {
					deleteThisDir = false;
					break;
				}
			}
			if (deleteThisDir) {
				BackupHelper.deleteDirectory(backupFolders[i]);
			}
		}
		String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.deletedBackupFolder");
		backupListener.printOut(outprint, false, task.getTaskName());
		backupListener.log(outprint, task);
	}
}
