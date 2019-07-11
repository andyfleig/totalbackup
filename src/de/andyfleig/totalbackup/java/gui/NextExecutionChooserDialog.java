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

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import listener.INextExecutionChooserDialogListener;

import java.net.URL;
import java.util.ResourceBundle;

public class NextExecutionChooserDialog implements Initializable {
	private static Stage stage;

	private INextExecutionChooserDialogListener listener;

	@FXML
	private RadioButton rb_skip;
	@FXML
	private RadioButton rb_postpone;
	@FXML
	private ComboBox cb_postpone_value;

	private String taskName;


	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		cb_postpone_value.setItems(
				FXCollections.observableArrayList("5min", "10min", "15min", "30min", "1h", "2h", "6h", "12h", "24h"));
		cb_postpone_value.setValue("5min");
		rb_skip.setSelected(true);
	}

	/**
	 * Set stage of this NextExecutionChooserDialog to the given stage.
	 *
	 * @param stage stage to set
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Initialize the NextExecutionChooserDialog with the corresponding NextExecutionChooserListener.
	 */
	public void init(INextExecutionChooserDialogListener listener, String taskName) {
		this.listener = listener;
		this.taskName = taskName;
	}

	@FXML
	private void okAction() {
		if (rb_skip.isSelected()) {
			listener.skipNextExecution(taskName);
		} else {
			// "postpone" is selected
			int delayInMinutes;
			String delayValue = cb_postpone_value.getValue().toString();
			switch (delayValue) {
				case "10min":
					delayInMinutes = 10;
					break;
				case "15min":
					delayInMinutes = 15;
					break;
				case "30min":
					delayInMinutes = 30;
					break;
				case "1h":
					delayInMinutes = 1 * 60;
					break;
				case "2h":
					delayInMinutes = 2 * 60;
					break;
				case "6h":
					delayInMinutes = 6 * 60;
					break;
				case "12h":
					delayInMinutes = 12 * 60;
					break;
				case "24h":
					delayInMinutes = 24 * 60;
					break;
				default:
					delayInMinutes = 5;
			}
			listener.postponeExecutionBy(taskName, delayInMinutes);
		}
		stage.close();
	}

	@FXML
	private void cancelAction() {
		stage.close();
	}
}
