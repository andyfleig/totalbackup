package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class BackupTaskDialog {
	private static Stage stage;

	private static IBackupTaskDialogListener backupTaskDialogListener;

	//Tab Main:
	@FXML
	private TextField tf_taskName;
	@FXML
	private TextField tf_destPath;
	@FXML
	private RadioButton rb_normalBackup;
	@FXML
	private RadioButton rb_hardlinkBackup;

	@FXML
	private ListView<String> lv_sourcePaths = new ListView<>();
	private ObservableList<String> sourcePaths = FXCollections.observableArrayList();
	@FXML
	private CheckBox cb_destinationVerification;
	@FXML
	private CheckBox cb_autostart;

	//Tab Autorun:
	@FXML
	private RadioButton rb_off;
	@FXML
	private RadioButton rb_weekday;
	@FXML
	private RadioButton rb_dayInMonth;
	@FXML
	private RadioButton rb_interval;

	//Untertab Wochentag:
	@FXML
	private CheckBox cb_monday;
	@FXML
	private CheckBox cb_tuesday;
	@FXML
	private CheckBox cb_wednesday;
	@FXML
	private CheckBox cb_thursday;
	@FXML
	private CheckBox cb_friday;
	@FXML
	private CheckBox cb_saturday;
	@FXML
	private CheckBox cb_sunday;
	//Untertab Tag im Monat:
	@FXML
	private CheckBox cb_day1;
	@FXML
	private CheckBox cb_day2;
	@FXML
	private CheckBox cb_day3;
	@FXML
	private CheckBox cb_day4;
	@FXML
	private CheckBox cb_day5;
	@FXML
	private CheckBox cb_day6;
	@FXML
	private CheckBox cb_day7;
	@FXML
	private CheckBox cb_day8;
	@FXML
	private CheckBox cb_day9;
	@FXML
	private CheckBox cb_day10;
	@FXML
	private CheckBox cb_day11;
	@FXML
	private CheckBox cb_day12;
	@FXML
	private CheckBox cb_day13;
	@FXML
	private CheckBox cb_day14;
	@FXML
	private CheckBox cb_day15;
	@FXML
	private CheckBox cb_day16;
	@FXML
	private CheckBox cb_day17;
	@FXML
	private CheckBox cb_day18;
	@FXML
	private CheckBox cb_day19;
	@FXML
	private CheckBox cb_day20;
	@FXML
	private CheckBox cb_day21;
	@FXML
	private CheckBox cb_day22;
	@FXML
	private CheckBox cb_day23;
	@FXML
	private CheckBox cb_day24;
	@FXML
	private CheckBox cb_day25;
	@FXML
	private CheckBox cb_day26;
	@FXML
	private CheckBox cb_day27;
	@FXML
	private CheckBox cb_day28;
	@FXML
	private CheckBox cb_day29;
	@FXML
	private CheckBox cb_day30;
	@FXML
	private CheckBox cb_day31;
	//Untertab Intervall:

	@FXML
	private TextField tf_startAt;
	@FXML
	private TextField tf_intervall;

	//Tab Autoclean:


	public BackupTaskDialog() {
		lv_sourcePaths.setItems(sourcePaths);
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void cancelAction() {
		stage.close();
	}

	public void addSourcePathAction() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("choose SourcePath");
		File sourcePath = dirChooser.showDialog(stage);
		sourcePaths.add(sourcePath.getAbsolutePath());
		lv_sourcePaths.setItems(sourcePaths);
	}

	public void editSourcePathAction() {
		//ToDo: Implementieren
	}

	public void removeSourcePathAction() {
		sourcePaths.remove(lv_sourcePaths.getSelectionModel().getSelectedIndex());
	}

	public void addDestinationPath() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("choose DestinationPath");
		File destPath = dirChooser.showDialog(stage);
		tf_destPath.setText(destPath.getAbsolutePath());
	}

	public void OKAction() {

	}

	@FXML
	public void startSourcesDialog() {
		SourcesDialog sourcesDialog = new SourcesDialog();
		final Stage sourcesDialogStage = new Stage(StageStyle.UTILITY);
		sourcesDialogStage.initModality(Modality.APPLICATION_MODAL);
		try {
			sourcesDialogStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("AboutDialog.fxml"))));
			sourcesDialog.setStage(sourcesDialogStage);
			sourcesDialogStage.showAndWait();
		} catch (IOException e) {
			System.err.println("IOException while starting AboutDialog");
		}
	}
}
