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
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.FilenameFilter;

public class HardlinkBackup implements Backupable {

	/**
	 * Liste der Quellpfade
	 */
	private ArrayList<Source> sources;
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
	 * Datum-Pattern
	 */
	private static final String BACKUP_FOLDER_NAME_PATTERN = "dd-MM-yyyy-HH-mm-ss";
	/**
	 * Zu bearbeitende Elemente
	 */
	private LinkedList<BackupElement> elementQueue;
	/**
	 * Zeigt ob die Vorbereitungen bereits getroffen wurde. Erst dann kann
	 * runBackup() aufgerufen werden.
	 */
	private boolean preparationDone = false;
	/**
	 * Aktuelles Backup-Directory.
	 */
	private File backupDir;
	/**
	 * Root-Verzeichnis der Quelle.
	 */
	private String sourceRootDir;
	/**
	 * Quelle an der aktuell "gearbeitet" wird (für das Filtern der zu queuenden
	 * Elemente).
	 */
	private Source currentSource;

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
	public HardlinkBackup(IBackupListener listener, String nameOfTask, ArrayList<Source> sources, String destination) {
		this.listener = listener;
		this.taskName = nameOfTask;
		this.sources = sources;
		this.destinationPath = destination;
		elementQueue = new LinkedList<BackupElement>();
	}

	@Override
	public void runPreparation() {

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
				String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.noValidIndexIndexing");
				listener.printOut(outprint, false);
				listener.log(outprint, listener.getCurrentTask());

				createIndex(destFolders[i]);

				// Indizierung wurde abgebrochen:
				if (directoryStructure == null) {
					throw new BackupCanceledException();
				}

				outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCreated");
				listener.printOut(outprint, false);
				listener.log(outprint, listener.getCurrentTask());

				outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaving");
				listener.printOut(outprint, false);
				listener.log(outprint, listener.getCurrentTask());

				serializeIndex(taskName, destFolders[i].getAbsolutePath());
				outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaved");
				listener.printOut(outprint, false);
				listener.log(outprint, listener.getCurrentTask());
			}
		}

		// Herausfinden welcher Backup-Satz der Neuste ist und diesen laden:
		// Neusten Backup-Ordner finden:
		newestBackupPath = findNewestBackup(destinationPath);
		if (newestBackupPath == null) {
			String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.noValidIndexCanceled");
			listener.printOut(outprint, true);
			listener.log(outprint, listener.getCurrentTask());
			return;
		}

		// Index dieses backups einlesen:
		File index = new File(destinationPath + File.separator + newestBackupPath + File.separator + "index_"
				+ taskName + ".ser");

		// Pfad prüfen:
		if (!index.exists()) {
			String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexNotFound");
			listener.printOut(outprint, true);
			listener.log(outprint, listener.getCurrentTask());
			return;
		}

		if (!loadSerialization(index)) {
			String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCorrupted");
			listener.printOut(outprint, false);
			listener.log(outprint, listener.getCurrentTask());

			createIndex(index);

			// Indizierung wurde abgebrochen:
			if (directoryStructure == null) {
				throw new BackupCanceledException();
			}

			outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCreated");
			listener.printOut(outprint, false);
			listener.log(outprint, listener.getCurrentTask());

			outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaving");
			listener.printOut(outprint, false);
			listener.log(outprint, listener.getCurrentTask());

			serializeIndex(taskName, index.getAbsolutePath());

			outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaved");
			listener.printOut(outprint, false);
			listener.log(outprint, listener.getCurrentTask());

			// Index erneut laden:
			if (!loadSerialization(index)) {
				outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.FatalErrorIndexing");
				listener.printOut(outprint, true);
				listener.log(outprint, listener.getCurrentTask());
				return;
			}
		}
		// Eigentliches Hardlink Backup:
		// Backup-Ordner anlegen:
		backupDir = BackupHelper.createBackupFolder(destinationPath, taskName, listener);

		String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.PreparationStarted");
		listener.printOut(outprint, false);
		listener.log(outprint, listener.getCurrentTask());

		if (backupDir == null) {
			outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.BackupFolderCreationError");
			listener.printOut(outprint, true);
			return;
		}

		try {
			for (int i = 0; i < sources.size(); i++) {
				// Für die Filterung:
				currentSource = sources.get(i);

				File sourceFile = new File(sources.get(i).getPath());

				String folder = backupDir.getAbsolutePath() + File.separator + sourceFile.getName();
				File f = new File(folder);

				if (f.mkdir()) {
					outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.FolderCreated");
					listener.printOut(outprint, false);
					listener.log(outprint, listener.getCurrentTask());
				} else {
					outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.FolderCreationError");
					listener.printOut(outprint, true);
					listener.log(outprint, listener.getCurrentTask());
				}

				// Queueing:
				try {
					for (int j = 0; j < sources.size(); j++) {
						sourceRootDir = sourceFile.getAbsolutePath().substring(0,
								sources.get(j).getPath().length() - sourceFile.getName().length());
						rekursivePreparation(new File(sources.get(j).getPath()), f);
					}
				} catch (BackupCanceledException e) {
					outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
					listener.printOut(outprint, false);
					listener.log(outprint, listener.getCurrentTask());
				}
			}
		} catch (BackupCanceledException e) {
			outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
			listener.printOut(outprint, false);
			listener.log(outprint, listener.getCurrentTask());
		}
		String output = ResourceBundle.getBundle("gui.messages").getString("Messages.PreparationDone");
		listener.printOut(output, false);
		listener.log(output, listener.getCurrentTask());
	}

	@Override
	public void runBackup(String taskName) throws FileNotFoundException, IOException {

		String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.startBackup");
		listener.printOut(outprint, false);
		listener.log(outprint, listener.getCurrentTask());
		try {
			// Eigentlicher Backup-Vorgang:
			while (!elementQueue.isEmpty()) {
				BackupElement currentElement = elementQueue.pop();
				if (currentElement.isDirectory()) {
					(new File(currentElement.getDestPath())).mkdir();
				} else {
					if (currentElement.toLink()) {
						BackupHelper.hardlinkFile(new File(currentElement.getSourcePath()),
								new File(currentElement.getDestPath()), listener);
					} else {
						BackupHelper.copyFile(new File(currentElement.getSourcePath()),
								new File(currentElement.getDestPath()), listener);
					}
				}
			}

			// Index des Backup-Satzen erzeugen und serialisiert:
			createIndex(backupDir);

			// Indizierung wurde abgebrochen:
			if (directoryStructure == null) {
				throw new BackupCanceledException();
			}

			outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCreated");
			listener.printOut(outprint, false);
			listener.log(outprint, listener.getCurrentTask());
			outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaving");
			listener.printOut(outprint, false);
			listener.log(outprint, listener.getCurrentTask());

			serializeIndex(taskName, backupDir.getAbsolutePath());

			outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.BackupComplete");
			listener.printOut(outprint, false);
			listener.log(outprint, listener.getCurrentTask());
		} catch (BackupCanceledException e) {
			outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.CanceledByUser");
			listener.printOut(outprint, false);
			listener.log(outprint, listener.getCurrentTask());
		}
	}

	/**
	 * Rekursive Mathode zur Vorbereitung ("analyse") eines Hardlink Backups.
	 * 
	 * @param sourceFile
	 *            Quell-Verzeichnis
	 * @param backupDir
	 *            Ziel-Verzeichnis
	 */
	private void rekursivePreparation(File sourceFile, File backupDir) {
		File[] files = sourceFile.listFiles();
		if (files == null) {
			String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.UnknownErrorAt") + " "
					+ sourceFile.getPath();
			listener.printOut(outprint, true);
			listener.log(outprint, listener.getCurrentTask());

			return;
		}
		for (int i = 0; i < files.length; i++) {
			if (Thread.interrupted()) {
				throw new BackupCanceledException();
			}
			if (files[i].isDirectory()) {
				// Filtern:
				ArrayList<String> filtersOfThisSource = currentSource.getFilter();
				boolean filterMatches = false;
				for (int j = 0; j < filtersOfThisSource.size(); j++) {
					if ((files[i].getAbsolutePath().equals(filtersOfThisSource.get(j)))) {
						filterMatches = true;
					}
				}
				if (!filterMatches) {
					File newBackupDir = new File(backupDir.getAbsolutePath() + File.separator + files[i].getName());
					elementQueue.add(new BackupElement(files[i].getAbsolutePath(), newBackupDir.getAbsolutePath(),
							true, false));
					listener.increaseNumberOfDirectories();
					rekursivePreparation(files[i], newBackupDir);
				}

			} else {
				// Filtern:
				ArrayList<String> filtersOfThisSource = currentSource.getFilter();
				boolean filterMatches = false;
				for (int j = 0; j < filtersOfThisSource.size(); j++) {
					if ((files[i].getAbsolutePath().equals(filtersOfThisSource.get(j)))) {
						filterMatches = true;
					}
				}
				if (!filterMatches) {
					// Herausfinden ob zu kopieren oder zu verlinken:
					// Entsprechendes StrucutreFile aus dem Index:

					StructureFile fileInIndex = getStructureFileFromIndex(files[i], sourceRootDir);

					File newFile = new File(backupDir.getAbsolutePath() + File.separator + files[i].getName());

					if (fileInIndex == null) {
						// Befindet die Datei sich nicht im Index, wird sie
						// kopiert
						// (nicht verlinkt)
						// Es handelt sich also um eine neue Datei (bisher nicht
						// im
						// Backup)
						elementQueue.add(new BackupElement(files[i].getAbsolutePath(), newFile.getAbsolutePath(),
								false, false));
						listener.increaseNumberOfFiles();
						listener.increaseSizeToCopyBy(files[i].length());
						continue;
					}
					if (files[i].lastModified() > fileInIndex.getLastModifiedDate()) {
						// Datei liegt in einer älteren Version im Backup vor
						// Datei zu kopieren:
						elementQueue.add(new BackupElement(files[i].getAbsolutePath(), newFile.getAbsolutePath(),
								false, false));
						listener.increaseNumberOfFiles();
						listener.increaseSizeToCopyBy(files[i].length());
					} else {
						// Datei liegt in der aktuellen Version vor

						// Test ob die Datei im Backup-Satz vorhanden ist:
						File fileToLinkFrom = new File(destinationPath + File.separator + newestBackupPath
								+ fileInIndex.getFilePath());
						if (fileToLinkFrom.exists()) {
							// Datei verlinken:
							elementQueue.add(new BackupElement(fileToLinkFrom.getAbsolutePath(), newFile
									.getAbsolutePath(), false, true));
							listener.increaseNumberOfFiles();
							listener.increaseSizeToLinkBy(files[i].length());
						} else {
							// File exisitiert im Backup-Satz nicht (aber im
							// Index)
							String outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.BadIndex");
							listener.printOut(outprint, false);
							listener.log(outprint, listener.getCurrentTask());

							outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.DeletingIndex");
							listener.printOut(outprint, false);
							listener.log(outprint, listener.getCurrentTask());

							// Root-Pfad des Index "sichern":
							String rootPathForIndex = files[i].getAbsolutePath();

							// Ungültiger Index wird gelöscht:
							File badIndex = new File(files[i].getAbsolutePath() + directoryStructure.getFilePath());
							badIndex.delete();

							outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexDeleted");
							listener.printOut(outprint, false);
							listener.log(outprint, listener.getCurrentTask());

							// Neu indizieren:
							outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.Indexing");
							listener.printOut(outprint, false);
							listener.log(outprint, listener.getCurrentTask());
							createIndex(new File(rootPathForIndex));

							// Indizierung wurde abgebrochen:
							if (directoryStructure == null) {
								throw new BackupCanceledException();
							}

							outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexCreated");
							listener.printOut(outprint, false);
							listener.log(outprint, listener.getCurrentTask());
							outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaving");
							listener.printOut(outprint, false);
							listener.log(outprint, listener.getCurrentTask());

							serializeIndex(taskName, rootPathForIndex);

							outprint = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexSaved");
							listener.printOut(outprint, false);
							listener.log(outprint, listener.getCurrentTask());
							// Datei zu kopieren:
							elementQueue.add(new BackupElement(files[i].getAbsolutePath(), newFile.getAbsolutePath(),
									false, false));
							listener.increaseNumberOfFiles();
							listener.increaseSizeToCopyBy(files[i].length());
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
				file.getAbsolutePath().substring(sourceRootPath.length()), File.separator);
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
			try {
				StructureFile rootFile = recCalcDirStruct(root.getAbsolutePath(), root.getAbsolutePath());
				directoryStructure = rootFile;
			} catch (BackupCanceledException e) {
				directoryStructure = null;
				String output = ResourceBundle.getBundle("gui.messages").getString("Messages.IndexingCanceled");
				listener.printOut(output, false);
				listener.log(output, listener.getCurrentTask());
			}
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
		File index = new File(backupSetPath + File.separator + "index_" + taskName + ".ser");
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
		if (Thread.interrupted()) {
			throw new BackupCanceledException();
		}

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
