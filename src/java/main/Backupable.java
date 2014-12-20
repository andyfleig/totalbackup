package main;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Backupable {

	/**
	 * Bereitet den Backup-Vorgang vor (sammelt zu bearbeitenden Dateien)
	 */
	public void runPreparation();
	
	/**
	 * Führt das Backup aus.
	 * 
	 * @param taskName
	 *            Name des auszuführenden Backup-Tasks
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void runBackup(String taskName) throws FileNotFoundException, IOException;

}
