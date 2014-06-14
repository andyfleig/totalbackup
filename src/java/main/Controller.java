package main;

import gui.Mainframe;
import main.BackupTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Controller {

	private Mainframe mainframe;
	private int numberOfBackupTasks = 0;
	private ArrayList<BackupTask> backupTasks = new ArrayList<BackupTask>();

	/**
	 * Startet und initialisiert den Controller.
	 */
	public void startController() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				mainframe = new Mainframe(Controller.this);
				mainframe.frmTotalbackup.setVisible(true);
			}
		});
	}

	/**
	 * Startet ein "normales" Backup.
	 * @param source Quellpfad
	 * @param destination Zielpfad
	 */
	public void startAllBackups() {
		
		for (int i = 0; i < numberOfBackupTasks; i++) {
			startBackup(backupTasks.get(i));
		}
		
		
		
		
		
		/*
		Backup backup = new Backup(this, source, destination);
		try {
			backup.runBackup();
		} catch (FileNotFoundException ex) {
			System.out.println("Datei existiert nicht!");
		} catch (IOException ex) {
			System.out.println("IO-Fehler!");
		}
		*/
	}
	
	public void startBackup(BackupTask task) {
		Backup backup = new Backup(this, task.getSourcePaths(), task.getDestinationPath());
		try {
			backup.runBackup(task.getTaskName());
		} catch (IOException e) {
			System.out.println("Fehler beim einlesen der Datei(en)");
		}
		
	}
	
	public ArrayList<String> getBackupTaskNames() {
		ArrayList<String> backupTaskNames = new ArrayList<String>();
		for (int i = 0; i < numberOfBackupTasks; i++) {
			backupTaskNames.add(backupTasks.get(i).getTaskName());
		}
		return backupTaskNames;
	}

	/**
	 * Gibt den übergebenen String auf dem Output-Panel aus.
	 * @param s auszugebender String
	 */
	public void printOut(String s) {
		mainframe.addToOutput(s);
	}
	
	/**
	 * Gibt den Zielpfad als String zurück.
	 * @return Zielpfad
	 */
	public String getDestinationPath() {
		return mainframe.getDestPath();
	}
	
	/**
	 * Setzt den Zielpfad auf den übergebenen String.
	 * @param path zu setzender Zielpfad
	 */
	public void setDestinationPath(String path) {
		mainframe.setDestPath(path);
	}
	
	public void setNumberOfBackupTasks(int number) {
		this.numberOfBackupTasks = number;
	}
	
	public int getNumberOfBackupTasks() {
		return numberOfBackupTasks;
	}
	
	public BackupTask getBackupTaskWithName(String name) {
		for (int i = 0; i < numberOfBackupTasks; i++) {
			if (backupTasks.get(i).getTaskName().equals(name)) {
				return backupTasks.get(i);
			}
		}
		//TODO: schöner!?
		return null;
	}
	
	public void addBackupTask(BackupTask task) {
		backupTasks.add(task);
		numberOfBackupTasks = numberOfBackupTasks + 1;
		mainframe.addBackupTaskToList(task);
	}
	
	public void removeBackupTask(BackupTask task) {
		backupTasks.remove(task);
		numberOfBackupTasks = numberOfBackupTasks - 1;
		mainframe.removeBackupTaskFromList(task);
	}
}
