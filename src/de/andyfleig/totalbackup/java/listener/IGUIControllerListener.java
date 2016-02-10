package listener;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public interface IGUIControllerListener {
	/**
	 * Prüft ob der gegebene String teil der übergebenen Argumente ist.
	 *
	 * @param s zu prüfender String (gesuchtes Argument)
	 * @return ob der gegebene String teil der übergebenen Argumente ist
	 */
	public boolean argsContains(String s);

	/**
	 * Beendet das TotalBackup.
	 */
	public void quitTotalBackup();

	/**
	 * Serialisiert die Programm-Einstellungen (Backup-Tasks).
	 */
	public void saveProperties();
}
