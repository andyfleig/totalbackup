package gui;

import main.BackupTask;
import main.Source;
import gui.IEditListener;

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

public class Edit extends JDialog {

	private IEditListener editListener;

	private final JPanel contentPanel = new JPanel();
	private JTextField tf_Name;

	private JList<Source> list_SourcePaths;
	private DefaultListModel<Source> listModel;

	private File sourceFile;
	private JTextField tf_Destination;

	private JRadioButton rdbtnNormal;
	private JRadioButton rdbtnHardlink;

	private JCheckBox cB_autoClean;
	private JSpinner s_numberOfBackupsToKeep;

	private Sources sourcesDialog;
	private ISourcesListener sourcesListener;

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
	public Edit(IEditListener listener) {
		setTitle(ResourceBundle.getBundle("gui.messages").getString("Edit.title"));
		inEditMode = false;
		this.editListener = listener;
		setBounds(100, 100, 511, 389);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.NORTH);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JLabel lbl_Name = new JLabel(ResourceBundle.getBundle("gui.messages").getString("Edit.lbl_Name.text")); //$NON-NLS-1$ //$NON-NLS-2$
				lbl_Name.setHorizontalAlignment(SwingConstants.CENTER);
				panel.add(lbl_Name);
			}
			{
				tf_Name = new JTextField();
				panel.add(tf_Name);
				tf_Name.setColumns(10);
				final ArrayList<Character> forbiddenChars = new ArrayList<Character>(Arrays.asList('_'));
				tf_Name.setDocument(new PlainDocument() {
					@Override
					public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
						for (Character c : forbiddenChars) {
							str = str.replace(String.valueOf(c), "");
						}
						super.insertString(offs, str, a);
					}
				});
			}
		}

		// Sources-Listener:
		sourcesListener = new ISourcesListener() {

			@Override
			public boolean isAlreadySourcePath(String path) {
				return Edit.this.isAlreadySourcePath(path);
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

		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.SOUTH);
			{
				JLabel lbl_Properties = new JLabel(ResourceBundle
						.getBundle("gui.messages").getString("Edit.lbl_Properties.text")); //$NON-NLS-1$ //$NON-NLS-2$
				panel.add(lbl_Properties);

				// JRadioButtons anlegen:
				rdbtnNormal = new JRadioButton(ResourceBundle
						.getBundle("gui.messages").getString("Edit.rdbtnNormal.text")); //$NON-NLS-1$ //$NON-NLS-2$
				rdbtnNormal.setSelected(true);
				rdbtnHardlink = new JRadioButton(ResourceBundle
						.getBundle("gui.messages").getString("Edit.rdbtnHardlink.text")); //$NON-NLS-1$ //$NON-NLS-2$

				// ButtonGroup anlegen:
				ButtonGroup btng_settings = new ButtonGroup();

				// JRadioButtons zur ButtonGroup hinzufügen:
				btng_settings.add(rdbtnNormal);
				btng_settings.add(rdbtnHardlink);

				// JRadioButtons zum Panel hinzufügen:
				panel.add(rdbtnNormal);
				panel.add(rdbtnHardlink);
			}

		}
		{
			{

				listModel = new DefaultListModel<Source>();
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			{
				Panel panel_3 = new Panel();
				panel.add(panel_3);
				panel_3.setLayout(new BorderLayout(0, 0));
				JPanel panel_2 = new JPanel();
				panel_3.add(panel_2, BorderLayout.NORTH);
				panel_2.setLayout(new BorderLayout(0, 0));
				JLabel lbl_Source = new JLabel(ResourceBundle
						.getBundle("gui.messages").getString("Edit.lbl_Source.text")); //$NON-NLS-1$ //$NON-NLS-2$
				panel_2.add(lbl_Source, BorderLayout.NORTH);
				{
					JPanel panel_1 = new JPanel();
					panel_2.add(panel_1, BorderLayout.CENTER);
					panel_1.setLayout(new BorderLayout(0, 0));
					list_SourcePaths = new JList<Source>(listModel);
					list_SourcePaths.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					list_SourcePaths.setSelectedIndex(0);
					list_SourcePaths.setVisibleRowCount(6);
					JScrollPane listScroller = new JScrollPane(list_SourcePaths);
					panel_1.add(listScroller);
					listScroller.setMaximumSize(new Dimension(200, 200));
					listScroller.setMinimumSize(new Dimension(200, 200));
					{
						Panel panel_1_1 = new Panel();
						panel_1.add(panel_1_1, BorderLayout.EAST);
						panel_1_1.setLayout(new BoxLayout(panel_1_1, BoxLayout.Y_AXIS));
						{
							// Button Add:
							JButton btn_Add = new JButton(ResourceBundle
									.getBundle("gui.messages").getString("Edit.btn_Add.text")); //$NON-NLS-1$ //$NON-NLS-2$
							btn_Add.setAlignmentX(CENTER_ALIGNMENT);
							btn_Add.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									sourcesDialog = new Sources(sourcesListener);
									sourcesDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
									sourcesDialog.setLocation(Edit.this.getLocationOnScreen());
									sourcesDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
									sourcesDialog.setVisible(true);
								}
							});
							panel_1_1.add(btn_Add);
						}
						{
							// Button Delete:
							JButton btn_Delete = new JButton(ResourceBundle
									.getBundle("gui.messages").getString("Edit.btn_Delete.text")); //$NON-NLS-1$ //$NON-NLS-2$
							btn_Delete.setAlignmentX(CENTER_ALIGNMENT);
							btn_Delete.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									int reply = JOptionPane
											.showConfirmDialog(null, ResourceBundle.getBundle("gui.messages")
													.getString("Messages.DeleteSource"), null,
													JOptionPane.YES_NO_OPTION);
									if (reply == JOptionPane.YES_OPTION) {
										if (!list_SourcePaths.isSelectionEmpty()) {
											listModel.remove(list_SourcePaths.getSelectedIndex());
										}
									}
								}
							});
							{
								// Button Bearbeiten:
								JButton button = new JButton("Bearbeiten");
								button.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										if (list_SourcePaths.isSelectionEmpty()) {
											return;
										}
										sourcesDialog = new Sources(sourcesListener);
										sourcesDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
										sourcesDialog.setEditMode(true);

										Source currentSource = list_SourcePaths.getSelectedValue();
										sourcesDialog.setOriginalPath(currentSource.getPath());
										sourcesDialog.setSource(currentSource);

										ArrayList<String> filterOfCurrentSource = currentSource.getFilter();

										for (int i = 0; i < filterOfCurrentSource.size(); i++) {
											sourcesDialog.addFilter(filterOfCurrentSource.get(i));
										}
										sourcesDialog.setLocation(Edit.this.getLocationOnScreen());
										sourcesDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
										sourcesDialog.setVisible(true);

									}
								});
								button.setAlignmentX(0.5f);
								panel_1_1.add(button);
							}
							panel_1_1.add(btn_Delete);
						}
					}
				}
				{
					Panel panel_1 = new Panel();
					panel_3.add(panel_1, BorderLayout.CENTER);
					{
						JLabel lbl_Destination = new JLabel(ResourceBundle
								.getBundle("gui.messages").getString("Edit.lbl_Destination.text")); //$NON-NLS-1$ //$NON-NLS-2$
						panel_1.add(lbl_Destination);
					}
					{
						tf_Destination = new JTextField();
						panel_1.add(tf_Destination);
						tf_Destination.setColumns(20);
					}
					{
						// Button Find:
						JButton btn_Find = new JButton(ResourceBundle
								.getBundle("gui.messages").getString("Edit.btn_Find.text")); //$NON-NLS-1$ //$NON-NLS-2$
						btn_Find.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								JFileChooser fc = new JFileChooser();
								fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								int state = fc.showOpenDialog(null);
								if (state == JFileChooser.APPROVE_OPTION) {
									sourceFile = fc.getSelectedFile();
									tf_Destination.setText(sourceFile.getAbsolutePath());
								}
							}
						});
						panel_1.add(btn_Find);
					}
				}
				{
					JPanel p_autoremove = new JPanel();
					panel_3.add(p_autoremove, BorderLayout.SOUTH);
					{
						JLabel lbl_autoclean = new JLabel(ResourceBundle
								.getBundle("gui.messages").getString("Edit.lblAutomatischesLschen.text")); //$NON-NLS-1$ //$NON-NLS-2$
						p_autoremove.add(lbl_autoclean);
					}
					{
						cB_autoClean = new JCheckBox(ResourceBundle
								.getBundle("gui.messages").getString("Edit.chckbxAnzahlZuBehlatender.text")); //$NON-NLS-1$ //$NON-NLS-2$
						p_autoremove.add(cB_autoClean);
					}
					{
						s_numberOfBackupsToKeep = new JSpinner();
						s_numberOfBackupsToKeep.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null,
								new Integer(1)));
						p_autoremove.add(s_numberOfBackupsToKeep);
					}
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);

			{
				// Button OK:
				JButton btn_Ok = new JButton(ResourceBundle.getBundle("gui.messages").getString("Edit.btn_Ok.text")); //$NON-NLS-1$ //$NON-NLS-2$
				btn_Ok.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						BackupTask task;
						boolean allInputsAreValid = true;
						// Namen prüfen und BackupTask erstellen (wenn der Name
						// gültig ist):
						if (isValidName(tf_Name.getText())) {
							if (nameIsNotTaken(tf_Name.getText())) {
								task = new BackupTask(tf_Name.getText());
							} else {
								if (!inEditMode) {
									JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("gui.messages")
											.getString("Edit.ErrSameName"), ResourceBundle.getBundle("gui.messages")
											.getString("Edit.ErrMsg"), JOptionPane.INFORMATION_MESSAGE);
									return;
								} else {
									task = editListener.getBackupTaskWithName(tf_Name.getText());
									editListener.removeBackupTask(task);
									task.resetPaths();
								}
							}
							// Backup-Modus speichern:
							if (rdbtnNormal.isSelected()) {
								task.setBackupMode(0);
							} else if (rdbtnHardlink.isSelected()) {
								task.setBackupMode(1);
							}

							// Prüfen ob Quellpfade eingefügt wurden:
							if (!listModel.isEmpty()) {
								for (int i = 0; i < listModel.getSize(); i++) {
									if (isValidPath(listModel.getElementAt(i).toString().trim())) {
										task.addSourcePath(listModel.getElementAt(i));
									} else {
										// Mindestens ein Quellpfad ist ungültig
										JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("gui.messages")
												.getString("Edit.ErrIllegalSource"),
												ResourceBundle.getBundle("gui.messages").getString("Edit.ErrMsg"),
												JOptionPane.INFORMATION_MESSAGE);
										return;
									}
								}
								// Prüfen ob ein Zielpfad eingefügt wurde:
								if (isValidPath(tf_Destination.getText())) {
									task.setDestinationPath(tf_Destination.getText());
									editListener.addBackupTask(task);
								} else {
									// Zielpfad ist ungültig oder leer:
									JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("gui.messages")
											.getString("Edit.ErrIllegalDestination"),
											ResourceBundle.getBundle("gui.messages").getString("Edit.ErrMsg"),
											JOptionPane.INFORMATION_MESSAGE);
									return;
								}
							} else {
								// Keine Quelle gewählt:
								JOptionPane.showMessageDialog(
										null,
										ResourceBundle.getBundle("gui.messages").getString(
												"Edit.ErrNoSourcePathSelected"),
										ResourceBundle.getBundle("gui.messages").getString("Edit.ErrMsg"),
										JOptionPane.INFORMATION_MESSAGE);
								return;
							}

							// Einstellungen für das automatische Aufräumen
							// sichern:
							task.setAutoCleanEnabled(cB_autoClean.isSelected());
							task.setNumberOfBackupsToKeep((Integer) s_numberOfBackupsToKeep.getValue());

						} else {
							// Ungültiger Name:
							JOptionPane.showMessageDialog(null,
									ResourceBundle.getBundle("gui.messages").getString("Edit.ErrIllegalName"),
									ResourceBundle.getBundle("gui.messages").getString("Edit.ErrMsg"),
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}
						// Einstellungen sichern (seriallisieren):
						editListener.saveProperties();
						if (allInputsAreValid) {
							Edit.this.dispose();
						}
					}
				});
				btn_Ok.setActionCommand(ResourceBundle.getBundle("gui.messages").getString("Edit.btn_Ok.actionCommand")); //$NON-NLS-1$ //$NON-NLS-2$
				buttonPane.add(btn_Ok);
				getRootPane().setDefaultButton(btn_Ok);
			}

			// Button Cancel:
			JButton btn_Abbrechen = new JButton(ResourceBundle
					.getBundle("gui.messages").getString("Edit.btn_Abbrechen.text")); //$NON-NLS-1$ //$NON-NLS-2$
			btn_Abbrechen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Edit.this.dispose();
				}
			});
			btn_Abbrechen.setActionCommand(ResourceBundle
					.getBundle("gui.messages").getString("Edit.btn_Abbrechen.actionCommand")); //$NON-NLS-1$ //$NON-NLS-2$
			buttonPane.add(btn_Abbrechen);
		}
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
		return tf_Destination.getText();
	}

	/**
	 * Legt den Namen des Backup-Tasks fest.
	 * 
	 * @param name
	 *            festzulegender Name
	 */
	public void setBackupTaskName(String name) {
		tf_Name.setText(name);
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
		tf_Destination.setText(path);
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
		if (rdbtnNormal.isSelected()) {
			return 0;
		} else if (rdbtnHardlink.isSelected()) {
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
			rdbtnNormal.setSelected(true);
			rdbtnHardlink.setSelected(false);
		} else if (mode == 1) {
			rdbtnNormal.setSelected(false);
			rdbtnHardlink.setSelected(true);
		}
	}

	/**
	 * Setzt die Einstellung für die Auto-Clean Funktion auf der GUI.
	 * 
	 * @param enabled
	 *            Auto-Clean Funktion aktiviert (true) oder Deaktiviert (false)
	 */
	public void setAutoCleanEnabled(boolean enabled) {
		cB_autoClean.setSelected(enabled);
	}

	/**
	 * Setzt die Anzahl der bei Auto-Clean zu behaltenden Backups auf der GUI.
	 * 
	 * @param numberOfBackupsToKeep
	 *            zu setzende Anzahl an zu behaltenden Backups
	 */
	public void setNumberOfBackupsToKeep(int numberOfBackupsToKeep) {
		s_numberOfBackupsToKeep.setValue(numberOfBackupsToKeep);
	}

	/**
	 * Sperrt das Namens-TextFeld für den Benutzer (Name eines Backups kann
	 * nachträglich nicht geändert werden).
	 * 
	 * @param editable
	 *            Editierbarkeit
	 */
	public void setEditable(boolean editable) {
		tf_Name.setEditable(editable);
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
