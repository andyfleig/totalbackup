package data;

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
	private ArrayList<Filter> filterOfSource;

	/**
	 * Erstellt ein neues Quellobjekt.
	 * 
	 * @param path
	 *            Quellpfad
	 */
	public Source(String path) {
		this.path = path;
		filterOfSource = new ArrayList<Filter>();
	}

	/**
	 * Fügt für diese Quelle einen Verzeichnisfilter hinzu.
	 * 
	 * @param filter
	 *            hinzuzufügender Filter
	 */
	public void addFilter(Filter filter) {
		filterOfSource.add(filter);
	}

	/**
	 * Gibt die Liste aller Filter für diese Quelle zurück.
	 * 
	 * @return Liste aller Filter für diese Quelle
	 */
	public ArrayList<Filter> getFilter() {
		return filterOfSource;
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
