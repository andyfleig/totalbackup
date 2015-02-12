package gui;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.io.File;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Panel;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner.DefaultEditor;
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

import javax.swing.JSlider;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JTabbedPane;

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

	private JComboBox[] unitComboBoxes;
	private JComboBox[] toKeepComboBoxes;
	private JSpinner[] spinners;
	private JComboBox comboBox_numberOfRules;

	private JLabel label_eS1_from;
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

	/**
	 * Legt fest, ob gerade ein existierender Filter bearbeitet, oder ein neuer
	 * erzeugt wird.
	 */
	private boolean inEditMode;

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
		JComboBox comboBox_eS1_unit;
		JComboBox comboBox_eS2_unit;
		JComboBox comboBox_eS3_unit;
		JComboBox comboBox_eS4_unit;
		JComboBox comboBox_eS1_toKeep;
		JComboBox comboBox_eS2_toKeep;
		JComboBox comboBox_eS3_toKeep;
		JComboBox comboBox_eS4_toKeep;
		JComboBox comboBox_eS5_toKeep;
		JSpinner spinner_eS1;
		JSpinner spinner_eS2;
		JSpinner spinner_eS3;
		JSpinner spinner_eS4;

		setResizable(false);
		setTitle(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.title"));
		setBounds(100, 100, 513, 571);
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
		JLabel label_source = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.EditDialog.label_sources"));
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
				int reply = JOptionPane.showConfirmDialog(null,
						ResourceBundle.getBundle("gui.messages").getString("Messages.DeleteSource"), null,
						JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					if (!list_sourcePaths.isSelectionEmpty()) {
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
					sourcesDialog.addFilter(filterOfCurrentSource.get(i).getPath(), filterOfCurrentSource.get(i)
							.getMode());
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

		JLabel label_destination = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.EditDialog.label_destination"));
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

		JPanel panel_autostart = new JPanel();
		panel_otherSettings.add(panel_autostart, BorderLayout.NORTH);

		checkBox_autostart = new JCheckBox(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.EditDialog.autostart"));
		panel_autostart.add(checkBox_autostart);

		JPanel panel_autoremove = new JPanel();
		panel_otherSettings.add(panel_autoremove, BorderLayout.SOUTH);
		panel_autoremove.setLayout(new BorderLayout(0, 0));

		JLabel label_autoclean = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.EditDialog.label_autoClean"));
		panel_autoremove.add(label_autoclean, BorderLayout.NORTH);

		// Autoclean:

		JPanel panel_autoclean_settings = new JPanel();
		panel_autoremove.add(panel_autoclean_settings);
		panel_autoclean_settings.setLayout(new BorderLayout(0, 0));

		JPanel panel_simpleSettings = new JPanel();
		panel_simpleSettings.setLayout(new BorderLayout(0, 0));

		JPanel panel_toggleSimpleSettings = new JPanel();
		panel_simpleSettings.add(panel_toggleSimpleSettings, BorderLayout.NORTH);
		panel_toggleSimpleSettings.setLayout(new BorderLayout(0, 0));

		checkBox_toggleSimpleSettings = new JCheckBox(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.EditDialog.checkbox_numberOfBackupsToKeep"));
		checkBox_toggleSimpleSettings.setHorizontalAlignment(SwingConstants.LEFT);
		panel_toggleSimpleSettings.add(checkBox_toggleSimpleSettings);

		// Textfeld des Spinners nicht editierbar machen:
		// spinner_numberOfBackupsToKeep.setEditor(new
		// JSpinner.DefaultEditor(spinner_numberOfBackupsToKeep));

		JPanel panel_extendedSettings = new JPanel();

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel_autoclean_settings.add(tabbedPane);
		tabbedPane.addTab("einfach", panel_simpleSettings);

		JPanel panel_settingsSimpleSettings = new JPanel();
		panel_simpleSettings.add(panel_settingsSimpleSettings);

		JLabel label_simple = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"EditDialog.lblNewLabel.text_6"));
		panel_settingsSimpleSettings.add(label_simple);

		spinner_numberOfBackupsToKeep = new JSpinner();
		panel_settingsSimpleSettings.add(spinner_numberOfBackupsToKeep);
		spinner_numberOfBackupsToKeep.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null,
				new Integer(1)));
		tabbedPane.addTab("erweitert", panel_extendedSettings);
		panel_autoclean_settings.add(tabbedPane, BorderLayout.WEST);
		panel_extendedSettings.setLayout(new BorderLayout(0, 0));

		JPanel panel_toggleExtendedSettings = new JPanel();
		panel_extendedSettings.add(panel_toggleExtendedSettings, BorderLayout.NORTH);
		panel_toggleExtendedSettings.setLayout(new BorderLayout(0, 0));

		checkBox_toggleExtendedSettings = new JCheckBox("aktiviert");
		checkBox_toggleExtendedSettings.setHorizontalAlignment(SwingConstants.LEFT);
		panel_toggleExtendedSettings.add(checkBox_toggleExtendedSettings, BorderLayout.WEST);

		JPanel panel_numberOfRules = new JPanel();
		panel_toggleExtendedSettings.add(panel_numberOfRules, BorderLayout.EAST);

		JLabel label_numberOfRules = new JLabel(ResourceBundle
				.getBundle("gui.messages").getString("EditDialog.lblNewLabel.text_9")); //$NON-NLS-1$ //$NON-NLS-2$
		panel_numberOfRules.add(label_numberOfRules);

		comboBox_numberOfRules = new JComboBox();
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
		comboBox_numberOfRules.setModel(new DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5" }));
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

		label_eS1_from = new JLabel(ResourceBundle.getBundle("gui.messages").getString("EditDialog.lblNewLabel.text_3"));
		panel_eS1.add(label_eS1_from);

		label_eS1_to = new JLabel(ResourceBundle.getBundle("gui.messages").getString("EditDialog.lblNewLabel.text_4"));
		panel_eS1.add(label_eS1_to);

		spinner_eS1 = new JSpinner();
		spinner_eS1.setModel(createSpinnerNumberModelFromTemplate(template_number_min));
		// spinner_eS1.setEditor(new JSpinner.DefaultEditor(spinner_eS1));

		panel_eS1.add(spinner_eS1);

		comboBox_eS1_unit = new JComboBox();
		comboBox_eS1_unit.setModel(createComboBoxModelFromTemplate(template_timeInterval_all));
		panel_eS1.add(comboBox_eS1_unit);

		label_eS1_toKeep = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"EditDialog.lblNewLabel.text_5"));
		panel_eS1.add(label_eS1_toKeep);

		comboBox_eS1_toKeep = new JComboBox();
		comboBox_eS1_toKeep.setModel(new DefaultComboBoxModel<String>(template_backupSets));
		panel_eS1.add(comboBox_eS1_toKeep);

		JPanel panel_eS2 = new JPanel();
		panel_settingsExtendedSettings.add(panel_eS2);
		FlowLayout flowLayout_1 = (FlowLayout) panel_eS2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);

		label_eS2_to = new JLabel("bis");
		label_eS2_to.setEnabled(false);
		panel_eS2.add(label_eS2_to);

		spinner_eS2 = new JSpinner();
		spinner_eS2.setModel(createSpinnerNumberModelFromTemplate(template_number_min));
		panel_eS2.add(spinner_eS2);
		// TODO: Nach letztem "setEditor"! Möglich? (bei allen Vorkommen von
		// setEditor)
		spinner_eS2.setEnabled(false);
		// spinner_eS2.setEditor(new JSpinner.DefaultEditor(spinner_eS2));

		comboBox_eS2_unit = new JComboBox();
		comboBox_eS2_unit.setEnabled(false);
		comboBox_eS2_unit.setModel(createComboBoxModelFromTemplate(template_timeInterval_all));
		panel_eS2.add(comboBox_eS2_unit);

		label_eS2_toKeep = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"EditDialog.lblZuBehaltendeBackupstze.text"));
		label_eS2_toKeep.setEnabled(false);
		panel_eS2.add(label_eS2_toKeep);

		comboBox_eS2_toKeep = new JComboBox();
		comboBox_eS2_toKeep.setEnabled(false);
		comboBox_eS2_toKeep.setModel(new DefaultComboBoxModel<String>(template_backupSets));
		panel_eS2.add(comboBox_eS2_toKeep);

		JPanel panel_eS3 = new JPanel();
		panel_settingsExtendedSettings.add(panel_eS3);
		FlowLayout fl_panel_eS3 = (FlowLayout) panel_eS3.getLayout();
		fl_panel_eS3.setAlignment(FlowLayout.RIGHT);

		label_eS3_to = new JLabel("bis");
		label_eS3_to.setEnabled(false);
		panel_eS3.add(label_eS3_to);

		spinner_eS3 = new JSpinner();
		spinner_eS3.setModel(createSpinnerNumberModelFromTemplate(template_number_min));
		panel_eS3.add(spinner_eS3);
		spinner_eS3.setEnabled(false);

		comboBox_eS3_unit = new JComboBox();
		comboBox_eS3_unit.setEnabled(false);
		comboBox_eS3_unit.setModel(createComboBoxModelFromTemplate(template_timeInterval_all));
		panel_eS3.add(comboBox_eS3_unit);

		label_eS3_toKeep = new JLabel("Zu behaltende Backupsätze (max):");
		label_eS3_toKeep.setEnabled(false);
		panel_eS3.add(label_eS3_toKeep);

		comboBox_eS3_toKeep = new JComboBox();
		comboBox_eS3_toKeep.setEnabled(false);
		comboBox_eS3_toKeep.setModel(new DefaultComboBoxModel<String>(template_backupSets));
		panel_eS3.add(comboBox_eS3_toKeep);

		JPanel panel_eS4 = new JPanel();
		panel_settingsExtendedSettings.add(panel_eS4);
		FlowLayout fl_panel_eS4 = (FlowLayout) panel_eS4.getLayout();
		fl_panel_eS4.setAlignment(FlowLayout.RIGHT);

		label_eS4_to = new JLabel("bis");
		label_eS4_to.setEnabled(false);
		panel_eS4.add(label_eS4_to);

		spinner_eS4 = new JSpinner();
		spinner_eS4.setModel(createSpinnerNumberModelFromTemplate(template_number_min));
		panel_eS4.add(spinner_eS4);
		spinner_eS4.setEnabled(false);

		comboBox_eS4_unit = new JComboBox();
		comboBox_eS4_unit.setEnabled(false);
		comboBox_eS4_unit.setModel(createComboBoxModelFromTemplate(template_timeInterval_all));
		panel_eS4.add(comboBox_eS4_unit);

		label_eS4_toKeep = new JLabel("Zu behaltende Backupsätze (max):");
		label_eS4_toKeep.setEnabled(false);
		panel_eS4.add(label_eS4_toKeep);

		comboBox_eS4_toKeep = new JComboBox();
		comboBox_eS4_toKeep.setEnabled(false);
		comboBox_eS4_toKeep.setModel(new DefaultComboBoxModel<String>(template_backupSets));
		panel_eS4.add(comboBox_eS4_toKeep);

		JPanel panel_eS5 = new JPanel();
		panel_settingsExtendedSettings.add(panel_eS5);
		FlowLayout flowLayout_2 = (FlowLayout) panel_eS5.getLayout();
		flowLayout_2.setAlignment(FlowLayout.RIGHT);

		label_eS5_to = new JLabel("bis");
		label_eS5_to.setEnabled(false);
		panel_eS5.add(label_eS5_to);

		label_eS5_inf = new JLabel("inf");
		label_eS5_inf.setEnabled(false);
		panel_eS5.add(label_eS5_inf);

		label_eS5_toKeep = new JLabel("Zu behaltende Backupsätze (max):");
		label_eS5_toKeep.setEnabled(false);
		panel_eS5.add(label_eS5_toKeep);

		comboBox_eS5_toKeep = new JComboBox();
		comboBox_eS5_toKeep.setEnabled(false);
		comboBox_eS5_toKeep.setModel(new DefaultComboBoxModel<String>(template_backupSets));
		panel_eS5.add(comboBox_eS5_toKeep);

		unitComboBoxes = new JComboBox[4];
		unitComboBoxes[0] = comboBox_eS1_unit;
		unitComboBoxes[1] = comboBox_eS2_unit;
		unitComboBoxes[2] = comboBox_eS3_unit;
		unitComboBoxes[3] = comboBox_eS4_unit;

		// TODO: 5 mit Konstante ersetzen (ganze Klasse)
		toKeepComboBoxes = new JComboBox[5];
		toKeepComboBoxes[0] = comboBox_eS1_toKeep;
		toKeepComboBoxes[1] = comboBox_eS2_toKeep;
		toKeepComboBoxes[2] = comboBox_eS3_toKeep;
		toKeepComboBoxes[3] = comboBox_eS4_toKeep;
		toKeepComboBoxes[4] = comboBox_eS5_toKeep;

		// TODO: Konstante auch
		spinners = new JSpinner[4];
		spinners[0] = spinner_eS1;
		spinners[1] = spinner_eS2;
		spinners[2] = spinner_eS3;
		spinners[3] = spinner_eS4;

		JPanel panel_backupType = new JPanel();
		panel_main.add(panel_backupType, BorderLayout.SOUTH);

		JLabel label_properties = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.EditDialog.label_backupType"));
		panel_backupType.add(label_properties);

		// Spinner deaktivieren (default):
		for (JSpinner s : spinners) {
			s.setEnabled(false);
		}

		// Listener:
		comboBox_eS1_unit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeCBTime(unitComboBoxes[0], spinners[0]);
				if (unitComboBoxes[0].getSelectedItem().toString().equals("inf")) {
					spinners[0].setEnabled(false);
				} else {
					spinners[0].setEnabled(true);
				}
			}
		});
		comboBox_eS2_unit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeCBTime(unitComboBoxes[1], spinners[1]);
				if (unitComboBoxes[1].getSelectedItem().toString().equals("inf")) {
					spinners[1].setEnabled(false);
				} else {
					spinners[1].setEnabled(true);
				}
			}
		});
		comboBox_eS3_unit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeCBTime(unitComboBoxes[2], spinners[2]);
				if (unitComboBoxes[2].getSelectedItem().toString().equals("inf")) {
					spinners[2].setEnabled(false);
				} else {
					spinners[2].setEnabled(true);
				}
			}
		});
		comboBox_eS4_unit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeCBTime(unitComboBoxes[3], spinners[3]);
				if (unitComboBoxes[3].getSelectedItem().toString().equals("inf")) {
					spinners[3].setEnabled(false);
				} else {
					spinners[3].setEnabled(true);
				}
			}
		});

		// JRadioButtons anlegen:
		radioButton_normalBackup = new JRadioButton(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.EditDialog.radioButton_Normal"));
		radioButton_normalBackup.setSelected(true);
		radioButton_hardlinkBackup = new JRadioButton(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.EditDialog.radioButton_Hardlink"));

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
							task = editListener.getBackupTaskWithName(textfield_name.getText());
							editListener.removeBackupTask(task);
							task.resetPaths();
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
								JOptionPane.showMessageDialog(
										null,
										ResourceBundle.getBundle("gui.messages").getString(
												"GUI.EditDialog.errIllegalSource"),
										ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
										JOptionPane.INFORMATION_MESSAGE);
								return;
							}
						}
						// Prüfen ob ein Zielpfad eingefügt wurde:
						if (isValidPath(textfield_destination.getText())) {
							task.setDestinationPath(textfield_destination.getText());
							editListener.addBackupTask(task);
						} else {
							// Zielpfad ist ungültig oder leer:
							JOptionPane.showMessageDialog(
									null,
									ResourceBundle.getBundle("gui.messages").getString(
											"GUI.EditDialog.errIllegalDestination"),
									ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					} else {
						// Keine Quelle gewählt:
						JOptionPane.showMessageDialog(
								null,
								ResourceBundle.getBundle("gui.messages").getString(
										"GUI.EditDialog.errNoSourcePathSelected"),
								ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					// Erweiterte AutoClean Einstellungen prüfen:
					if (checkBox_toggleExtendedSettings.isSelected()) {
						if (!checkExtendedAutoCleanSettings()) {
							JOptionPane
									.showMessageDialog(null, "Ungültige AutoClean Einstellungen", ResourceBundle
											.getBundle("gui.messages").getString("GUI.errMsg"),
											JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}

					// Einstellungen für das automatische Aufräumen sichern:
					// Unterscheidung für einfaches und erweitertes Aufräumen:

					// TODO: funktionalität prüfen, ev. problem wenn kein
					// autoClean aktiviert ist...
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

				} else {
					// Ungültiger Name:
					JOptionPane.showMessageDialog(null,
							ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.errIllegalName"),
							ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				// Einstellungen sichern (seriallisieren):
				editListener.saveProperties();
				if (allInputsAreValid) {
					EditDialog.this.dispose();
				}
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

	private DefaultComboBoxModel<String> createComboBoxModelFromTemplate(String[] template) {
		DefaultComboBoxModel<String> result = new DefaultComboBoxModel<String>();
		for (int i = 0; i < template.length; i++) {
			result.addElement(template[i]);
		}
		return result;
	}

	private SpinnerNumberModel createSpinnerNumberModelFromTemplate(int[] template) {
		return new SpinnerNumberModel(template[0], template[1], template[2], template[3]);
	}

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

	// TODO: JavaDoc
	private void changeCBTime(JComboBox comboBox, JSpinner spinner) {
		if (comboBox.getSelectedItem().toString().equalsIgnoreCase("inf")) {
			spinner.setEnabled(false);
		} else {
			spinner.setEnabled(true);
			if (comboBox.getSelectedItem().toString().equalsIgnoreCase("min")) {
				spinner.setModel(createSpinnerNumberModelFromTemplate(template_number_min));
			} else if (comboBox.getSelectedItem().toString().equalsIgnoreCase("h")) {
				spinner.setModel(createSpinnerNumberModelFromTemplate(template_number_h));
			} else if (comboBox.getSelectedItem().toString().equalsIgnoreCase("d")) {
				spinner.setModel(createSpinnerNumberModelFromTemplate(template_number_d));
			} else if (comboBox.getSelectedItem().toString().equalsIgnoreCase("m")) {
				spinner.setModel(createSpinnerNumberModelFromTemplate(template_number_m));
			} else if (comboBox.getSelectedItem().toString().equalsIgnoreCase("y")) {
				spinner.setModel(createSpinnerNumberModelFromTemplate(template_number_y));
			}
		}
		// TODO: Nur auf default setzen wenn der alte Wert im neuen Model
		// ungültig ist
		spinner.setValue(1);
	}

	private boolean checkExtendedAutoCleanSettings() {
		int numberOfRules = Integer.valueOf(comboBox_numberOfRules.getSelectedItem().toString());
		// Letzte Regel muss bis "inf" gehen:
		if (numberOfRules < 5 && !unitComboBoxes[numberOfRules - 1].getSelectedItem().toString().equals("inf")) {
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
			return checkRulesUntit(1);
		case 3:
			return checkRulesUntit(2);
		case 4:
			return checkRulesUntit(3);
		case 5:
			return checkRulesUntit(4);
		}
		return true;
	}

	private boolean checkRulesUntit(int endIndex) {
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
	 * Prüpft die Abhängigkeiten der Einheiten für die Combobox index zu ihrem
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
			if (Integer.valueOf(spinners[index - 1].getValue().toString()) < Integer.valueOf(spinners[index].getValue()
					.toString())) {
				return true;
			}
			return false;
		}
		return true;
	}
}
