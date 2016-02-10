package gui;

import data.BackupTask;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import listener.IFxMainframeListener;
import listener.IMainframeListener;
import main.Backupable;
import main.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class FxMainframe extends Application implements Initializable {

	@FXML
	public ListView listView;
	final ObservableList<cellContent> observableList = FXCollections.observableArrayList();
	@FXML
	public VBox vBoxMain;

	private static IFxMainframeListener mainframeListener;

	public void init(IFxMainframeListener listener) {
		this.mainframeListener = listener;
	}

	@Override
	public void start(Stage stage) throws Exception {
		mainframeListener.startMainframe(stage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rs) {
		listView.setItems(observableList);
		listView.setCellFactory(new Callback<ListView, BackupTaskListCell>() {
			@Override
			public BackupTaskListCell call(ListView listView) {
				BackupTaskListCell cell = new BackupTaskListCell();
				return cell;
			}
		});
	}

	@FXML
	public void quitAction() {
		//ToDo
		System.exit(0);
	}

	@FXML
	public void addEntryAction() {
		mainframeListener.startBackupTaskDialog();
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
		//Vorlage: observableList.add(new cellContent("testName", "testStatus"));
		observableList.add(new cellContent(taskName, ""));
	}

	/**
	 * Fügt einen Task (Namen) in die Liste der Tasks in der GUI hinzu.
	 *
	 * @param taskName
	 */
	public void removeBackupTask(String taskName) {
		int indexOfTask = getIndexOfObservableListFromName(taskName);
		if (indexOfTask >= 0) {
			observableList.remove(getIndexOfObservableListFromName(taskName));
		}

	}

	//ToDo: Wohin? + private?
	/**
	 * Sucht aus der observableList den Eintrag eines bestimmten Tasks heraus.
	 *
	 * @param taskName Name des gesuchten Tasks
	 * @return Index an dem sich der gesuchte Task in der observalbe List befindet
	 */
	private int getIndexOfObservableListFromName(String taskName) {
		for (int i = 0; i < observableList.size(); i++) {
			if (observableList.get(i).getTaskName().equals(taskName)) {
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
			observableList.get(indexOfTask).setTaskStaus(taskStatus);
		}
	}

	/**
	 * Öffnet einen neuen SummeryDialog.
	 *
	 * @param task entsprechender BackupTask
	 */
	public void showSummaryDialog(final BackupTask task, final Backupable backup) {
		//ToDo: Implementieren!
	}
}
