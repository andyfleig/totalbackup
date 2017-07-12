package gui;

import data.BackupTask;
import data.Source;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.*;
import listener.IBackupTaskDialogListener;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

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
	private TextField tf_intervall;
	@FXML
	private ComboBox cb_unit;

	//Alle 3 Untertabs:
	@FXML
	private TextField tf_startAt;
	@FXML
	private CheckBox cb_catchUpBackups;
	@FXML
	private ComboBox cb_catchUpComboBox;

	//Tab Autoclean:
	@FXML
	private RadioButton rb_autoclean_off;
	@FXML
	private RadioButton rb_autoclean_simple;
	@FXML
	private RadioButton rb_autoclean_extended;

	//Untertab Einfach:
	@FXML
	private Spinner sp_keep_last_x_backups;

	//Untertab Erweitert:
	@FXML
	private Spinner<Integer> sp_number_of_rules;
	@FXML
	private Spinner<Integer> sp_rule_1;
	@FXML
	private ComboBox cb_rule_1_unit;
	@FXML
	private Spinner<Integer> sp_rule_1_number;
	@FXML
	private Spinner<Integer> sp_rule_2;
	@FXML
	private ComboBox cb_rule_2_unit;
	@FXML
	private Spinner<Integer> sp_rule_2_number;
	@FXML
	private Spinner<Integer> sp_rule_3;
	@FXML
	private ComboBox cb_rule_3_unit;
	@FXML
	private Spinner<Integer> sp_rule_3_number;
	@FXML
	private Spinner<Integer> sp_rule_4;
	@FXML
	private ComboBox cb_rule_4_unit;
	@FXML
	private Spinner<Integer> sp_rule_4_number;
	@FXML
	private Spinner<Integer> sp_rule_5_number;

	//Autoclean:
	//Einfach:
	SpinnerValueFactory<Integer> valueFactory_keep_last_x_backups = new SpinnerValueFactory.IntegerSpinnerValueFactory(
			1, 50, 1);

	//Erweitert:
	SpinnerValueFactory<Integer> valueFactory_number_of_rules = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5,
			1);

	//Einzelne Regeln:
	SpinnerValueFactory<Integer> valueFactory_rules_1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 59, 1);
	SpinnerValueFactory<Integer> valueFactory_rules_2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 59, 1);
	SpinnerValueFactory<Integer> valueFactory_rules_3 = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 59, 1);
	SpinnerValueFactory<Integer> valueFactory_rules_4 = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 59, 1);

	SpinnerValueFactory<Integer> valueFactory_number_1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
	SpinnerValueFactory<Integer> valueFactory_number_2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
	SpinnerValueFactory<Integer> valueFactory_number_3 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
	SpinnerValueFactory<Integer> valueFactory_number_4 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
	SpinnerValueFactory<Integer> valueFactory_number_5 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);


	public BackupTaskDialog() {

		lv_sourcePaths.setItems(sourcePaths);
	}
	public void setBackupTaskDialogListener(IBackupTaskDialogListener listener) {
		this.backupTaskDialogListener = listener;
	}

	public void initialize() {
		cb_unit.setItems(FXCollections.observableArrayList("min", "hour", "day", "month"));
		cb_catchUpComboBox.setItems(
				FXCollections.observableArrayList("5min", "10min", "15min", "30min", "1h", "2h", "6h", "12h", "24h"));
		cb_catchUpComboBox.setValue("24h");

		sp_keep_last_x_backups.setValueFactory(valueFactory_keep_last_x_backups);

		sp_number_of_rules.setValueFactory(valueFactory_number_of_rules);

		cb_rule_1_unit.setValue("inf");
		cb_rule_2_unit.setValue("inf");
		cb_rule_3_unit.setValue("inf");
		cb_rule_4_unit.setValue("inf");

		sp_rule_1.setValueFactory(valueFactory_rules_1);
		sp_rule_2.setValueFactory(valueFactory_rules_2);
		sp_rule_3.setValueFactory(valueFactory_rules_3);
		sp_rule_4.setValueFactory(valueFactory_rules_4);

		sp_rule_1_number.setValueFactory(valueFactory_number_1);
		sp_rule_2_number.setValueFactory(valueFactory_number_2);
		sp_rule_3_number.setValueFactory(valueFactory_number_3);
		sp_rule_4_number.setValueFactory(valueFactory_number_4);
		sp_rule_5_number.setValueFactory(valueFactory_number_5);


		cb_rule_1_unit.setItems(
				FXCollections.observableArrayList("inf", "min", "hour(s)", "day(s)", "week(s)", "year(s)"));
		cb_rule_2_unit.setItems(
				FXCollections.observableArrayList("inf", "min", "hour(s)", "day(s)", "week(s)", "year(s)"));
		cb_rule_3_unit.setItems(
				FXCollections.observableArrayList("inf", "min", "hour(s)", "day(s)", "week(s)", "year(s)"));
		cb_rule_4_unit.setItems(
				FXCollections.observableArrayList("inf", "min", "hour(s)", "day(s)", "week(s)", "year(s)"));

		sp_rule_1.setEditable(true);
		sp_rule_2.setEditable(true);
		sp_rule_3.setEditable(true);
		sp_rule_4.setEditable(true);

		cb_rule_2_unit.setDisable(true);
		cb_rule_3_unit.setDisable(true);
		cb_rule_4_unit.setDisable(true);

		sp_rule_1_number.setEditable(true);
		sp_rule_2_number.setEditable(true);
		sp_rule_3_number.setEditable(true);
		sp_rule_4_number.setEditable(true);
		sp_rule_5_number.setEditable(true);

		//Alle Regeln ab Nr.2 sind deaktiviert (da Anzahl Regeln default = 1)
		sp_rule_2.setDisable(true);
		sp_rule_3.setDisable(true);
		sp_rule_4.setDisable(true);

		sp_rule_2_number.setDisable(true);
		sp_rule_3_number.setDisable(true);
		sp_rule_4_number.setDisable(true);
		sp_rule_5_number.setDisable(true);

		sp_number_of_rules.valueProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
				int value = sp_number_of_rules.getValue();
				if (value == 1) {
					sp_rule_2.setDisable(true);
					sp_rule_3.setDisable(true);
					sp_rule_4.setDisable(true);
					cb_rule_2_unit.setDisable(true);
					cb_rule_3_unit.setDisable(true);
					cb_rule_4_unit.setDisable(true);
					sp_rule_2_number.setDisable(true);
					sp_rule_3_number.setDisable(true);
					sp_rule_4_number.setDisable(true);
					sp_rule_5_number.setDisable(true);
				} else if (value == 2) {
					sp_rule_2.setDisable(false);
					sp_rule_3.setDisable(true);
					sp_rule_4.setDisable(true);
					cb_rule_2_unit.setDisable(false);
					cb_rule_3_unit.setDisable(true);
					cb_rule_4_unit.setDisable(true);
					sp_rule_2_number.setDisable(false);
					sp_rule_3_number.setDisable(true);
					sp_rule_4_number.setDisable(true);
					sp_rule_5_number.setDisable(true);
				} else if (value == 3) {
					sp_rule_2.setDisable(false);
					sp_rule_3.setDisable(false);
					sp_rule_4.setDisable(true);
					cb_rule_2_unit.setDisable(false);
					cb_rule_3_unit.setDisable(false);
					cb_rule_4_unit.setDisable(true);
					sp_rule_2_number.setDisable(false);
					sp_rule_3_number.setDisable(false);
					sp_rule_4_number.setDisable(true);
					sp_rule_5_number.setDisable(true);
				} else if (value == 4) {
					sp_rule_2.setDisable(false);
					sp_rule_3.setDisable(false);
					sp_rule_4.setDisable(false);
					cb_rule_2_unit.setDisable(false);
					cb_rule_3_unit.setDisable(false);
					cb_rule_4_unit.setDisable(false);
					sp_rule_2_number.setDisable(false);
					sp_rule_3_number.setDisable(false);
					sp_rule_4_number.setDisable(false);
					sp_rule_5_number.setDisable(true);
				} else if (value == 5) {
					sp_rule_2.setDisable(false);
					sp_rule_3.setDisable(false);
					sp_rule_4.setDisable(false);
					cb_rule_2_unit.setDisable(false);
					cb_rule_3_unit.setDisable(false);
					cb_rule_4_unit.setDisable(false);
					sp_rule_2_number.setDisable(false);
					sp_rule_3_number.setDisable(false);
					sp_rule_4_number.setDisable(false);
					sp_rule_5_number.setDisable(false);
				}
			}
		});

	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void cancelAction() {
		stage.close();
	}

	public void okAction() {
		if (!settingsAreValid()) {
			System.out.println("Error: invalid parameters!");
			return;
		}

		BackupTask newTask = new BackupTask(tf_taskName.getText());
		if (rb_hardlinkBackup.isPressed()) {
			newTask.setBackupMode(1);
		} else {
			newTask.setBackupMode(0);
		}
		for (String path : sourcePaths) {
			//TODO: check if path is ok?
			newTask.addSourcePath(new Source(path));
		}
		newTask.setDestinationPath(tf_destPath.getText());

		if (cb_destinationVerification.isSelected()) {
			newTask.setDestinationVerification(true);
		}

		if (cb_autostart.isSelected()) {
			newTask.setAutostart(true);
		}

		// auto-backup settings:
		if (rb_weekday.isPressed()) {
			// weekdays:
			newTask.setAutoBackupMode(1);
			boolean[] weekdays = new boolean[7];
			if (cb_monday.isSelected()) {
				weekdays[0] = true;
			}
			if (cb_tuesday.isSelected()) {
				weekdays[1] = true;
			}
			if (cb_wednesday.isSelected()) {
				weekdays[2] = true;
			}
			if (cb_thursday.isSelected()) {
				weekdays[3] = true;
			}
			if (cb_friday.isSelected()) {
				weekdays[4] = true;
			}
			if (cb_saturday.isSelected()) {
				weekdays[5] = true;
			}
			if (cb_sunday.isSelected()) {
				weekdays[6] = true;
			}
			newTask.setBackupWeekdays(weekdays);

		} else if (rb_dayInMonth.isPressed()) {
			newTask.setAutoBackupMode(2);
			boolean[] daysInMonth = new boolean[31];
			if (cb_day1.isSelected()) {
				daysInMonth[0] = true;
			}
			if (cb_day2.isSelected()) {
				daysInMonth[1] = true;
			}
			if (cb_day3.isSelected()) {
				daysInMonth[2] = true;
			}
			if (cb_day4.isSelected()) {
				daysInMonth[3] = true;
			}
			if (cb_day5.isSelected()) {
				daysInMonth[4] = true;
			}
			if (cb_day6.isSelected()) {
				daysInMonth[5] = true;
			}
			if (cb_day7.isSelected()) {
				daysInMonth[6] = true;
			}
			if (cb_day8.isSelected()) {
				daysInMonth[7] = true;
			}
			if (cb_day9.isSelected()) {
				daysInMonth[8] = true;
			}
			if (cb_day10.isSelected()) {
				daysInMonth[9] = true;
			}
			if (cb_day11.isSelected()) {
				daysInMonth[10] = true;
			}
			if (cb_day12.isSelected()) {
				daysInMonth[11] = true;
			}
			if (cb_day13.isSelected()) {
				daysInMonth[12] = true;
			}
			if (cb_day14.isSelected()) {
				daysInMonth[13] = true;
			}
			if (cb_day15.isSelected()) {
				daysInMonth[14] = true;
			}
			if (cb_day16.isSelected()) {
				daysInMonth[15] = true;
			}
			if (cb_day17.isSelected()) {
				daysInMonth[16] = true;
			}
			if (cb_day18.isSelected()) {
				daysInMonth[17] = true;
			}
			if (cb_day19.isSelected()) {
				daysInMonth[18] = true;
			}
			if (cb_day20.isSelected()) {
				daysInMonth[19] = true;
			}
			if (cb_day21.isSelected()) {
				daysInMonth[20] = true;
			}
			if (cb_day22.isSelected()) {
				daysInMonth[21] = true;
			}
			if (cb_day23.isSelected()) {
				daysInMonth[22] = true;
			}
			if (cb_day24.isSelected()) {
				daysInMonth[23] = true;
			}
			if (cb_day25.isSelected()) {
				daysInMonth[24] = true;
			}
			if (cb_day26.isSelected()) {
				daysInMonth[25] = true;
			}
			if (cb_day27.isSelected()) {
				daysInMonth[26] = true;
			}
			if (cb_day28.isSelected()) {
				daysInMonth[27] = true;
			}
			if (cb_day29.isSelected()) {
				daysInMonth[28] = true;
			}
			if (cb_day30.isSelected()) {
				daysInMonth[29] = true;
			}
			if (cb_day31.isSelected()) {
				daysInMonth[30] = true;
			}

			newTask.setBackupDaysInMonth(daysInMonth);
		} else if (rb_interval.isPressed()) {
			// intervall:
			newTask.setAutoBackupMode(3);
			newTask.setIntervalTime(Integer.parseInt(tf_intervall.getText()));
			newTask.setIntervalUnit(cb_unit.getValue().toString());

		} else if (rb_weekday.isPressed() || rb_dayInMonth.isPressed() || rb_interval.isPressed()) {
			// start-zeit:
			try {
				newTask.setBackupStartTime(LocalTime.parse(tf_startAt.getText()));
			} catch (DateTimeParseException e) {
				//TODO: error message
			}
			if (cb_catchUpBackups.isSelected()) {
				newTask.setCatchUpEnabled(true);
				newTask.setCatchUpTime(cb_catchUpComboBox.getValue().toString());
			}
		}

		// Aufräum-Einstellungen:
		if (rb_autoclean_simple.isPressed()) {
			newTask.setSimpleAutoCleanEnabled(true);
			newTask.setNumberOfBackupsToKeep(Integer.getInteger(sp_keep_last_x_backups.getValue().toString()));
		}
		if (rb_autoclean_extended.isPressed()) {
			newTask.setExtendedAutoCleanEnabled(true);
			int numberOfRules = sp_number_of_rules.getValue();
			newTask.setNumberOfExtendedAutoCleanRules(numberOfRules);
			int[] threshold = new int[numberOfRules];
			String[] thresholdUnits = new String[numberOfRules];
			int[] backupsToKeep = new int[numberOfRules];

			for (int i = 0; i < sp_number_of_rules.getValue(); i++) {
				threshold[i] = sp_rule_1.getValue();
				thresholdUnits[i] = cb_rule_1_unit.getValue().toString();
				backupsToKeep[i] = sp_rule_1_number.getValue();
			}
			newTask.setThreshold(threshold);
			newTask.setThresholdUnits(thresholdUnits);
			newTask.setBackupsToKeep(backupsToKeep);
		}
		backupTaskDialogListener.addBackupTask(newTask);
		backupTaskDialogListener.saveProperties();

		backupTaskDialogListener.scheduleBackupTask(newTask);
		stage.close();
	}

	private boolean settingsAreValid() {
		//Tab: Main
		if (tf_taskName.getText().matches("^.*[^a-zA-Z0-9 ].*$")) {
			System.out.println("Taskname invalid");
			return false;
		}
		//Quellpfade werden im entsprechenden Untertab gecheckt
		if (!(new File(tf_destPath.getText())).isDirectory()) {
			System.out.println("Dest-Path invalid");
			return false;
		}

		//Tab: Autorun
		if (!rb_off.isSelected()) {
			try {
				LocalTime.parse(tf_startAt.getText());
			} catch (DateTimeParseException e) {
				System.out.println("Autorun-Time invalid");
				return false;
			}
			if (rb_weekday.isSelected()) {
				//Untertab: Wochentag
				if (!(cb_monday.isSelected() || cb_tuesday.isSelected() || cb_wednesday.isSelected() ||
						cb_thursday.isSelected() || cb_friday.isSelected() || cb_saturday.isSelected() ||
						cb_sunday.isSelected())) {
					System.out.println("At least one weekday has to be selected");
					return false;
				}
			} else if (rb_dayInMonth.isSelected()) {
				//Untertab: Tag im Monat
				if (!(cb_day1.isSelected() || cb_day2.isSelected() || cb_day3.isSelected() || cb_day4.isSelected() ||
						cb_day5.isSelected() || cb_day6.isSelected() || cb_day7.isSelected() || cb_day8.isSelected() ||
						cb_day9.isSelected() || cb_day10.isSelected() || cb_day11.isSelected() || cb_day12.isSelected() ||
						cb_day13.isSelected() || cb_day14.isSelected() || cb_day15.isSelected() || cb_day16.isSelected() ||
						cb_day17.isSelected() || cb_day18.isSelected() || cb_day19.isSelected() || cb_day20.isSelected() ||
						cb_day21.isSelected() || cb_day22.isSelected() || cb_day23.isSelected() || cb_day24.isSelected() ||
						cb_day25.isSelected() || cb_day26.isSelected() || cb_day27.isSelected() || cb_day28.isSelected() ||
						cb_day29.isSelected() || cb_day30.isSelected() || cb_day31.isSelected())) {
					System.out.println("At least one day has to be selected");
					return false;
				}
			} else if (rb_interval.isSelected()) {
				//Untertab: Intervall
				try {
					Integer.parseInt(tf_intervall.getText());
				} catch (NumberFormatException e) {
					System.out.println("Interval not valid");
					return false;
				}
			}
		}
		return true;
	}

	public void addSourcePathAction() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("choose SourcePath");
		File sourcePath = dirChooser.showDialog(stage);
		if (sourcePath == null) {
			return;
		}
		sourcePaths.add(sourcePath.getAbsolutePath());
		lv_sourcePaths.setItems(sourcePaths);
	}

	public void editSourcePathAction() {
		int selectedIndex = lv_sourcePaths.getSelectionModel().getSelectedIndex();
		if (selectedIndex == -1) {
			return;
		}
		File selectedPath = new File(sourcePaths.get(selectedIndex));
		if (!selectedPath.isDirectory()) {
			return;
		}
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setInitialDirectory(selectedPath);
		File destPath = dirChooser.showDialog(stage);
		if (destPath != null) {
			tf_destPath.setText(destPath.getAbsolutePath());
		}
	}

	public void removeSourcePathAction() {
		int selectedIndex = lv_sourcePaths.getSelectionModel().getSelectedIndex();
		if (selectedIndex == -1) {
			return;
		}
		sourcePaths.remove(selectedIndex);
	}

	public void addDestinationPath() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("choose DestinationPath");
		File destPath = dirChooser.showDialog(stage);
		if (destPath != null) {
			tf_destPath.setText(destPath.getAbsolutePath());
		}

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

	@FXML
	public void numberOfRulesChanged() {
		System.out.println("test");
	}
}
