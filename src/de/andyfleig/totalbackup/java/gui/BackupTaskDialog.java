package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class BackupTaskDialog {
	private static Stage stage;

	//Tab Main:
	@FXML
	private TextField tf_taskName;
	@FXML
	private ListView lv_sourcePaths;
	@FXML
	private TextField tf_destPath;
	@FXML
	private RadioButton rb_normalBackup = new RadioButton();
	@FXML
	private RadioButton rb_hardlinkBackup = new RadioButton();
	private ToggleGroup tg_backupModeRadioButtons = new ToggleGroup();

	//Tab Autorun:
	@FXML
	private TextField tf_startAt;
	@FXML
	private TextField tf_intervall;

	//Tab Autoclean:


	public BackupTaskDialog() {
		rb_normalBackup.setToggleGroup(tg_backupModeRadioButtons);
		rb_hardlinkBackup.setToggleGroup(tg_backupModeRadioButtons);
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void cancelAction() {
		stage.close();
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
