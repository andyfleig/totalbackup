package gui;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.*;
import java.awt.Dimension;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JFileChooser;

import gui.About;
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

public class Mainframe {
	
	private Mainframe window;

	// private mit setter?
	public JFrame frmTotalbackup;
	private final Action action_about = new SA_About();
	private final Action action_quit = new SA_Quit();
	private JTextField tf_destinationPath;
	private JTextArea ta_output;
	private JList l_source;
	private final Action action = new SA_opendialog_source();
	
	private DefaultListModel listModel;

	private Controller controller;

	File sourceFile;
	File destinationFile;
	private final Action action_1 = new SA_opendialog_destination();
	private final Action action_2 = new SA_runBackup();
	private final Action action_3 = new SwingAction();

	/**
	 * Launch the application.
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

		JMenuItem mntmQuit = new JMenuItem(ResourceBundle.getBundle("gui.messages").getString("Mainframe.mntmQuit.text")); //$NON-NLS-1$ //$NON-NLS-2$
		mntmQuit.setAction(action_quit);
		mnFile.add(mntmQuit);

		JMenu mnHelp = new JMenu(ResourceBundle.getBundle("gui.messages").getString("Mainframe.mnHelp.text")); //$NON-NLS-1$ //$NON-NLS-2$
		menuBar.add(mnHelp);

		JMenuItem mntmAbout_1 = new JMenuItem(ResourceBundle.getBundle("gui.messages").getString("Mainframe.mntmAbout_1.text")); //$NON-NLS-1$ //$NON-NLS-2$
		mntmAbout_1.setAction(action_about);
		mnHelp.add(mntmAbout_1);

		JPanel panel = new JPanel();
		frmTotalbackup.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblZiel = new JLabel(ResourceBundle.getBundle("gui.messages").getString("Mainframe.lblZiel.text")); //$NON-NLS-1$ //$NON-NLS-2$
		panel_1.add(lblZiel);

		tf_destinationPath = new JTextField();
		panel_1.add(tf_destinationPath);
		tf_destinationPath.setColumns(20);

		JButton btnDurchsuchen_1 = new JButton(ResourceBundle.getBundle("gui.messages").getString("Mainframe.btnDurchsuchen_1.text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnDurchsuchen_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDurchsuchen_1.setAction(action_1);
		panel_1.add(btnDurchsuchen_1);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblQuelle = new JLabel(ResourceBundle.getBundle("gui.messages").getString("Mainframe.lblQuelle.text")); //$NON-NLS-1$ //$NON-NLS-2$
		panel_2.add(lblQuelle);
		
		listModel = new DefaultListModel<String>();
		
		l_source = new JList(listModel);
		l_source.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		l_source.setSelectedIndex(0);
		l_source.setVisibleRowCount(6);
		JScrollPane listScroller = new JScrollPane(l_source);
		listScroller.setMaximumSize(new Dimension(200, 200));
		listScroller.setMinimumSize (new Dimension (200, 200));
		
		
		panel_2.add(listScroller);
		
		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3);
				panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		
				JButton btnHinzufuegen = new JButton(ResourceBundle.getBundle("gui.messages").getString("Mainframe.btnHinzufuegen.text")); //$NON-NLS-1$ //$NON-NLS-2$
				panel_3.add(btnHinzufuegen);
				btnHinzufuegen.setAlignmentX(Component.CENTER_ALIGNMENT);
				btnHinzufuegen.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
					}
				});
		
		btnHinzufuegen.setAction(action);
		
		JButton btnLoeschen = new JButton(ResourceBundle.getBundle("gui.messages").getString("Mainframe.btnLoeschen.text")); //$NON-NLS-1$ //$NON-NLS-2$
		panel_3.add(btnLoeschen);
		btnLoeschen.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnLoeschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!l_source.isSelectionEmpty()) {
					listModel.remove(l_source.getSelectedIndex());
				}
			}
		});
		btnLoeschen.setAction(action_3);

		JButton btnBackupStarten = new JButton(ResourceBundle.getBundle("gui.messages").getString("Mainframe.btnBackupStarten.text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnBackupStarten.setAction(action_2);
		btnBackupStarten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
			putValue(NAME, "Über");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			About.main(null);
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

	private class SA_opendialog_source extends AbstractAction {
		public SA_opendialog_source() {
			putValue(NAME, ResourceBundle.getBundle("gui.messages").getString("Mainframe.btnHinzufuegen.text"));
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int state = fc.showOpenDialog(null);

			if (state == JFileChooser.APPROVE_OPTION) {
				sourceFile = fc.getSelectedFile();
				listModel.addElement(sourceFile.getAbsolutePath() + "\n");
			}
		}
	}

	private class SA_opendialog_destination extends AbstractAction {

		public SA_opendialog_destination() {
			putValue(NAME, ResourceBundle.getBundle("gui.messages").getString("Mainframe.btnDurchsuchen_1.text"));
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int state = fc.showOpenDialog(null);

			if (state == JFileChooser.APPROVE_OPTION) {
				destinationFile = fc.getSelectedFile();
				tf_destinationPath.setText(destinationFile.getAbsolutePath());
			}
		}
	}

	private class SA_runBackup extends AbstractAction {
		public SA_runBackup() {
			putValue(NAME, ResourceBundle.getBundle("gui.messages").getString("Mainframe.btnBackupStarten.text"));
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {

			if (listModel.isEmpty()) {
				ta_output.append("Fehler: Kein Quellverzeichnis angegeben\n");
			} else if (tf_destinationPath.getText().isEmpty()) {
				ta_output.append("Fehler: Kein Zielverzeichnis angegeben\n");
			} else {
				if (!checkPathValidity(tf_destinationPath.getText())) {
					ta_output.append("Ungültiger Zielpfad\n");
				} else {
					for (int i = 0; i < listModel.size(); i++) {
						if (!checkPathValidity(listModel.getElementAt(i).toString().trim())) {
							ta_output.append("Ungültiger Quellpfad\n");
						} else {
							ta_output.append("Backup wird erstellt...\n");
							controller.startBackup(listModel.getElementAt(i).toString().trim(), tf_destinationPath.getText());
							ta_output.append("Backup erfolgreich erstellt");
						}
					}
				}
			}
		}
	}

	/**
	 * Gibt einen String auf der GUI (im Textfeld) aus.
	 * @param output String welcher auf der GUI angezeigt werden soll.
	 */
	public void addToOutput(String output) {
		if (output == null) {
			throw new NullPointerException();
		}
		ta_output.append(output + "\n");
	}
	
	/**
	 * Prüft einen Pfad auf Gültigkeit.
	 * @param s Zu prüfender Pfad (als String)
	 * @return Gültigkeit des Pfades
	 */
	private boolean checkPathValidity(String s) {
		File f = new File(s);
		if (f.exists()) {
			return true;
		}
		return false;
	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, ResourceBundle.getBundle("gui.messages").getString("Mainframe.btnLoeschen.text"));
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
}
