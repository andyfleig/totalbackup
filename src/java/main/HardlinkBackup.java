package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class HardlinkBackup implements Backupable {
	
	private ArrayList<String> sourcePaths;
	private String destinationPath;
	private Controller controller;
	
	
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
	}


	@Override
	public void runBackup(String taskName) throws FileNotFoundException, IOException {
		// TODO implementation
		
	}

}
