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

import data.BackupTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import listener.IFxMainframeListener;
import listener.INextExecutionChooserDialogListener;
import listener.IPreparingDialogListener;
import listener.ISummaryDialogListener;
import main.Backupable;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * JavaFx based Mainframe of TotalBackup.
 *
 * @author Andreas Fleig
 */
public class FxMainframe extends Application implements Initializable {

	@FXML
	public ListView<MainframeCellContent> lv_backupTasks;
	final ObservableList<MainframeCellContent> ol_backupTasks = FXCollections.observableArrayList();
	@FXML
	public VBox vBoxMain;

	IPreparingDialogListener preparingDialogListener;

	@FXML
	public ContextMenu contextMenu;

	private static IFxMainframeListener mainframeListener;

	public void init(IFxMainframeListener listener) {
		this.mainframeListener = listener;
		// prevent javafx thread to end when mainframe is hidden
		Platform.setImplicitExit(false);
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		lv_backupTasks.setItems(ol_backupTasks);
		lv_backupTasks.setCellFactory(new Callback<ListView<MainframeCellContent>, ListCell<MainframeCellContent>>() {
			@Override
			public ListCell<MainframeCellContent> call(ListView<MainframeCellContent> listView) {
				return new BackupTaskListCell();
			}
		});
	}

	@Override
	public void start(Stage stage) {
		mainframeListener.startMainframe(stage);
	}

	@FXML
	public void quitAction() {
		mainframeListener.quitTotalBackup();
	}

	@FXML
	public void addEntryAction() {
		mainframeListener.startBackupTaskDialog(null);
	}

	@FXML
	public void runNowAction() {
		if (lv_backupTasks.getSelectionModel().getSelectedIndex() == -1) {
			return;
		}
		MainframeCellContent selectedCell = lv_backupTasks.getSelectionModel().getSelectedItem();
		mainframeListener.runBackupTaskWithName(selectedCell.getTaskName());

	}

	@FXML
	public void editEntryAction() {
		if (lv_backupTasks.getSelectionModel().getSelectedIndex() == -1) {
			return;
		}
		String taskName = lv_backupTasks.getSelectionModel().getSelectedItem().getTaskName();
		// prevent from editing when BackupTask is currently running:
		if (mainframeListener.taskIsRunning(taskName)) {
			// show error message
			GuiHelper.showErrorWindow("BackupTask is currently running.",
					"Can not edit the selected BackupTask because it is currently running.");
			return;
		}
		contextMenu.hide();

		mainframeListener.startBackupTaskDialog(taskName);

	}

	@FXML
	public void deleteEntryAction() {
		if (lv_backupTasks.getSelectionModel().getSelectedIndex() == -1) {
			return;
		}
		String taskName = lv_backupTasks.getSelectionModel().getSelectedItem().getTaskName();
		// prevent from deleting when BackupTask is currently running:
		if (mainframeListener.taskIsRunning(taskName)) {
			GuiHelper.showErrorWindow("BackupTask is currently running.",
					"Can not edit the selected BackupTask because it is currently running.");
			return;
		}
		// Ask for confirmation
		Optional<ButtonType> result = GuiHelper.showConfirmationWindows(
				"Do you really want to delete the selected " + "backup-task?",
				"Deleting the backup-task will cancel " + "future executions of this task. \nThis can" +
						" not be undone.");
		if (result.isPresent() && result.get() == ButtonType.YES) {
			mainframeListener.deleteBackupTaskWithName(taskName);
		}
	}

	@FXML
	public void cancelAction() {
		if (lv_backupTasks.getSelectionModel().getSelectedIndex() == -1) {
			return;
		}
		String taskName = lv_backupTasks.getSelectionModel().getSelectedItem().getTaskName();
		// checks whether the selected BackupTask is running
		if (!mainframeListener.taskIsRunning(taskName)) {
			return;
		}
		// ask for confirmation
		Optional<ButtonType> result = GuiHelper.showConfirmationWindows("Cancel task?",
				"Do you really want to cancel" + " the selected backup task?");
		if (result.isPresent() && result.get() == ButtonType.YES) {
			mainframeListener.cancelBackupTaskWithName(taskName);
		}

	}

	@FXML
	public void rescheduleAction() {
		if (lv_backupTasks.getSelectionModel().getSelectedIndex() == -1) {
			return;
		}
		String taskName = lv_backupTasks.getSelectionModel().getSelectedItem().getTaskName();
		// checks whether the selected BackupTask is running
		if (mainframeListener.taskIsRunning(taskName)) {
			return;
		}
		contextMenu.hide();
		startNextExecutionChooserDialog(taskName);
	}

	@FXML
	public void startAboutDialog() {
		mainframeListener.startAboutDialog();
	}

	public void startFxMainframe() {
		launch(null);
	}

	/**
	 * Adds the given name of a BackupTask to the list of BackupTasks.
	 *
	 * @param task BackupTask to add
	 */
	public void addBackupTask(BackupTask task) {
		ol_backupTasks.add(new MainframeCellContent(task.getTaskName(), "Okay", null));

	}

	/**
	 * Removes the BackupTask with the given name from the list of BackupTasks.
	 *
	 * @param taskName name of the BackupTask to remove
	 */
	public void removeBackupTask(String taskName) {
		int indexOfTask = getIndexOfObservableListFromName(taskName);
		if (indexOfTask >= 0) {
			ol_backupTasks.remove(getIndexOfObservableListFromName(taskName));
		}

	}

	/**
	 * Returns the index of the BackupTask with the given name within the list of BackupTasks (ol_backupTasks).
	 *
	 * @param taskName name of the BackupTask
	 * @return index of the BackupTask
	 */
	private int getIndexOfObservableListFromName(String taskName) {
		for (int i = 0; i < ol_backupTasks.size(); i++) {
			if (ol_backupTasks.get(i).getTaskName().equals(taskName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Sets the status for the BackupTask with the given name.
	 *
	 * @param taskName   name of the BackupTask to set status
	 * @param error      whether it is an error status or not
	 * @param taskStatus status to set
	 */
	public void setStatusOfBackupTask(String taskName, boolean error, String taskStatus) {
		Color color;
		if (error) {
			color = Color.web("#ff0000", 0.8);
		} else {
			color = Color.web("#000000", 0.8);
		}
		int indexOfTask = getIndexOfObservableListFromName(taskName);
		if (indexOfTask >= 0) {
			ol_backupTasks.get(indexOfTask).setTaskStaus(taskStatus);
			ol_backupTasks.get(indexOfTask).setStausColor(color);

			lv_backupTasks.fireEvent(new ListView.EditEvent<>(lv_backupTasks, ListView.editCommitEvent(),
					ol_backupTasks.get(indexOfTask), indexOfTask));
		}
	}

	/**
	 * Sets the next-execution part of the status for the BackupTask with the given name.
	 *
	 * @param taskName                    name of the BackupTask to set next-execution part of the status
	 * @param taskNextExecutionTimeStatus next-execution-time status to set (as LocalDateTime)
	 */
	public void setNextExecutionTimeStatusOfBackupTask(String taskName, LocalDateTime taskNextExecutionTimeStatus) {
		int indexOfTask = getIndexOfObservableListFromName(taskName);
		if (indexOfTask >= 0) {
			ol_backupTasks.get(indexOfTask).setTaskNextExecutionTimeStatus(taskNextExecutionTimeStatus);
		}
	}

	/**
	 * Opens the SummaryDialog window giving an overview over some stats of the BackupTask (like number of files to
	 * copy)
	 *
	 * @param task   corresponding BackupTask
	 * @param backup Backup object of the BackupTask
	 */
	public void showSummaryDialog(final BackupTask task, final Backupable backup) {
		// Avoid throwing IllegalStateException by running from a non-JavaFX thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final Stage summaryDialogStage = new Stage(StageStyle.UTILITY);
				summaryDialogStage.initModality(Modality.APPLICATION_MODAL);
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SummaryDialog.fxml"));
					Scene scene = new Scene(loader.load());
					SummaryDialog summaryDialog = loader.getController();
					summaryDialogStage.setScene(scene);

					summaryDialog.setStage(summaryDialogStage);

					summaryDialog.init(new ISummaryDialogListener() {
										   @Override
										   public void deleteEmptyBackupFolders(BackupTask task) {
											   mainframeListener.deleteEmptyBackupFolders(task);

										   }

										   @Override
										   public void setStatusToCanceled(BackupTask task) {
											   setStatusOfBackupTask(task.getTaskName(), false, "Canceled");
										   }

										   @Override
										   public void taskFinished(BackupTask task, boolean schedule) {
											   mainframeListener.taskFinished(task, schedule);
										   }
									   }, task, backup.getBackupInfos().getNumberOfFilesToCopy(),
							backup.getBackupInfos().getNumberOfFilesToLink(),
							backup.getBackupInfos().getNumberOfDirectories(), backup.getBackupInfos().getSizeToCopy(),
							backup.getBackupInfos().getSizeToLink());

					summaryDialogStage.showAndWait();
				} catch (IOException e) {
					System.err.println("IOException while loading fxml of SummaryDialog.");
				}
			}
		});
	}

	/**
	 * Opens the PreparingDialog window.
	 *
	 * @param task corresponding BackupTask
	 */
	public void showPreparingDialog(BackupTask task, Backupable backup) {
		// Avoid throwing IllegalStateException by running from a non-JavaFX thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final Stage preparingDialogStage = new Stage(StageStyle.UTILITY);
				preparingDialogStage.initModality(Modality.APPLICATION_MODAL);
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/PreparingDialog.fxml"));
					Scene scene = new Scene(loader.load());
					PreparingDialog preparingDialog = loader.getController();
					preparingDialogStage.setScene(scene);

					preparingDialog.setStage(preparingDialogStage);
					preparingDialogListener = new IPreparingDialogListener() {
						@Override
						public void cancelBackup(String taskName) {
							setStatusOfBackupTask(task.getTaskName(), false, "Canceled");
							mainframeListener.taskFinished(task, true);
							mainframeListener.deleteEmptyBackupFolders(task);
							synchronized (task) {
								task.notify();
							}
							preparingDialogListener.disposeDialog();
						}

						@Override
						public void disposeDialog() {
							preparingDialogStage.close();
						}
					};

					preparingDialog.init(preparingDialogListener, task.getTaskName(), backup);

					preparingDialogStage.showAndWait();
				} catch (IOException e) {
					System.err.println("IOException while loading fxml of PreparingDialog.");
				}
			}
		});
	}

	/**
	 * Disposes the PreparingDialog (if any).
	 */
	public void disposePreparingDialogIfNotNull() {
		// Avoid throwing IllegalStateException by running from a non-JavaFX thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (preparingDialogListener != null) {
					preparingDialogListener.disposeDialog();
				}
			}
		});
	}

	/**
	 * Forces to update the list of BackupTasks within the mainframe.
	 */
	public void forceGuiUpdate() {
		// Avoid throwing IllegalStateException by running from a non-JavaFX thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				lv_backupTasks.fireEvent(
						new ListView.EditEvent<>(lv_backupTasks, ListView.editCommitEvent(), ol_backupTasks.get(0), 0));
			}
		});

	}

	private void startNextExecutionChooserDialog(String taskName) {
		final Stage nextExecutionChooserDialogStage = new Stage(StageStyle.UTILITY);
		nextExecutionChooserDialogStage.initModality(Modality.APPLICATION_MODAL);
		try {
			FXMLLoader loader = new FXMLLoader((getClass().getResource("NextExecutionChooserDialog.fxml")));
			Scene scene = new Scene(loader.load());
			NextExecutionChooserDialog nextExecutionChooserDialog = loader.getController();
			nextExecutionChooserDialog.init(new INextExecutionChooserDialogListener() {
				@Override
				public void skipNextExecution(String taskName) {
					mainframeListener.skipNextExecution(taskName);
				}

				@Override
				public void postponeExecutionBy(String taskName, int minutesToPostponeBy) {
					mainframeListener.postponeExecutionBy(taskName, minutesToPostponeBy);
				}
			}, taskName);
			nextExecutionChooserDialogStage.setScene(scene);
			nextExecutionChooserDialog.setStage(nextExecutionChooserDialogStage);

			nextExecutionChooserDialogStage.showAndWait();
		} catch (IOException e) {
			System.err.println("IOException while starting NextExecutionChooserDialog: " + e.toString());
		}
	}
}
