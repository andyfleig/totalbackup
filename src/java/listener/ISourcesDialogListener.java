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

import data.Source;

public interface ISourcesDialogListener {
	/**
	 * Prüft für den gegebenen String ob dieser bereits Quellpfad ist.
	 *
	 * @param path zu prüfender Quellpfad
	 * @return ob der Pfad bereits Quellpfad ist
	 */
	public boolean isAlreadySourcePath(String path);

	/**
	 * Fügt eine Quelle hinzu.
	 *
	 * @param source hinzuzufügende Quelle.
	 */
	public void addSource(Source source);

	/**
	 * Sucht nach einer Quelle mit dem gegebenen Pfad. Wird diese gefunden wird
	 * sie gelöscht.
	 *
	 * @param path zu löschende Quelle
	 */
	public void deleteSource(String path);
}
