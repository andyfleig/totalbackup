package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.FilenameFilter;

import javax.swing.SwingUtilities;

public class HardlinkBackup implements Backupable {

	/**
	 * Liste der Quellpfade
	 */
	private ArrayList<String> sourcePaths;
	/**
	 * Name des zu bearbeitenden BackupTasks
	 */
	private String taskName;
	/**
	 * Zielpfad
	 */
	private String destinationPath;
	/**
	 * Listener zur Interaktion mit dem Controller
	 */
	private IBackupListener listener;
	/**
	 * Index für diesen Backup-Task
	 */
	private StructureFile directoryStructure;
	/**
	 * aktuellster Backup-Satz
	 */
	private String newestBackupPath;
	/**
	 * Root-Pfad der Quelle
	 */
	private String sourceRootDir;
	/**
	 * Datum-Pattern
	 */
	private static final String BACKUP_FOLDER_NAME_PATTERN = "dd-MM-yyyy-HH-mm-ss";

	/**
	 * Backup-Objekt zur Datensicherung.
	 * 
	 * @param listener
	 *            Listener
	 * @param source
	 *            Quellpfade
	 * @param destination
	 *            Zielpfad
	 */
	public HardlinkBackup(IBackupListener listener, String nameOfTask, ArrayList<String> sources, String destination) {
		this.listener = listener;
		this.taskName = nameOfTask;
		this.sourcePaths = sources;
		this.destinationPath = destination;
	}

	@Override
	public void runBackup(String taskName) throws FileNotFoundException, IOException {

		listener.printOut(listener.getCurrentTask(),
				ResourceBundle.getBundle("gui.messages").getString("Messages.startBackup"), 1, false);

		// Kontrollieren ob für jeden Backup-Satz ein Index vorhanden ist:
		File dest = new File(destinationPath);

		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		};

		File[] destFolders = dest.listFiles(filter);

		// Prüfen bei welchen Ordnern es sich um Backup-Sätze handelt und den
		// aktuellsten Backup-Satz finden:
		for (int i = 0; i < destFolders.length; i++) {
			boolean indexExists = false;
			if (destFolders[i].isDirectory()) {
				// Namen des Ordners "zerlegen":
				StringTokenizer tokenizer = new StringTokenizer(destFolders[i].getName(), "_");
				// Es wird geprüft ob der Name aus genau 2 Tokens besteht:
				if (tokenizer.countTokens() != 2) {
					continue;
				}
				// Erster Token muss dem TaskName entsprechen:
				if (!tokenizer.nextToken().equals(taskName)) {
					continue;
				}
				// Es handelt sich wohl um einen Backup-Satz
				File[] destFolder = destFolders[i].listFiles();
				for (int j = 0; j < destFolder.length; j++) {
					if (!destFolder[j].isDirectory() && destFolder[j].getName().contains(taskName)
							&& destFolder[j].getName().contains(".ser")) {
						// Ab hier wird davon ausgegangen, dass ein index-file
						// exisitert.
						indexExists = true;
						break;
					}
				}
			}
			// Falls kein index gefunden wurde, wird ein index angelegt:
			// TODO: Unschön: Task jedes Mal neu holen...
			if (indexExists == false) {
				listener.printOut(listener.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.noValidIndexIndexing"), 1, false);
				createIndex(destFolders[i]);
				listener.printOut(listener.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCreated"), 1, false);
				listener.printOut(listener.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaving"), 1, false);
				serializeIndex(taskName, destFolders[i].getAbsolutePath());
				listener.printOut(listener.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaved"), 1, false);
			}
		}

		// Herausfinden welcher Backup-Satz der Neuste ist und diesen laden:
		// Neusten Backup-Ordner finden:
		newestBackupPath = findNewestBackup(destinationPath);
		if (newestBackupPath == null) {
			listener.printOut(listener.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.noValidIndexCanceled"), 1, true);
			return;
		}

		// Index dieses backups einlesen:
		File index = new File(destinationPath + System.getProperty("file.separator") + newestBackupPath
				+ System.getProperty("file.separator") + "index_" + taskName + ".ser");

		// Pfad prüfen:
		if (!index.exists()) {
			listener.printOut(listener.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.IndexNotFound"), 1, true);
			return;
		}

		if (!loadSerialization(index)) {
			listener.printOut(listener.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCorrupted"), 1, false);
			createIndex(index);
			listener.printOut(listener.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCreated"), 1, false);
			listener.printOut(listener.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaving"), 1, false);
			serializeIndex(taskName, index.getAbsolutePath());
			listener.printOut(listener.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaved"), 1, false);
			// Index erneut laden:
			if (!loadSerialization(index)) {
				listener.printOut(listener.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.FatalErrorIndexing"), 1, true);
				return;
			}
		}

		// Hardlink-Backup:
		File dir = BackupHelper.createBackupFolder(destinationPath, taskName, listener);
		if (dir == null) {
			listener.printOut(listener.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.BackupFolderCreationError"), 0, true);
			return;
		}

		try {
			for (int i = 0; i < sourcePaths.size(); i++) {
				File sourceFile = new File(sourcePaths.get(i));

				String folder = dir.getAbsolutePath() + System.getProperty("file.separator") + sourceFile.getName();
				File f = new File(folder);

				if (f.mkdir()) {
					listener.printOut(listener.getCurrentTask(),
							ResourceBundle.getBundle("gui.messages").getString("Messages.FolderCreated"), 1, false);
				} else {
					listener.printOut(listener.getCurrentTask(),
							ResourceBundle.getBundle("gui.messages").getString("Messages.FolderCreationError"), 1, true);
				}
				// Eigentlicher Backup-Vorgang:
				sourceRootDir = sourcePaths.get(i).substring(0,
						sourcePaths.get(i).length() - new File(sourcePaths.get(i)).getName().length());
				recursiveBackup(sourceFile, f);
			}
			// Index des Backup-Satzen erzeugen und serialisiert:
			createIndex(dir);
			listener.printOut(listener.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCreated"), 1, false);
			listener.printOut(listener.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaving"), 1, false);
			serializeIndex(taskName, dir.getAbsolutePath());
			listener.printOut(listener.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.BackupComplete"), 1, false);
		} catch (BackupCanceledException e) {
			listener.printOut(listener.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser"), 1, false);
		}
	}

	/**
	 * Rekursive Mathode zur Durchführung eines Hardlink Backups.
	 * 
	 * @param sourceFile
	 *            Quell-Verzeichnis
	 * @param backupDir
	 *            Ziel-Verzeichnis
	 */
	private void recursiveBackup(File sourceFile, File backupDir) {

		File[] files = sourceFile.listFiles();

		if (files == null) {
			// TODO: Fehlermeldung
			return;
		}

		for (int i = 0; i < files.length; i++) {
			if (Thread.interrupted()) {
				throw new BackupCanceledException();
			}
			if (files[i].isDirectory()) {
				File newBackupDir = new File(backupDir.getAbsolutePath() + System.getProperty("file.separator")
						+ files[i].getName());
				newBackupDir.mkdir();
				recursiveBackup(files[i], newBackupDir);
			} else {
				// Entsprechendes StrucutreFile aus dem Index:
				StructureFile fileInIndex = getStructureFileFromIndex(files[i], sourceRootDir);

				File newFile = new File(backupDir.getAbsolutePath() + System.getProperty("file.separator")
						+ files[i].getName());

				if (fileInIndex == null) {
					// Befindet die Datei sich nicht im Index, wird sie kopiert
					// (nicht verlinkt)
					// Es handelt sich also um eine neue Datei (bisher nicht im
					// Backup)
					try {
						BackupHelper.copyFile(files[i], newFile, listener);
					} catch (IOException e) {
						// Fehler beim kopieren einer Datei (z.B. wegen
						// fehlenden Rechten)
						String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IOError")
								+ System.getProperty("file.separator") + sourceFile.getPath();
						listener.printOut(listener.getCurrentTask(), outprint, 1, true);
					}
					continue;
				}

				if (files[i].lastModified() > fileInIndex.getLastModifiedDate()) {
					// Datei liegt in einer älteren Version im Backup vor
					// Datei zu kopieren:
					try {
						BackupHelper.copyFile(files[i], newFile, listener);
					} catch (IOException e) {
						// Fehler beim kopieren einer Datei (z.B. wegen
						// fehlenden Rechten)
						String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IOError")
								+ System.getProperty("file.separator") + sourceFile.getPath();
						listener.printOut(listener.getCurrentTask(), outprint, 1, true);
					}
				} else {
					// Datei liegt in der neuesten Version im Backup vor
					File fileToLinkFrom = new File(destinationPath + System.getProperty("file.separator")
							+ newestBackupPath + fileInIndex.getFilePath());
					if (fileToLinkFrom.exists()) {
						BackupHelper.hardlinkFile(fileToLinkFrom, newFile, listener);
					} else {
						// File exisitiert im Backup-Satz nicht (aber im Index)

						listener.printOut(listener.getCurrentTask(), ResourceBundle.getBundle("gui.messages")
								.getString("Messages.BadIndex"), 1, false);
						listener.printOut(listener.getCurrentTask(), ResourceBundle.getBundle("gui.messages")
								.getString("Messages.DeletingIndex"), 1, false);

						// Root-Pfad des Index "sichern":
						String rootPathForIndex = files[i].getAbsolutePath();

						// Ungültiger Index wird gelöscht:
						File badIndex = new File(files[i].getAbsolutePath() + directoryStructure.getFilePath());
						badIndex.delete();

						listener.printOut(listener.getCurrentTask(), ResourceBundle.getBundle("gui.messages")
								.getString("Messages.IndexDeleted"), 1, false);

						// Neu indizieren:
						createIndex(new File(rootPathForIndex));
						listener.printOut(listener.getCurrentTask(), ResourceBundle.getBundle("gui.messages")
								.getString("Messages.Indexing"), 1, false);
						listener.printOut(listener.getCurrentTask(), ResourceBundle.getBundle("gui.messages")
								.getString("Messages.IndexCreated"), 1, false);
						listener.printOut(listener.getCurrentTask(), ResourceBundle.getBundle("gui.messages")
								.getString("Messages.IndexSaving"), 1, false);
						serializeIndex(taskName, rootPathForIndex);
						listener.printOut(listener.getCurrentTask(), ResourceBundle.getBundle("gui.messages")
								.getString("Messages.IndexSaved"), 1, false);

						// Datei kopieren
						try {
							BackupHelper.copyFile(files[i], newFile, listener);
						} catch (IOException e) {
							// Fehler beim kopieren einer Datei (z.B. wegen
							// fehlenden Rechten)
							String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IOError")
									+ System.getProperty("file.separator") + sourceFile.getPath();
							listener.printOut(listener.getCurrentTask(), outprint, 1, true);
						}
					}
				}
			}
		}

	}

	/**
	 * Gibt die Datei (als StructureFile) aus dem Index zurück, falls diese dort
	 * vorhanden ist. Ist die Datei nicht im Index wird null zurückgegeben.
	 * 
	 * @param file
	 *            Datei für welche das StrucutreFile zurückgegeben werden soll
	 * @param sourceRootPath
	 *            Root-Pfad der Quelle
	 * @return Gefundenes StructureFile oder null
	 */
	private StructureFile getStructureFileFromIndex(File file, String sourceRootPath) {

		// Namen der Datei "zerlegen":
		StringTokenizer tokenizerOfFile = new StringTokenizer(
				file.getAbsolutePath().substring(sourceRootPath.length()), System.getProperty("file.separator"));
		StructureFile currentStructureFile = directoryStructure;
		StructureFile tmp;

		while (tokenizerOfFile.hasMoreTokens()) {

			tmp = currentStructureFile.getStructureFile(tokenizerOfFile.nextToken());

			if (tmp != null) {
				currentStructureFile = tmp;
			} else {
				return null;
			}
		}
		return currentStructureFile;
	}

	/**
	 * Erzeugt den Index.
	 * 
	 * @param root
	 *            Root-File zur Indizierung
	 */
	private void createIndex(File root) {
		if (root.isDirectory()) {
			// Verzeichnisstruktur-Objekt erzeugen:
			StructureFile rootFile = recCalcDirStruct(root.getAbsolutePath(), root.getAbsolutePath());
			directoryStructure = rootFile;
		}
	}

	/**
	 * Serialisiert den Index.
	 * 
	 * @param taskName
	 *            Name des Tasks des zu serialisierenden Index
	 * @param backupSetPath
	 *            Pfad zum Backup-Satz
	 */
	private void serializeIndex(String taskName, String backupSetPath) {

		// Verzeichnisstruktur speichern:
		// File anlegen:
		File index = new File(backupSetPath + System.getProperty("file.separator") + "index_" + taskName + ".ser");
		// Prüfen ob bereits ein Index existert:
		if (!index.exists()) {
			try {
				index.createNewFile();
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}
		// OutputStreams anlegen:
		OutputStream fos = null;
		ObjectOutputStream o = null;

		try {
			fos = new FileOutputStream(index);
			o = new ObjectOutputStream(fos);

			o.writeObject(this.directoryStructure);
		} catch (IOException ex) {
			System.out.println(ex);
		} finally {
			if (o != null)
				try {
					o.close();
				} catch (IOException ex) {
					System.err.println(ex);
				}
			if (fos != null)
				try {
					fos.close();
				} catch (IOException ex) {
					System.err.println(ex);
				}
		}
	}

	/**
	 * Läd einen seriallisierten Index. Gibt bei Erfolg TRUE und sonst FALSE
	 * zurück;
	 * 
	 * @param index
	 *            zu ladender Index
	 */
	private boolean loadSerialization(File index) {

		boolean result = true;

		ObjectInputStream ois = null;
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(index);
			ois = new ObjectInputStream(fis);

			directoryStructure = (StructureFile) ois.readObject();
		} catch (IOException e) {
			System.err.println(e);
			result = false;
		} catch (ClassNotFoundException e) {
			System.err.println(e);
			result = false;
		} finally {
			if (ois != null)
				try {
					ois.close();
				} catch (IOException e) {
					System.err.println(e);
					result = false;
				}
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					System.err.println(e);
					result = false;
				}
		}
		return result;
	}

	/**
	 * Rekursive Methode zur Berechnung der Verzeichnisstruktur.
	 * 
	 * @param rootPath
	 *            root Pfad des Backups
	 * @param path
	 *            Pfad der aktuell zu analysierenden Datei (relativer Pfad)
	 * @return StructureFile für die Verzeichnisstruktur
	 */
	private StructureFile recCalcDirStruct(String rootPath, String path) {
		// TODO: Interrupt!?

		File[] files = new File(path).listFiles();

		StructureFile sFile = new StructureFile(rootPath, path.substring(rootPath.length()));
		for (int i = 0; i < files.length; i++) {
			StructureFile newFile;
			if (files[i].isDirectory()) {
				newFile = recCalcDirStruct(rootPath, files[i].getAbsolutePath());
			} else {
				newFile = new StructureFile(rootPath, files[i].getAbsolutePath().substring(rootPath.length()));
			}
			sFile.addFile(newFile);
		}
		return sFile;
	}

	/**
	 * Gibt den Pfad (als String) zum aktuellsten Backup-Satz zurück.
	 * 
	 * @param rootPath
	 *            Ordner in dem nach Backup-Sätzen gesucht werden soll
	 * @return Pfad zum aktuellsten Backup-Satz
	 */
	private String findNewestBackup(String rootPath) {
		File root = new File(rootPath);
		File[] directories = root.listFiles();

		Date newestDate = null;
		String newestBackupPath = null;
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
				if (!tokenizer.nextToken().equals(taskName)) {
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
				if (newestDate == null) {
					newestDate = foundDate;
					newestBackupPath = directories[i].getName();
				} else {
					if (newestDate.compareTo(foundDate) < 0) {
						newestDate = foundDate;
						newestBackupPath = directories[i].getName();
					}
				}
			}
		}
		return newestBackupPath;
	}
}
