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

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

/**
 * ListCells for the list of BackupTasks within the FxMainframe.
 *
 * @author Andreas Fleig
 */
public class BackupTaskListCell extends ListCell<MainframeCellContent> {
	private GridPane gridPane = new GridPane();
	private Label taskName = new Label();
	private Label taskStatus = new Label();
	private Label taskNextExecution = new Label();

	public BackupTaskListCell() {
		gridPane.add(taskName, 0, 0);
		gridPane.add(taskStatus, 0, 1);
		gridPane.add(taskNextExecution, 0, 2);
	}

	@Override
	public void updateItem(MainframeCellContent content, boolean empty) {
		super.updateItem(content, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setText(null);
			taskName.setText(content.getTaskName());
			taskName.setStyle("-fx-font-weight: bold");
			taskStatus.setText(content.getTaskStaus());
			taskStatus.setTextFill(content.getStatusColor());
			taskNextExecution.setText(content.getTaskNextExecutionStatus());
			setGraphic(gridPane);
		}
	}
}
