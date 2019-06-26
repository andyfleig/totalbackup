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

public class Filter implements Serializable {
	private static final long serialVersionUID = -1492728068400793184L;
	/**
	 * Pfad der zu filternden Datei oder des zu filternden Verzeichnisses.
	 */
	private String path;
	/**
	 * mode of filter: 0 = exclusion-filter (default); 1 = MD5-filter
	 */
	private int mode = 0;

	/**
	 * Creates a new Filter with the given path and mode.
	 *
	 * @param path path of the filter
	 * @param mode mode of the filter 0 = exclusion-filter; 1 = MD5-filter
	 */
	public Filter(String path, int mode) {
		this.path = path;
		this.mode = mode;
	}

	/**
	 * Creates a new empty Filter.
	 */
	public Filter() {

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
	 * @return Filter-Modus (0 = Ausschluss-Filter; 1 = MD5-Filter)
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Setzt den Pfad des Filters.
	 *
	 * @param path zu setzender Pfad
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Setzt den Filter-Modus des Filters.
	 *
	 * @param mode Filter-Modus (0 = Ausschluss-Filter; 1 = MD5-Filter)
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

	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof Filter) {
			if (this.mode == ((Filter) object).mode && this.path.equals(((Filter) object).path)) {
				return true;
			}
		}
		return false;
	}
}
