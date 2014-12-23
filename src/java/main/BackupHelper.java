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

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (Thread.interrupted()) {
					throw new BackupCanceledException();
				}
				newFile = new File(destination.getAbsolutePath() + File.separator + files[i].getName());
				if (files[i].isDirectory()) {
					copyDirectory(files[i], newFile, listener);
				} else {
					try {
						copyFile(files[i], newFile, listener);
					} catch (IOException e) {
						// Fehler beim kopieren einer Datei (z.B. wegen
						// fehlenden Rechten)
						String output = ResourceBundle.getBundle("gui.messages").getString("Messages.IOError")
								+ File.separator + source.getPath();
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

	/**
	 * Erstellt einen Hardlink (dest) der auf source zeigt
	 * 
	 * @param source
	 *            Quell-Datei des Hardlinks
	 * @param destination
	 *            Ziel-Datei des Hardlinks
	 */
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
		String backupDir = destinationFile.getAbsolutePath() + File.separator + taskName + "_" + df.format(date);

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

	/**
	 * Löscht ein Verzeichnis und dessen Inhalt rekursiv.
	 * 
	 * @param path
	 *            Pfad des zu löschenden Ordners
	 * @return true falls erfolgreich, false sonst
	 */
	public static boolean deleteDirectory(File path) {
		if (Thread.interrupted()) {
			throw new BackupCanceledException();
		}
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					if (!files[i].delete()) {
					}
				}
			}
		}
		return (path.delete());
	}

	/**
	 * Berechnet den MD5 Hashwert der gegebenen Datei.
	 * 
	 * @param f
	 *            Datei von der der Hadhwert berechnet werden soll
	 * @return MD5-Hashwert der gegebenen Datei
	 * @throws NoSuchAlgorithmException
	 *             wird nie geworfen (da der Algorithmus hardcoded ist)
	 * @throws FileNotFoundException
	 *             Wird bei ungültiger gegebener Datei geworfen
	 * @throws IOException
	 */
	public static String calcMD5(File f) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
		MessageDigest md;

		md = MessageDigest.getInstance("MD5");

		FileInputStream fis;

		fis = new FileInputStream(f.getAbsoluteFile());

		byte[] dataBytes = new byte[1024];

		int nread = 0;
		while ((nread = fis.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		}
		;
		byte[] mdbytes = md.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}
