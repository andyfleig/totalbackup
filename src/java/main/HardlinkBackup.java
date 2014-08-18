package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
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
		// TODO
		// Wenn nein, Verzeichnisstruktur anlegen/ berechnen:
		//TODO
		
		// Eigentliches Hardlink-Backup
		
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
		// Verzeichnisstruktur speichern:
		//TODO
		
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
