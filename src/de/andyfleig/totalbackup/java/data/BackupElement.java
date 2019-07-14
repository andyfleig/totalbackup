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

/**
 * Element like dictionary or file to be backed up as part of a certain BackupTask.
 *
 * @author Andreas Fleig
 */
public class BackupElement {
	/**
	 * Current path of the file to back up.
	 */
	private String sourcePath;
	/**
	 * Path of where to back up the file to.
	 */
	private String destPath;
	/**
	 * Whether the BackupElement is a dictionary or not.
	 */
	private boolean isDirectory;
	/**
	 * Whether the element has to be linked (true) or copied (false).
	 */
	private boolean toLink;

	/**
	 * Creates a new BackupElement.
	 *
	 * @param sourcePath  path of the file to backup
	 * @param destPath    where to backup to
	 * @param isDirectory whether the element is a directory or not
	 * @param toLink      whether it has to be linked or copied (only valid for non-directory elements)
	 */
	public BackupElement(String sourcePath, String destPath, boolean isDirectory, boolean toLink) {
		this.sourcePath = sourcePath;
		this.destPath = destPath;
		this.isDirectory = isDirectory;
		if (toLink) {
			assert !isDirectory;
		}
		this.toLink = toLink;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public String getDestPath() {
		return destPath;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public boolean toLink() {
		return toLink;
	}
}
