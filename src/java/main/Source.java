package main;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Quellobjekt zur Haltung des Quellpfades und der dazughörigen Filter.
 * 
 * @author andy
 *
 */
public class Source implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6082953307431457183L;
	/**
	 * Quellpfad
	 */
	private String path;
	/**
	 * Liste von Pfaden welche vom Backup ausgeschlossen werden sollen.
	 */
	private ArrayList<String> dirFilter;

	/**
	 * Erstellt ein neues Quellobjekt.
	 * 
	 * @param path
	 *            Quellpfad
	 */
	public Source(String path) {
		this.path = path;
	}

	/**
	 * Fügt für diese Quelle einen Verzeichnisfilter hinzu.
	 * 
	 * @param filter
	 *            hinzuzufügender Filter
	 */
	public void addFilter(String filter) {
		dirFilter.add(filter);
	}

	/**
	 * Gibt die Liste aller Filter für diese Quelle zurück.
	 * 
	 * @return Liste aller Filter für diese Quelle
	 */
	public ArrayList<String> getFilter() {
		return dirFilter;
	}

	/**
	 * Gibt den Quellpfad dieser Quelle zurück.
	 * 
	 * @return Quellpfad dieser Quelle
	 */
	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return path;
	}
}
