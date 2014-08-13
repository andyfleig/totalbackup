package gui;

import main.Controller;
import main.BackupTask;

import java.util.ResourceBundle;
import java.util.ArrayList;

import java.io.File;

import java.awt.BorderLayout;
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
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;


public class Edit extends JDialog {

	private Controller controller;

	private final JPanel contentPanel = new JPanel();
	private JTextField tf_Name;

	private JList<String> list_SourcePaths;
	private DefaultListModel<String> listModel;

	private File sourceFile;
	private JTextField tf_Destination;
	
	private JRadioButton rdbtnNormal;
	private JRadioButton rdbtnHarlink;

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
	public Edit(Controller c) {
		this.controller = c;
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
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.SOUTH);
			{
				JLabel lbl_Properties = new JLabel(ResourceBundle
						.getBundle("gui.messages").getString("Edit.lbl_Properties.text")); //$NON-NLS-1$ //$NON-NLS-2$
				panel.add(lbl_Properties);
				
				// JRadioButtons anlegen:
				rdbtnNormal = new JRadioButton(ResourceBundle.getBundle("gui.messages").getString("Edit.rdbtnNormal.text")); //$NON-NLS-1$ //$NON-NLS-2$
				rdbtnNormal.setSelected(true);
				rdbtnHarlink = new JRadioButton(ResourceBundle.getBundle("gui.messages").getString("Edit.rdbtnHarlink.text")); //$NON-NLS-1$ //$NON-NLS-2$
				
				// ButtonGroup anlegen:
				ButtonGroup btng_settings = new ButtonGroup();
				
				// JRadioButtons zur ButtonGroup hinzufügen:
				btng_settings.add(rdbtnNormal);
				btng_settings.add(rdbtnHarlink);

				// JRadioButtons zum Panel hinzufügen:
				panel.add(rdbtnNormal);
				panel.add(rdbtnHarlink);
			}

		}
		{
			{

				listModel = new DefaultListModel<String>();
			}
		}
		{
			Panel panel = new Panel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			JPanel panel_2 = new JPanel();
			panel.add(panel_2);
			panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			JLabel lbl_Source = new JLabel(ResourceBundle.getBundle("gui.messages").getString("Edit.lbl_Source.text")); //$NON-NLS-1$ //$NON-NLS-2$
			panel_2.add(lbl_Source);
			list_SourcePaths = new JList<String>(listModel);
			list_SourcePaths.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list_SourcePaths.setSelectedIndex(0);
			list_SourcePaths.setVisibleRowCount(6);
			JScrollPane listScroller = new JScrollPane(list_SourcePaths);
			listScroller.setMaximumSize(new Dimension(200, 200));
			listScroller.setMinimumSize(new Dimension(200, 200));
			panel_2.add(listScroller);
			{
				Panel panel_1 = new Panel();
				panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
				panel_2.add(panel_1);
				{
					// Button Add:
					JButton btn_Add = new JButton(ResourceBundle
							.getBundle("gui.messages").getString("Edit.btn_Add.text")); //$NON-NLS-1$ //$NON-NLS-2$
					btn_Add.setAlignmentX(CENTER_ALIGNMENT);
					btn_Add.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							JFileChooser fc = new JFileChooser();
							fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							int state = fc.showOpenDialog(null);

							if (state == JFileChooser.APPROVE_OPTION) {
								sourceFile = fc.getSelectedFile();
								if (!isAlreadySourcePath(sourceFile.getAbsolutePath())) {
									listModel.addElement(sourceFile.getAbsolutePath());
								} else {
									//TODO: outprint: "Fehler: Pfad exisitert bereits"
								}
							}
						}
					});
					panel_1.add(btn_Add);
				}
				{
					// Button Delete:
					JButton btn_Delete = new JButton(ResourceBundle
							.getBundle("gui.messages").getString("Edit.btn_Delete.text")); //$NON-NLS-1$ //$NON-NLS-2$
					btn_Delete.setAlignmentX(CENTER_ALIGNMENT);
					btn_Delete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (!list_SourcePaths.isSelectionEmpty()) {
								
								listModel.remove(list_SourcePaths.getSelectedIndex());
							}
						}
					});
					panel_1.add(btn_Delete);
				}
			}
			{
				Panel panel_1 = new Panel();
				panel.add(panel_1);
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
								task = controller.getBackupTaskWithName(tf_Name.getText());
								controller.removeBackupTask(task);
								task.resetPaths();
							}
							// Prüfen ob Quellpfade eingefügt wurden:
							if (!listModel.isEmpty()) {
								for (int i = 0; i < listModel.getSize(); i++) {
									if (isValidPath(listModel.getElementAt(i).toString().trim())) {
										task.addSourcePath(listModel.getElementAt(i).toString().trim());
									} else {
										allInputsAreValid = false;
									}
								}
								// Prüfen ob ein Zielpfad eingefügt wurde:
								if (isValidPath(tf_Destination.getText())) {
									task.setDestinationPath(tf_Destination.getText());

									controller.addBackupTask(task);
								} else {
									allInputsAreValid = false;
								}
							} else {
								allInputsAreValid = false;
							}
						} else {
							allInputsAreValid = false;
						}
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
	 * @param s zu prüfender Pfad
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
	 * @param name zu prüfender Name
	 * @return ob der Name noch nicht benutzt ist
	 */
	private boolean nameIsNotTaken(String name) {
		if (controller.getBackupTaskNames().contains(name)) {
			return false;
		}
		return true;
	}

	/**
	 * Prüft den gegebenen Namen auf Gültigkeit.
	 * @param name zu prüfender Name
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
	 * @return Zielpfad
	 */
	public String getDestinationPath() {
		return tf_Destination.getText();
	}

	/**
	 * Legt den Namen des Backup-Tasks fest.
	 * @param name festzulegender Name
	 */
	public void setBackupTaskName(String name) {
		tf_Name.setText(name);
	}

	/**
	 * Legt die Quellpfade fest.
	 * @param sourcePaths festzulegende Quellpfade
	 */
	public void setSourcePaths(ArrayList<String> sourcePaths) {
		for (int i = 0; i < sourcePaths.size(); i++) {
			listModel.addElement(sourcePaths.get(i));
		}
	}

	/**
	 * Legt den Zielpfad fest.
	 * @param path festzulegender Zielpfad
	 */
	public void setDestinationPath(String path) {
		tf_Destination.setText(path);
	}
	
	/**
	 * Prüft ob ein bestimmter Pfad bereits als Quellpfad festgelegt ist.
	 * @param path zu prüfender Pfad
	 * @return ob der Pfad bereits festgelegt ist
	 */
	private boolean isAlreadySourcePath(String path) {
		return listModel.contains(path);
	}
}
