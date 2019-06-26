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
import java.util.ResourceBundle;
import java.io.File;

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
 * Dialog zum Erstellen und Bearbeiten eines Filters.
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

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Initialize the FilterDialog with the corresponding FilterDialogListener and Filter.
	 * @param filter corresponding Filter
	 */
	public void init(IFilterDialogListener listener, Filter filter, boolean inEditMode) {
		this.listener = listener;
		this.filter = filter;
		this.inEditMode = inEditMode;
		this.tf_filterPath.setText(filter.getPath());
		if (filter.getMode() == 1)  {
			rb_md5Filter.setSelected(true);
		}
	}

	public void setInitPath(String initPath) {
		tf_filterPath.setText(initPath);
	}

	public void setInitMode(int mode) {
		if (mode == 1) {
			rb_md5Filter.setSelected(true);
		}
	}

	/**
	 * Legt fest, ob gerade ein existierender Filter bearbeitet, oder ein neuer erzeugt wird.
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
			// show error message
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid filter.");
			alert.setContentText("The following filter path is not valid: " + tf_filterPath.getText());
			alert.showAndWait();
			return;
		}
		Filter newFilter = new Filter();

		// filterMode 0 (default) is exclusion-filter, 1 is md5-filter
		int filterMode = 0;
		if (rb_md5Filter.isSelected()) {
			newFilter.setMode(1);
		}
		if (!isUnderSourceRoot(filterPath.getAbsolutePath())) {
			// show error message
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid Filter.");
			alert.setContentText("The filter path has to be inside the specified source!");
			alert.setResizable(true);
			alert.showAndWait();
			return;
		}
		newFilter.setPath(filterPath.getAbsolutePath());
		if (inEditMode) {
			// delete old filter first
			listener.removeFilter(filter);
		}
		// check whether same filter already exists:
		if (listener.hasFilter(newFilter)) {
			// show error message
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setResizable(true);
			alert.setTitle("Error");
			alert.setHeaderText("Filter already exists.");
			alert.setContentText("The given filter already exists for this source path.");
			alert.showAndWait();
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
	 * Prüft ob der gegebene Pfad unter dem Rootpfad der gewählten Quelle ist.
	 *
	 * @param path zu prüfender Pfad
	 * @return ob der gegebene Pfad unter dem Rootpfad der Quelle ist
	 */
	private boolean isUnderSourceRoot(String path) {
		return listener.isUnderSourceRoot(path);
	}
}
