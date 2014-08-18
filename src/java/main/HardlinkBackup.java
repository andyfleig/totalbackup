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

public class HardlinkBackup implements Backupable {
	
	private ArrayList<String> sourcePaths;
	private String destinationPath;
	private Controller controller;
	private ArrayList<StructureFile> directoryStructure;
	
	
	/**
	 * Backup-Objekt zur Datensicherung.
	 * @param c Controller
	 * @param source Quellpfade
	 * @param destination Zielpfad
	 */
	public HardlinkBackup(Controller c, ArrayList<String> sources, String destination) {
		this.controller = c;
		this.sourcePaths = sources;
		this.destinationPath = destination;
		directoryStructure = new ArrayList<StructureFile>();
	}


	@Override
	public void runBackup(String taskName) throws FileNotFoundException, IOException {
		
		// Kontrollieren ob die Verzeichnisstruktur vorhanden ist:
		File index = new File(destinationPath + "/index_" + taskName + ".ser");
		if (!index.exists()) {
			// Wenn nein, Verzeichnisstruktur anlegen und serialisieren:
			createDirectoryStructure();
			serializeDirectoryStructure(taskName);
		} else {
			loadSerialization(taskName);
		}
		// Hardlink-Backup:
		File dir = BackupHelper.createBackupFolder(destinationPath, taskName);
		
		for (int i = 0; i < sourcePaths.size(); i++) {
			File sourceFile = new File(sourcePaths.get(i));
			
			String folder = dir + "/" + sourceFile.getName();
			File f = new File(folder);
			
			if (f.mkdir()) {
				System.out.println("Ordner erfolgreich erstellt!");
			} else {
				System.out.println("Fehler beim erstellen des Ordners");
			}
			// Eigentlicher Backup-Vorgang:
			//TODO
			
		}
		
	}
	
	private void recursiveBackup(String path, File backupDir) {
		//TODO
	}
	
	/**
	 * Erzeugt die Verzeichnisstruktur.
	 */
	private void createDirectoryStructure() {
		
		// Verzeichnisstruktur-Objekt erzeugen:
		for (int i = 0; i < sourcePaths.size(); i++) {
			StructureFile rootFile = recCalcDirStruct(sourcePaths.get(i), sourcePaths.get(i));
			directoryStructure.add(rootFile);
		}
	}
	
	private void serializeDirectoryStructure(String taskName) {
		// Verzeichnisstruktur speichern:
		// File anlegen:
		File index = new File(destinationPath + "/index_" + taskName + ".ser");
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
	
	private void loadSerialization(String taskName) {
		// Prüfen ob bereits Einstellungen gespeichert wurden:
		File index = new File(destinationPath + "/index_" + taskName + ".ser");
		if (index.exists()) {
			ObjectInputStream ois = null;
			FileInputStream fis = null;

			try {
				fis = new FileInputStream(index);
				ois = new ObjectInputStream(fis);

				directoryStructure = (ArrayList<StructureFile>) ois.readObject();
			} catch (IOException e) {
				System.err.println(e);
			} catch (ClassNotFoundException e) {
				System.err.println(e);
			} finally {
				if (ois != null)
					try {
						ois.close();
					} catch (IOException e) {
						System.err.println(e);
					}
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
						System.err.println(e);
					}
			}
		}
	}
	
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

}
