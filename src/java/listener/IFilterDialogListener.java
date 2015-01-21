package listener;

import java.io.File;

public interface IFilterDialogListener {
	/**
	 * Fügt einen neuen Filter hinzu.
	 * 
	 * @param path
	 *            Pfad des Filters
	 * @param mode
	 *            Filter-Modus 0 = Ausschluss-Filter; 1 = MD5-Filter
	 */
	public void addFilter(String path, int mode);

	/**
	 * Prüft ob der gegebene Pfad unter dem Rootpfad der gewählten Quelle ist.
	 * 
	 * @param path
	 *            zu prüfender Pfad
	 * @return ob der gegebene Pfad unter dem Rootpfad der Quelle ist
	 */
	public boolean isUnderSourceRoot(String path);

	/**
	 * Gibt die Quelldatei zurück.
	 * 
	 * @return Quelldatei
	 */
	public File getSourceFile();

	/**
	 * Durchsucht die Liste der Filter nach dem gegebenen Pfad. Wird ein Filter
	 * mit diesem Pfad gefunden wird dieser gelöscht.
	 * 
	 * @param path
	 *            zu löschender Filterpfad
	 */
	public void deleteFilter(String path);
}
