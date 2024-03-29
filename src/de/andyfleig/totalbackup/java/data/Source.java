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
 * Object containing information of a certain source of a BackupTask.
 *
 * @author Andreas Fleig
 */
public class Source implements Serializable {
	/**
	 * Version number for serialization.
	 */
	private static final long serialVersionUID = 6082953307431457183L;
	/**
	 * Path of the source
	 */
	private String path;
	/**
	 * LList of filters
	 */
	private ArrayList<Filter> filters;

	/**
	 * Creates a new Source object from the given path.
	 *
	 * @param path given source path
	 */
	public Source(String path) {
		this.path = path;
		filters = new ArrayList<>();
	}

	/**
	 * Adds a filter to the source.
	 *
	 * @param filter filter object to add
	 */
	public void addFilter(Filter filter) {
		filters.add(filter);
	}

	/**
	 * Returns all filters of this source.
	 *
	 * @return list of filters
	 */
	public ArrayList<Filter> getFilters() {
		return filters;
	}

	/**
	 * Returns the path of the source.
	 *
	 * @return source path
	 */
	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return path;
	}
}
