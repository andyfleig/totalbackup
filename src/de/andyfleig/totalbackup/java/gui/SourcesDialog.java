/*
 * Copyright 2014 - 2016 Andreas Fleig (andy DOT fleig AT gmail DOT com)
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
 * Dialog zum Festlegen und Bearbeiten der Quellen.
 *
 * @author Andreas Fleig
 */
public class SourcesDialog implements Initializable {
	private static Stage stage;
	private ISourcesDialogListener listener;

	@FXML
	private TextField tf_sourcePath;
	@FXML
	private ListView<SourceFilterCellContent> lv_filters;
	final ObservableList<SourceFilterCellContent> ol_filters = FXCollections.observableArrayList();


	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		lv_filters.setItems(ol_filters);
		lv_filters.setCellFactory(new Callback<ListView<SourceFilterCellContent>, ListCell<SourceFilterCellContent>>() {
			@Override
			public ListCell<SourceFilterCellContent> call(
					ListView<SourceFilterCellContent> sourceFilterCellContentListView) {
				return new FilterListCell();
			}
		});
	}

	public void init(ISourcesDialogListener listener) {
		this.listener = listener;
	}

	@FXML
	public void addSourcePathAction() {
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
		startFilterDialog("", 0);
	}

	@FXML
	public void editFilterAction() {
		int selectedIndex = lv_filters.getSelectionModel().getSelectedIndex();
		if (selectedIndex == -1) {
			return;
		}
		SourceFilterCellContent selectedCell = lv_filters.getSelectionModel().getSelectedItem();
		startFilterDialog(selectedCell.getFilterPath(), selectedCell.getFilerMode());
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
			SourceFilterCellContent currentCell = (SourceFilterCellContent)itr.next();
			source.addFilter(new Filter(currentCell.getFilterPath(), currentCell.getFilerMode()));
		}
		listener.addSource(source);
		stage.close();
	}

	public void setPath(String path) {
		tf_sourcePath.setText(path);
	}

	private void startFilterDialog(String initPath, int initMode) {
		final Stage filterDialogStage = new Stage(StageStyle.UTILITY);
		filterDialogStage.initModality(Modality.APPLICATION_MODAL);
		try {
			FXMLLoader loader = new FXMLLoader((getClass().getResource("FilterDialog.fxml")));
			Scene scene = new Scene(loader.load());
			FilterDialog filterDialog = loader.getController();
			filterDialog.init(new IFilterDialogListener() {
				@Override
				public void addFilter(String path, int mode) {
					ol_filters.add(new SourceFilterCellContent(path, mode));
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
			});

			filterDialogStage.setScene(scene);
			filterDialog.setStage(filterDialogStage);

			if (initPath.equals("")) {
				filterDialog.setInitPath(initPath);
			}
			filterDialog.setInitMode(initMode);

			filterDialogStage.showAndWait();
		} catch (IOException e) {
			System.err.println("IOException while starting FilterDialog: " + e.toString());
		}
	}
}
