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
 * Dialog welcher nach der Preparation die Zusammenfassung des bevorstehenden Backups anzeigt.
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

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

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
	 * Formatiert die gegeben Größe für die Anzeige im Summary-Dialog.
	 *
	 * @param size Größe (als double)
	 * @return Formatierter String der Größe
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
		outprintBackupCanceled(task);
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
	 * Löscht alle leeren Backup-Ordner eines Backuptasks (im Zielverzeichnis).
	 *
	 * @param task entsprechender BackupTask
	 */
	private void deleteEmptyBackupFolders(BackupTask task) {
		listener.deleteEmptyBackupFolders(task);
	}

	private void outprintBackupCanceled(BackupTask task) {
		listener.outprintBackupCanceled(task);
	}
}
