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
	public static void copyDirectory(File source, File destination, Controller controller)
			throws FileNotFoundException, IOException {
		File[] files = source.listFiles();
		File newFile = null;

		destination.mkdirs();

		String output = "Verzeichnis " + destination.getAbsolutePath() + " erstellt";
		controller.printOut(controller.getCurrentTask(), output, 1);

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				newFile = new File(destination.getAbsolutePath() + System.getProperty("file.separator")
						+ files[i].getName());
				if (files[i].isDirectory()) {
					copyDirectory(files[i], newFile, controller);
				} else {
					copyFile(files[i], newFile, controller);
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
	public static void copyFile(File source, File destination, Controller controller) throws FileNotFoundException,
			IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destination, true));
		int bytes = 0;
		while ((bytes = in.read()) != -1) {
			out.write(bytes);
		}

		in.close();
		out.close();

		String output = ResourceBundle.getBundle("gui.messages").getString("Messages.File") + source.getPath()
				+ System.getProperty("file.separator") + source.getName()
				+ ResourceBundle.getBundle("gui.messages").getString("Messages.to") + destination.getPath() + "/"
				+ destination.getName() + ResourceBundle.getBundle("gui.messages").getString("Messages.copied");
		controller.printOut(controller.getCurrentTask(), output, 1);
	}

	public static void hardlinkFile(File source, File destination, Controller controller) {
		try {
			Files.createLink(Paths.get(destination.getAbsolutePath()), Paths.get(source.getAbsolutePath()));
		} catch (IOException e) {
			System.out.println("Fehler: IO-Problem");
		}
		String output = ResourceBundle.getBundle("gui.messages").getString("Messages.File") + source.getPath()
				+ System.getProperty("file.separator") + source.getName()
				+ ResourceBundle.getBundle("gui.messages").getString("Messages.with") + destination.getPath() + "/"
				+ destination.getName() + ResourceBundle.getBundle("gui.messages").getString("Messages.linked");
		controller.printOut(controller.getCurrentTask(), output, 1);

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
	public static File createBackupFolder(String destinationPath, String taskName) {
		// Ordnername mit Datum festlegen:
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		df.setTimeZone(TimeZone.getDefault());

		File destinationFile = new File(destinationPath);
		String backupDir = destinationFile.getAbsolutePath() + System.getProperty("file.separator") + taskName + "_"
				+ df.format(date);

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
