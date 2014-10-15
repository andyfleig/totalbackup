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
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JCheckBox;

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
	public static void copyDirectory(File source, File destination, IBackupListener listener)
			throws FileNotFoundException, IOException {
		File[] files = source.listFiles();
		File newFile = null;

		destination.mkdirs();

		String output = "Verzeichnis " + destination.getAbsolutePath() + " erstellt";
		listener.printOut(output, false);
		listener.log(output, listener.getCurrentTask());

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (Thread.interrupted()) {
					throw new BackupCanceledException();
				}
				newFile = new File(destination.getAbsolutePath() + System.getProperty("file.separator")
						+ files[i].getName());
				if (files[i].isDirectory()) {
					copyDirectory(files[i], newFile, listener);
				} else {
					try {
						copyFile(files[i], newFile, listener);
					} catch (IOException e) {
						// Fehler beim kopieren einer Datei (z.B. wegen
						// fehlenden Rechten)
						String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IOError")
								+ System.getProperty("file.separator") + source.getPath();
						listener.printOut(output, true);
						listener.log(output, listener.getCurrentTask());
					}
				}
			}
		}
	}

	/**
	 * Kopiert eine Datei vom angegebenen Quellpfad zum angegebenen Zielpfad.
	 * 
	 * @param source
	 *            Quellpfad
	 * @param destination
	 *            Zielpfad
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination, IBackupListener listener) throws FileNotFoundException,
			IOException {

		String output = ResourceBundle.getBundle("gui.messages").getString("Messages.copying") + " " + source.getPath();
		listener.setStatus(output);
		if (listener.advancedOutputIsEnabled()) {
			listener.printOut(output, false);
		}
		listener.log(output, listener.getCurrentTask());

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

	public static void hardlinkFile(File source, File destination, IBackupListener listener) {
		String output = ResourceBundle.getBundle("gui.messages").getString("Messages.linking") + " " + source.getPath();
		if (listener.advancedOutputIsEnabled()) {
			listener.printOut(output, false);
		}
		listener.setStatus(output);
		listener.log(output, listener.getCurrentTask());

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
	 * @param destinationPath
	 *            Zielpfad des Backups (Ort an dem der Root-Ordner angelegt
	 *            werden soll)
	 * @param taskName
	 *            Name des Backup-Tasks
	 * @return angelegter Root-Ordner
	 */
	public static File createBackupFolder(String destinationPath, String taskName, IBackupListener listener) {

		// Ordnername mit Datum festlegen:
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		df.setTimeZone(TimeZone.getDefault());

		File destinationFile = new File(destinationPath);
		if (!destinationFile.exists()) {
			String output = ResourceBundle.getBundle("gui.messages").getString("Messages.BackupFolderCreationError");
			listener.printOut(output, true);
			listener.log(output, listener.getCurrentTask());
			;
			return null;
		}
		String backupDir = destinationFile.getAbsolutePath() + System.getProperty("file.separator") + taskName + "_"
				+ df.format(date);

		File dir = new File(backupDir);
		// Backup-Ordner anlegen:
		if (dir.mkdir()) {
			String output = ResourceBundle.getBundle("gui.messages").getString("Messages.BackupFolderCreated");
			listener.printOut(output, false);
			listener.log(output, listener.getCurrentTask());
			;
		} else {
			String output = ResourceBundle.getBundle("gui.messages").getString("Messages.BackupFolderCreationError");
			listener.printOut(output, true);
			listener.log(output, listener.getCurrentTask());
			;
			return null;
		}
		return dir;
	}
}
