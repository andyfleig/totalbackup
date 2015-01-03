package main;

import gui.Mainframe;
import gui.IMainframeListener;
import main.BackupTask;
import main.IBackupListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.SwingUtilities;

/**
 * Controller zur Steuerung der Anwendung.
 * 
 * @author andy
 *
 */
public class Controller {

	private Mainframe mainframe;
	private ArrayList<BackupTask> backupTasks = new ArrayList<BackupTask>();
	private BackupTask currentTask = null;
	private static final String BACKUP_FOLDER_NAME_PATTERN = "dd-MM-yyyy-HH-mm-ss";
	private Backupable backup;
	private IBackupListener backupListener;
	private BackupInfos backupInfos;

	/**
	 * Startet und initialisiert den Controller.
	 */
	public void startController() {
		backupInfos = new BackupInfos();
		try {
			java.awt.EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					mainframe = new Mainframe(new IMainframeListener() {

						@Override
						public void startPreparation(BackupTask task) {
							Controller.this.startPreparation(task);
						}

						@Override
						public void startBackupTask(BackupTask task) {
							Controller.this.startBackup(task);

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
						public long getNumberOfDirectories() {
							return backupInfos.getNumberOfDirectories();
						}

						@Override
						public long getNumberOfFiles() {
							return backupInfos.getNumberOfFiles();
						}

						@Override
						public double getSizeToCopy() {
							return backupInfos.getSizeToCopy();
						}

						@Override
						public double getSizeToLink() {
							return backupInfos.getSizeToLink();
						}

						@Override
						public void clearBackupInfos() {
							backupInfos.clear();
						}

						@Override
						public void deleteEmptyBackupFolders(String path) {
							File currentDest = new File(path);
							File[] backupFolders = currentDest.listFiles();
							for (int i = 0; i < backupFolders.length; i++) {
								if (!backupFolders[i].isDirectory()) {
									continue;
								}
								boolean deleteThisDir = true;
								File[] filesInBackupFolder = backupFolders[i].listFiles();
								for (int j = 0; j < filesInBackupFolder.length; j++) {
									if (filesInBackupFolder[j].listFiles().length != 0) {
										deleteThisDir = false;
										break;
									}
								}
								if (deleteThisDir) {
									BackupHelper.deleteDirectory(backupFolders[i]);
								}
							}
							String outprint = ResourceBundle.getBundle("gui.messages").getString(
									"Messages.deletedBackupFolder");
							backupListener.printOut(outprint, false);
							backupListener.log(outprint, backupListener.getCurrentTask());
						}

						@Override
						public void outprintBackupCanceled() {
							String outprint = ResourceBundle.getBundle("gui.messages").getString(
									"Messages.BackupCanceled");
							backupListener.printOut(outprint, false);
							backupListener.log(outprint, backupListener.getCurrentTask());
						}

						@Override
						public void printOut(String s, boolean error) {
							Controller.this.printOut(s, error);
						}

						@Override
						public void log(String event, BackupTask task) {
							Controller.this.log(event, task);
						}
					});
					mainframe.frmTotalbackup.setVisible(true);
				}
			});
			loadSerialization();
		} catch (Exception ex) {
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

	/**
	 * Startet alle Backup-Tasks.
	 */
	public void startAllBackups() {
		for (int i = 0; i < backupTasks.size(); i++) {
			startBackup(backupTasks.get(i));
		}
	}

	/**
	 * Startet die Backup-Vorbereitung.
	 */
	public void startPreparation(BackupTask task) {
		mainframe.setButtonsToBackupRunning(false);
		currentTask = task;

		// Listener anlegen:
		backupListener = new IBackupListener() {

			@Override
			public void printOut(final String s, final boolean error) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						Controller.this.printOut(s, error);
					}

				});
			}

			@Override
			public BackupTask getCurrentTask() {
				return Controller.this.getCurrentTask();
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
			public void increaseNumberOfDirectories() {
				backupInfos.increaseNumberOfDirectories();

			}

			@Override
			public void increaseNumberOfFiles() {
				backupInfos.increaseNumberOfFiles();

			}

			@Override
			public void increaseSizeToCopyBy(double sizeToIncreaseBy) {
				backupInfos.increaseSizeToCopyBy(sizeToIncreaseBy);

			}

			@Override
			public void increaseSizeToLinkBy(double sizeToIncreaseBy) {
				backupInfos.increaseSizeToLinkBy(sizeToIncreaseBy);

			}

		};

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
				printOut(output, false);
				log(output, currentTask);
				backup = new HardlinkBackup(backupListener, task.getTaskName(), task.getSources(),
						task.getDestinationPath());
			} else {
				String output = ResourceBundle.getBundle("gui.messages").getString("Messages.startNormalBackup");
				printOut(output, false);
				log(output, currentTask);
				backup = new NormalBackup(backupListener, task.getTaskName(), task.getSources(),
						task.getDestinationPath());
			}
		} else {
			backup = new NormalBackup(backupListener, task.getTaskName(), task.getSources(), task.getDestinationPath());
		}

		try {
			backup.runPreparation();
		} catch (BackupCanceledException ex) {
			String output = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
			printOut(output, false);
			log(output, currentTask);
		}

		currentTask = null;
		mainframe.setButtonsToBackupRunning(true);

		task.setPrepared(true);
	}

	/**
	 * Startet den Backup-Vorgang eines bestimmten Backup-Tasks.
	 * 
	 * @param task
	 *            Backup-Task welcher ausgeführt werden soll
	 */
	public void startBackup(BackupTask task) {
		mainframe.setButtonsToBackupRunning(false);
		currentTask = task;

		if (!task.isPrepered()) {
			currentTask = null;
			mainframe.setButtonsToBackupRunning(true);
			return;
		}

		try {
			backup.runBackup(task.getTaskName());
		} catch (IOException e) {
			System.err.println("Fehler beim einlesen der Datei(en)");
		} catch (BackupCanceledException ex) {
			String output = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
			printOut(output, false);
			log(output, currentTask);
		}
		// alte Backups aufräumen (wenn gewünscht):
		if (currentTask.autoCleanIsEnabled()) {
			try {
				while (this.calcNumberOfBackups() > currentTask.getNumberOfBackupsToKeep()) {

					File toDelete = new File(currentTask.getDestinationPath() + File.separator + findOldestBackup());

					String output = ResourceBundle.getBundle("gui.messages").getString("Messages.deleting") + " "
							+ toDelete.getAbsolutePath();
					setStatus(output);
					log(output, currentTask);
					if (!BackupHelper.deleteDirectory(toDelete)) {
						System.err.println("FEHLER: Ordner konnte nicht gelöscht werden");
					}
					printOut(
							toDelete.getAbsolutePath() + " "
									+ ResourceBundle.getBundle("gui.messages").getString("Messages.deleted"), false);
				}
			} catch (BackupCanceledException e) {
				String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
				printOut(outprint, false);
				log(outprint, currentTask);
			}
		}
		currentTask = null;
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
	private void printOut(String s, boolean error) {
		mainframe.addToOutput(s, error);
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
			// TODO: Endlos-Schleife möglich?
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
		// TODO: schöner!?
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
	 * Gibt den aktuell laufenden Task zurück.
	 * 
	 * @return aktuell laufender Task
	 */
	public BackupTask getCurrentTask() {
		return currentTask;
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
	 * Gibt die Anzahl der Backup-Sätze zum aktuellen Backup-Task zurück.
	 * 
	 * @return Anzahl der Backup-Sätze zum aktuellen Backup-Task
	 */
	private int calcNumberOfBackups() {
		File dest = new File(currentTask.getDestinationPath());
		File[] files = dest.listFiles();

		// Gültige Backup-Sätze suchen:
		int backupCounter = 0;
		for (int i = 0; i < files.length; i++) {
			// Namen des Ordners "zerlegen":
			StringTokenizer tokenizer = new StringTokenizer(files[i].getName(), "_");
			// Es wird geprüft ob der Name aus genau 2 Tokens besteht:
			if (tokenizer.countTokens() != 2) {
				continue;
			}
			// Erster Token muss dem TaskName entsprechen:
			if (!tokenizer.nextToken().equals(currentTask.getTaskName())) {
				continue;
			}
			// Zweiter Token muss analysiert werden:
			String backupDate = tokenizer.nextToken();

			try {
				SimpleDateFormat sdfToDate = new SimpleDateFormat(BACKUP_FOLDER_NAME_PATTERN);
				sdfToDate.parse(backupDate);
				backupCounter++;
			} catch (ParseException e) {
				// Offenbar kein gültiges Datum
				continue;
			}
		}
		return backupCounter;
	}

	/**
	 * Gibt den Pfad des ältesten Backup-Satzes zurück.
	 * 
	 * @return Pfad des ältesten Backup-Satzes
	 */
	private String findOldestBackup() {
		File root = new File(currentTask.getDestinationPath());
		File[] directories = root.listFiles();

		Date oldestDate = null;
		String oldestBackupPath = null;
		Date foundDate;
		for (int i = 0; i < directories.length; i++) {
			if (directories[i].isDirectory()) {
				// Namen des Ordners "zerlegen":
				StringTokenizer tokenizer = new StringTokenizer(directories[i].getName(), "_");
				// Es wird geprüft ob der Name aus genau 2 Tokens besteht:
				if (tokenizer.countTokens() != 2) {
					continue;
				}
				// Erster Token muss dem TaskName entsprechen:
				if (!tokenizer.nextToken().equals(currentTask.getTaskName())) {
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
					oldestBackupPath = directories[i].getName();
				} else {
					if (oldestDate.compareTo(foundDate) > 0) {
						oldestDate = foundDate;
						oldestBackupPath = directories[i].getName();
					}
				}
			}
		}
		return oldestBackupPath;
	}
}
