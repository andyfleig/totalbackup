/*
 * Copyright 2014, 2015 Andreas Fleig (andy DOT fleig AT gmail DOT com)
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

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Panel;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.AttributeSet;

import listener.IEditDialogListener;
import listener.ISourcesDialogListener;
import data.BackupTask;
import data.Filter;
import data.Source;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTabbedPane;

import java.awt.GridLayout;

/**
 * Dialog zum Bearbeiten des BackupTasks.
 * 
 * @author Andreas Fleig
 *
 */
public class EditDialog extends JDialog {

	private IEditDialogListener editListener;

	private final JPanel panel_main = new JPanel();
	private JTextField textfield_name;

	private JList<Source> list_sourcePaths;
	private DefaultListModel<Source> listModel;

	private File sourceFile;
	private JTextField textfield_destination;

	private JRadioButton radioButton_normalBackup;
	private JRadioButton radioButton_hardlinkBackup;

	private JCheckBox checkBox_toggleSimpleSettings;
	private JCheckBox checkBox_toggleExtendedSettings;
	private JCheckBox checkBox_autostart;
	private JCheckBox checkBox_destinationVerification;
	private JSpinner spinner_numberOfBackupsToKeep;

	private SourcesDialog sourcesDialog;
	private ISourcesDialogListener sourcesListener;

	// Vorlagen für die Spinner:
	private int[] template_number_min = { 1, 1, 59, 1 };
	private int[] template_number_h = { 1, 1, 23, 1 };
	private int[] template_number_d = { 1, 1, 29, 1 };
	private int[] template_number_m = { 1, 1, 11, 1 };
	private int[] template_number_y = { 1, 1, 10, 1 };

	// Vorlagen für die Komboboxen mit den Zeitintervallen:
	String[] template_timeInterval_all = { "inf", "min", "h", "d", "m", "y" };
	String[] template_timeInterval_h = { "inf", "h", "d", "m", "y" };
	String[] template_timeInterval_d = { "inf", "d", "m", "y" };
	String[] template_timeInterval_m = { "inf", "m", "y" };
	String[] template_timeInterval_y = { "inf", "y" };

	String[] template_backupSets = { "all", "50", "45", "40", "35", "30", "25", "20", "15", "10", "9", "8", "7", "6",
			"5", "4", "3", "2", "1" };

	private JComboBox<String>[] unitComboBoxes;
	private JComboBox<String>[] toKeepComboBoxes;
	private JSpinner[] spinners;
	private JComboBox<String> comboBox_numberOfRules;

	private JCheckBox[] daysOfMonthCheckboxes;

	private JLabel label_eS1_now;
	private JLabel label_eS1_to;
	private JLabel label_eS2_to;
	private JLabel label_eS3_to;
	private JLabel label_eS4_to;
	private JLabel label_eS5_to;
	private JLabel label_eS1_toKeep;
	private JLabel label_eS2_toKeep;
	private JLabel label_eS3_toKeep;
	private JLabel label_eS4_toKeep;
	private JLabel label_eS5_toKeep;
	private JLabel label_eS5_inf;

	private static final int MAX_NUMBER_OF_RULES = 5;

	/**
	 * Legt fest, ob gerade ein existierender Filter bearbeitet, oder ein neuer
	 * erzeugt wird.
	 */
	private boolean inEditMode;
	private JTextField textField_timeToStart;
	private JTextField textField_interval;

	private JCheckBox checkBox_toggleWeekday;
	private JCheckBox checkBox_toggleDayInMonth;
	private JCheckBox checkBox_toggleInterval;

	private JCheckBox checkBox_day1;
	private JCheckBox checkBox_day2;
	private JCheckBox checkBox_day3;
	private JCheckBox checkBox_day4;
	private JCheckBox checkBox_day5;
	private JCheckBox checkBox_day6;
	private JCheckBox checkBox_day7;
	private JCheckBox checkBox_day8;
	private JCheckBox checkBox_day9;
	private JCheckBox checkBox_day10;
	private JCheckBox checkBox_day11;
	private JCheckBox checkBox_day12;
	private JCheckBox checkBox_day13;
	private JCheckBox checkBox_day14;
	private JCheckBox checkBox_day15;
	private JCheckBox checkBox_day16;
	private JCheckBox checkBox_day17;
	private JCheckBox checkBox_day18;
	private JCheckBox checkBox_day19;
	private JCheckBox checkBox_day20;
	private JCheckBox checkBox_day21;
	private JCheckBox checkBox_day22;
	private JCheckBox checkBox_day23;
	private JCheckBox checkBox_day24;
	private JCheckBox checkBox_day25;
	private JCheckBox checkBox_day26;
	private JCheckBox checkBox_day27;
	private JCheckBox checkBox_day28;
	private JCheckBox checkBox_day29;
	private JCheckBox checkBox_day30;
	private JCheckBox checkBox_day31;

	private JCheckBox checkBox_monday;
	private JCheckBox checkBox_tuesday;
	private JCheckBox checkBox_wednesday;
	private JCheckBox checkBox_thursday;
	private JCheckBox checkBox_friday;
	private JCheckBox checkBox_saturday;
	private JCheckBox checkBox_sunday;

	private JComboBox<String> comboBox_intervalUnit;
	private JComboBox<String> comboBox_catchUp;
	private JCheckBox checkBox_catchUp;

	/**
	 * @deprecated
	 */
	public static void main(String[] args) {
		/*
		 * try { Edit dialog = new Edit();
		 * dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		 * dialog.setVisible(true); } catch (Exception e) { e.printStackTrace();
		 * }
		 */
	}

	/**
	 * Erzeugt einen Edit-Dialog.
	 * 
	 */
	public EditDialog(IEditDialogListener listener) {
		JComboBox<String> comboBox_eS1_unit;
		JComboBox<String> comboBox_eS2_unit;
		JComboBox<String> comboBox_eS3_unit;
		JComboBox<String> comboBox_eS4_unit;
		JComboBox<String> comboBox_eS1_toKeep;
		JComboBox<String> comboBox_eS2_toKeep;
		JComboBox<String> comboBox_eS3_toKeep;
		JComboBox<String> comboBox_eS4_toKeep;
		JComboBox<String> comboBox_eS5_toKeep;
		JSpinner spinner_eS1;
		JSpinner spinner_eS2;
		JSpinner spinner_eS3;
		JSpinner spinner_eS4;

		unitComboBoxes = new JComboBox[MAX_NUMBER_OF_RULES - 1];
		toKeepComboBoxes = new JComboBox[MAX_NUMBER_OF_RULES];
		spinners = new JSpinner[MAX_NUMBER_OF_RULES - 1];

		daysOfMonthCheckboxes = new JCheckBox[31];

		setResizable(false);
		setTitle(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.title"));
		setBounds(100, 100, 512, 707);
		getContentPane().setLayout(new BorderLayout());
		panel_main.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel_main, BorderLayout.CENTER);
		panel_main.setLayout(new BorderLayout(0, 0));

		inEditMode = false;
		this.editListener = listener;

		JPanel panel_name = new JPanel();
		panel_main.add(panel_name, BorderLayout.NORTH);
		panel_name.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel label_name = new JLabel(ResourceBundle.getBundle("gui.messages").getString("GUI.label_name"));
		label_name.setHorizontalAlignment(SwingConstants.CENTER);
		panel_name.add(label_name);

		textfield_name = new JTextField();
		panel_name.add(textfield_name);
		textfield_name.setColumns(10);
		final ArrayList<Character> forbiddenChars = new ArrayList<Character>(Arrays.asList('_'));
		textfield_name.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				for (Character c : forbiddenChars) {
					str = str.replace(String.valueOf(c), "");
				}
				super.insertString(offs, str, a);
			}
		});

		// Sources-Listener:
		sourcesListener = new ISourcesDialogListener() {

			@Override
			public boolean isAlreadySourcePath(String path) {
				return EditDialog.this.isAlreadySourcePath(path);
			}

			@Override
			public void addSource(Source source) {
				listModel.addElement(source);
			}

			@Override
			public void deleteSource(String path) {
				for (int i = 0; i < listModel.getSize(); i++) {
					if (listModel.get(i).getPath().equals(path)) {
						listModel.remove(i);
						return;
					}
				}
			}
		};

		JPanel panel_backupConfig = new JPanel();
		panel_main.add(panel_backupConfig, BorderLayout.CENTER);

		Panel panel_setup = new Panel();
		panel_backupConfig.add(panel_setup);
		panel_setup.setLayout(new BorderLayout(0, 0));

		JPanel panel_source = new JPanel();
		panel_setup.add(panel_source, BorderLayout.NORTH);
		panel_source.setLayout(new BorderLayout(0, 0));
		JLabel label_source = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_sources"));
		panel_source.add(label_source, BorderLayout.NORTH);

		JPanel panel_sourcePaths = new JPanel();
		panel_source.add(panel_sourcePaths, BorderLayout.CENTER);
		panel_sourcePaths.setLayout(new BorderLayout(0, 0));

		listModel = new DefaultListModel<Source>();

		list_sourcePaths = new JList<Source>(listModel);
		list_sourcePaths.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list_sourcePaths.setSelectedIndex(0);
		list_sourcePaths.setVisibleRowCount(6);

		JScrollPane listScroller_sourcePaths = new JScrollPane(list_sourcePaths);
		panel_sourcePaths.add(listScroller_sourcePaths);
		listScroller_sourcePaths.setMaximumSize(new Dimension(200, 200));
		listScroller_sourcePaths.setMinimumSize(new Dimension(200, 200));

		Panel panel_sourcePathsEdit = new Panel();
		panel_sourcePaths.add(panel_sourcePathsEdit, BorderLayout.EAST);
		panel_sourcePathsEdit.setLayout(new BoxLayout(panel_sourcePathsEdit, BoxLayout.Y_AXIS));

		// Button Add:
		JButton button_add = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_add"));
		button_add.setAlignmentX(CENTER_ALIGNMENT);
		button_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sourcesDialog = new SourcesDialog(sourcesListener);
				sourcesDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				sourcesDialog.setLocation(EditDialog.this.getLocationOnScreen());
				sourcesDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
				sourcesDialog.setVisible(true);
			}
		});
		panel_sourcePathsEdit.add(button_add);

		// Button Delete:
		JButton button_delete = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_delete"));
		button_delete.setAlignmentX(CENTER_ALIGNMENT);
		button_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!list_sourcePaths.isSelectionEmpty()) {
					int reply = JOptionPane.showConfirmDialog(null,
							ResourceBundle.getBundle("gui.messages").getString("Messages.DeleteSource"), null,
							JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.YES_OPTION) {
						listModel.remove(list_sourcePaths.getSelectedIndex());
					}
				}

			}
		});

		// Button Edit:
		JButton button_edit = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_edit"));
		button_edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (list_sourcePaths.isSelectionEmpty()) {
					return;
				}
				sourcesDialog = new SourcesDialog(sourcesListener);
				sourcesDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				sourcesDialog.setEditMode(true);

				Source currentSource = list_sourcePaths.getSelectedValue();
				sourcesDialog.setOriginalPath(currentSource.getPath());
				sourcesDialog.setSource(currentSource);

				ArrayList<Filter> filterOfCurrentSource = currentSource.getFilter();

				for (int i = 0; i < filterOfCurrentSource.size(); i++) {
					sourcesDialog.addFilter(filterOfCurrentSource.get(i).getPath(),
							filterOfCurrentSource.get(i).getMode());
				}
				sourcesDialog.setLocation(EditDialog.this.getLocationOnScreen());
				sourcesDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
				sourcesDialog.setVisible(true);

			}
		});

		button_edit.setAlignmentX(0.5f);
		panel_sourcePathsEdit.add(button_edit);

		panel_sourcePathsEdit.add(button_delete);

		Panel panel_destination = new Panel();
		panel_setup.add(panel_destination, BorderLayout.CENTER);

		JLabel label_destination = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_destination"));
		panel_destination.add(label_destination);

		textfield_destination = new JTextField();
		panel_destination.add(textfield_destination);
		textfield_destination.setColumns(20);

		// Button Find:
		JButton button_find = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_find"));
		button_find.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int state = fc.showOpenDialog(null);
				if (state == JFileChooser.APPROVE_OPTION) {
					sourceFile = fc.getSelectedFile();
					textfield_destination.setText(sourceFile.getAbsolutePath());
				}
			}
		});

		panel_destination.add(button_find);

		JPanel panel_otherSettings = new JPanel();
		panel_setup.add(panel_otherSettings, BorderLayout.SOUTH);
		panel_otherSettings.setLayout(new BorderLayout(0, 0));

		JPanel panel_settings2 = new JPanel();
		panel_otherSettings.add(panel_settings2, BorderLayout.NORTH);
		panel_settings2.setLayout(new BoxLayout(panel_settings2, BoxLayout.Y_AXIS));

		checkBox_destinationVerification = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.chckbxDestinationverification.text"));
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			checkBox_destinationVerification.setSelected(true);
			checkBox_destinationVerification.setEnabled(true);
		} else {
			checkBox_destinationVerification.setSelected(false);
			checkBox_destinationVerification.setEnabled(false);
		}
		panel_settings2.add(checkBox_destinationVerification);

		checkBox_autostart = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.autostart"));
		panel_settings2.add(checkBox_autostart);

		JTabbedPane tabbedPane_auto = new JTabbedPane(JTabbedPane.TOP);
		panel_otherSettings.add(tabbedPane_auto, BorderLayout.SOUTH);

		JTabbedPane tabbedPane_autostart = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_auto.addTab(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.tabbedPane_autostart.title"), null,
				tabbedPane_autostart, null);

		JPanel panel_time = new JPanel();
		tabbedPane_autostart.addTab(ResourceBundle.getBundle("gui.messages").getString("EditDialog.panel.title_2"),
				null, panel_time, null);
		panel_time.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane_time = new JTabbedPane(JTabbedPane.TOP);
		panel_time.add(tabbedPane_time, BorderLayout.NORTH);

		JPanel panel_day = new JPanel();
		tabbedPane_time.addTab(ResourceBundle.getBundle("gui.messages").getString("EditDialog.panel_day.title"), null,
				panel_day, null);
		panel_day.setLayout(new BorderLayout(0, 0));

		checkBox_toggleWeekday = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.activated"));
		panel_day.add(checkBox_toggleWeekday, BorderLayout.NORTH);

		JPanel panel_daySelection = new JPanel();
		panel_day.add(panel_daySelection, BorderLayout.CENTER);
		panel_daySelection.setLayout(new BorderLayout(0, 0));

		JLabel label_daySelection = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.lblTageAnDenen.text"));
		label_daySelection.setHorizontalAlignment(SwingConstants.LEFT);
		panel_daySelection.add(label_daySelection, BorderLayout.NORTH);

		JPanel panel_weekdays = new JPanel();
		panel_weekdays.setLayout(new BoxLayout(panel_weekdays, BoxLayout.Y_AXIS));
		panel_daySelection.add(panel_weekdays, BorderLayout.CENTER);

		checkBox_monday = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.chckbxMontag.text"));
		panel_weekdays.add(checkBox_monday);

		checkBox_tuesday = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.chckbxDienstag.text"));
		panel_weekdays.add(checkBox_tuesday);

		checkBox_wednesday = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.chckbxMittwoch.text"));
		panel_weekdays.add(checkBox_wednesday);

		checkBox_thursday = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.chckbxDonnerstag.text"));
		panel_weekdays.add(checkBox_thursday);

		checkBox_friday = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.chckbxFreitag.text"));
		panel_weekdays.add(checkBox_friday);

		checkBox_saturday = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.chckbxSamstag.text"));
		panel_weekdays.add(checkBox_saturday);

		checkBox_sunday = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.chckbxSonntag.text"));
		panel_weekdays.add(checkBox_sunday);

		JPanel panel_dayInMonth = new JPanel();
		tabbedPane_time.addTab(ResourceBundle.getBundle("gui.messages").getString("EditDialog.panel_dayInMonth.title"),
				null, panel_dayInMonth, null);
		panel_dayInMonth.setLayout(new BorderLayout(0, 0));

		checkBox_toggleDayInMonth = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.activated"));
		panel_dayInMonth.add(checkBox_toggleDayInMonth, BorderLayout.NORTH);

		JPanel panel_dayInMonthSelection = new JPanel();
		panel_dayInMonth.add(panel_dayInMonthSelection, BorderLayout.CENTER);
		panel_dayInMonthSelection.setLayout(new BorderLayout(0, 0));

		JLabel label_dayInMonthSelection = new JLabel(
				(ResourceBundle.getBundle("gui.messages").getString("EditDialog.lblTageAnDenen.text")));
		label_dayInMonthSelection.setHorizontalAlignment(SwingConstants.LEFT);
		panel_dayInMonthSelection.add(label_dayInMonthSelection, BorderLayout.NORTH);

		JPanel panel_days = new JPanel();
		panel_dayInMonthSelection.add(panel_days, BorderLayout.CENTER);
		panel_days.setLayout(new GridLayout(4, 8, 0, 0));

		// Checkboxen für die Tage im Monat:
		checkBox_day1 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day1.text"));
		panel_days.add(checkBox_day1);

		checkBox_day2 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day2.text"));
		panel_days.add(checkBox_day2);

		checkBox_day3 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day3.text"));
		panel_days.add(checkBox_day3);

		checkBox_day4 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day4.text"));
		panel_days.add(checkBox_day4);

		checkBox_day5 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day5.text"));
		panel_days.add(checkBox_day5);

		checkBox_day6 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day6.text"));
		panel_days.add(checkBox_day6);

		checkBox_day7 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day7.text"));
		panel_days.add(checkBox_day7);

		checkBox_day8 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day8.text"));
		panel_days.add(checkBox_day8);

		checkBox_day9 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day9.text"));
		panel_days.add(checkBox_day9);

		checkBox_day10 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day10.text"));
		panel_days.add(checkBox_day10);

		checkBox_day11 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day11.text"));
		panel_days.add(checkBox_day11);

		checkBox_day12 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day12.text"));
		panel_days.add(checkBox_day12);

		checkBox_day13 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day13.text"));
		panel_days.add(checkBox_day13);

		checkBox_day14 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day14.text"));
		panel_days.add(checkBox_day14);

		checkBox_day15 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day15.text"));
		panel_days.add(checkBox_day15);

		checkBox_day16 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day16.text"));
		panel_days.add(checkBox_day16);

		checkBox_day17 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day17.text"));
		panel_days.add(checkBox_day17);

		checkBox_day18 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day18.text"));
		panel_days.add(checkBox_day18);

		checkBox_day19 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day19.text"));
		panel_days.add(checkBox_day19);

		checkBox_day20 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day20.text"));
		panel_days.add(checkBox_day20);

		checkBox_day21 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day21.text"));
		panel_days.add(checkBox_day21);

		checkBox_day22 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day22.text"));
		panel_days.add(checkBox_day22);

		checkBox_day23 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day23.text"));
		panel_days.add(checkBox_day23);

		checkBox_day24 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day24.text"));
		panel_days.add(checkBox_day24);

		checkBox_day25 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day25.text"));
		panel_days.add(checkBox_day25);

		checkBox_day26 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day26.text"));
		panel_days.add(checkBox_day26);

		checkBox_day27 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day27.text"));
		panel_days.add(checkBox_day27);

		checkBox_day28 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day28.text"));
		panel_days.add(checkBox_day28);

		checkBox_day29 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day29.text"));
		panel_days.add(checkBox_day29);

		checkBox_day30 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day30.text"));
		panel_days.add(checkBox_day30);

		checkBox_day31 = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_day31.text"));
		panel_days.add(checkBox_day31);

		daysOfMonthCheckboxes[0] = checkBox_day1;
		daysOfMonthCheckboxes[1] = checkBox_day2;
		daysOfMonthCheckboxes[2] = checkBox_day3;
		daysOfMonthCheckboxes[3] = checkBox_day4;
		daysOfMonthCheckboxes[4] = checkBox_day5;
		daysOfMonthCheckboxes[5] = checkBox_day6;
		daysOfMonthCheckboxes[6] = checkBox_day7;
		daysOfMonthCheckboxes[7] = checkBox_day8;
		daysOfMonthCheckboxes[8] = checkBox_day9;
		daysOfMonthCheckboxes[9] = checkBox_day10;
		daysOfMonthCheckboxes[10] = checkBox_day11;
		daysOfMonthCheckboxes[11] = checkBox_day12;
		daysOfMonthCheckboxes[12] = checkBox_day13;
		daysOfMonthCheckboxes[13] = checkBox_day14;
		daysOfMonthCheckboxes[14] = checkBox_day15;
		daysOfMonthCheckboxes[15] = checkBox_day16;
		daysOfMonthCheckboxes[16] = checkBox_day17;
		daysOfMonthCheckboxes[17] = checkBox_day18;
		daysOfMonthCheckboxes[18] = checkBox_day19;
		daysOfMonthCheckboxes[19] = checkBox_day20;
		daysOfMonthCheckboxes[20] = checkBox_day21;
		daysOfMonthCheckboxes[21] = checkBox_day22;
		daysOfMonthCheckboxes[22] = checkBox_day23;
		daysOfMonthCheckboxes[23] = checkBox_day24;
		daysOfMonthCheckboxes[24] = checkBox_day25;
		daysOfMonthCheckboxes[25] = checkBox_day26;
		daysOfMonthCheckboxes[26] = checkBox_day27;
		daysOfMonthCheckboxes[27] = checkBox_day28;
		daysOfMonthCheckboxes[28] = checkBox_day29;
		daysOfMonthCheckboxes[29] = checkBox_day30;
		daysOfMonthCheckboxes[30] = checkBox_day31;

		JPanel panel_timeToStart = new JPanel();
		panel_time.add(panel_timeToStart, BorderLayout.CENTER);

		JLabel label_startAt = new JLabel(ResourceBundle.getBundle("gui.messages").getString("EditDialog.label.text"));
		panel_timeToStart.add(label_startAt);

		textField_timeToStart = new JTextField();
		textField_timeToStart.setToolTipText(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.textField_timeToStart.toolTipText"));
		panel_timeToStart.add(textField_timeToStart);
		textField_timeToStart.setColumns(10);

		JPanel panel_catchUp = new JPanel();
		panel_time.add(panel_catchUp, BorderLayout.SOUTH);

		checkBox_catchUp = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.checkBox_catchUp"));
		checkBox_catchUp.setSelected(true);
		panel_catchUp.add(checkBox_catchUp);

		comboBox_catchUp = new JComboBox<String>();
		comboBox_catchUp.setLightWeightPopupEnabled(false);
		comboBox_catchUp.setModel(new DefaultComboBoxModel<String>(
				new String[] { "5min", "10min", "15min", "30min", "1h", "2h", "6h", "12h", "24h" }));
		panel_catchUp.add(comboBox_catchUp);

		JPanel panel_interval = new JPanel();
		tabbedPane_autostart.addTab(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.panel_interval.title"), null,
				panel_interval, null);
		panel_interval.setLayout(new BorderLayout(0, 0));

		checkBox_toggleInterval = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.activated"));
		panel_interval.add(checkBox_toggleInterval, BorderLayout.NORTH);

		JPanel panel_intervalSettings = new JPanel();
		panel_interval.add(panel_intervalSettings);

		JLabel label_all = new JLabel(ResourceBundle.getBundle("gui.messages").getString("EditDialog.lblAlle.text"));
		panel_intervalSettings.add(label_all);

		textField_interval = new JTextField();
		panel_intervalSettings.add(textField_interval);
		textField_interval.setColumns(10);

		comboBox_intervalUnit = new JComboBox<String>();
		comboBox_intervalUnit.setLightWeightPopupEnabled(false);
		comboBox_intervalUnit.setLightWeightPopupEnabled(false);
		panel_intervalSettings.add(comboBox_intervalUnit);
		comboBox_intervalUnit.setModel(new DefaultComboBoxModel<String>(new String[] { "min", "h", "d", "m" }));

		JPanel panel_simpleSettings = new JPanel();
		panel_simpleSettings.setLayout(new BorderLayout(0, 0));

		JPanel panel_toggleSimpleSettings = new JPanel();
		panel_simpleSettings.add(panel_toggleSimpleSettings, BorderLayout.NORTH);
		panel_toggleSimpleSettings.setLayout(new BorderLayout(0, 0));

		checkBox_toggleSimpleSettings = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.activated"));
		checkBox_toggleSimpleSettings.setHorizontalAlignment(SwingConstants.LEFT);
		panel_toggleSimpleSettings.add(checkBox_toggleSimpleSettings);

		// Textfeld des Spinners nicht editierbar machen:
		// spinner_numberOfBackupsToKeep.setEditor(new
		// JSpinner.DefaultEditor(spinner_numberOfBackupsToKeep));

		JPanel panel_extendedSettings = new JPanel();

		JTabbedPane tabbedPane_autoclean = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_auto.addTab(
				ResourceBundle.getBundle("gui.messages").getString("EditDialog.tabbedPane_autoclean.title"), null,
				tabbedPane_autoclean, null);
		tabbedPane_autoclean.addTab(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.simple"),
				panel_simpleSettings);

		JPanel panel_settingsSimpleSettings = new JPanel();
		panel_simpleSettings.add(panel_settingsSimpleSettings);

		JLabel label_simple = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_simple"));
		panel_settingsSimpleSettings.add(label_simple);

		spinner_numberOfBackupsToKeep = new JSpinner();
		panel_settingsSimpleSettings.add(spinner_numberOfBackupsToKeep);
		spinner_numberOfBackupsToKeep
				.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		tabbedPane_autoclean.addTab(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.extended"),
				panel_extendedSettings);
		panel_extendedSettings.setLayout(new BorderLayout(0, 0));

		JPanel panel_toggleExtendedSettings = new JPanel();
		panel_extendedSettings.add(panel_toggleExtendedSettings, BorderLayout.NORTH);
		panel_toggleExtendedSettings.setLayout(new BorderLayout(0, 0));

		checkBox_toggleExtendedSettings = new JCheckBox(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.activated"));
		checkBox_toggleExtendedSettings.setHorizontalAlignment(SwingConstants.LEFT);
		panel_toggleExtendedSettings.add(checkBox_toggleExtendedSettings, BorderLayout.WEST);

		JPanel panel_numberOfRules = new JPanel();
		panel_toggleExtendedSettings.add(panel_numberOfRules, BorderLayout.EAST);

		JLabel label_numberOfRules = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_numberOfRules"));
		panel_numberOfRules.add(label_numberOfRules);

		comboBox_numberOfRules = new JComboBox<String>();
		comboBox_numberOfRules.setLightWeightPopupEnabled(false);
		comboBox_numberOfRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch (comboBox_numberOfRules.getSelectedItem().toString()) {
				case ("1"):
					setLayerEnabled(2, false);
					setLayerEnabled(3, false);
					setLayerEnabled(4, false);
					setLayerEnabled(5, false);
					break;
				case ("2"):
					setLayerEnabled(2, true);
					setLayerEnabled(3, false);
					setLayerEnabled(4, false);
					setLayerEnabled(5, false);
					break;
				case ("3"):
					setLayerEnabled(2, true);
					setLayerEnabled(3, true);
					setLayerEnabled(4, false);
					setLayerEnabled(5, false);
					break;
				case ("4"):
					setLayerEnabled(2, true);
					setLayerEnabled(3, true);
					setLayerEnabled(4, true);
					setLayerEnabled(5, false);
					break;
				case ("5"):
					setLayerEnabled(2, true);
					setLayerEnabled(3, true);
					setLayerEnabled(4, true);
					setLayerEnabled(5, true);
					break;
				}
			}
		});
		comboBox_numberOfRules.setModel(new DefaultComboBoxModel<String>(new String[] { "1", "2", "3", "4", "5" }));
		panel_numberOfRules.add(comboBox_numberOfRules);

		checkBox_toggleSimpleSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkBox_toggleExtendedSettings.setSelected(false);
			}
		});
		checkBox_toggleExtendedSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkBox_toggleSimpleSettings.setSelected(false);
			}
		});

		JPanel panel_settingsExtendedSettings = new JPanel();
		panel_settingsExtendedSettings.setLayout(new BoxLayout(panel_settingsExtendedSettings, BoxLayout.Y_AXIS));
		panel_extendedSettings.add(panel_settingsExtendedSettings);

		JPanel panel_eS1 = new JPanel();
		panel_settingsExtendedSettings.add(panel_eS1);
		FlowLayout flowLayout = (FlowLayout) panel_eS1.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);

		label_eS1_now = new JLabel(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_now"));
		panel_eS1.add(label_eS1_now);

		label_eS1_to = new JLabel(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_eSX_to"));
		panel_eS1.add(label_eS1_to);

		spinner_eS1 = new JSpinner();
		spinner_eS1.setModel(createSpinnerNumberModelFromTemplate(template_number_min));
		// spinner_eS1.setEditor(new JSpinner.DefaultEditor(spinner_eS1));

		panel_eS1.add(spinner_eS1);

		comboBox_eS1_unit = new JComboBox<String>();
		comboBox_eS1_unit.setLightWeightPopupEnabled(false);
		comboBox_eS1_unit.setModel(createComboBoxModelFromTemplate(template_timeInterval_all));
		panel_eS1.add(comboBox_eS1_unit);

		label_eS1_toKeep = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_eSX_toKeep"));
		panel_eS1.add(label_eS1_toKeep);

		comboBox_eS1_toKeep = new JComboBox<String>();
		comboBox_eS1_toKeep.setLightWeightPopupEnabled(false);
		comboBox_eS1_toKeep.setModel(new DefaultComboBoxModel<String>(template_backupSets));
		panel_eS1.add(comboBox_eS1_toKeep);

		JPanel panel_eS2 = new JPanel();
		panel_settingsExtendedSettings.add(panel_eS2);
		FlowLayout flowLayout_1 = (FlowLayout) panel_eS2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);

		label_eS2_to = new JLabel(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_eSX_to"));
		label_eS2_to.setEnabled(false);
		panel_eS2.add(label_eS2_to);

		spinner_eS2 = new JSpinner();
		spinner_eS2.setModel(createSpinnerNumberModelFromTemplate(template_number_min));
		panel_eS2.add(spinner_eS2);
		spinner_eS2.setEnabled(false);
		// spinner_eS2.setEditor(new JSpinner.DefaultEditor(spinner_eS2));

		comboBox_eS2_unit = new JComboBox<String>();
		comboBox_eS2_unit.setLightWeightPopupEnabled(false);
		comboBox_eS2_unit.setEnabled(false);
		comboBox_eS2_unit.setModel(createComboBoxModelFromTemplate(template_timeInterval_all));
		panel_eS2.add(comboBox_eS2_unit);

		label_eS2_toKeep = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_eSX_toKeep"));
		label_eS2_toKeep.setEnabled(false);
		panel_eS2.add(label_eS2_toKeep);

		comboBox_eS2_toKeep = new JComboBox<String>();
		comboBox_eS2_toKeep.setLightWeightPopupEnabled(false);
		comboBox_eS2_toKeep.setEnabled(false);
		comboBox_eS2_toKeep.setModel(new DefaultComboBoxModel<String>(template_backupSets));
		panel_eS2.add(comboBox_eS2_toKeep);

		JPanel panel_eS3 = new JPanel();
		panel_settingsExtendedSettings.add(panel_eS3);
		FlowLayout fl_panel_eS3 = (FlowLayout) panel_eS3.getLayout();
		fl_panel_eS3.setAlignment(FlowLayout.RIGHT);

		label_eS3_to = new JLabel(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_eSX_to"));
		label_eS3_to.setEnabled(false);
		panel_eS3.add(label_eS3_to);

		spinner_eS3 = new JSpinner();
		spinner_eS3.setModel(createSpinnerNumberModelFromTemplate(template_number_min));
		panel_eS3.add(spinner_eS3);
		spinner_eS3.setEnabled(false);

		comboBox_eS3_unit = new JComboBox<String>();
		comboBox_eS3_unit.setLightWeightPopupEnabled(false);
		comboBox_eS3_unit.setEnabled(false);
		comboBox_eS3_unit.setModel(createComboBoxModelFromTemplate(template_timeInterval_all));
		panel_eS3.add(comboBox_eS3_unit);

		label_eS3_toKeep = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_eSX_toKeep"));
		label_eS3_toKeep.setEnabled(false);
		panel_eS3.add(label_eS3_toKeep);

		comboBox_eS3_toKeep = new JComboBox<String>();
		comboBox_eS3_toKeep.setLightWeightPopupEnabled(false);
		comboBox_eS3_toKeep.setEnabled(false);
		comboBox_eS3_toKeep.setModel(new DefaultComboBoxModel<String>(template_backupSets));
		panel_eS3.add(comboBox_eS3_toKeep);

		JPanel panel_eS4 = new JPanel();
		panel_settingsExtendedSettings.add(panel_eS4);
		FlowLayout fl_panel_eS4 = (FlowLayout) panel_eS4.getLayout();
		fl_panel_eS4.setAlignment(FlowLayout.RIGHT);

		label_eS4_to = new JLabel(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_eSX_to"));
		label_eS4_to.setEnabled(false);
		panel_eS4.add(label_eS4_to);

		spinner_eS4 = new JSpinner();
		spinner_eS4.setModel(createSpinnerNumberModelFromTemplate(template_number_min));
		panel_eS4.add(spinner_eS4);
		spinner_eS4.setEnabled(false);

		comboBox_eS4_unit = new JComboBox<String>();
		comboBox_eS4_unit.setLightWeightPopupEnabled(false);
		comboBox_eS4_unit.setEnabled(false);
		comboBox_eS4_unit.setModel(createComboBoxModelFromTemplate(template_timeInterval_all));
		panel_eS4.add(comboBox_eS4_unit);

		label_eS4_toKeep = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_eSX_toKeep"));
		label_eS4_toKeep.setEnabled(false);
		panel_eS4.add(label_eS4_toKeep);

		comboBox_eS4_toKeep = new JComboBox<String>();
		comboBox_eS4_toKeep.setLightWeightPopupEnabled(false);
		comboBox_eS4_toKeep.setEnabled(false);
		comboBox_eS4_toKeep.setModel(new DefaultComboBoxModel<String>(template_backupSets));
		panel_eS4.add(comboBox_eS4_toKeep);

		JPanel panel_eS5 = new JPanel();
		panel_settingsExtendedSettings.add(panel_eS5);
		FlowLayout flowLayout_2 = (FlowLayout) panel_eS5.getLayout();
		flowLayout_2.setAlignment(FlowLayout.RIGHT);

		label_eS5_to = new JLabel(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_eSX_to"));
		label_eS5_to.setEnabled(false);
		panel_eS5.add(label_eS5_to);

		label_eS5_inf = new JLabel(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_eS5_inf"));
		label_eS5_inf.setEnabled(false);
		panel_eS5.add(label_eS5_inf);

		label_eS5_toKeep = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_eSX_toKeep"));
		label_eS5_toKeep.setEnabled(false);
		panel_eS5.add(label_eS5_toKeep);

		comboBox_eS5_toKeep = new JComboBox<String>();
		comboBox_eS5_toKeep.setLightWeightPopupEnabled(false);
		comboBox_eS5_toKeep.setEnabled(false);
		comboBox_eS5_toKeep.setModel(new DefaultComboBoxModel<String>(template_backupSets));
		panel_eS5.add(comboBox_eS5_toKeep);
		unitComboBoxes[0] = comboBox_eS1_unit;
		unitComboBoxes[1] = comboBox_eS2_unit;
		unitComboBoxes[2] = comboBox_eS3_unit;
		unitComboBoxes[3] = comboBox_eS4_unit;
		toKeepComboBoxes[0] = comboBox_eS1_toKeep;
		toKeepComboBoxes[1] = comboBox_eS2_toKeep;
		toKeepComboBoxes[2] = comboBox_eS3_toKeep;
		toKeepComboBoxes[3] = comboBox_eS4_toKeep;
		toKeepComboBoxes[4] = comboBox_eS5_toKeep;
		spinners[0] = spinner_eS1;
		spinners[1] = spinner_eS2;
		spinners[2] = spinner_eS3;
		spinners[3] = spinner_eS4;

		// Listener:
		comboBox_eS1_unit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeSpinner(unitComboBoxes[0], spinners[0]);
				if (unitComboBoxes[0].getSelectedItem().toString().equals("inf")) {
					spinners[0].setEnabled(false);
				} else {
					spinners[0].setEnabled(true);
				}
			}
		});
		comboBox_eS2_unit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeSpinner(unitComboBoxes[1], spinners[1]);
				if (unitComboBoxes[1].getSelectedItem().toString().equals("inf")) {
					spinners[1].setEnabled(false);
				} else {
					spinners[1].setEnabled(true);
				}
			}
		});
		comboBox_eS3_unit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeSpinner(unitComboBoxes[2], spinners[2]);
				if (unitComboBoxes[2].getSelectedItem().toString().equals("inf")) {
					spinners[2].setEnabled(false);
				} else {
					spinners[2].setEnabled(true);
				}
			}
		});
		comboBox_eS4_unit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeSpinner(unitComboBoxes[3], spinners[3]);
				if (unitComboBoxes[3].getSelectedItem().toString().equals("inf")) {
					spinners[3].setEnabled(false);
				} else {
					spinners[3].setEnabled(true);
				}
			}
		});

		JPanel panel_backupType = new JPanel();
		panel_main.add(panel_backupType, BorderLayout.SOUTH);

		JLabel label_properties = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_backupType"));
		panel_backupType.add(label_properties);

		// Spinner deaktivieren (default):
		for (JSpinner s : spinners) {
			s.setEnabled(false);
		}

		// JRadioButtons anlegen:
		radioButton_normalBackup = new JRadioButton(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.radioButton_Normal"));
		radioButton_normalBackup.setSelected(true);
		radioButton_hardlinkBackup = new JRadioButton(
				ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.radioButton_Hardlink"));

		// ButtonGroup anlegen:
		ButtonGroup btng_settings = new ButtonGroup();

		// JRadioButtons zur ButtonGroup hinzufügen:
		btng_settings.add(radioButton_normalBackup);
		btng_settings.add(radioButton_hardlinkBackup);

		// JRadioButtons zum Panel hinzufügen:
		panel_backupType.add(radioButton_normalBackup);
		panel_backupType.add(radioButton_hardlinkBackup);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		// Button OK:
		JButton btn_Ok = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_ok"));
		btn_Ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean deleteOldTask = false;
				BackupTask task;
				boolean allInputsAreValid = true;
				// Namen prüfen und BackupTask erstellen (wenn der Name
				// gültig ist):
				if (isValidName(textfield_name.getText())) {
					if (nameIsNotTaken(textfield_name.getText())) {
						task = new BackupTask(textfield_name.getText());
					} else {
						if (!inEditMode) {
							JOptionPane.showMessageDialog(null,
									ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.errSameName"),
									ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
									JOptionPane.INFORMATION_MESSAGE);
							return;
						} else {
							// task =
							// editListener.getBackupTaskWithName(textfield_name.getText());
							task = new BackupTask(textfield_name.getText());
							deleteOldTask = true;
							// task.resetPaths();
						}
					}
					// Backup-Modus speichern:
					if (radioButton_normalBackup.isSelected()) {
						task.setBackupMode(0);
					} else if (radioButton_hardlinkBackup.isSelected()) {
						task.setBackupMode(1);
					}

					// Autostart-Option sichern:
					task.setAutostart(checkBox_autostart.isSelected());

					// Prüfen ob Quellpfade eingefügt wurden:
					if (!listModel.isEmpty()) {
						for (int i = 0; i < listModel.getSize(); i++) {
							if (isValidPath(listModel.getElementAt(i).toString().trim())) {
								task.addSourcePath(listModel.getElementAt(i));
							} else {
								// Mindestens ein Quellpfad ist ungültig
								JOptionPane.showMessageDialog(null,
										ResourceBundle.getBundle("gui.messages")
												.getString("GUI.EditDialog.errIllegalSource"),
										ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
										JOptionPane.INFORMATION_MESSAGE);
								return;
							}
						}
						// Prüfen ob ein Zielpfad eingefügt wurde:
						if (isValidPath(textfield_destination.getText())) {
							// Prüfen ob sich die DestinationVerification
							// Einstellung geändert hat und ensprechend den
							// Identifier handeln:
							if (checkBox_destinationVerification.isSelected() && !task.getDestinationVerification()) {
								// Neuen Identifier anlegen:
								createIdentifier(task.getTaskName(), textfield_destination.getText());
							} else if (checkBox_destinationVerification.isSelected()
									&& task.getDestinationVerification()) {
								// Wenn sich der Zielpfad geändert hat, muss der
								// Identifier angepasst werden:
								if (task.getDestinationPath() != null
										&& task.getDestinationPath().equals(textfield_destination.getText())) {
									// Alten Identifier löschen:
									deleteIdentifier(task.getTaskName(), textfield_destination.getText());
									// Neuen Identifier anlegen:
									createIdentifier(task.getTaskName(), textfield_destination.getText());
								}
							} else if (!checkBox_destinationVerification.isSelected()
									&& task.getDestinationVerification()) {
								// Alten Identifier löschen:
								deleteIdentifier(task.getTaskName(), textfield_destination.getText());
							}
							task.setDestinationPath(textfield_destination.getText());
						} else {
							// Zielpfad ist ungültig oder leer:
							JOptionPane.showMessageDialog(null,
									ResourceBundle.getBundle("gui.messages")
											.getString("GUI.EditDialog.errIllegalDestination"),
									ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					} else {
						// Keine Quelle gewählt:
						JOptionPane.showMessageDialog(null,
								ResourceBundle.getBundle("gui.messages")
										.getString("GUI.EditDialog.errNoSourcePathSelected"),
								ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					// DestinationVerification-Option sichern:
					task.setDestinationVerification(checkBox_destinationVerification.isSelected());

					// Erweiterte AutoClean Einstellungen prüfen:
					if (checkBox_toggleExtendedSettings.isSelected()) {
						if (!checkExtendedAutoCleanSettings()) {
							JOptionPane.showMessageDialog(null,
									ResourceBundle.getBundle("gui.messages")
											.getString("Messages.illegalAutoCleanSettings"),
									ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}

					// Prüfen ob die Einstellungen für das automatische Backup
					// (Wochentag) korrekt sind:
					if (checkBox_toggleWeekday.isSelected()) {
						// Prüfen ob mindestens ein Wochentag ausgewählt ist:
						if (!(checkBox_monday.isSelected() || checkBox_tuesday.isSelected()
								|| checkBox_wednesday.isSelected() || checkBox_thursday.isSelected()
								|| checkBox_friday.isSelected() || checkBox_saturday.isSelected()
								|| checkBox_sunday.isSelected())) {
							JOptionPane.showMessageDialog(null,
									ResourceBundle.getBundle("gui.messages").getString("Messages.atLeastOneWeekday"),
									ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}
					// Prüfen ob die Einstellungen für das automatische Backup
					// (Tag im Monat) korrekt sind:
					if (checkBox_toggleDayInMonth.isSelected()) {
						// Prüfen ob mindestens ein Tag ausgewählt ist:
						boolean validSettings = false;
						for (int i = 0; i < daysOfMonthCheckboxes.length; i++) {
							if (daysOfMonthCheckboxes[i].isSelected()) {
								validSettings = true;
							}
						}
						if (!validSettings) {
							JOptionPane.showMessageDialog(null,
									ResourceBundle.getBundle("gui.messages").getString("Messages.atLeastOneDay"),
									ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}
					// Prüfen ob die Start-Zeit für das automatische Backup eine
					// gültige Zeit ist:
					if (checkBox_toggleWeekday.isSelected() || checkBox_toggleDayInMonth.isSelected()) {
						try {
							LocalTime startTime = LocalTime.parse(textField_timeToStart.getText());
						} catch (DateTimeParseException ex) {
							JOptionPane.showMessageDialog(null,
									ResourceBundle.getBundle("gui.messages").getString("Messages.illegalStartTime"),
									ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}

					// Einstellungen für das automatische Aufräumen sichern:
					// Unterscheidung für einfaches und erweitertes Aufräumen:.
					task.clearAutoCleanInformations();
					if (checkBox_toggleSimpleSettings.isSelected()) {
						task.setSimpleAutoCleanEnabled(true);
						task.setNumberOfBackupsToKeep((Integer) spinner_numberOfBackupsToKeep.getValue());
					} else if (checkBox_toggleExtendedSettings.isSelected()) {
						task.setExtendedAutoCleanEnabled(true);
						int numberOfRules = Integer.valueOf(comboBox_numberOfRules.getSelectedItem().toString());
						task.setNumberOfExtendedAutoCleanRules(numberOfRules);
						int[] threshold = new int[numberOfRules - 1];
						for (int i = 0; i < threshold.length; i++) {
							threshold[i] = (int) spinners[i].getValue();
						}
						task.setThreshold(threshold);
						String[] thresholdUnits = new String[numberOfRules - 1];
						for (int i = 0; i < thresholdUnits.length; i++) {
							thresholdUnits[i] = unitComboBoxes[i].getSelectedItem().toString();
						}
						task.setThresholdUnits(thresholdUnits);
						String[] backupsToKeep = new String[numberOfRules];
						for (int i = 0; i < backupsToKeep.length; i++) {
							backupsToKeep[i] = toKeepComboBoxes[i].getSelectedItem().toString();
						}
						task.setBackupsToKeep(backupsToKeep);
					}

					// AutoBackup Einstellungen sichern:
					// AutoBackup-Modus sichern:
					int mode = 0;
					if (checkBox_toggleWeekday.isSelected()) {
						mode = 1;
						boolean[] weekdays = new boolean[7];
						if (checkBox_monday.isSelected()) {
							weekdays[0] = true;
						}
						if (checkBox_tuesday.isSelected()) {
							weekdays[1] = true;
						}
						if (checkBox_wednesday.isSelected()) {
							weekdays[2] = true;
						}
						if (checkBox_thursday.isSelected()) {
							weekdays[3] = true;
						}
						if (checkBox_friday.isSelected()) {
							weekdays[4] = true;
						}
						if (checkBox_saturday.isSelected()) {
							weekdays[5] = true;
						}
						if (checkBox_sunday.isSelected()) {
							weekdays[6] = true;
						}
						task.setBackupWeekdays(weekdays);
						try {
							LocalTime startTime = LocalTime.parse(textField_timeToStart.getText());
							task.setBackupStartTime(startTime);
						} catch (DateTimeParseException ex) {
							System.err
									.println("Error: DateTimeParseException while saving autoBackup Settings. Code: 1");
							return;
						}

					} else if (checkBox_toggleDayInMonth.isSelected()) {
						mode = 2;
						boolean[] daysOfMonth = new boolean[31];
						for (int i = 0; i < 31; i++) {
							daysOfMonth[i] = daysOfMonthCheckboxes[i].isSelected();
						}
						task.setBackupDaysInMonth(daysOfMonth);
						try {
							LocalTime startTime = LocalTime.parse(textField_timeToStart.getText());
							task.setBackupStartTime(startTime);
						} catch (DateTimeParseException ex) {
							System.err
									.println("Error: DateTimeParseException while saving autoBackup Settings. Code: 2");
							return;
						}
					} else if (checkBox_toggleInterval.isSelected()) {
						mode = 3;
						try {
							task.setIntervalTime(Integer.parseInt(textField_interval.getText()));
							task.setIntervalUnit(comboBox_intervalUnit.getSelectedItem().toString());
						} catch (NumberFormatException ex) {
							System.err
									.println("Error: DateTimeParseException while saving autoBackup Settings. Code: 3");
							return;
						}
					}
					task.setAutoBackupMode(mode);
					task.setCatchUpEnabled(checkBox_catchUp.isSelected());
					if (checkBox_catchUp.isSelected()) {
						task.setCatchUpTime(comboBox_catchUp.getSelectedItem().toString());
					}

				} else {
					// Ungültiger Name:
					JOptionPane.showMessageDialog(null,
							ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.errIllegalName"),
							ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				if (deleteOldTask) {
					// alte Version des Tasks löschen:
					editListener.removeBackupTask(editListener.getBackupTaskWithName(textfield_name.getText()));
				}
				editListener.addBackupTask(task);

				// Einstellungen sichern (seriallisieren):
				editListener.saveProperties();
				if (allInputsAreValid) {
					EditDialog.this.dispose();
				}
				editListener.scheduleBackupTasks();
			}
		});
		btn_Ok.setActionCommand(ResourceBundle.getBundle("gui.messages").getString("GUI.button_ok"));
		buttonPane.add(btn_Ok);
		getRootPane().setDefaultButton(btn_Ok);

		// Button Cancel:
		JButton btn_Abbrechen = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_cancel"));
		btn_Abbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EditDialog.this.dispose();
			}
		});
		btn_Abbrechen.setActionCommand(ResourceBundle.getBundle("gui.messages").getString("GUI.button_cancel"));
		buttonPane.add(btn_Abbrechen);

		checkBox_toggleWeekday.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkBox_toggleDayInMonth.setSelected(false);
				checkBox_toggleInterval.setSelected(false);
			}
		});
		checkBox_toggleDayInMonth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkBox_toggleWeekday.setSelected(false);
				checkBox_toggleInterval.setSelected(false);
			}
		});
		checkBox_toggleInterval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkBox_toggleWeekday.setSelected(false);
				checkBox_toggleDayInMonth.setSelected(false);
			}
		});
	}

	/**
	 * Prüft den gegebenen Pfad auf Gültigkeit. Dabei ist ein Pfad genau dann
	 * Gültig wenn er existiert und der root keine Datei sondern ein Verzeichnis
	 * ist.
	 * 
	 * @param s
	 *            zu prüfender Pfad
	 * @return Gültigkeit des Pfades.
	 */
	private boolean isValidPath(String pfad) {
		File f = new File(pfad);
		if (f.exists() && f.isDirectory()) {
			return true;
		}
		return false;
	}

	/**
	 * Prüft ob der gegebene Name noch ungenutzt (noch nicht verwendet) ist.
	 * 
	 * @param name
	 *            zu prüfender Name
	 * @return ob der Name noch nicht benutzt ist
	 */
	private boolean nameIsNotTaken(String name) {
		if (editListener.getBackupTaskNames().contains(name)) {
			return false;
		}
		return true;
	}

	/**
	 * Prüft den gegebenen Namen auf Gültigkeit.
	 * 
	 * @param name
	 *            zu prüfender Name
	 * @return ültigkeit des Namens
	 */
	private boolean isValidName(String name) {
		if (!name.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * Gibt den Zielpfad zurück.
	 * 
	 * @return Zielpfad
	 */
	public String getDestinationPath() {
		return textfield_destination.getText();
	}

	/**
	 * Legt den Namen des Backup-Tasks fest.
	 * 
	 * @param name
	 *            festzulegender Name
	 */
	public void setBackupTaskName(String name) {
		textfield_name.setText(name);
	}

	/**
	 * Legt die Quellpfade fest.
	 * 
	 * @param sources
	 *            festzulegende Quellen
	 */
	public void setSourcePaths(ArrayList<Source> sources) {
		for (int i = 0; i < sources.size(); i++) {
			listModel.addElement(sources.get(i));
		}
	}

	/**
	 * Legt den Zielpfad fest.
	 * 
	 * @param path
	 *            festzulegender Zielpfad
	 */
	public void setDestinationPath(String path) {
		textfield_destination.setText(path);
	}

	/**
	 * Prüft ob ein bestimmter Pfad bereits als Quellpfad festgelegt ist.
	 * 
	 * @param path
	 *            zu prüfender Pfad
	 * @return ob der Pfad bereits festgelegt ist
	 */
	private boolean isAlreadySourcePath(String path) {
		for (int i = 0; i < listModel.getSize(); i++) {
			if (listModel.get(i).getPath().equals(path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gibt den Backup-Modus zurück.
	 * 
	 * @return 0 für normal oder 1 für hardlink oder -1 für kein Modus
	 *         ausgewählt
	 */
	public int getBackupMode() {
		if (radioButton_normalBackup.isSelected()) {
			return 0;
		} else if (radioButton_hardlinkBackup.isSelected()) {
			return 1;
		}
		return -1;
	}

	/**
	 * Setzt die Auswahl des Backup-Modes auf der GUI.
	 * 
	 * @param mode
	 *            Backup-Modus (0 = normal, 1 = hardlink)
	 */
	public void setBackupMode(int mode) {
		if (mode == 0) {
			radioButton_normalBackup.setSelected(true);
			radioButton_hardlinkBackup.setSelected(false);
		} else if (mode == 1) {
			radioButton_normalBackup.setSelected(false);
			radioButton_hardlinkBackup.setSelected(true);
		}
	}

	/**
	 * Setzt die Einstellung für die Auto-Clean Funktion auf der GUI.
	 * 
	 * @param enabled
	 *            Auto-Clean Funktion aktiviert (true) oder Deaktiviert (false)
	 */
	public void setAutoCleanEnabled(boolean enabled) {
		checkBox_toggleSimpleSettings.setSelected(enabled);
	}

	/**
	 * Setzt die Anzahl der bei Auto-Clean zu behaltenden Backups auf der GUI.
	 * 
	 * @param numberOfBackupsToKeep
	 *            zu setzende Anzahl an zu behaltenden Backups
	 */
	public void setNumberOfBackupsToKeep(int numberOfBackupsToKeep) {
		spinner_numberOfBackupsToKeep.setValue(numberOfBackupsToKeep);
	}

	/**
	 * Sperrt das Namens-TextFeld für den Benutzer (Name eines Backups kann
	 * nachträglich nicht geändert werden).
	 * 
	 * @param editable
	 *            Editierbarkeit
	 */
	public void setEditable(boolean editable) {
		textfield_name.setEditable(editable);
	}

	/**
	 * Schaltet den EditMode an bzw. aus.
	 * 
	 * @param editMode
	 *            true = an, false = aus
	 */
	public void setEditMode(boolean editMode) {
		this.inEditMode = editMode;
	}

	/**
	 * Aktiviert bzw. deaktiviert den Autostart-Modus.
	 * 
	 * @param autostart
	 *            zu setzender Autostart-Modus
	 */
	public void setAutostart(boolean autostart) {
		checkBox_autostart.setSelected(autostart);
	}

	/**
	 * Aktiviert bzw. deaktiviert den DestinationVerification-Modus.
	 * 
	 * @param autostart
	 *            zu setzender DestinationVerification-Modus
	 */
	public void setDestinationVerification(boolean destVerification) {
		checkBox_destinationVerification.setSelected(destVerification);
	}

	/**
	 * Gibt ein neues DefaultComboBoxModel zurück, welches aus der gegebenen
	 * Vorlage erstellt wird.
	 * 
	 * @param template
	 *            Vorlage
	 * @return DefaultComboBoxModel
	 */
	private DefaultComboBoxModel<String> createComboBoxModelFromTemplate(String[] template) {
		DefaultComboBoxModel<String> result = new DefaultComboBoxModel<String>();
		for (int i = 0; i < template.length; i++) {
			result.addElement(template[i]);
		}
		return result;
	}

	/**
	 * Gibt ein neues SpinnerNumberModel zurück, welches aus der gegebenen
	 * Vorlage erstellt wird.
	 * 
	 * @param template
	 *            Vorlage
	 * @return SpinnerNumberModel
	 */
	private SpinnerNumberModel createSpinnerNumberModelFromTemplate(int[] template) {
		return new SpinnerNumberModel(template[0], template[1], template[2], template[3]);
	}

	/**
	 * Aktiviert bzw. deaktiviert ein bestimmtes Regel-Layer (eine Regel-Zeile)
	 * 
	 * @param layerToDisable
	 *            Layer welches aktiviert bzw. deaktiviert werden soll
	 * @param enabled
	 *            true = aktivieren, false = deaktivieren
	 */
	private void setLayerEnabled(int layerToDisable, boolean enabled) {
		if (layerToDisable == 2) {
			unitComboBoxes[1].setEnabled(enabled);
			toKeepComboBoxes[1].setEnabled(enabled);
			label_eS2_to.setEnabled(enabled);
			label_eS2_toKeep.setEnabled(enabled);
		} else if (layerToDisable == 3) {
			unitComboBoxes[2].setEnabled(enabled);
			toKeepComboBoxes[2].setEnabled(enabled);
			label_eS3_to.setEnabled(enabled);
			label_eS3_toKeep.setEnabled(enabled);
		} else if (layerToDisable == 4) {
			unitComboBoxes[3].setEnabled(enabled);
			toKeepComboBoxes[3].setEnabled(enabled);
			label_eS4_to.setEnabled(enabled);
			label_eS4_toKeep.setEnabled(enabled);
		} else if (layerToDisable == 5) {
			toKeepComboBoxes[4].setEnabled(enabled);
			label_eS5_to.setEnabled(enabled);
			label_eS5_toKeep.setEnabled(enabled);
			label_eS5_inf.setEnabled(enabled);
		}
	}

	/**
	 * Legt die Einstellungen des erweiterten AutoClean in der GUI fest (zum
	 * erstellen der GUI mit vorhandenen Einstellugen)
	 * 
	 * @param numberOfRules
	 *            Anzahl der aktivierten Regeln
	 * @param threshold
	 *            Threshold-Zahlen (siehe BackupTask)
	 * @param thresholdUnits
	 *            Threshold-Eiheiten (siehe BackupTask)
	 * @param backupsToKeep
	 *            Anzahl der zu behaltenden Backupsätze für die einzelnen Regeln
	 */
	public void setExtendedAutoCleanSettings(int numberOfRules, int[] threshold, String[] thresholdUnits,
			String[] backupsToKeep) {
		checkBox_toggleExtendedSettings.setSelected(true);
		comboBox_numberOfRules.setSelectedIndex(numberOfRules - 1);
		for (int i = 0; i < thresholdUnits.length; i++) {
			unitComboBoxes[i].setSelectedItem(thresholdUnits[i]);
		}
		for (int i = 0; i < threshold.length; i++) {
			spinners[i].setValue(threshold[i]);
		}
		for (int i = 0; i < backupsToKeep.length; i++) {
			toKeepComboBoxes[i].setSelectedItem(backupsToKeep[i]);
		}

	}

	/**
	 * Legt den Backup-Modus fest. 0 = Auto-Backup deaktiviert, 1 =
	 * Zeitpunkt-Wochentag, 2 = Zeitpunkt-TagImMonat, 3 = Intervall
	 * 
	 * @param mode
	 *            Backup-Modus
	 */
	public void setAutoBackupMode(int mode) {
		if (mode == 1) {
			checkBox_toggleWeekday.setSelected(true);
		} else if (mode == 2) {
			checkBox_toggleDayInMonth.setSelected(true);
		} else if (mode == 3) {
			checkBox_toggleInterval.setSelected(true);
		}
	}

	/**
	 * Legt die Wochentage fest an denen das Backup ausgeführt werden soll. Die
	 * Array-Felder entsprechen den Wochentagen von [0] = Montag bis [6] =
	 * Sonntag.
	 * 
	 * @param weekdays
	 *            Wochentage an denen gesichert werden soll
	 */
	public void setBackupWeekdays(boolean[] weekdays) {
		if (weekdays[0] == true) {
			checkBox_monday.setSelected(true);
		}
		if (weekdays[1] == true) {
			checkBox_tuesday.setSelected(true);
		}
		if (weekdays[2] == true) {
			checkBox_wednesday.setSelected(true);
		}
		if (weekdays[3] == true) {
			checkBox_thursday.setSelected(true);
		}
		if (weekdays[4] == true) {
			checkBox_friday.setSelected(true);
		}
		if (weekdays[5] == true) {
			checkBox_saturday.setSelected(true);
		}
		if (weekdays[6] == true) {
			checkBox_sunday.setSelected(true);
		}
	}

	/**
	 * Legt die Tage im Monat fest an denen das Backup ausgefürt werden soll.
	 * Die Array-Felder entsprechen den Tagen im Monat von [0] = 1. bis [30] =
	 * 31.
	 * 
	 * @param daysInMonth
	 *            Tage im Monat an denen das Backup ausgeführt werden soll.
	 */
	public void setBackupDaysInMonth(boolean[] daysInMonth) {
		for (int i = 0; i < 31; i++) {
			if (daysInMonth[i]) {
				daysOfMonthCheckboxes[i].setSelected(true);
			}
		}
	}

	/**
	 * Legt die Startzeit für das AutoBackup fest.
	 * 
	 * @param startTime
	 *            festzulegende Startzeit
	 */
	public void setBackupStartTime(LocalTime startTime) {
		textField_timeToStart.setText(startTime.toString());
	}

	/**
	 * Legt die Intervallzeit fest.
	 * 
	 * @param time
	 *            Intervallzeit
	 */
	public void setIntervalTime(int time) {
		textField_interval.setText(String.valueOf(time));
	}

	/**
	 * Legt die Intervalleinheit fest.
	 * 
	 * @param intervalUnit
	 *            Intervalleinheit
	 */
	public void setIntervalUnit(String intervalUnit) {
		comboBox_intervalUnit.setSelectedItem(intervalUnit);
	}

	/**
	 * Legt die catchUp-Funktion als aktiviert/ deaktiviert fest.
	 * 
	 * @param enabled
	 *            festzulegender Wert
	 */
	public void setCatchUpEnabled(boolean enabled) {
		checkBox_catchUp.setSelected(enabled);
	}

	/**
	 * Legt die Dauer für die catchUp-Funktion fest.
	 * 
	 * @param catchUpTime
	 *            Dauer für catchUp
	 */
	public void setCatchUpTime(String catchUpTime) {
		comboBox_catchUp.setSelectedItem(catchUpTime);
	}

	/**
	 * Passt den Spinner an die Auswahl in der ComboBox an.
	 * 
	 * @param comboBox
	 *            ComboBox an die die Spinner angepasst werden sollen
	 * @param spinner
	 *            anzupassende Spinner
	 */
	private void changeSpinner(JComboBox<String> comboBox, JSpinner spinner) {
		if (comboBox.getSelectedItem().toString().equalsIgnoreCase("inf")) {
			spinner.setEnabled(false);
		} else {
			spinner.setEnabled(true);
			int oldValue = Integer.valueOf(spinner.getValue().toString());
			if (comboBox.getSelectedItem().toString().equalsIgnoreCase("min")) {
				spinner.setModel(createSpinnerNumberModelFromTemplate(template_number_min));
				checkValueOfSpinner(spinner, template_number_min, oldValue);
			} else if (comboBox.getSelectedItem().toString().equalsIgnoreCase("h")) {
				spinner.setModel(createSpinnerNumberModelFromTemplate(template_number_h));
				checkValueOfSpinner(spinner, template_number_h, oldValue);
			} else if (comboBox.getSelectedItem().toString().equalsIgnoreCase("d")) {
				spinner.setModel(createSpinnerNumberModelFromTemplate(template_number_d));
				checkValueOfSpinner(spinner, template_number_d, oldValue);
			} else if (comboBox.getSelectedItem().toString().equalsIgnoreCase("m")) {
				spinner.setModel(createSpinnerNumberModelFromTemplate(template_number_m));
				checkValueOfSpinner(spinner, template_number_m, oldValue);
			} else if (comboBox.getSelectedItem().toString().equalsIgnoreCase("y")) {
				spinner.setModel(createSpinnerNumberModelFromTemplate(template_number_y));
				checkValueOfSpinner(spinner, template_number_y, oldValue);
			}
		}
	}

	/**
	 * Prüft ob der alte Spinner-Wert für die neue "Einheit" noch gültig ist.
	 * Wenn ja wird sie übernommen, sonst wird der spinner auf default (1)
	 * gesetzt.
	 * 
	 * @param spinner
	 *            entsprechender Spinner
	 * @param template
	 *            template für die entsprechende "Einheit"
	 * @param oldValue
	 *            alter Wert des spinners
	 */
	private void checkValueOfSpinner(JSpinner spinner, int[] template, int oldValue) {
		if ((template[1] <= oldValue) && (oldValue <= template[2])) {
			spinner.setValue(oldValue);
		}
	}

	/**
	 * Prüft die eingestellten extended AutoClean Einstellungen auf Gültigkeit.
	 * 
	 * @return true wenn die Einstekllungen gültig sind, false sonst
	 */
	private boolean checkExtendedAutoCleanSettings() {
		int numberOfRules = Integer.valueOf(comboBox_numberOfRules.getSelectedItem().toString());
		// Letzte Regel muss bis "inf" gehen:
		if (numberOfRules < MAX_NUMBER_OF_RULES
				&& !unitComboBoxes[numberOfRules - 1].getSelectedItem().toString().equals("inf")) {
			return false;
		}
		// Restliche Regeln müssen ungleich "inf" sein:
		for (int i = 0; i < (numberOfRules - 1); i++) {
			if (unitComboBoxes[i].getSelectedItem().toString().equals("inf")) {
				return false;
			}
		}
		// Jede Regel muss später Enden, als die Regel davor endet (der gewählte
		// Bereich muss > 0 sein und sie dürfen sich nicht überlappen):
		// Für die 1. Regel existieren keine Abhängigkeiten
		switch (numberOfRules) {
		case 1:
			return true;
		case 2:
			return checkRulesUnit(1);
		case 3:
			return checkRulesUnit(2);
		case 4:
			return checkRulesUnit(3);
		case 5:
			return checkRulesUnit(4);
		}
		return true;
	}

	/**
	 * Prüft die Abhängigkeiten aller Regeln bis zum gegebenen Index.
	 * 
	 * @param endIndex
	 *            index bis zu welchem die Regeln geprüft werden sollen
	 * @return true, wenn alle Regeln gültig sind, false sonst
	 */
	private boolean checkRulesUnit(int endIndex) {
		// Da die 5. Regel nicht überprüft werden muss:
		if (endIndex == 4) {
			endIndex--;
		}
		for (int i = 1; i <= endIndex; i++) {
			if (!checkUnitDependenciesOfRule(i) || !checkSpinnerDependencies(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Prüft die Abhängigkeiten der Einheiten für die Combobox index zu ihrem
	 * Vorgänger.
	 * 
	 * @param index
	 *            index Index (also eS - 1) der Regel
	 * @return true wenn die Einstellungen gültig sind, false sonst
	 */
	private boolean checkUnitDependenciesOfRule(int index) {
		String currentUnit = unitComboBoxes[index].getSelectedItem().toString();
		String unitAboveCurrent = unitComboBoxes[index - 1].getSelectedItem().toString();
		switch (unitAboveCurrent) {
		case "min":
			return true;
		case "h":
			if (!currentUnit.equals("min")) {
				return true;
			}
			return false;
		case "d":
			if (!currentUnit.equals("min") && !currentUnit.equals("h")) {
				return true;
			}
			return false;
		case "m":
			if (currentUnit.equals("m") || currentUnit.equals("y")) {
				return true;
			}
			return false;
		case "y":
			if (currentUnit.equals("y")) {
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * Prüft die Abhängigkeiten der Spinner, wenn die Combobox index die gleiche
	 * Einheit wie die ComboBox index - 1 hat.
	 * 
	 * @param index
	 *            Index (also eS - 1) der Regel
	 * @return true wenn die Einstellungen gültig sind, false sonst
	 */
	private boolean checkSpinnerDependencies(int index) {
		String currentUnit = unitComboBoxes[index].getSelectedItem().toString();
		String unitAboveCurrent = unitComboBoxes[index - 1].getSelectedItem().toString();
		if (currentUnit.equals(unitAboveCurrent)) {
			if (Integer.valueOf(spinners[index - 1].getValue().toString()) < Integer
					.valueOf(spinners[index].getValue().toString())) {
				return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * Legt den Identifier für DestinationVerification an (falls noch nicht
	 * vorhanden).
	 * 
	 * @param taskName
	 *            Name des Tasks
	 * @param destPath
	 *            Zielpfad des Tasks
	 */
	private void createIdentifier(String taskName, String destPath) {
		File identifier = new File(destPath + "/" + taskName + ".id");
		if (!identifier.exists()) {
			try {
				identifier.createNewFile();
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}

	}

	/**
	 * Löscht den bisherigen Identifier.
	 * 
	 * @param taskName
	 *            Name des Tasks dessen Identifier gelöscht werden soll
	 * @param destPath
	 *            Zielpfad des Tasks dessen Identifier gelöscht werden soll
	 */
	private void deleteIdentifier(String taskName, String destPath) {
		File identifier = new File(destPath + "/" + taskName + ".id");
		if (!identifier.exists()) {
			identifier.delete();
		}
	}
}
