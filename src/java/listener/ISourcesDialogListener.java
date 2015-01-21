package listener;

import data.Source;

public interface ISourcesDialogListener {
	/**
	 * Prüft für den gegebenen String ob dieser bereits Quellpfad ist.
	 * 
	 * @param path
	 *            zu prüfender Quellpfad
	 * @return ob der Pfad bereits Quellpfad ist
	 */
	public boolean isAlreadySourcePath(String path);

	/**
	 * Fügt eine Quelle hinzu.
	 * 
	 * @param source
	 *            hinzuzufügende Quelle.
	 */
	public void addSource(Source source);

	/**
	 * Sucht nach einer Quelle mit dem gegebenen Pfad. Wird diese gefunden wird
	 * sie gelöscht.
	 * 
	 * @param path
	 *            zu löschende Quelle
	 */
	public void deleteSource(String path);
}
