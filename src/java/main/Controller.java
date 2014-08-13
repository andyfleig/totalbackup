package main;

import gui.Mainframe;
import main.BackupTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Controller zur Steuerung der Anwendung.
 * 
 * @author andy
 *
 */
public class Controller {

	private Mainframe mainframe;
	private ArrayList<BackupTask> backupTasks = new ArrayList<BackupTask>();

	/**
	 * Startet und initialisiert den Controller.
	 */
	public void startController() {
		try {
			java.awt.EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					mainframe = new Mainframe(Controller.this);
					mainframe.frmTotalbackup.setVisible(true);
				}
			});
			loadSerialization();
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

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
	 * Startet ein "normales" Backup.
	 * 
	 * @param source
	 *            Quellpfad
	 * @param destination
	 *            Zielpfad
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
		NormalBackup backup = new NormalBackup(this, task.getSourcePaths(), task.getDestinationPath());
		try {
			backup.runBackup(task.getTaskName());
		} catch (IOException e) {
			System.out.println("Fehler beim einlesen der Datei(en)");
		}

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
	 * Gibt den übergebenen String auf dem Output-Panel aus.
	 * 
	 * @param s
	 *            auszugebender String
	 */
	public void printOut(String s) {
		mainframe.addToOutput(s);
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

	public ArrayList<BackupTask> getBackupTasks() {
		return backupTasks;
	}
}
