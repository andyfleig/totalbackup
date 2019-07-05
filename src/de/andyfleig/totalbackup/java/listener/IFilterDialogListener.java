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

import data.Filter;

/**
 * Listener of the FilterDialog.
 *
 * @author Andreas Fleig
 */
public interface IFilterDialogListener {
	/**
	 * Adds the given filter.
	 *
	 * @param filter filter to add
	 */
	public void addFilter(Filter filter);

	/**
	 * Removes the given filter.
	 *
	 * @param filter filter to remove
	 */
	public void removeFilter(Filter filter);


	/**
	 * Returns whether the given filter already exists.
	 *
	 * @param filter filter to check
	 */
	public boolean hasFilter(Filter filter);

	/**
	 * Checks whether the given path is a sub-path of the selected source.
	 *
	 * @param path path to check
	 * @return whether path is sub-path of source (true) or not (false)
	 */
	public boolean isUnderSourceRoot(String path);

	/**
	 * Returns the path of the current source.
	 *
	 * @return path of the source
	 */
	public String getSourcePath();
}
