package main;

public interface IBackupListener {

	/**
	 * Gibt den gegebenen String auf der GUI aus. error bestimmt ob es sich um
	 * eine Fehlermeldung (rot) handelt oder nicht.
	 * 
	 * @param s
	 *            auszugebender String
	 * @param error
	 *            legt fest ob es sich um eine Fehlermeldung handelt oder nicht
	 */
	public void printOut(String s, boolean error);

	/**
	 * Schreibt den gegebenen String in das log-File des gegebenen Tasks.
	 * 
	 * @param event
	 *            zu loggender String
	 * @param task
	 *            zugehöriger Task
	 */
	public void log(String event, BackupTask task);

	/**
	 * Gibt den gegebenen String auf dem Status-Textfeld auf der GUI aus.
	 * 
	 * @param status
	 *            auszugebender String
	 */
	public void setStatus(String status);

	/**
	 * Gibt den aktuell laufenden Task zurück.
	 * 
	 * @return aktuell laufender Task
	 */
	public BackupTask getCurrentTask();

	/**
	 * Gibt zurück ob die erweiterte Ausgabe aktiviert ist.
	 * 
	 * @return Status der erweiterten Ausgabe
	 */
	public boolean advancedOutputIsEnabled();
	
	/**
	 * Erhöht die Anzahl der zu bearbeitenden Ordner um 1.
	 */
	public void increaseNumberOfDirectories();
	/**
	 * Erhöht die Anzahl der zu bearbeitenden Ordner um 1.
	 */
	public void increaseNumberOfFiles();
	/**
	 * Erhöht die Gesamtgröße der zu kopierenden Dateien um den gegebenen Wert.
	 * @param sizeToIncreaseBy Größe um die der Gesamtwert erhöht wird
	 */
	public void increaseSizeToCopyBy(double sizeToIncreaseBy);
	/**
	 * Erhöht die Gesamtgröße der zu verlinkenden Dateien um den gegebenen Wert.
	 * @param sizeToIncreaseBy Größe um die der Gesamtwert erhöht wird
	 */
	public void increaseSizeToLinkBy(double sizeToIncreaseBy);
}
