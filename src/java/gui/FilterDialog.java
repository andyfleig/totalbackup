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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JRadioButton;

import listener.IFilterDialogListener;

/**
 * Dialog zum Erstellen und Bearbeiten eines Filters.
 * 
 * @author Andreas Fleig
 *
 */
public class FilterDialog extends JDialog {

	private final JPanel panel_source = new JPanel();
	private JTextField textfield_filter;

	private IFilterDialogListener listener;
	/**
	 * Legt fest, ob gerade ein existierender Filter bearbeitet, oder ein neuer
	 * erzeugt wird.
	 */
	private boolean inEditMode;
	/**
	 * Speichert den Originalpfad der Quelle.
	 */
	private String originalPath;
	/**
	 * RadioButton für den Ausschluss-Filter.
	 */
	private JRadioButton radioButton_excludeFilter;
	/**
	 * RadioButton für den MD5-Filter.
	 */
	private JRadioButton radioButton_useMD5;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FilterDialog dialog = new FilterDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public FilterDialog(IFilterDialogListener listener) {
		setTitle(ResourceBundle.getBundle("gui.messages").getString("GUI.FilterDialog.title"));
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.listener = listener;
		setBounds(100, 100, 462, 148);
		getContentPane().setLayout(new BorderLayout());

		JPanel panel_filterType = new JPanel();
		getContentPane().add(panel_filterType, BorderLayout.NORTH);

		radioButton_excludeFilter = new JRadioButton(
				ResourceBundle.getBundle("gui.messages").getString("GUI.FilterDialog.radioButton_excludeFilter"));
		radioButton_excludeFilter.setSelected(true);
		radioButton_excludeFilter
				.setToolTipText(ResourceBundle.getBundle("gui.messages").getString("GUI.FilterDialog.excludeToolTip"));
		panel_filterType.add(radioButton_excludeFilter);
		radioButton_useMD5 = new JRadioButton(
				ResourceBundle.getBundle("gui.messages").getString("GUI.FilterDialog.radioButton_useMD5"));
		radioButton_useMD5
				.setToolTipText(ResourceBundle.getBundle("gui.messages").getString("GUI.FilterDialog.md5ToolTip"));
		panel_filterType.add(radioButton_useMD5);

		ButtonGroup rBtnGroup = new ButtonGroup();
		rBtnGroup.add(radioButton_excludeFilter);
		rBtnGroup.add(radioButton_useMD5);

		panel_source.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel_source, BorderLayout.CENTER);
		panel_source.setLayout(new BorderLayout(0, 0));

		textfield_filter = new JTextField();
		panel_source.add(textfield_filter);
		textfield_filter.setColumns(10);

		JLabel lbl_filter = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.FilterDialog.label_filter"));
		panel_source.add(lbl_filter, BorderLayout.NORTH);

		// Button Durchsuchen:
		JButton button_Find = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_find"));
		button_Find.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Beschränkung des FC auf den Sourceroot:
				FileSystemView fsv = new DirectoryRestrictedFileSystemView(getSourceFile());

				JFileChooser fc = new JFileChooser(fsv.getHomeDirectory(), fsv);
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int state = fc.showOpenDialog(null);
				if (state == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();
					if (isUnderSourceRoot(selectedFile.getAbsolutePath())) {
						textfield_filter.setText(selectedFile.getAbsolutePath());
					} else {
						JOptionPane.showMessageDialog(null,
								ResourceBundle.getBundle("gui.messages")
										.getString("GUI.FilterDialog.errNotUnderSourceRoot"),
								ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		panel_source.add(button_Find, BorderLayout.EAST);

		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(panel_buttons, BorderLayout.SOUTH);

		// Button OK:
		JButton button_ok = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_ok"));
		button_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Pfad auf gültigkeit Prüfen:
				if (!(new File(textfield_filter.getText()).exists())) {
					return;
				}
				if (inEditMode) {
					deleteFilter(originalPath);
				}
				// Unterscheidung der verschiedenen Filter:
				if (radioButton_excludeFilter.isSelected()) {
					addFilter(textfield_filter.getText(), 0);
				} else if (radioButton_useMD5.isSelected()) {
					addFilter(textfield_filter.getText(), 1);
				}
				FilterDialog.this.dispose();
			}
		});
		button_ok.setActionCommand("OK");
		panel_buttons.add(button_ok);
		getRootPane().setDefaultButton(button_ok);

		// Button Abbrechen:
		JButton button_cancel = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_cancel"));
		button_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FilterDialog.this.dispose();
			}
		});
		button_cancel.setActionCommand("Cancel");
		panel_buttons.add(button_cancel);

	}

	/**
	 * Fügt einen Filter zur Liste der Filter hinzu.
	 * 
	 * @param path
	 *            hinzuzufügender Filter
	 * @param mode
	 *            Filter-Art - 0 = Exclusion-Filter; 1 = MD5Filter
	 */
	private void addFilter(String path, int mode) {
		if (mode == 0) {
			listener.addFilter(path, 0);
		} else if (mode == 1) {
			listener.addFilter(path, 1);
		}
	}

	/**
	 * Prüft ob der gegebene Pfad unter dem Rootpfad der gewählten Quelle ist.
	 * 
	 * @param path
	 *            zu prüfender Pfad
	 * @return ob der gegebene Pfad unter dem Rootpfad der Quelle ist
	 */
	private boolean isUnderSourceRoot(String path) {
		return listener.isUnderSourceRoot(path);
	}

	/**
	 * Gibt die Quelldatei zurück.
	 * 
	 * @return Quelldatei
	 */
	private File getSourceFile() {
		return listener.getSourceFile();
	}

	/**
	 * Setzt das Textfeld für den Filter-Pfad auf den gegebenen Pfad.
	 * 
	 * @param filter
	 *            Pfad des Filters
	 */
	public void setFilter(String filter) {
		textfield_filter.setText(filter);
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
	 * Legt den Originalpfad fest.
	 * 
	 * @param originalPath
	 *            festzulegender Originalpfad
	 */
	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}

	/**
	 * Durchsucht die Liste der Filter nach dem gegebenen Pfad. Wird ein Filter
	 * mit diesem Pfad gefunden wird dieser gelöscht.
	 * 
	 * @param path
	 *            zu löschender Filterpfad
	 */
	private void deleteFilter(String path) {
		listener.deleteFilter(originalPath);
	}

	/**
	 * Legt den gegebenen Modus in der GUI fest.
	 * 
	 * @param mode
	 *            festzulegender Modus
	 */
	public void setMode(int mode) {
		if (mode == 0) {
			radioButton_excludeFilter.setSelected(true);
			radioButton_useMD5.setSelected(false);
		} else if (mode == 1) {
			radioButton_excludeFilter.setSelected(false);
			radioButton_useMD5.setSelected(true);
		}
	}
}
