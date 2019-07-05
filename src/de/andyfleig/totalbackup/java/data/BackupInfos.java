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
 * Contains information about the BackupTask to run like size or number of files and dictionaries.
 *
 * @author Andreas Fleig
 */
public class BackupInfos {
	/**
	 * Number of dictionaries to back up.
	 */
	private long numberOfDirectories = 0;
	/**
	 * Number of files to copy.
	 */
	private long numberOfFilesToCopy = 0;
	/**
	 * Number of files to link.
	 */
	private long numberOfFilesToLink = 0;

	/**
	 * Total size of files to copy in Bytes.
	 */
	private double sizeToCopy = 0;
	/**
	 * Total size of files to link in Bytes.
	 */
	private double sizeToLink = 0;

	/**
	 * Returns the number of dictionaries to back up.
	 *
	 * @return number of dictionaries
	 */
	public long getNumberOfDirectories() {

		return numberOfDirectories;
	}

	/**
	 * Increases the number of dictionaries by 1.
	 */
	public void increaseNumberOfDirectories() {

		numberOfDirectories++;
	}

	/**
	 * Returns the number of files to copy.
	 *
	 * @return number of files to copy
	 */
	public long getNumberOfFilesToCopy() {

		return numberOfFilesToCopy;
	}

	/**
	 * Increases the number of files to copy by 1.
	 */
	public void increaseNumberOfFilesToCopy() {

		numberOfFilesToCopy++;
	}

	/**
	 * Returns the number of files to link.
	 *
	 * @return number of files to link
	 */
	public long getNumberOfFilesToLink() {

		return numberOfFilesToLink;
	}

	/**
	 * Increases the number of files to link by 1.
	 */
	public void increaseNumberOfFilesToLink() {

		numberOfFilesToLink++;
	}

	/**
	 * Returns the total size of all files to copy.
	 *
	 * @return total size of files to copy in Bytes
	 */
	public double getSizeToCopy() {

		return sizeToCopy;
	}

	/**
	 * Increases the size of files to copy by the given amount of Bytes.
	 *
	 * @param sizeToIncreaseBy amount of Bytes to increase the total size by
	 */
	public void increaseSizeToCopyBy(double sizeToIncreaseBy) {
		sizeToCopy += sizeToIncreaseBy;
	}

	/**
	 * Returns the total size of all files to link.
	 *
	 * @return total size of files to link in Bytes
	 */
	public double getSizeToLink() {
		return sizeToLink;
	}

	/**
	 * Increases the size of files to link by the given amount of Bytes.
	 *
	 * @param sizeToIncreaseBy amount of Bytes to increase the total size by
	 */
	public void increaseSizeToLinkBy(double sizeToIncreaseBy) {
		sizeToLink += sizeToIncreaseBy;
	}
}
