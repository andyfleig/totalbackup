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

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import data.Filter;
import data.Source;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ResourceBundle;

import listener.IFilterDialogListener;
import listener.ISourcesDialogListener;

/**
 * Dialog zum Festlegen und Bearbeiten der Quellen.
 * 
 * @author Andreas Fleig
 *
 */
public class SourcesDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8855971977478046562L;
	private final JPanel panel_main = new JPanel();
	private JTextField textfield_source;
	private JList<Filter> list_filter;
	private DefaultListModel<Filter> listModel;

	private ISourcesDialogListener sourcesListener;
	private FilterDialog filterDialog;

	/**
	 * Legt fest, ob gerade ein existierender Filter bearbeitet, oder ein neuer
	 * erzeugt wird.
	 */
	private boolean inEditMode;
	/**
	 * Speichert den Originalpfad der Qulle.
	 */
	private String originalPath;
	private JPanel panel_path;
	private JPanel panel_filter;
	private JButton button_find;
	private JButton button_edit;
	private JButton button_delete;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SourcesDialog dialog = new SourcesDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SourcesDialog(ISourcesDialogListener sourcesListener) {
		setResizable(false);
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.sourcesListener = sourcesListener;
		setTitle(ResourceBundle.getBundle("gui.messages").getString("GUI.SourcesDialog.title"));
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		panel_main.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel_main, BorderLayout.CENTER);
		panel_main.setLayout(new BorderLayout(0, 0));

		JPanel panel;
		panel_path = new JPanel();
		panel_main.add(panel_path, BorderLayout.NORTH);
		panel_path.setLayout(new BorderLayout(0, 0));

		JLabel label_source = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.SourcesDialog.sourcePath"));
		panel_path.add(label_source, BorderLayout.WEST);

		textfield_source = new JTextField();
		panel_path.add(textfield_source);
		textfield_source.setColumns(10);

		// Button Druchsuchen:
		JButton button;
		button_find = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_find"));
		button_find.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int state = fc.showOpenDialog(null);

				if (state == JFileChooser.APPROVE_OPTION) {
					File sourceFile = fc.getSelectedFile();
					if (!isAlreadySourcePath(sourceFile.getAbsolutePath())) {
						textfield_source.setText(sourceFile.getAbsolutePath());
					} else {
						JOptionPane.showMessageDialog(null,
								ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.errSamePath"),
								ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		button_find.setAlignmentX(0.5f);
		panel_path.add(button_find, BorderLayout.EAST);

		JPanel panel_filter;
		panel_filter = new JPanel();
		panel_main.add(panel_filter, BorderLayout.CENTER);
		panel_filter.setLayout(new BorderLayout(0, 0));

		listModel = new DefaultListModel<Filter>();
		list_filter = new JList<Filter>(listModel);
		list_filter.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list_filter.setSelectedIndex(0);
		list_filter.setVisibleRowCount(6);
		JScrollPane listScroller_filter = new JScrollPane(list_filter);
		panel_filter.add(listScroller_filter);
		listScroller_filter.setMaximumSize(new Dimension(200, 200));
		listScroller_filter.setMinimumSize(new Dimension(200, 200));

		JLabel label_filter = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.SourcesDialog.filter"));
		panel_filter.add(label_filter, BorderLayout.NORTH);

		JPanel panel_ConfigurateFilter = new JPanel();
		panel_filter.add(panel_ConfigurateFilter, BorderLayout.EAST);
		panel_ConfigurateFilter.setLayout(new BoxLayout(panel_ConfigurateFilter, BoxLayout.Y_AXIS));
		{
			// Button Add:
			JButton button_add = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_add"));
			button_add.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					filterDialog = new FilterDialog(new IFilterDialogListener() {

						@Override
						public void addFilter(String path, int mode) {
							listModel.addElement(new Filter(path, mode));
						}

						@Override
						public boolean isUnderSourceRoot(String path) {
							return SourcesDialog.this.isUnderSourceRoot(path);
						}

						@Override
						public void deleteFilter(String path) {
							SourcesDialog.this.deleteFilter(path);
						}

						@Override
						public File getSourceFile() {
							return SourcesDialog.this.getSourceFile();
						}
					});
					filterDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					filterDialog.setLocation(SourcesDialog.this.getLocationOnScreen());
					filterDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
					filterDialog.setVisible(true);
				}
			});
			button_add.setAlignmentX(0.5f);
			panel_ConfigurateFilter.add(button_add);
		}

		// Button Bearbeiten:
		JButton button_edit;
		button_edit = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_edit"));
		button_edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (list_filter.isSelectionEmpty()) {
					return;
				}

				filterDialog = new FilterDialog(new IFilterDialogListener() {

					@Override
					public void addFilter(String path, int mode) {
						listModel.addElement(new Filter(path, mode));
					}

					@Override
					public boolean isUnderSourceRoot(String path) {
						return SourcesDialog.this.isUnderSourceRoot(path);
					}

					@Override
					public void deleteFilter(String path) {
						SourcesDialog.this.deleteFilter(path);
					}

					@Override
					public File getSourceFile() {
						return SourcesDialog.this.getSourceFile();
					}
				});
				filterDialog.setFilter(listModel.get(list_filter.getSelectedIndex()).getPath());
				filterDialog.setEditMode(true);
				filterDialog.setOriginalPath(list_filter.getSelectedValue().getPath());
				filterDialog.setMode(listModel.get(list_filter.getSelectedIndex()).getMode());

				filterDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				filterDialog.setLocation(SourcesDialog.this.getLocationOnScreen());
				filterDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
				filterDialog.setVisible(true);
			}
		});
		button_edit.setAlignmentX(0.5f);
		panel_ConfigurateFilter.add(button_edit);

		JButton button_delete;
		button_delete = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_delete"));
		button_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int reply = JOptionPane.showConfirmDialog(null,
						ResourceBundle.getBundle("gui.messages").getString("Messages.DeleteFilter"), null,
						JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					listModel.remove(list_filter.getSelectedIndex());
				}
			}
		});
		button_delete.setAlignmentX(0.5f);
		panel_ConfigurateFilter.add(button_delete);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		// Button OK:
		JButton button_ok = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_ok"));
		button_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Quellpfad prüfen:
				File source = new File(textfield_source.getText());
				if (!source.exists() || !source.isDirectory()) {
					JOptionPane.showMessageDialog(null,
							ResourceBundle.getBundle("gui.messages").getString("GUI.SourcesDialog.errIllegalSource"),
							ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				// Prüfen ob der gewählte Pfad bereits Quellpfad
				// ist:
				if (isAlreadySourcePath(textfield_source.getText()) && !inEditMode) {
					JOptionPane.showMessageDialog(null,
							ResourceBundle.getBundle("gui.messages").getString("GUI.EditDialog.errSamePath"),
							ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				// Quellobjekt erzeugen und hinzufügen:
				Source newSource = new Source(textfield_source.getText());

				// Filter hinzufügen:
				for (int i = 0; i < listModel.getSize(); i++) {
					newSource.addFilter(listModel.get(i));
				}

				if (inEditMode) {
					deleteSource(originalPath);
				}

				addSource(newSource);
				SourcesDialog.this.dispose();

			}
		});
		button_ok.setActionCommand("OK");
		buttonPane.add(button_ok);
		getRootPane().setDefaultButton(button_ok);

		JButton button_cancel = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_cancel"));
		button_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SourcesDialog.this.dispose();
			}
		});
		button_cancel.setActionCommand("Cancel");
		buttonPane.add(button_cancel);

	}

	/**
	 * Prüft für den gegebenen String ob dieser bereits Quellpfad ist.
	 * 
	 * @param path
	 *            zu prüfender Quellpfad
	 * @return ob der Pfad bereits Quellpfad ist
	 */
	private boolean isAlreadySourcePath(String path) {
		return sourcesListener.isAlreadySourcePath(path);
	}

	/**
	 * Fügt eine Quelle hinzu.
	 * 
	 * @param source
	 *            hinzuzufügende Quelle.
	 */
	private void addSource(Source source) {
		sourcesListener.addSource(source);
	}

	/**
	 * Sucht nach einer Quelle mit dem gegebenen Pfad. Wird diese gefunden wird
	 * sie gelöscht.
	 * 
	 * @param path
	 *            zu löschende Quelle
	 */
	public void deleteSource(String path) {
		sourcesListener.deleteSource(path);
	}

	/**
	 * Setzt den Pfad der im Quellpfad-Textfeld angezeigt wird.
	 * 
	 * @param source
	 *            zu setzende Quelle
	 */
	public void setSource(Source source) {
		textfield_source.setText(source.getPath());
	}

	/**
	 * Schaltet den EditMode an bzw. aus.
	 * 
	 * @param editMode
	 *            true = an, false = aus
	 */
	public void setEditMode(boolean inEditMode) {
		this.inEditMode = inEditMode;
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
	 * Prüft ob der gegebene Pfad unter dem Rootpfad der gewählten Quelle ist.
	 * 
	 * @param path
	 *            zu prüfender Pfad
	 * @return ob der gegebene Pfad unter dem Rootpfad der Quelle ist
	 */
	private boolean isUnderSourceRoot(String path) {
		if (path.startsWith(textfield_source.getText())) {
			return true;
		}
		return false;
	}

	/**
	 * Gibt die Quelldatei zurück.
	 * 
	 * @return Quelldatei
	 */
	private File getSourceFile() {
		return new File(textfield_source.getText());
	}

	/**
	 * Durchsucht die Liste der Filter nach dem gegebenen Pfad. Wird ein Filter
	 * mit diesem Pfad gefunden wird dieser gelöscht.
	 * 
	 * @param path
	 *            zu löschender Filterpfad
	 */
	private void deleteFilter(String path) {
		for (int i = 0; i < listModel.getSize(); i++) {
			if (listModel.get(i).equals(path)) {
				listModel.remove(i);
			}
		}
	}

	/**
	 * Fügt einen Filter zur Liste der Filter hinzu.
	 * 
	 * @param path
	 *            hinzuzufügender Filter
	 * @param mode
	 *            Filter-Mdous (0 = Ausschluss-Filter; 1 = MD5-Filter)
	 */
	public void addFilter(String path, int mode) {
		listModel.addElement(new Filter(path, mode));
	}
}
