package data;

public class BackupInfos {
	/**
	 * Anzahl der zu sichernden Ordner
	 */
	private long numberOfDirectories = 0;
	/**
	 * Anzahl der zu kopierenden Dateien
	 */
	private long numberOfFilesToCopy = 0;
	/**
	 * Anzahl der zu verlinkenden Dateien
	 */
	private long numberOfFilesToLink = 0;

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
	 * 
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
	 * Gibt die Anzahl der zu kopierenden Dateien zurück.
	 * 
	 * @return Anzahl der zu kopierenden Dateien
	 */
	public long getNumberOfFilesToCopy() {
		return numberOfFilesToCopy;
	}

	/**
	 * Erhöht die Anzahl der zu kopierenden Ordner um 1.
	 */
	public void increaseNumberOfFilesToCopy() {
		numberOfFilesToCopy++;
	}

	/**
	 * Gibt die Anzahl der zu verlinkenden Dateien zurück.
	 * 
	 * @return Anzahl der zu verlinkenden Dateien
	 */
	public long getNumberOfFilesToLink() {
		return numberOfFilesToLink;
	}

	/**
	 * Erhöht die Anzahl der zu verlinkenden Ordner um 1.
	 */
	public void increaseNumberOfFilesToLink() {
		numberOfFilesToLink++;
	}

	/**
	 * Gibt die Gesamtgröße der zu kopierenden Dateien zurück.
	 * 
	 * @return Gesamtgröße der zu kopierenden Dateien
	 */
	public double getSizeToCopy() {
		return sizeToCopy;
	}

	/**
	 * Erhöht die Gesamtgröße der zu kopierenden Dateien um den gegebenen Wert.
	 * 
	 * @param sizeToIncreaseBy
	 *            Größe um die der Gesamtwert erhöht wird
	 */
	public void increaseSizeToCopyBy(double sizeToIncreaseBy) {
		sizeToCopy += sizeToIncreaseBy;
	}

	/**
	 * Gibt die Gesamtgröße der zu verlinkenden Dateien zurück.
	 * 
	 * @return Gesamtgröße der zu verlinkenden Dateien
	 */
	public double getSizeToLink() {
		return sizeToLink;
	}

	/**
	 * Erhöht die Gesamtgröße der zu verlinkenden Dateien um den gegebenen Wert.
	 * 
	 * @param sizeToIncreaseBy
	 *            Größe um die der Gesamtwert erhöht wird
	 */
	public void increaseSizeToLinkBy(double sizeToIncreaseBy) {
		sizeToLink += sizeToIncreaseBy;
	}

	/**
	 * Löscht alle Backup-Infos.
	 */
	public void clear() {
		numberOfDirectories = 0;
		numberOfFilesToCopy = 0;
		numberOfFilesToLink = 0;
		sizeToCopy = 0;
		sizeToLink = 0;
	}
}
