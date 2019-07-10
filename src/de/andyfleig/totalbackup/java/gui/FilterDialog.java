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
package gui;

import java.net.URL;
import java.io.File;
import java.util.ResourceBundle;

import data.Filter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import listener.IFilterDialogListener;

/**
 * Dialog for creating and editing of Source-filters.
 *
 * @author Andreas Fleig
 */
public class FilterDialog implements Initializable {
	private static Stage stage;

	private IFilterDialogListener listener;
	private boolean inEditMode;

	@FXML
	private TextField tf_filterPath;
	@FXML
	private RadioButton rb_exclusionFilter;
	@FXML
	private RadioButton rb_md5Filter;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	/**
	 * Set stage of this FilterDialog to the given stage.
	 *
	 * @param stage stage to set
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Initialize the FilterDialog with the corresponding FilterDialogListener and filter.
	 *
	 * @param filter corresponding Filter
	 */
	public void init(IFilterDialogListener listener, Filter filter, boolean inEditMode) {
		this.listener = listener;
		this.filter = filter;
		this.inEditMode = inEditMode;
		this.tf_filterPath.setText(filter.getPath());
		if (filter.getMode() == 1) {
			rb_md5Filter.setSelected(true);
		}
	}

	/**
	 * Sets the initial source path of this FilterDialog.
	 *
	 * @param initPath initial source path
	 */
	public void setInitPath(String initPath) {
		tf_filterPath.setText(initPath);
	}

	/**
	 * Current filter of this FilterDialog instance.
	 */
	private Filter filter;

	@FXML
	private void addFilterAction() {
		// ToDo: DirectoryChooser only allows to choose dirs and FileChooser only allows to choose files
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setInitialDirectory(new File(listener.getSourcePath()));
		dirChooser.setTitle("choose File or Directory to Filter");
		File filterPath = dirChooser.showDialog(stage);
		if (filterPath == null) {
			return;
		}
		tf_filterPath.setText(filterPath.getAbsolutePath());
	}

	@FXML
	private void okAction() {
		File filterPath = new File(tf_filterPath.getText());
		if (!filterPath.exists()) {
			GuiHelper.showErrorWindow("Invalid filter.",
					"The following filter path is not valid: " + tf_filterPath.getText());
			return;
		}
		Filter newFilter = new Filter();

		// filterMode 0 (default) is exclusion-filter, 1 is md5-filter
		int filterMode = 0;
		if (rb_md5Filter.isSelected()) {
			newFilter.setMode(1);
		}
		if (!isUnderSourceRoot(filterPath.getAbsolutePath())) {
			GuiHelper.showErrorWindow("Invalid Filter.", "The filter path has to be inside the specified source!");
			return;
		}
		newFilter.setPath(filterPath.getAbsolutePath());
		if (inEditMode) {
			// delete old filter first
			listener.removeFilter(filter);
		}
		// check whether same filter already exists:
		if (listener.hasFilter(newFilter)) {
			GuiHelper.showErrorWindow("Filter already exists.",
					"The given filter already exists for this source path.");
			return;
		}
		listener.addFilter(newFilter);
		stage.close();
	}

	@FXML
	private void cancelAction() {
		stage.close();
	}

	/**
	 * Checks whether the given path is a sub-path of the selected source.
	 *
	 * @param path path to check
	 * @return whether path is sub-path of source (true) or not (false)
	 */
	private boolean isUnderSourceRoot(String path) {
		return listener.isUnderSourceRoot(path);
	}
}
