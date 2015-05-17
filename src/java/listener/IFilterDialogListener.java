/*
 * Copyright 2014, 2015 Andreas Fleig (andy DOT fleig AT gmail DOT com)
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
package listener;

import java.io.File;

public interface IFilterDialogListener {
	/**
	 * Fügt einen neuen Filter hinzu.
	 * 
	 * @param path
	 *            Pfad des Filters
	 * @param mode
	 *            Filter-Modus (0 = Ausschluss-Filter; 1 = MD5-Filter)
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
