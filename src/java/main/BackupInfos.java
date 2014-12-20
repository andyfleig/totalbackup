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
	
	//TODO: JavaDoc...
	public long getNumberOfDirectories() {
		return numberOfDirectories;
	}
	
	public void increaseNumberOfDirectories() {
		numberOfDirectories++;
	}
	
	public long getNumberOfFiles() {
		return numberOfFiles;
	}
	
	public void increaseNumberOfFiles() {
		numberOfFiles++;
	}
	
	public double getSizeToCopy() {
		return sizeToCopy;
	}
	
	public void increaseSizeToCopyBy(double sizeToIncreaseBy) {
		sizeToCopy += sizeToIncreaseBy;
	}
	
	public double getSizeToLink() {
		return sizeToLink;
	}
	
	public void increaseSizeToLinkBy(double sizeToIncreaseBy) {
		sizeToLink += sizeToIncreaseBy;
	}
	
	public void clear() {
		numberOfDirectories = 0;
		numberOfFiles = 0;
		sizeToCopy = 0;
		sizeToLink = 0;
	}
}
