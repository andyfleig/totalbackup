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

public class HardlinkBackup implements Backupable {

	private ArrayList<String> sourcePaths;
	private String destinationPath;
	private Controller controller;
	private StructureFile directoryStructure;
	private static final String BACKUP_FOLDER_NAME_PATTERN = "dd-MM-yyyy-HH-mm-ss";

	/**
	 * Backup-Objekt zur Datensicherung.
	 * 
	 * @param c
	 *            Controller
	 * @param source
	 *            Quellpfade
	 * @param destination
	 *            Zielpfad
	 */
	public HardlinkBackup(Controller c, ArrayList<String> sources, String destination) {
		this.controller = c;
		this.sourcePaths = sources;
		this.destinationPath = destination;
	}

	@Override
	public void runBackup(String taskName) throws FileNotFoundException, IOException {

		controller.printOut(controller.getCurrentTask(),
				ResourceBundle.getBundle("gui.messages").getString("Messages.startBackup"), 1);
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
		//TODO: Überprüfung anpassen (siehe findNewestBackup())
		for (int i = 0; i < destFolders.length; i++) {
			boolean indexExists = false;
			if (destFolders[i].isDirectory() && destFolders[i].getName().contains(taskName)) {
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
			if (indexExists == false) {
				controller.printOut(controller.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.noValidIndexIndexing"), 1);
				createIndex(destFolders[i]);
				controller.printOut(controller.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCreated"), 1);
				controller.printOut(controller.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaving"), 1);
				serializeIndex(taskName, destFolders[i].getAbsolutePath());
				controller.printOut(controller.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaved"), 1);
			}
		}

		// Herausfinden welcher Backup-Satz der Neuste ist und diesen laden:
		// Neusten Backup-Ordner finden:
		String newestBackupPath = findNewestBackup(destinationPath);
		if (newestBackupPath == null) {
			controller.printOut(controller.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.noValidIndexCanceled"), 1);
			return;
		}

		// Index dieses backups einlesen:
		File index = new File(destinationPath + System.getProperty("file.separator") + newestBackupPath
				+ System.getProperty("file.separator") + "index_" + taskName + ".ser");

		// Pfad prüfen:
		//TODO
		if (!index.exists()) {
			System.err.println("Fehler: Index-Datei nicht gefunden");
			return;
		}

		if (!loadSerialization(index)) {
			controller.printOut(controller.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCorrupted"), 1);
			createIndex(index);
			controller.printOut(controller.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCreated"), 1);
			controller.printOut(controller.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaving"), 1);
			serializeIndex(taskName, index.getAbsolutePath());
			controller.printOut(controller.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaved"), 1);
			// Index erneut laden:
			if (!loadSerialization(index)) {
				controller.printOut(controller.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.FatalErrorIndexing"), 1);
				return;
			}
		}

		// Hardlink-Backup:
		File dir = BackupHelper.createBackupFolder(destinationPath, taskName);
		if (dir == null) {
			controller.printOut(controller.getCurrentTask(),
					ResourceBundle.getBundle("gui.messages").getString("Messages.tooFast"), 0);
			return;
		}

		for (int i = 0; i < sourcePaths.size(); i++) {
			File sourceFile = new File(sourcePaths.get(i));

			String folder = dir.getAbsolutePath() + System.getProperty("file.separator") + sourceFile.getName();
			File f = new File(folder);

			if (f.mkdir()) {
				controller.printOut(controller.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.FolderCreated"), 1);
			} else {
				controller.printOut(controller.getCurrentTask(),
						ResourceBundle.getBundle("gui.messages").getString("Messages.FolderCreationError"), 1);
			}
			// Eigentlicher Backup-Vorgang:
			recursiveBackup(sourceFile, f);
		}
		// Index des Backup-Satzen erzeugen und serialisiert:
		createIndex(dir);
		controller.printOut(controller.getCurrentTask(),
				ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCreated"), 1);
		controller.printOut(controller.getCurrentTask(),
				ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaving"), 1);
		serializeIndex(taskName, dir.getAbsolutePath());
		controller.printOut(controller.getCurrentTask(),
				ResourceBundle.getBundle("gui.messages").getString("Messages.BackupComplete"), 1);
	}

	/**
	 * Rekursive Mathode zur Druchführung eines Hardlink Backups.
	 * 
	 * @param sourceFile
	 *            Quell-Verzeichnis
	 * @param backupDir
	 *            Ziel-Verzeichnis
	 */
	private void recursiveBackup(File sourceFile, File backupDir) {

		File[] files = sourceFile.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				File newBackupDir = new File(backupDir.getAbsolutePath() + System.getProperty("file.separator")
						+ files[i].getName());
				newBackupDir.mkdir();
				recursiveBackup(files[i], newBackupDir);
			} else {
				File newFile = new File(backupDir.getAbsolutePath() + System.getProperty("file.separator")
						+ files[i].getName());
				if (files[i].lastModified() > getLastModifiedDateFromIndex(files[i])) {
					// Neue Datei zu sichern:
					try {
						BackupHelper.copyFile(files[i], newFile, controller);
					} catch (IOException e) {
						System.out.println("Fehler: IO-Fehler beim kopieren");
					}
				} else {
					// Datei zu verlinken (Hardlink):
					BackupHelper.hardlinkFile(files[i], newFile, controller);
				}
			}
		}

	}

	/**
	 * Gibt zurück, von wann eine Datei aus dem Index ist, oder -1 wenn die
	 * Datein im Index nicht existiert.
	 * 
	 * @param file
	 *            Datei für welche das Datum zurückgegeben werden soll
	 * @return ms seit 1.1.1970
	 */
	private long getLastModifiedDateFromIndex(File file) {
		// Namen der Datei "zerlegen":
		StringTokenizer tokenizer = new StringTokenizer(file.getAbsolutePath(), System.getProperty("file.separator"));
		StructureFile currentStructureFile = directoryStructure;
		StructureFile tmp;
		while (tokenizer.hasMoreTokens()) {

			tmp = currentStructureFile.getStructureFile(tokenizer.nextToken());
			if (tmp != null) {
				currentStructureFile = tmp;
			}
		}
		return currentStructureFile.getLastModifiedDate();
	}
	
	
	/**
	 *  Erzeugt den Index.
	 * @param root Root-File zur Indizierung
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
	 * Läd einen seriallisierten Index.
	 * Gibt bei Erfolg TRUE und sonst FALSE zurück;
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
	 *            aktueller (zu analysierender) Pfad
	 * @return StructureFile für die Verzeichnisstruktur
	 */
	private StructureFile recCalcDirStruct(String rootPath, String path) {
		File currentFile = new File(path);
		File[] files = currentFile.listFiles();

		StructureFile sFile = new StructureFile(rootPath, path);
		for (int i = 0; i < files.length; i++) {
			StructureFile newFile;
			if (files[i].isDirectory()) {
				newFile = recCalcDirStruct(rootPath, files[i].getAbsolutePath());
			} else {
				newFile = new StructureFile(rootPath, files[i].getAbsolutePath());
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
				// Erster Token kann ignoriert werden:
				tokenizer.nextToken();
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
					if (newestDate.compareTo(foundDate) > 0) {
						newestDate = foundDate;
						newestBackupPath = directories[i].getName();
					}
				}
			}
		}
		return newestBackupPath;
	}

}
