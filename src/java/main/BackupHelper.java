package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class BackupHelper {

	/**
	 * Kopiert ein Verzeichnis rekursiv.
	 * 
	 * @param source
	 *            Quellverzeichnis
	 * @param destination
	 *            Zielverzeichnis
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void copyDirectory(File source, File destination) throws FileNotFoundException, IOException {
		File[] files = source.listFiles();
		File newFile = null;

		destination.mkdirs();

		String output = "Verzeichnis " + destination.getAbsolutePath() + " erstellt";
		// TODO: logging
		// controller.printOut(output);

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				newFile = new File(destination.getAbsolutePath() + System.getProperty("file.separator")
						+ files[i].getName());
				if (files[i].isDirectory()) {
					copyDirectory(files[i], newFile);
				} else {
					copyFile(files[i], newFile);
				}
			}
		}
	}

	/**
	 * Kopiet eine Datei vom angegebenen Quellpfad zum angegebenen Zielpfad.
	 * 
	 * @param source
	 *            Quellpfad
	 * @param destination
	 *            Zielpfad
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination) throws FileNotFoundException, IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destination, true));
		int bytes = 0;
		while ((bytes = in.read()) != -1) {
			out.write(bytes);
		}
		in.close();
		out.close();

		String output = "Datei " + source.getPath() + System.getProperty("file.separator") + source.getName() + " nach " + destination.getPath() + "/"
				+ destination.getName() + " kopiert";
		// TODO: logging
		// controller.printOut(output);
	}

	/**
	 * Erstellt den Root-Ordner für ein Backup.
	 * @param destinationPath Zielpfad des Backups (Ort an dem der Root-Ordner angelegt werden soll)
	 * @param taskName Name des Backup-Tasks
	 * @return angelegter Root-Ordner
	 */
	public static File createBackupFolder(String destinationPath, String taskName) {
		// Ordnername mit Datum festlegen:
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy-HH-mm");
		df.setTimeZone(TimeZone.getDefault());

		File destinationFile = new File(destinationPath);
		String backupDir = destinationFile.getAbsolutePath() + System.getProperty("file.separator") + taskName + "_" + df.format(date);

		File dir = new File(backupDir);
		// Backup-Ordner anlegen:
		if (dir.mkdir()) {
			System.out.println("Backup-Ordner erfolgreich erstellt!");
		} else {
			System.out.println("Fehler beim erstellen des Backup-Ordners");
			return null;
		}
		return dir;
	}
}
