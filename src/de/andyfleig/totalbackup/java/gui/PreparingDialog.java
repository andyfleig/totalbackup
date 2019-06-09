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

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import listener.IPreparingDialogListener;
import main.Backupable;

/**
 * Small dialog indicating that the preparation of the backup is running with the option of canceling the backup.
 *
 * @author Andreas Fleig
 */
public class PreparingDialog implements Initializable {

	static Stage stage;
	private IPreparingDialogListener listener;
	String taskName;
	Backupable backup;

	public PreparingDialog() {

	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	public void init(IPreparingDialogListener listener, String taskName, Backupable backup) {
		this.listener = listener;
		this.taskName = taskName;
		this.backup = backup;
	}

	@FXML
	private void cancelAction() {
		listener.cancelBackup(taskName);
		backup.cancel();
	}
}
