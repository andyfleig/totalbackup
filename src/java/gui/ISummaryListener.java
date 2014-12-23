package gui;

public interface ISummaryListener {

	/**
	 * Startet den Backup-Vorgang.
	 */
	public void startBackup();

	/**
	 * Gibt den Namen des Backup-Tasks zurück.
	 * 
	 * @return Name des Backup-Tasks
	 */
	public String getTaskName();

	/**
	 * Löscht alle Backup-Infos.
	 */
	public void clearBackupInfos();

	/**
	 * Gibt die Anzahl der zu kopierenden Ordner zurück.
	 * 
	 * @return Anzahl der zu kopierenden Ordner
	 */
	public long getNumberOfDirectories();

	/**
	 * Gibt die Anzahl der zu kopierenden Dateien zurück.
	 * 
	 * @return Anzahl der zu kopierenden Dateien
	 */
	public long getNumberOfFiles();

	/**
	 * Gibt die Gesamtgröße der zu kopierenden Dateien zurück.
	 * 
	 * @return Gesamtgröße der zu kopierenden Dateien
	 */
	public double getSizeToCopy();

	/**
	 * Gibt die Gesamtgröße der zu verlinkenden Dateien zurück.
	 * 
	 * @return Gesamtgröße der zu verlinkenden Dateien
	 */
	public double getSizeToLink();

	/**
	 * Löscht alle leeren Backup-Ordner (erzeugt z.B. durch das Abbrechen eines
	 * Backup-Vorgangs nach der Übersicht)
	 */
	public void deleteEmptyBackupFolders();
}