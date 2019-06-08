package gui;

import data.BackupTask;
import data.Filter;
import data.Source;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.*;
import javafx.util.Callback;
import listener.IBackupTaskDialogListener;
import javafx.scene.image.ImageView;
import listener.ISourcesDialogListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;


/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class BackupTaskDialog {
	private static Stage stage;

	private static IBackupTaskDialogListener backupTaskDialogListener;

	private BackupTask initTask;

	@FXML
	private Tab tab_main;
	@FXML
	private Tab tab_autorun;
	@FXML
	private Tab tab_autoclean;

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
	private ListView<Source> lv_sources = new ListView<>();
	private ObservableList<Source> ol_sources = FXCollections.observableArrayList();
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
	SpinnerValueFactory<Integer> valueFactory_rules_1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 59, 1);
	SpinnerValueFactory<Integer> valueFactory_rules_2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 59, 1);
	SpinnerValueFactory<Integer> valueFactory_rules_3 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 59, 1);
	SpinnerValueFactory<Integer> valueFactory_rules_4 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 59, 1);

	SpinnerValueFactory<Integer> valueFactory_number_1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
	SpinnerValueFactory<Integer> valueFactory_number_2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
	SpinnerValueFactory<Integer> valueFactory_number_3 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
	SpinnerValueFactory<Integer> valueFactory_number_4 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
	SpinnerValueFactory<Integer> valueFactory_number_5 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);


	public BackupTaskDialog(BackupTask task) {
		if (task != null) {
			initTask = task;
		}
		lv_sources.setItems(ol_sources);
		lv_sources.setCellFactory(new Callback<ListView<Source>, ListCell<Source>>() {
			@Override
			public ListCell<Source> call(ListView<Source> sourceListView) {
				return new SourceListCell();
			}
		});
	}


	public void setBackupTaskDialogListener(IBackupTaskDialogListener listener) {
		this.backupTaskDialogListener = listener;
	}

	@FXML
	public void initialize() {

		try {
			FileInputStream in_general = new FileInputStream("./src/de/andyfleig/totalbackup/resources/tab_general" +
					".png");
			FileInputStream in_autorun = new FileInputStream("./src/de/andyfleig/totalbackup/resources" +
					"/tab_autorun.png");
			FileInputStream in_autoclean = new FileInputStream("./src/de/andyfleig/totalbackup/resources" +
					"/tab_autoclean.png");


			Image image_general = new Image(in_general);
			Image image_autorun = new Image(in_autorun);
			Image image_autobclean = new Image(in_autoclean);

			ImageView iv_general = new ImageView(image_general);
			ImageView iv_autorun = new ImageView(image_autorun);
			ImageView iv_autoclean = new ImageView(image_autobclean);

			iv_general.setRotate(90.0);
			iv_autorun.setRotate(90.0);
			iv_autoclean.setRotate(90.0);

			BorderPane tp_general = new BorderPane();
			BorderPane tp_autorun = new BorderPane();
			BorderPane tp_autoclean = new BorderPane();

			tp_general.setCenter(iv_general);
			tp_autorun.setCenter(iv_autorun);
			tp_autoclean.setCenter(iv_autoclean);

			tab_main.setGraphic(tp_general);
			tab_autorun.setGraphic(tp_autorun);
			tab_autoclean.setGraphic(tp_autoclean);
		} catch(IOException e) {
			System.out.println(e);
		}


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

		//limit rules to numbers:
		sp_rule_1.getEditor().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					sp_rule_1.getEditor().setText(oldValue);
				}
			}
		});
		sp_rule_2.getEditor().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					sp_rule_2.getEditor().setText(oldValue);
				}
			}
		});
		sp_rule_3.getEditor().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					sp_rule_3.getEditor().setText(oldValue);
				}
			}
		});
		sp_rule_4.getEditor().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					sp_rule_4.getEditor().setText(oldValue);
				}
			}
		});

		sp_rule_1_number.getEditor().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					sp_rule_1_number.getEditor().setText(oldValue);
				}
			}
		});
		sp_rule_2_number.getEditor().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					sp_rule_2_number.getEditor().setText(oldValue);
				}
			}
		});
		sp_rule_3_number.getEditor().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					sp_rule_3_number.getEditor().setText(oldValue);
				}
			}
		});
		sp_rule_4_number.getEditor().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					sp_rule_4_number.getEditor().setText(oldValue);
				}
			}
		});
		sp_rule_5_number.getEditor().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					sp_rule_5_number.getEditor().setText(oldValue);
				}
			}
		});



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
		if (initTask != null) {
			initWithTask();
		}

		lv_sources.setItems(ol_sources);

	}

	public void initWithTask() {
		tf_taskName.setText(initTask.getTaskName());
		tf_taskName.setDisable(true);

		for (Source source : initTask.getSources()) {
			ol_sources.add(source);
		}
		tf_destPath.setText(initTask.getDestinationPath());

		if (initTask.getBackupMode() == 1) {
			rb_hardlinkBackup.setSelected(true);
		} else {
			rb_normalBackup.setSelected(true);
		}
		cb_destinationVerification.setSelected(initTask.getDestinationVerification());
		cb_autostart.setSelected(initTask.getAutostart());

		//Auto-Backup Einstellungen:
		if (initTask.getAutoBackupMode() != 0) {
			if (initTask.getAutoBackupMode() == 1) {
				//Wochentag:
				rb_weekday.setSelected(true);
				if (initTask.getBackupWeekdays()[0] == true) {
					cb_monday.setSelected(true);
				}
				if (initTask.getBackupWeekdays()[1] == true) {
					cb_tuesday.setSelected(true);
				}
				if (initTask.getBackupWeekdays()[2] == true) {
					cb_wednesday.setSelected(true);
				}
				if (initTask.getBackupWeekdays()[3] == true) {
					cb_thursday.setSelected(true);
				}
				if (initTask.getBackupWeekdays()[4] == true) {
					cb_friday.setSelected(true);
				}
				if (initTask.getBackupWeekdays()[5] == true) {
					cb_saturday.setSelected(true);
				}
				if (initTask.getBackupWeekdays()[6] == true) {
					cb_sunday.setSelected(true);
				}
			}
			if (initTask.getAutoBackupMode() == 2) {
				rb_dayInMonth.setSelected(true);
				if (initTask.getBackupDaysInMonth()[0] == true) {
					cb_day1.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[1] == true) {
					cb_day2.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[2] == true) {
					cb_day3.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[3] == true) {
					cb_day4.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[4] == true) {
					cb_day5.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[5] == true) {
					cb_day6.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[6] == true) {
					cb_day7.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[7] == true) {
					cb_day8.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[8] == true) {
					cb_day9.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[9] == true) {
					cb_day10.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[10] == true) {
					cb_day11.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[11] == true) {
					cb_day12.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[12] == true) {
					cb_day13.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[13] == true) {
					cb_day14.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[14] == true) {
					cb_day15.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[15] == true) {
					cb_day16.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[16] == true) {
					cb_day17.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[17] == true) {
					cb_day18.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[18] == true) {
					cb_day19.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[19] == true) {
					cb_day20.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[20] == true) {
					cb_day21.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[21] == true) {
					cb_day22.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[22] == true) {
					cb_day23.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[23] == true) {
					cb_day24.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[24] == true) {
					cb_day25.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[25] == true) {
					cb_day26.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[26] == true) {
					cb_day27.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[27] == true) {
					cb_day28.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[28] == true) {
					cb_day29.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[29] == true) {
					cb_day30.setSelected(true);
				}
				if (initTask.getBackupDaysInMonth()[30] == true) {
					cb_day31.setSelected(true);
				}
			}
			if (initTask.getAutoBackupMode() == 3) {
				rb_interval.setSelected(true);
				tf_intervall.setText(String.valueOf(initTask.getIntervalTime()));
				cb_unit.setValue(initTask.getIntervalUnit());
			}
			tf_startAt.setText(initTask.getStartTime().toString());
			if (initTask.isCatchUpEnabled()) {
				cb_catchUpBackups.setSelected(true);
				cb_catchUpComboBox.setValue(initTask.getCatchUpTime());
			}
		}
		//Auto-Clean Einstellungen:
		if (initTask.simpleAutoCleanIsEnabled()) {
			rb_autoclean_simple.setSelected(true);
			sp_keep_last_x_backups.getValueFactory().setValue(initTask.getNumberOfBackupsToKeep());
		}
		if (initTask.extendedAutoCleanIsEnabled()) {
			rb_autoclean_extended.setSelected(true);
			sp_number_of_rules.getValueFactory().setValue(initTask.getNumberOfExtendedCleanRules());
			int numberOfRules = initTask.getNumberOfExtendedCleanRules();
			if (numberOfRules >= 1) {
				sp_rule_1.getValueFactory().setValue(initTask.getThreshold()[0]);
				cb_rule_1_unit.setValue(initTask.getThresholdUnits()[0]);
				sp_rule_1_number.getValueFactory().setValue(initTask.getBackupsToKeep()[0]);
			}
			if (numberOfRules >= 2) {
				sp_rule_2.getValueFactory().setValue(initTask.getThreshold()[1]);
				cb_rule_2_unit.setValue(initTask.getThresholdUnits()[1]);
				sp_rule_2_number.getValueFactory().setValue(initTask.getBackupsToKeep()[1]);
			}
			if (numberOfRules >= 3) {
				sp_rule_3.getValueFactory().setValue(initTask.getThreshold()[2]);
				cb_rule_3_unit.setValue(initTask.getThresholdUnits()[2]);
				sp_rule_3_number.getValueFactory().setValue(initTask.getBackupsToKeep()[2]);
			}

			if (numberOfRules >= 4) {
				sp_rule_4.getValueFactory().setValue(initTask.getThreshold()[3]);
				cb_rule_4_unit.setValue(initTask.getThresholdUnits()[3]);
				sp_rule_4_number.getValueFactory().setValue(initTask.getBackupsToKeep()[3]);
			}
			if (numberOfRules >= 5) {
				sp_rule_5_number.getValueFactory().setValue(initTask.getBackupsToKeep()[4]);
			}

		}
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
		for (Source source : ol_sources) {
			File currentSource = new File(source.getPath());
			if (currentSource == null || !currentSource.exists()) {
				// show error message
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Invalid source.");
				alert.setContentText("The following source path is not valid: " + source.getPath());

				Optional<ButtonType> result = alert.showAndWait();
				return;
			}
			newTask.addSourcePath(source);
		}
		newTask.setDestinationPath(tf_destPath.getText());

		if (cb_destinationVerification.isSelected()) {
			newTask.setDestinationVerification(true);
		}

		if (cb_autostart.isSelected()) {
			newTask.setAutostart(true);
		}

		// auto-backup settings:
		if (rb_weekday.isSelected()) {
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

		} else if (rb_dayInMonth.isSelected()) {
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
		} else if (rb_interval.isSelected()) {
			// intervall:
			newTask.setAutoBackupMode(3);
			newTask.setIntervalTime(Integer.parseInt(tf_intervall.getText()));
			newTask.setIntervalUnit(cb_unit.getValue().toString());

		}
		if (rb_weekday.isSelected() || rb_dayInMonth.isSelected() || rb_interval.isSelected()) {
			// start-zeit:
			try {
				newTask.setBackupStartTime(LocalTime.parse(tf_startAt.getText()));
			} catch (DateTimeParseException e) {
				System.out.println("Error: " + e.toString());
			}
			if (cb_catchUpBackups.isSelected()) {
				newTask.setCatchUpEnabled(true);
				newTask.setCatchUpTime(cb_catchUpComboBox.getValue().toString());
			}
		}

		// auto-clean settings:
		if (rb_autoclean_simple.isSelected()) {
			newTask.setSimpleAutoCleanEnabled(true);
			newTask.setNumberOfBackupsToKeep(Integer.parseInt(sp_keep_last_x_backups.getValue().toString()));
		}
		if (rb_autoclean_extended.isSelected()) {
			newTask.setExtendedAutoCleanEnabled(true);
			int numberOfRules = sp_number_of_rules.getValue();
			newTask.setNumberOfExtendedAutoCleanRules(numberOfRules);
			int[] threshold = new int[numberOfRules];
			String[] thresholdUnits = new String[numberOfRules];
			int[] backupsToKeep = new int[numberOfRules];

			if (numberOfRules >= 1) {
				threshold[0] = sp_rule_1.getValue();
				thresholdUnits[0] = cb_rule_1_unit.getValue().toString();
				backupsToKeep[0] = sp_rule_1_number.getValue();
			}
			if (numberOfRules >= 2) {
				threshold[1] = sp_rule_2.getValue();
				thresholdUnits[1] = cb_rule_2_unit.getValue().toString();
				backupsToKeep[1] = sp_rule_2_number.getValue();
			}
			if (numberOfRules >= 3) {
				threshold[2] = sp_rule_3.getValue();
				thresholdUnits[2] = cb_rule_3_unit.getValue().toString();
				backupsToKeep[2] = sp_rule_3_number.getValue();
			}
			if (numberOfRules >= 4) {
				threshold[3] = sp_rule_4.getValue();
				thresholdUnits[3] = cb_rule_4_unit.getValue().toString();
				backupsToKeep[3] = sp_rule_4_number.getValue();
			}
			if (numberOfRules >= 5) {
				backupsToKeep[4] = sp_rule_5_number.getValue();
			}
			newTask.setThreshold(threshold);
			newTask.setThresholdUnits(thresholdUnits);
			newTask.setBackupsToKeep(backupsToKeep);
		}

		//Prüfen ob bereits ein BackupTask mit diesem Namen existiert:
		if (initTask == null && backupTaskDialogListener.backupTaskWithNameExisting(newTask.getTaskName())) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Warning");
			alert.setHeaderText("BackupTask with this name already exists.");
			alert.setContentText("Old BackupTask with same name will be overwritten!");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				backupTaskDialogListener.deleteBackupTaskWithName(newTask.getTaskName());
			} else {
				return;
			}
		}
		if (initTask != null) {
			backupTaskDialogListener.deleteBackupTaskWithName(newTask.getTaskName());
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

		if (rb_autoclean_extended.isSelected()) {
			//last rule_unit and only it must be "inf"
			if (sp_number_of_rules.getValue() == 1) {
				if (!cb_rule_1_unit.getValue().equals("inf")) {
					return false;
				}
			} else if (sp_number_of_rules.getValue() == 2) {
				if (cb_rule_1_unit.getValue().equals("inf") || !cb_rule_2_unit.getValue().equals("inf")) {
					return false;
				}
			} else if (sp_number_of_rules.getValue() == 3) {
				if (cb_rule_1_unit.getValue().equals("inf") || cb_rule_2_unit.getValue().equals("inf") ||
						!cb_rule_3_unit.getValue().equals("inf")) {
					return false;
				}
			} else if (sp_number_of_rules.getValue() == 4) {
				if (cb_rule_1_unit.getValue().equals("inf") || cb_rule_2_unit.getValue().equals("inf") ||
						cb_rule_3_unit.getValue().equals("inf") || !cb_rule_4_unit.getValue().equals("inf")) {
					return false;
				}
			} else if (sp_number_of_rules.getValue() == 5) {
				if (cb_rule_1_unit.getValue().equals("inf") || cb_rule_2_unit.getValue().equals("inf") ||
						cb_rule_3_unit.getValue().equals("inf") || cb_rule_4_unit.getValue().equals("inf")) {
					return false;
				}
			}

			//time slots have to be rising:
			if (sp_number_of_rules.getValue() == 2) {
				// check whether rule 1 is "smaller" than rule 2
				if (getValueOfRuleInMinutes(2) < getValueOfRuleInMinutes(1)) {
					return false;
				}
			} else if (sp_number_of_rules.getValue() == 3) {
				// check whether rule 1 is "smaller" than rule 2
				if (getValueOfRuleInMinutes(2) < getValueOfRuleInMinutes(1)) {
					return false;
				}
				if (getValueOfRuleInMinutes(3) < getValueOfRuleInMinutes(2)) {
					return false;
				}
			} else if (sp_number_of_rules.getValue() == 4 || sp_number_of_rules.getValue() == 5) {
				// check whether rule 1 is "smaller" than rule 2
				if (getValueOfRuleInMinutes(2) < getValueOfRuleInMinutes(1)) {
					return false;
				}
				if (getValueOfRuleInMinutes(3) < getValueOfRuleInMinutes(2)) {
					return false;
				}
				if (getValueOfRuleInMinutes(4) < getValueOfRuleInMinutes(3)) {
					return false;
				}
			}
		}

		return true;
	}

	private int getValueOfRuleInMinutes(int numberOfRuleToCheck) {
		assert numberOfRuleToCheck > 0 && numberOfRuleToCheck < 5;

		String unit;
		int value;

		if (numberOfRuleToCheck == 1) {
			unit = String.valueOf(cb_rule_1_unit.getValue());
			value = sp_rule_1.getValue();
		} else if (numberOfRuleToCheck == 2) {
			unit = String.valueOf(cb_rule_2_unit.getValue());
			value = sp_rule_2.getValue();
		} else if (numberOfRuleToCheck == 3) {
			unit = String.valueOf(cb_rule_3_unit.getValue());
			value = sp_rule_3.getValue();
		} else {
			unit = String.valueOf(cb_rule_4_unit.getValue());
			value = sp_rule_4.getValue();
		}
		if (unit.equals("min")) {
			return sp_rule_1.getValue();
		} else if (unit.equals("hour(s)")) {
			return sp_rule_1.getValue() * 60;
		} else if (unit.equals("days(s)")) {
			return sp_rule_1.getValue() * 60 * 24;
		} else if (unit.equals("days(s)")) {
			return sp_rule_1.getValue() * 60 * 24 * 7;
		} else {
			return sp_rule_1.getValue() * 60 * 24 * 7 * 365;
		}
	}

	@FXML
	public void addSourcePathAction() {
		startSourcesDialog(null);
	}

	public void editSourcePathAction() {
		int selectedIndex = lv_sources.getSelectionModel().getSelectedIndex();
		if (selectedIndex == -1) {
			return;
		}
		Source source = lv_sources.getSelectionModel().getSelectedItem();
		// CONTINUE: handle filter
		startSourcesDialog(source);
	}

	public void removeSourcePathAction() {
		int selectedIndex = lv_sources.getSelectionModel().getSelectedIndex();
		if (selectedIndex == -1) {
			return;
		}
		ol_sources.remove(selectedIndex);
	}

	public void addDestinationPath() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("choose DestinationPath");
		File destPath = dirChooser.showDialog(stage);
		if (destPath != null) {
			tf_destPath.setText(destPath.getAbsolutePath());
		}

	}

	public void startSourcesDialog(Source source) {
		final Stage sourcesDialogStage = new Stage(StageStyle.UTILITY);
		sourcesDialogStage.initModality(Modality.APPLICATION_MODAL);
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("SourcesDialog.fxml"));
			Scene scene = new Scene(loader.load());
			SourcesDialog sourcesDialog = loader.getController();
			sourcesDialog.init(new ISourcesDialogListener() {
				@Override
				public boolean isAlreadyCoveredByExistingSource(String path) {
					Iterator itr = ol_sources.iterator();
					while (itr.hasNext()) {
						Source currentSource = (Source)itr.next();
						if (path.startsWith(currentSource.getPath())) {
							return true;
						}
					}
					return false;
				}

				@Override
				public void addSource(Source source) {
					ol_sources.add(source);
					lv_sources.setItems(ol_sources);
				}
			});
			sourcesDialogStage.setScene(scene);
			sourcesDialog.setStage(sourcesDialogStage);

			// set initial values:
			if (source != null) {
				sourcesDialog.setPath(source.getPath());
				ArrayList<Filter> filters = source.getFilter();
				for (int i = 0; i < filters.size(); i++) {
					Filter current_filter = filters.get(i);
					sourcesDialog.ol_filters.add(new SourceFilterCellContent(current_filter.getPath(),
							current_filter.getMode()));
				}
			}

			sourcesDialogStage.showAndWait();
		} catch (IOException e) {
			System.err.println("IOException while starting SourcesDialog: " + e.toString());
		}
	}

	@FXML
	public void numberOfRulesChanged() {
		System.out.println("test");
	}
}
