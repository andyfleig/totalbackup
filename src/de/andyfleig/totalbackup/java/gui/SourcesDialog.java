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

import data.Filter;
import data.Source;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import listener.IFilterDialogListener;
import listener.ISourcesDialogListener;

/**
 * Dialog to configure a certain source of a BackupTask.
 *
 * @author Andreas Fleig
 */
public class SourcesDialog implements Initializable {
	private static Stage stage;
	private ISourcesDialogListener listener;

	@FXML
	private TextField tf_sourcePath;
	@FXML
	private ListView<Filter> lv_filters;
	final ObservableList<Filter> ol_filters = FXCollections.observableArrayList();

	/**
	 * Set stage of this SourcesDialog to the given stage.
	 *
	 * @param stage stage to set
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		lv_filters.setItems(ol_filters);
		lv_filters.setCellFactory(new Callback<ListView<Filter>, ListCell<Filter>>() {
			@Override
			public ListCell<Filter> call(ListView<Filter> sourceFilterListView) {
				return new FilterListCell();
			}
		});
	}

	/**
	 * Initializes the SourcesDialog with the given listener.
	 *
	 * @param listener corresponding instance of ISourcesDialogListener
	 */
	public void init(ISourcesDialogListener listener) {
		this.listener = listener;
	}

	@FXML
	public void addSourceAction() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("choose SourcePath");
		File sourcePath = dirChooser.showDialog(stage);
		if (sourcePath == null) {
			return;
		}
		tf_sourcePath.setText(sourcePath.getAbsolutePath());
	}

	@FXML
	public void addFilterAction() {
		startFilterDialog(new Filter(), false);
	}

	@FXML
	public void editFilterAction() {
		int selectedIndex = lv_filters.getSelectionModel().getSelectedIndex();
		if (selectedIndex == -1) {
			return;
		}
		Filter filter = lv_filters.getSelectionModel().getSelectedItem();
		startFilterDialog(new Filter(filter.getPath(), filter.getMode()), true);
	}

	@FXML
	public void removeFilterAction() {
		int selectedIndex = lv_filters.getSelectionModel().getSelectedIndex();
		if (selectedIndex == -1) {
			return;
		}
		ol_filters.remove(selectedIndex);
	}

	@FXML
	public void cancelAction() {
		stage.close();
	}

	@FXML
	public void okAction() {
		// check source-path validity
		File file = new File(tf_sourcePath.getText());
		if (!file.exists()) {
			// show error message
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid source path.");
			alert.setContentText("The selected source is not a valid path.");
			alert.showAndWait();
			return;
		}
		// check whether the selected source is already defined as source
		if (listener.isAlreadyCoveredByExistingSource(tf_sourcePath.getText())) {
			// show error message
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Same source.");
			alert.setContentText("The selected source is already defined as source.");

			Optional<ButtonType> result = alert.showAndWait();
			return;
		}

		Source source = new Source(tf_sourcePath.getText());
		Iterator itr = ol_filters.iterator();
		while (itr.hasNext()) {
			Filter currFilter = (Filter) itr.next();
			source.addFilter(new Filter(currFilter.getPath(), currFilter.getMode()));
		}
		listener.addSource(source);
		stage.close();
	}

	public void setPath(String path) {
		tf_sourcePath.setText(path);
	}

	private void startFilterDialog(Filter filter, boolean inEditMode) {
		final Stage filterDialogStage = new Stage(StageStyle.UTILITY);
		filterDialogStage.initModality(Modality.APPLICATION_MODAL);
		try {
			FXMLLoader loader = new FXMLLoader((getClass().getResource("FilterDialog.fxml")));
			Scene scene = new Scene(loader.load());
			FilterDialog filterDialog = loader.getController();
			filterDialog.init(new IFilterDialogListener() {
				@Override
				public void addFilter(Filter filter) {
					ol_filters.add(filter);
				}

				@Override
				public void removeFilter(Filter filter) {
					ol_filters.remove(filter);
				}

				@Override
				public boolean hasFilter(Filter filter) {
					if (ol_filters.contains(filter)) {
						return true;
					}
					return false;
				}

				@Override
				public boolean isUnderSourceRoot(String path) {
					if (path.startsWith(tf_sourcePath.getText())) {
						return true;
					}
					return false;
				}

				@Override
				public String getSourcePath() {
					return tf_sourcePath.getText();
				}
			}, filter, inEditMode);
			filterDialogStage.setScene(scene);
			filterDialog.setStage(filterDialogStage);

			filterDialogStage.showAndWait();
		} catch (IOException e) {
			System.err.println("IOException while starting FilterDialog: " + e.toString());
		}
	}
}
