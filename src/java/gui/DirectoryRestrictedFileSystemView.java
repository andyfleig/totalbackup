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
package gui;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

/**
 * FileSystemView welcher nur Verzeichnisse anzeigt.
 *
 * @author Andreas Fleig
 */
class DirectoryRestrictedFileSystemView extends FileSystemView {

	private final File[] rootDirectories;

	@Override
	public File getHomeDirectory() {
		return rootDirectories[0];
	}

	DirectoryRestrictedFileSystemView(File rootDirectory) {
		this.rootDirectories = new File[]{rootDirectory};
	}

	DirectoryRestrictedFileSystemView(File[] rootDirectories) {
		this.rootDirectories = rootDirectories;
	}

	@Override
	public File createNewFolder(File containingDir) throws IOException {
		throw new UnsupportedOperationException("Unable to create directory");
	}

	@Override
	public File[] getRoots() {
		return rootDirectories;
	}

	@Override
	public boolean isRoot(File file) {
		for (File root : rootDirectories) {
			if (root.equals(file)) {
				return true;
			}
		}
		return false;
	}
}