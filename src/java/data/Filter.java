package data;

import java.io.Serializable;

public class Filter implements Serializable {
	private static final long serialVersionUID = -1492728068400793184L;
	/**
	 * Pfad der zu filternden Datei oder des zu filternden Verzeichnisses.
	 */
	private String path;
	/**
	 * Filter-Modus 0 = Ausschluss-Filter; 1 = MD5-Filter
	 */
	private int mode;

	/**
	 * Erstellt einen neuen Filter.
	 * 
	 * @param path
	 *            Pfad des Filters
	 * @param mode
	 *            Filter-Modus 0 = Ausschluss-Filter; 1 = MD5-Filter
	 */
	public Filter(String path, int mode) {
		this.path = path;
		this.mode = mode;
	}

	/**
	 * Gibt den Pfad des Filters zurück.
	 * 
	 * @return Pfad des Filters
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Gibt den Filter-Modus des Filters zurück.
	 * 
	 * @return Filter-Mdous (0 = Ausschluss-Filter; 1 = MD5-Filter)
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Setzt den Pfad des Filters.
	 * 
	 * @param path
	 *            zu setzender Pfad
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Setzt den Filter-Modus des Filters.
	 * 
	 * @param mode
	 *            Filter-Mdous (0 = Ausschluss-Filter; 1 = MD5-Filter)
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		if (mode == 0) {
			return "Ex: " + path;
		} else if (mode == 1) {
			return "MD5: " + path;
		}
		return null;
	}
}
