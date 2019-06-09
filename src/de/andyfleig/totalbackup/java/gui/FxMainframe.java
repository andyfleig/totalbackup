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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import listener.IFxMainframeListener;
import listener.ISummaryDialogListener;
import main.Backupable;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class FxMainframe extends Application implements Initializable {

	@FXML
	public ListView<MainframeCellContent> lv_backupTasks;
	final ObservableList<MainframeCellContent> ol_backupTasks = FXCollections.observableArrayList();
	@FXML
	public VBox vBoxMain;

	private SummaryDialog summaryDialog;

	@FXML
	public ContextMenu contextMenu;

	private static IFxMainframeListener mainframeListener;

	public void init(IFxMainframeListener listener) {
		this.mainframeListener = listener;
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
	public void start(Stage stage) throws Exception {

		mainframeListener.startMainframe(stage);

	}

	@FXML
	public void quitAction() {
		//ToDo
		mainframeListener.saveProperties();
		System.exit(0);
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
		contextMenu.hide();

		mainframeListener.startBackupTaskDialog(lv_backupTasks.getSelectionModel().getSelectedItem().getTaskName());

	}

	@FXML
	public void deleteEntryAction() {
		if (lv_backupTasks.getSelectionModel().getSelectedIndex() == -1) {
			return;
		}
		// Ask for confirmation
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Deleting the backup-task will cancel " +
				"future executions of this task. \nThis can not be undone.", ButtonType.YES, ButtonType.NO);
		alert.setTitle("Warning");
		alert.setHeaderText("Do you really want to delete the selected backup-task?");
		alert.setResizable(true);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.YES) {
			mainframeListener.deleteBackupTaskWithName(lv_backupTasks.getSelectionModel().getSelectedItem().getTaskName());
		} else {
			return;
		}


	}

	@FXML
	public void startAboutDialog() {
		mainframeListener.startAboutDialog();
	}

	public void startFxMainframe() {
		launch(null);
	}

	/**
	 * Fügt einen Task (Namen) in die Liste der Tasks in der GUI hinzu.
	 *
	 * @param taskName
	 */
	public void addBackupTask(String taskName) {
		ol_backupTasks.add(new MainframeCellContent(taskName, "Okay"));

	}

	/**
	 * Fügt einen Task (Namen) in die Liste der Tasks in der GUI hinzu.
	 *
	 * @param taskName
	 */
	public void removeBackupTask(String taskName) {
		int indexOfTask = getIndexOfObservableListFromName(taskName);
		if (indexOfTask >= 0) {
			ol_backupTasks.remove(getIndexOfObservableListFromName(taskName));
		}

	}

	//ToDo: Wohin? + private?
	/**
	 * Sucht aus der ol_filters den Eintrag eines bestimmten Tasks heraus.
	 *
	 * @param taskName Name des gesuchten Tasks
	 * @return Index an dem sich der gesuchte Task in der observalbe List befindet
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
	 * Setzt den Status für den Task mit dem gegebenen Namen (taskName).
	 *
	 * @param taskName   Task für den der Status gesetzt werden soll
	 * @param error      Ob es sich um einen Fehler oder um einen "normalen" Status handelt. true = Fehler, false =
	 *                   Status
	 * @param taskStatus zu setzender Status
	 */
	public void setStatusOfBackupTask(String taskName, boolean error, String taskStatus) {
		//ToDo: Farbe (Error)
		int indexOfTask = getIndexOfObservableListFromName(taskName);
		if (indexOfTask >= 0) {
			ol_backupTasks.get(indexOfTask).setTaskStaus(taskStatus);
		}
	}

	/**
	 * Öffnet einen neuen SummaryDialog.
	 *
	 * @param task entsprechender BackupTask
	 */
	public void showSummaryDialog(final BackupTask task, final Backupable backup) {
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
										   public void startBackup() {

										   }

										   @Override
										   public String getTaskName() {
											   return null;
										   }

										   @Override
										   public void deleteEmptyBackupFolders(BackupTask task) {
										   	mainframeListener.deleteEmptyBackupFolders("", task);

										   }

										   @Override
										   public void outprintBackupCanceled(BackupTask task) {

										   }

										   @Override
										   public void taskFinished(BackupTask task) {

										   }
									   }, task, backup.getBackupInfos().getNumberOfFilesToCopy(),
							backup.getBackupInfos().getNumberOfFilesToCopy(),
							backup.getBackupInfos().getNumberOfDirectories(), backup.getBackupInfos().getSizeToCopy(),
							backup.getBackupInfos().getSizeToLink());

					summaryDialogStage.showAndWait();
				} catch (IOException e) {
					System.err.println(e);
				}
			}
		});

	}
}
