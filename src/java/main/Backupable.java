package main;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Backupable {

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
