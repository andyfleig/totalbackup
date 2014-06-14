package gui;

import main.BackupTask;

import java.awt.EventQueue;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JFileChooser;

import gui.About;
import gui.Edit;
import main.Controller;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.FlowLayout;

import javax.swing.JTextField;

import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.lang.NullPointerException;

import javax.swing.JList;

import java.util.ResourceBundle;
import java.util.ArrayList;

public class Mainframe {

	private Mainframe window;

	// private mit setter?
	public JFrame frmTotalbackup;
	private final Action action_about = new SA_About();
	private final Action action_quit = new SA_Quit();
	private JTextArea ta_output;
	private JList<BackupTask> list_backupTasks;

	private Edit editDialog;

	private DefaultListModel<BackupTask> listModel;

	private Controller controller;

	File sourceFile;
	File destinationFile;

	/**
	 * Launch the application.
	 * 
	 * @deprecated
	 */
	public void main(String[] args) {
		// Mainframe window = new Mainframe(controller);
		// window.frmTotalbackup.setVisible(true);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new Mainframe(controller);
					window.frmTotalbackup.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the application.
	 */
	public Mainframe(Controller c) {
		this.controller = c;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frmTotalbackup = new JFrame();
		frmTotalbackup.setTitle(ResourceBundle.getBundle("gui.messages").getString("Mainframe.frmTotalbackup.title")); //$NON-NLS-1$ //$NON-NLS-2$
		frmTotalbackup.setBounds(100, 100, 894, 569);
		frmTotalbackup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmTotalbackup.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu(ResourceBundle.getBundle("gui.messages").getString("Mainframe.mnFile.text")); //$NON-NLS-1$ //$NON-NLS-2$
		menuBar.add(mnFile);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmQuit = new JMenuItem(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.mntmQuit.text")); //$NON-NLS-1$ //$NON-NLS-2$
		mntmQuit.setAction(action_quit);
		mnFile.add(mntmQuit);

		JMenu mnHelp = new JMenu(ResourceBundle.getBundle("gui.messages").getString("Mainframe.mnHelp.text")); //$NON-NLS-1$ //$NON-NLS-2$
		menuBar.add(mnHelp);

		JMenuItem mntmAbout_1 = new JMenuItem(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.mntmAbout_1.text")); //$NON-NLS-1$ //$NON-NLS-2$
		mntmAbout_1.setAction(action_about);
		mnHelp.add(mntmAbout_1);

		JPanel panel = new JPanel();
		frmTotalbackup.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblQuelle = new JLabel(ResourceBundle.getBundle("gui.messages").getString("Mainframe.lblTask.text")); //$NON-NLS-1$ //$NON-NLS-2$
		panel_2.add(lblQuelle);

		listModel = new DefaultListModel<BackupTask>();

		list_backupTasks = new JList(listModel);
		list_backupTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list_backupTasks.setSelectedIndex(0);
		list_backupTasks.setVisibleRowCount(6);
		JScrollPane listScroller = new JScrollPane(list_backupTasks);
		listScroller.setMaximumSize(new Dimension(200, 200));
		listScroller.setMinimumSize(new Dimension(200, 200));

		panel_2.add(listScroller);

		// Button Hinzufügen:
		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		JButton btnHinzufuegen = new JButton(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.btnHinzufuegen.text")); //$NON-NLS-1$ //$NON-NLS-2$
		panel_3.add(btnHinzufuegen);
		btnHinzufuegen.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnHinzufuegen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					editDialog = new Edit(controller);
					editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					editDialog.setVisible(true);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		// Button Bearbeiten:
		JButton btnBearbeiten = new JButton(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.btnBearbeiten.text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnBearbeiten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Prüfen ob ein Listenelement selektiert ist:
				if (!list_backupTasks.isSelectionEmpty()) {
					try {
						// Neuen Edit-Dialog erzeugen:
						editDialog = new Edit(controller);
						editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						editDialog.setVisible(true);

						// Gespeicherte werte in den Edit-Dialog eintragen:
						BackupTask task = list_backupTasks.getSelectedValue();
						editDialog.setBackupTaskName(task.getTaskName());
						editDialog.setSourcePaths(task.getSourcePaths());
						editDialog.setDestinationPath(task.getDestinationPath());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		btnBearbeiten.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_3.add(btnBearbeiten);

		// Button Löschen:
		JButton btnLoeschen = new JButton(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.btnLoeschen.text")); //$NON-NLS-1$ //$NON-NLS-2$
		panel_3.add(btnLoeschen);
		btnLoeschen.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnLoeschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!list_backupTasks.isSelectionEmpty()) {
					listModel.remove(list_backupTasks.getSelectedIndex());
					controller.setNumberOfBackupTasks(controller.getNumberOfSources() - 1);
				}
			}
		});

		JButton btnBackupStarten = new JButton(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.btnBackupStarten.text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnBackupStarten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.startAllBackups();
			}
		});
		frmTotalbackup.getContentPane().add(btnBackupStarten, BorderLayout.SOUTH);

		ta_output = new JTextArea();

		JScrollPane scrollPane = new JScrollPane(ta_output);
		frmTotalbackup.getContentPane().add(scrollPane, BorderLayout.CENTER);
	}

	public void init(Controller c) {
		this.controller = c;
	}

	private void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	private class SA_About extends AbstractAction {
		public SA_About() {
			putValue(NAME, ResourceBundle.getBundle("gui.messages").getString("About.this.title"));
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				About dialog = new About();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private class SA_Quit extends AbstractAction {
		public SA_Quit() {
			putValue(NAME, ResourceBundle.getBundle("gui.messages").getString("Mainframe.mntmQuit.text"));
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	/**
	 * @deprecated
	 */
	/*
	 * private class SA_opendialog_source extends AbstractAction { public
	 * SA_opendialog_source() { putValue(NAME,
	 * ResourceBundle.getBundle("gui.messages"
	 * ).getString("Mainframe.btnHinzufuegen.text"));
	 * putValue(SHORT_DESCRIPTION, "Some short description"); }
	 * 
	 * public void actionPerformed(ActionEvent e) { JFileChooser fc = new
	 * JFileChooser(); fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	 * int state = fc.showOpenDialog(null);
	 * 
	 * if (state == JFileChooser.APPROVE_OPTION) { sourceFile =
	 * fc.getSelectedFile(); listModel.addElement(sourceFile.getAbsolutePath());
	 * controller.setNumberOfSources(controller.getNumberOfSources() + 1); } } }
	 */


	/**
	 * Gibt einen String auf der GUI (im Textfeld) aus.
	 * 
	 * @param output
	 *            String welcher auf der GUI angezeigt werden soll.
	 */
	public void addToOutput(String output) {
		if (output == null) {
			throw new NullPointerException();
		}
		ta_output.append(output + "\n");
	}

	/**
	 * Prüft einen Pfad auf Gültigkeit.
	 * 
	 * @param s
	 *            Zu prüfender Pfad (als String)
	 * @return Gültigkeit des Pfades
	 */
	private boolean checkPathValidity(String s) {
		File f = new File(s);
		if (f.exists()) {
			return true;
		}
		return false;
	}

	/*
	 * private class SA_delete extends AbstractAction { public SA_delete() {
	 * putValue( NAME, ResourceBundle.getBundle("gui.messages").getString(
	 * "Mainframe.btnLoeschen.text")); putValue(SHORT_DESCRIPTION,
	 * "Some short description"); }
	 * 
	 * public void actionPerformed(ActionEvent e) { } }
	 */

	public ArrayList<String> getSourcePaths() {
		ArrayList<String> sources = new ArrayList<String>();
		for (int i = 0; i < controller.getNumberOfSources(); i++) {
			sources.add(list_backupTasks.getModel().getElementAt(i).toString());
		}
		return sources;
	}

	/*
	 * public void setSourcePaths(ArrayList<String> sourcePaths) { for (int i =
	 * 0; i < sourcePaths.size(); i++) {
	 * listModel.addElement(sourcePaths.get(i)); } }
	 */

	public void addBackupTaskToList(BackupTask task) {
		listModel.addElement(task);
	}

	public void removeBackupTaskFromList(BackupTask task) {
		listModel.removeElement(task);
	}

	public String getDestPath() {
		if (editDialog != null) {
			return editDialog.getDestinationPath();
		}
		return null;
		// TODO: schöner!?
	}

	public void setDestPath(String path) {
		if (editDialog != null) {
			editDialog.setDestinationPath(path);
		}
	}
}
