/*
 * Copyright 2014 - 2016 Andreas Fleig (andy DOT fleig AT gmail DOT com)
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

import java.awt.*;
import java.net.URL;
import java.util.Optional;
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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import listener.IFilterDialogListener;
import main.BackupHelper;

/**
 * Dialog zum Erstellen und Bearbeiten eines Filters.
 *
 * @author Andreas Fleig
 */
public class FilterDialog implements Initializable {
	private static Stage stage;

	private IFilterDialogListener listener;

	@FXML
	private TextField tf_filterPath;
	@FXML
	private RadioButton rb_exclutionFilter;
	@FXML
	private RadioButton rb_md5Filter;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void init(IFilterDialogListener listener) {
		this.listener = listener;
	}

	public void setInitPath(String initPath) {
		tf_filterPath.setText(initPath);
	}

	public void setInitMode(int mode) {
		if (mode == 1) {
			rb_md5Filter.setSelected(true);
		}
	}

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
	 * Create the dialog.
	 */
//	public FilterDialog(IFilterDialogListener listener) {
//		setTitle(ResourceBundle.getBundle("messages").getString("GUI.FilterDialog.title"));
//		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
//		this.listener = listener;
//		setBounds(100, 100, 462, 148);
//		setIconImage(Toolkit.getDefaultToolkit().getImage(BackupHelper.ICON_LOCATION));
//		getContentPane().setLayout(new BorderLayout());
//
//		JPanel panel_filterType = new JPanel();
//		getContentPane().add(panel_filterType, BorderLayout.NORTH);
//
//		radioButton_excludeFilter = new JRadioButton(
//				ResourceBundle.getBundle("messages").getString("GUI.FilterDialog.radioButton_excludeFilter"));
//		radioButton_excludeFilter.setSelected(true);
//		radioButton_excludeFilter
//				.setToolTipText(ResourceBundle.getBundle("messages").getString("GUI.FilterDialog.excludeToolTip"));
//		panel_filterType.add(radioButton_excludeFilter);
//		radioButton_useMD5 = new JRadioButton(
//				ResourceBundle.getBundle("messages").getString("GUI.FilterDialog.radioButton_useMD5"));
//		radioButton_useMD5
//				.setToolTipText(ResourceBundle.getBundle("messages").getString("GUI.FilterDialog.md5ToolTip"));
//		panel_filterType.add(radioButton_useMD5);
//
//		ButtonGroup rBtnGroup = new ButtonGroup();
//		rBtnGroup.add(radioButton_excludeFilter);
//		rBtnGroup.add(radioButton_useMD5);
//
//		panel_source.setBorder(new EmptyBorder(5, 5, 5, 5));
//		getContentPane().add(panel_source, BorderLayout.CENTER);
//		panel_source.setLayout(new BorderLayout(0, 0));
//
//		textfield_filter = new JTextField();
//		panel_source.add(textfield_filter);
//		textfield_filter.setColumns(10);
//
//		JLabel lbl_filter = new JLabel(ResourceBundle.getBundle("messages").getString("GUI.FilterDialog.label_filter"));
//		panel_source.add(lbl_filter, BorderLayout.NORTH);
//
//		// Button Durchsuchen:
//		JButton button_Find = new JButton(ResourceBundle.getBundle("messages").getString("GUI.button_find"));
//		button_Find.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// Beschränkung des FC auf den Sourceroot:
//				FileSystemView fsv = new DirectoryRestrictedFileSystemView(getSourceFile());
//
//				JFileChooser fc = new JFileChooser(fsv.getHomeDirectory(), fsv);
//				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//				int state = fc.showOpenDialog(null);
//				if (state == JFileChooser.APPROVE_OPTION) {
//					File selectedFile = fc.getSelectedFile();
//					if (isUnderSourceRoot(selectedFile.getAbsolutePath())) {
//						textfield_filter.setText(selectedFile.getAbsolutePath());
//					} else {
//						JOptionPane.showMessageDialog(null,
//								ResourceBundle.getBundle("messages")
//										.getString("GUI.FilterDialog.errNotUnderSourceRoot"),
//								ResourceBundle.getBundle("messages").getString("GUI.errMsg"),
//								JOptionPane.INFORMATION_MESSAGE);
//					}
//				}
//			}
//		});
//		panel_source.add(button_Find, BorderLayout.EAST);
//
//		JPanel panel_buttons = new JPanel();
//		panel_buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
//		getContentPane().add(panel_buttons, BorderLayout.SOUTH);
//
//		// Button OK:
//		JButton button_ok = new JButton(ResourceBundle.getBundle("messages").getString("GUI.button_ok"));
//		button_ok.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// Prüfen ob der gewählte Task gerade ausgeführt wird:
//				if (listener.isBackupTaskRunning()) {
//					JOptionPane.showMessageDialog(null,
//							ResourceBundle.getBundle("messages").getString("GUI.Mainframe.errTaskIsRunning"),
//							ResourceBundle.getBundle("messages").getString("GUI.errMsg"), JOptionPane.ERROR_MESSAGE);
//					return;
//				}
//				// Pfad auf gültigkeit Prüfen:
//				if (!(new File(textfield_filter.getText()).exists())) {
//					return;
//				}
//				if (inEditMode) {
//					deleteFilter(originalPath);
//				}
//				// Unterscheidung der verschiedenen Filter:
//				if (radioButton_excludeFilter.isSelected()) {
//					addFilter(textfield_filter.getText(), 0);
//				} else if (radioButton_useMD5.isSelected()) {
//					addFilter(textfield_filter.getText(), 1);
//				}
//				FilterDialog.this.disposeDialog();
//			}
//		});
//		button_ok.setActionCommand("OK");
//		panel_buttons.add(button_ok);
//		getRootPane().setDefaultButton(button_ok);
//
//		// Button Abbrechen:
//		JButton button_cancel = new JButton(ResourceBundle.getBundle("messages").getString("GUI.button_cancel"));
//		button_cancel.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				FilterDialog.this.disposeDialog();
//			}
//		});
//		button_cancel.setActionCommand("Cancel");
//		panel_buttons.add(button_cancel);
//
//	}
	@FXML
	private void addFilterAction() {
		// ToDo: DirectoryChooser only allows to choose dirs and FileChooser only allows to choose files
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("choose File or Directory to Filter");
		File filterPath = dirChooser.showDialog(stage);
		if (filterPath == null) {
			return;
		}
		tf_filterPath.setText(filterPath.getAbsolutePath());
	}

	@FXML
	private void okAction() {
		File filterPath = new File(tf_filterPath.getText());
		if (filterPath == null || !filterPath.exists()) {
			// show error message
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid filter.");
			alert.setContentText("The following filter path is not valid: " + tf_filterPath.getText());

			Optional<ButtonType> result = alert.showAndWait();
			return;
		}
		// filterMode 0 (default) is exclusion-filter, 1 is md5-filter
		int filterMode = 0;
		if (rb_md5Filter.isSelected()) {
			filterMode = 1;
		}
		if (!listener.isUnderSourceRoot(filterPath.getAbsolutePath())) {
			// show error message
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid Filter.");
			alert.setContentText("The filter path has to be inside the specified source!");
			alert.setResizable(true);

			Optional<ButtonType> result = alert.showAndWait();
			return;
		}
		listener.addFilter(filterPath.getAbsolutePath(), filterMode);
		stage.close();
	}

	@FXML
	private void cancelAction() {
		stage.close();
	}

	/**
	 * Prüft ob der gegebene Pfad unter dem Rootpfad der gewählten Quelle ist.
	 *
	 * @param path zu prüfender Pfad
	 * @return ob der gegebene Pfad unter dem Rootpfad der Quelle ist
	 */
	private boolean isUnderSourceRoot(String path) {
		return listener.isUnderSourceRoot(path);
	}
}
