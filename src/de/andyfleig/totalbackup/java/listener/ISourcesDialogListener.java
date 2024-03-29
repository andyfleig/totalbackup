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
package listener;

import data.Source;

/**
 * Listener of the SourcesDialog.
 *
 * @author Andreas Fleig
 */
public interface ISourcesDialogListener {
	/**
	 * Checks whether the given source-path is already covered by the specified sources. An existing source covers the
	 * new one if they are either equal or the new one is a sub-path of an existing one.
	 *
	 * @param path path to check
	 * @return whether the path is already covered
	 */
	public boolean isAlreadyCoveredByExistingSource(String path);

	/**
	 * Adds the given source to the list of sources of this BackupTask.
	 *
	 * @param source source to add
	 */
	public void addSource(Source source);

}
