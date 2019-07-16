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
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.nio.file.Files;
import java.nio.file.Paths;

import data.BackupTask;
import listener.IBackupListener;

/**
 * Static helper containing various general methods for backups.
 *
 * @author Andreas Fleig
 */
public final class BackupHelper {

	/**
	 * Logo of TotalBackup.
	 */
	public static final String TB_LOGO = "TB_logo.png";

	/**
	 * templates for date and time following ISO 8601 *_NAMING is for the naming of files or directories (does not
	 * include colons) *_SHOW is for printing anywhere within the gui or in log files
	 */
	public static final String DATE_TIME_PATTERN_NAMING = "yyyy-MM-dd'T'HHmmss";
	public static final String DATE_TIME_PATTERN_SHOW = "yyyy-MM-dd'T'HH:mm:ss";

	/**
	 * Copies the given source file to the given destination.
	 *
	 * @param source      source-file
	 * @param destination destination-file
	 * @throws FileNotFoundException if the given file does not exist
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination, IBackupListener listener, BackupTask currentTask)
			throws FileNotFoundException, IOException {

		if (!source.isFile()) {
			return;
		}

		String msg = "copying" + " " + source.getPath();
		listener.setStatus(msg, false, currentTask.getTaskName());
		listener.log(msg, currentTask);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destination, true));
		int bytes = 0;
		while ((bytes = in.read()) != -1) {
			out.write(bytes);
		}

		in.close();
		out.close();
		listener.setStatus("", false, currentTask.getTaskName());
	}

	/**
	 * Creates a hardlink of the given source file at the given destination.
	 *
	 * @param source      source file
	 * @param destination destination file
	 * @param task        corresponding BackupTask
	 */
	public static void hardlinkFile(File source, File destination, IBackupListener listener, BackupTask task) {
		if (!source.isFile()) {
			return;
		}

		String output = "linking" + " " + source.getPath();
		listener.setStatus(output, false, task.getTaskName());
		listener.log(output, task);

		try {
			Files.createLink(Paths.get(destination.getAbsolutePath()), Paths.get(source.getAbsolutePath()));
		} catch (IOException e) {
			System.err.println("Error: Could not create hardlink to file");
		}

		listener.setStatus("", false, task.getTaskName());
	}

	/**
	 * Creates the root directory for a BackupTask with the given name.
	 *
	 * @param destinationPath destination path of the BackupTask
	 * @param taskName        name of the BackupTask
	 * @param task            corresponding BackupTask
	 * @return created root directory as {@link File}
	 */
	public static File createBackupFolder(String destinationPath, String taskName, IBackupListener listener,
			BackupTask task) {

		// create directory with date
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_PATTERN_NAMING);
		df.setTimeZone(TimeZone.getDefault());

		File destinationFile = new File(destinationPath);
		if (!destinationFile.exists()) {
			String output = "Error while creating Backup-Folder";
			listener.setStatus(output, true, task.getTaskName());
			listener.log(output, task);

			return null;
		}
		String backupDir = destinationFile.getAbsolutePath() + File.separator + taskName + "_" + df.format(date);

		File dir = new File(backupDir);
		// create backup dir
		if (dir.mkdir()) {
			String output = "Backup-Folder created";
			listener.setStatus(output, false, task.getTaskName());
			listener.log(output, task);

		} else {
			String output = "Error while creating Backup-Folder";
			listener.setStatus(output, true, task.getTaskName());
			listener.log(output, task);

			return null;
		}
		return dir;
	}

	/**
	 * Deletes a directory and all its content recursively.
	 *
	 * @param path path of the directory to delete
	 * @return true if successful, else false
	 */
	public static boolean deleteDirectory(File path) {
		if (Thread.interrupted()) {
			throw new BackupCanceledException();
		}
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files.length > 0) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
		}
		return (path.delete());
	}

	/**
	 * Calculates the MD5 hash value of the given file.
	 *
	 * @param file file to calculate the hash value from
	 * @return calculated hash value
	 */
	public static String calcMD5(File file) {
		MessageDigest md;

		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Error: NoSuchAlgorithmException in calcMD5");
			return null;
		}

		FileInputStream fis;

		try {
			fis = new FileInputStream(file.getAbsoluteFile());
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

		StringBuilder sb = new StringBuilder();
		for (byte mdbyte : mdbytes) {
			sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	/**
	 * Checks whether the given directory-name is a valid name of a backup set for the BackupTask with the given name.
	 *
	 * @param dirName  name of the directory to validate
	 * @param taskName name of the corresponding BackupTask
	 * @return whether it is a valid name for the given task
	 */
	public static boolean isValidBackupSet(String dirName, String taskName) {
		// split up name of the directory
		StringTokenizer tokenizer = new StringTokenizer(dirName, "_");
		// has to consist of exactly two parts (name of the BackupTask and date)
		if (tokenizer.countTokens() != 2) {
			return false;
		}
		if (!tokenizer.nextToken().equals(taskName)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the time of the most recent execution of the given BackupTask based on the created backup.
	 *
	 * @param task BackupTask to find the most recent execution for
	 * @return time of the most recent execution
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
				if (!isValidBackupSet(directory.getName(), task.getTaskName())) {
					// not a valid backup set
					continue;
				}
				// analyze date token (second one)
				StringTokenizer tokenizer = new StringTokenizer(directory.getName(), "_");
				tokenizer.nextToken();
				String backupDate = tokenizer.nextToken();
				try {
					SimpleDateFormat sdfToDate = new SimpleDateFormat(DATE_TIME_PATTERN_NAMING);
					foundDate = LocalDateTime.ofInstant(sdfToDate.parse(backupDate).toInstant(),
							ZoneId.systemDefault());
				} catch (ParseException e) {
					// no valid date means no valid backup set
					continue;
				}
				if (newestDate != null) {
					if (newestDate.compareTo(foundDate) < 0) {
						newestDate = foundDate;
					}
				} else {
					newestDate = foundDate;
				}
			}
		}
		return newestDate;
	}
}
