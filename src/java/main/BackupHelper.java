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

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.nio.file.Files;
import java.nio.file.Paths;

import data.BackupTask;
import listener.IBackupListener;

/**
 * Statische Helper Klasse welche statische Methoden für Backups anbieten (z.B. Datei/ Verzeichnis kopieren).
 *
 * @author Andreas Fleig
 */
public final class BackupHelper {

	/**
	 * Ort des Programm-Icons.
	 */
	public static final String ICON_LOCATION = "./resources/TB_logo.png";

	/**
	 * Datum-Vorlage.
	 */
	public static final String BACKUP_FOLDER_NAME_PATTERN = "dd-MM-yyyy-HH-mm-ss";

	/**
	 * Kopiert eine Datei vom angegebenen Quellpfad zum angegebenen Zielpfad.
	 *
	 * @param source      Quellpfad
	 * @param destination Zielpfad
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination, IBackupListener listener, BackupTask currentTask)
			throws FileNotFoundException, IOException {

		if (!source.isFile()) {
			return;
		}

		String output = ResourceBundle.getBundle("messages").getString("Messages.copying") + " " + source.getPath();
		listener.setStatus(output);
		if (listener.advancedOutputIsEnabled()) {
			listener.printOut(output, false, currentTask.getTaskName());
		}
		listener.log(output, currentTask);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destination, true));
		int bytes = 0;
		while ((bytes = in.read()) != -1) {
			out.write(bytes);
		}

		in.close();
		out.close();
		listener.setStatus("");
	}

	/**
	 * Erstellt einen Hardlink (dest) der auf source zeigt
	 *
	 * @param source      Quell-Datei des Hardlinks
	 * @param destination Ziel-Datei des Hardlinks
	 * @param task        entsprechender BackupTask
	 */
	public static void hardlinkFile(File source, File destination, IBackupListener listener, BackupTask task) {
		if (!source.isFile()) {
			return;
		}

		String output = ResourceBundle.getBundle("messages").getString("Messages.linking") + " " + source.getPath();
		if (listener.advancedOutputIsEnabled()) {
			listener.printOut(output, false, task.getTaskName());
		}
		listener.setStatus(output);
		listener.log(output, task);

		try {
			Files.createLink(Paths.get(destination.getAbsolutePath()), Paths.get(source.getAbsolutePath()));
		} catch (IOException e) {
			System.out.println("Fehler: IO-Problem");
		}

		listener.setStatus("");
	}

	/**
	 * Erstellt den Root-Ordner für ein Backup.
	 *
	 * @param destinationPath Zielpfad des Backups (Ort an dem der Root-Ordner angelegt werden soll)
	 * @param taskName        Name des Backup-Tasks
	 * @param task            entsprechender BackupTask
	 * @return angelegter Root-Ordner
	 */
	public static File createBackupFolder(String destinationPath, String taskName, IBackupListener listener,
			BackupTask task) {

		// Ordnername mit Datum festlegen:
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat(BACKUP_FOLDER_NAME_PATTERN);
		df.setTimeZone(TimeZone.getDefault());

		File destinationFile = new File(destinationPath);
		if (!destinationFile.exists()) {
			String output = ResourceBundle.getBundle("messages").getString("Messages.BackupFolderCreationError");
			listener.printOut(output, true, task.getTaskName());
			listener.log(output, task);

			return null;
		}
		String backupDir = destinationFile.getAbsolutePath() + File.separator + taskName + "_" + df.format(date);

		File dir = new File(backupDir);
		// Backup-Ordner anlegen:
		if (dir.mkdir()) {
			String output = ResourceBundle.getBundle("messages").getString("Messages.BackupFolderCreated");
			listener.printOut(output, false, task.getTaskName());
			listener.log(output, task);

		} else {
			String output = ResourceBundle.getBundle("messages").getString("Messages.BackupFolderCreationError");
			listener.printOut(output, true, task.getTaskName());
			listener.log(output, task);

			return null;
		}
		return dir;
	}

	/**
	 * Löscht ein Verzeichnis und dessen Inhalt rekursiv.
	 *
	 * @param path Pfad des zu löschenden Ordners
	 * @return true falls erfolgreich, false sonst
	 */
	public static boolean deleteDirectory(File path) {
		if (Thread.interrupted()) {
			throw new BackupCanceledException();
		}
		if (path.exists()) {
			File[] files = path.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					//TODO: if raus?
					if (!file.delete()) {
					}
				}
			}
		}
		return (path.delete());
	}

	/**
	 * Berechnet den MD5 Hashwert der gegebenen Datei.
	 *
	 * @param f Datei von der der Hadhwert berechnet werden soll
	 * @return MD5-Hashwert der gegebenen Datei
	 */
	public static String calcMD5(File f) {
		MessageDigest md;

		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Error: NoSuchAlgorithmException in calcMD5");
			return null;
		}

		FileInputStream fis;

		try {
			fis = new FileInputStream(f.getAbsoluteFile());
		} catch (FileNotFoundException e) {
			return null;
		}

		byte[] dataBytes = new byte[1024];

		int nread = 0;
		try {
			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			fis.close();
		} catch (IOException e) {
			System.err.println("Error: IOException in calcMD5");
			return null;
		}
		byte[] mdbytes = md.digest();

		StringBuffer sb = new StringBuffer();
		for (byte mdbyte : mdbytes) {
			sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	/**
	 * Gibt den Zeitpunkt des aktuellsten Backups zurück.
	 *
	 * @param task Task für den das aktuellste Backup gesucht werden soll
	 * @return Zeitpunkt des aktuellsten Backups (als LocalDateTime)
	 */
	public static LocalDateTime getLocalDateTimeOfNewestBackupSet(BackupTask task) {
		String rootPath = task.getDestinationPath();
		File root = new File(rootPath);
		File[] directories = root.listFiles();
		if (directories == null) {
			return null;
		}

		LocalDateTime newestDate = null;
		LocalDateTime foundDate;
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
					SimpleDateFormat sdfToDate = new SimpleDateFormat(BACKUP_FOLDER_NAME_PATTERN);
					foundDate = LocalDateTime.ofInstant(sdfToDate.parse(backupDate).toInstant(),
							ZoneId.systemDefault());
				} catch (ParseException e) {
					// Offenbar kein gültiges Datum
					continue;
				}
				if (newestDate == null) {
					newestDate = foundDate;
				} else {
					if (newestDate.compareTo(foundDate) < 0) {
						newestDate = foundDate;
					}
				}
			}
		}
		return newestDate;
	}
}
