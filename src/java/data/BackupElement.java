package data;

public class BackupElement {
	/**
	 * Legt den Quell-Pfad des Elements fest
	 */
	private String sourcePath;
	/**
	 * Legt den Ziel-Pfad des Elements fest
	 */
	private String destPath;
	/**
	 * Legt fest ob das Element ein Ordner ist
	 */
	private boolean isDirectory;
	/**
	 * Legt fest ob das Element verlinkt (true) oder kopiert (false) werden soll
	 */
	private boolean toLink = false;
	
	/**
	 * Erzeugt ein neues Backup-Element.
	 * @param path Pfad des Elements
	 * @param isDirectory ob das Element ein Ordner ist
	 * @param toLink ob das Element kopiert oder verlinkt werden soll (Achtung: Ordner können nicht verlinkt werden)
	 */
	public BackupElement(String sourcePath, String destPath, boolean isDirectory, boolean toLink) {
		this.sourcePath = sourcePath;
		this.destPath = destPath;
		this.isDirectory = isDirectory;
		if (!isDirectory) {
			this.toLink = toLink;
		}
	}
	
	public String getSourcePath() {
		return sourcePath;
	}
	
	public String getDestPath() {
		return destPath;
	}
	
	public boolean isDirectory() {
		return isDirectory;
	}
	
	public boolean toLink() {
		return toLink;
	}
}
