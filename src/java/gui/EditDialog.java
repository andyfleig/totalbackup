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

	private JCheckBox checkBox_autoClean;
	private JSpinner spinner_numberOfBackupsToKeep;

	private SourcesDialog sourcesDialog;
	private ISourcesDialogListener sourcesListener;

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
		setTitle(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.title"));
		setBounds(100, 100, 511, 330);
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
		JLabel label_source = new JLabel(ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.label_sources"));
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

		JPanel panel_autoremove = new JPanel();
		panel_setup.add(panel_autoremove, BorderLayout.SOUTH);

		JLabel label_autoclean = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.EditDialog.label_autoClean"));
		panel_autoremove.add(label_autoclean);

		checkBox_autoClean = new JCheckBox(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.EditDialog.checkbox_numberOfBackupsToKeep"));
		panel_autoremove.add(checkBox_autoClean);

		spinner_numberOfBackupsToKeep = new JSpinner();
		spinner_numberOfBackupsToKeep.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null,
				new Integer(1)));
		panel_autoremove.add(spinner_numberOfBackupsToKeep);

		JPanel panel_backupType = new JPanel();
		panel_main.add(panel_backupType, BorderLayout.SOUTH);

		JLabel label_properties = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.EditDialog.label_backupType"));
		panel_backupType.add(label_properties);

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

					// Einstellungen für das automatische Aufräumen
					// sichern:
					task.setAutoCleanEnabled(checkBox_autoClean.isSelected());
					task.setNumberOfBackupsToKeep((Integer) spinner_numberOfBackupsToKeep.getValue());

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
	 * Prüft den gegebenen Pfad auf Gültigkeit.
	 * 
	 * @param s
	 *            zu prüfender Pfad
	 * @return Gültigkeit des Pfades.
	 */
	private boolean isValidPath(String pfad) {
		File f = new File(pfad);
		if (f.exists()) {
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
		checkBox_autoClean.setSelected(enabled);
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
}
