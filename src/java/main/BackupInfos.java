package main;

public class BackupInfos {
	/**
	 * Anzahl der zu sichernden Ordner
	 */
	private long numberOfDirectories = 0;
	/**
	 * Anzahl der zu sichernden Dateien
	 */
	private long numberOfFiles = 0;
	
	/**
	 * Gesamtgröße der zu kopierenden Dateien
	 */
	private double sizeToCopy = 0;
	/**
	 * Gesamtgröße der zu verlinkenden Dateien
	 */
	private double sizeToLink = 0;
	
	/**
	 * Gibt die Anzahl der zu bearbeitenden Ordner zurück.
	 * @return Anzahl der zu bearbeitenden Ordner
	 */
	public long getNumberOfDirectories() {
		return numberOfDirectories;
	}
	/**
	 * Erhöht die Anzahl der zu bearbeitenden Ordner um 1.
	 */
	public void increaseNumberOfDirectories() {
		numberOfDirectories++;
	}
	/**
	 * Gibt die Anzahl der zu bearbeitenden Dateien zurück.
	 * @return Anzahl der zu bearbeitenden Dateien
	 */
	public long getNumberOfFiles() {
		return numberOfFiles;
	}
	/**
	 * Erhöht die Anzahl der zu bearbeitenden Ordner um 1.
	 */
	public void increaseNumberOfFiles() {
		numberOfFiles++;
	}
	
	/**
	 * Gibt die Gesamtgröße der zu kopierenden Dateien zurück.
	 * @return Gesamtgröße der zu kopierenden Dateien
	 */
	public double getSizeToCopy() {
		return sizeToCopy;
	}
	/**
	 * Erhöht die Gesamtgröße der zu kopierenden Dateien um den gegebenen Wert.
	 * @param sizeToIncreaseBy Größe um die der Gesamtwert erhöht wird
	 */
	public void increaseSizeToCopyBy(double sizeToIncreaseBy) {
		sizeToCopy += sizeToIncreaseBy;
	}
	/**
	 * Gibt die Gesamtgröße der zu verlinkenden Dateien zurück.
	 * @return Gesamtgröße der zu verlinkenden Dateien
	 */
	public double getSizeToLink() {
		return sizeToLink;
	}
	/**
	 * Erhöht die Gesamtgröße der zu verlinkenden Dateien um den gegebenen Wert.
	 * @param sizeToIncreaseBy Größe um die der Gesamtwert erhöht wird
	 */
	public void increaseSizeToLinkBy(double sizeToIncreaseBy) {
		sizeToLink += sizeToIncreaseBy;
	}
	
	/**
	 * Löscht alle Backup-Infos.
	 */
	public void clear() {
		numberOfDirectories = 0;
		numberOfFiles = 0;
		sizeToCopy = 0;
		sizeToLink = 0;
	}
}
