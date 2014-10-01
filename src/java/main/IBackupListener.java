package main;

public interface IBackupListener {

	/**
	 * Gibt den übergebenen String auf dem Output-Panel aus und schreibt ihn
	 * (abhängig vom Level) in die log-Datei.
	 * 
	 * @param task
	 *            zugehöriger Task (zu welchem der Outprint gehört)
	 * @param s
	 *            auszugebender String
	 * @param level
	 *            Ausgabe Level: 0 = nur ausgeben, 1 = ausgeben und loggen
	 */
	public void printOut(BackupTask task, String s, int level);

	/**
	 * Gibt den aktuell laufenden Task zurück.
	 * 
	 * @return aktuell laufender Task
	 */
	public BackupTask getCurrentTask();

}
