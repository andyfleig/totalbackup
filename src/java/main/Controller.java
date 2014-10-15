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

	/**
	 * Startet und initialisiert den Controller.
	 */
	public void startController() {
		try {
			java.awt.EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					mainframe = new Mainframe(new IMainframeListener() {

						@Override
						public void startAllBackups() {
							Controller.this.startAllBackups();
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
	 * Läd die serialisierten Einstellungen.
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
	 * Startet den Backup-Vorgang eines bestimmten Backup-Tasks.
	 * 
	 * @param task
	 *            Backup-Task welcher ausgeführt werden soll
	 */
	public void startBackup(BackupTask task) {
		mainframe.setButtonsToBackupRunning(false);
		currentTask = task;
		Backupable backup;
		// Listener anlegen:
		IBackupListener backupListener = new IBackupListener() {

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

		};

		// Backup-Object in abhängigkeit des Backup-Modus erstellen:
		if (task.getBackupMode() == 1) {
			// Prüfen ob bereits ein "normales" Backup erstellt wurde oder ob es
			// sich um die erste Ausführung handelt:
			File[] files = new File(task.getDestinationPath()).listFiles();
			boolean backupSetFound = false;
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory() && files[i].getName().contains(task.getTaskName())) {
					backupSetFound = true;
					break;
				}
			}
			if (backupSetFound) {
				String output = ResourceBundle.getBundle("gui.messages").getString("Messages.startHardlinkBackup");
				printOut(output, false);
				log(output, currentTask);
				backup = new HardlinkBackup(backupListener, task.getTaskName(), task.getSourcePaths(),
						task.getDestinationPath());
			} else {
				String output = ResourceBundle.getBundle("gui.messages").getString("Messages.startNormalBackup");
				printOut(output, false);
				log(output, currentTask);
				backup = new NormalBackup(backupListener, task.getTaskName(), task.getSourcePaths(),
						task.getDestinationPath());
			}
		} else {
			backup = new NormalBackup(backupListener, task.getTaskName(), task.getSourcePaths(),
					task.getDestinationPath());
		}
		try {
			backup.runBackup(task.getTaskName());
		} catch (IOException e) {
			System.err.println("Fehler beim einlesen der Datei(en)");
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
	 * //TODO: JavaDoc
	 * 
	 * @param s
	 * @param error
	 */
	public void printOut(String s, boolean error) {
		mainframe.addToOutput(s, error);
	}

	/**
	 * //TODO: JavaDoc
	 * 
	 * @param status
	 */
	private void setStatus(String status) {
		mainframe.setStatus(status);
	}

	/**
	 * //TODO: JavaDoc
	 * 
	 * @param event
	 * @param task
	 */
	private void log(String event, BackupTask task) {
		// Log-Datei anlegen:
		if (task == null) {
			// TODO: Endlos-Schleife:
			// printOut(currentTask,
			// ResourceBundle.getBundle("gui.messages").getString("Messages.ErrorLoggingDisabled"),
			// 0, true);
			return;
		}
		File log = new File(task.getDestinationPath() + System.getProperty("file.separator") + task.getTaskName()
				+ ".log");
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
}
