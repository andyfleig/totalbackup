/*
 * Copyright 2014 - 2019 Andreas Fleig (github AT andyfleig DOT de)
 * 
 * All rights reserved.
 * 
 * This file is part of TotalBackup.
 *
 * TotalBackup is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TotalBackup is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TotalBackup.  If not, see <http://www.gnu.org/licenses/>.
 */
package data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Quellobjekt zur Haltung des Quellpfades und der dazugehörigen Filter.
 *
 * @author Andreas Fleig
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
	 * @param path Quellpfad
	 */
	public Source(String path) {
		this.path = path;
		filterOfSource = new ArrayList<>();
	}

	/**
	 * Fügt für diese Quelle einen Filter hinzu.
	 *
	 * @param filter hinzuzufügender Filter
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
