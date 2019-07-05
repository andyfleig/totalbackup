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
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import data.BackupTask;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import listener.ISummaryDialogListener;

/**
 * Dialog showing an overview over the results of the Preparation of a certain BackupTask.
 *
 * @author Andreas Fleig
 */
public class SummaryDialog implements Initializable {

	private ISummaryDialogListener listener;
	private boolean backupCanceled;
	private boolean backupIsNotFinished;
	private BackupTask task;

	static Stage stage;

	@FXML
	private Label label_taskNameDyn;
	@FXML
	private Label label_numberOfDirsDyn;
	@FXML
	private Label label_numberToCopyDyn;
	@FXML
	private Label label_numberToLinkDyn;
	@FXML
	private Label label_sizeToCopyDyn;
	@FXML
	private Label label_sizeToLinkDyn;

	/**
	 * Set stage of this SummaryDialog to the given stage.
	 *
	 * @param stage stage to set
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	/**
	 * Initializes the SummaryDialog with the corresponding listener and its content.
	 *
	 * @param listener            corresponding instance of ISummaryDialogListener
	 * @param task                corresponding BackupTask
	 * @param numberOfFilesToCopy number of files to be copied within the backup
	 * @param numberOfFilesToLink number of files to be linked within the backup
	 * @param numberOfDirectories number of directories to be copied within the backup
	 * @param sizeToCopy          total size of the files (in Bytes) to copy within the backup
	 * @param sizeToLink          total size of files (in Bytes) to linke within the backup
	 */
	public void init(ISummaryDialogListener listener, BackupTask task, long numberOfFilesToCopy,
			long numberOfFilesToLink, long numberOfDirectories, double sizeToCopy, double sizeToLink) {
		this.listener = listener;
		this.task = task;
		String taskname = task.getTaskName();
		// Inhalte hinzufügen:
		label_taskNameDyn.setText(taskname);
		label_numberToCopyDyn.setText(String.valueOf(numberOfFilesToCopy));
		label_numberToLinkDyn.setText(String.valueOf(numberOfFilesToLink));
		label_numberOfDirsDyn.setText(String.valueOf(numberOfDirectories));

		// Größe der zu kopierenden Dateien:
		label_sizeToCopyDyn.setText(formatSize(sizeToCopy));
		// Größe der zu verlinkenden Dateien:
		label_sizeToLinkDyn.setText(formatSize(sizeToLink));
	}

	/**
	 * Correclty formats the given size to the printed text for the summary.
	 *
	 * @param size size in Bytes
	 * @return formatted string of the size
	 */
	private String formatSize(double size) {
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		if (size < 1000) {
			return String.valueOf(decimalFormat.format(size)) + "Byte";
		} else if (size < 1000000) {
			return String.valueOf(decimalFormat.format(size / 1000)) + "kB";
		} else if (size < 1000000000) {
			return String.valueOf(decimalFormat.format(size / 1000000)) + "MB";
		} else {
			return String.valueOf(decimalFormat.format(size / 1000000000)) + "GB";
		}
	}

	@FXML
	public void cancelAction() {
		setStatusToCanceled(task);
		listener.taskFinished(task, true);
		deleteEmptyBackupFolders(task);
		synchronized (task) {
			task.notify();
			stage.close();
		}
	}

	@FXML
	public void okAction() {
		backupIsNotFinished = true;
		synchronized (task) {
			task.notify();
			stage.close();
		}
	}

	/**
	 * Deletes empty backup folders within the destination path.
	 *
	 * @param task corresponding BackupTask
	 */
	private void deleteEmptyBackupFolders(BackupTask task) {
		listener.deleteEmptyBackupFolders(task);
	}

	/**
	 * Sets the status of the given BackupTask to canceled.
	 *
	 * @param task corresponding BackupTask
	 */
	private void setStatusToCanceled(BackupTask task) {
		listener.setStatusToCanceled(task);
	}
}
