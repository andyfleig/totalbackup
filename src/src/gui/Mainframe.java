package gui;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

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

public class Mainframe {

	private JFrame frmTotalbackup;
	private final Action action_about = new SA_About();
	private final Action action_quit = new SA_Quit();
	private JTextField tf_sourcePath;
	private JTextField tf_destinationPath;
	private JTextArea ta_output;
	private final Action action = new SA_opendialog_source();

	private Controller controller;
	private Mainframe window;

	File sourceFile;
	File destinationFile;
	private final Action action_1 = new SA_opendialog_destination();
	private final Action action_2 = new SA_runBackup();

	/**
	 * Launch the application.
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
	private void initialize() {
		frmTotalbackup = new JFrame();
		frmTotalbackup.setTitle("TotalBackup");
		frmTotalbackup.setBounds(100, 100, 894, 569);
		frmTotalbackup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmTotalbackup.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("Datei");
		menuBar.add(mnFile);

		JMenuItem mntmQuit = new JMenuItem("Beenden");
		mntmQuit.setAction(action_quit);
		mnFile.add(mntmQuit);

		JMenu mnHelp = new JMenu("Hilfe");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout_1 = new JMenuItem("Über");
		mntmAbout_1.setAction(action_about);
		mnHelp.add(mntmAbout_1);

		JPanel panel = new JPanel();
		frmTotalbackup.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblZiel = new JLabel("Ziel:");
		panel_1.add(lblZiel);

		tf_destinationPath = new JTextField();
		panel_1.add(tf_destinationPath);
		tf_destinationPath.setColumns(20);

		JButton btnDurchsuchen_1 = new JButton("Durchsuchen...");
		btnDurchsuchen_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDurchsuchen_1.setAction(action_1);
		panel_1.add(btnDurchsuchen_1);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblQuelle = new JLabel("Quelle:");
		panel_2.add(lblQuelle);

		tf_sourcePath = new JTextField();
		panel_2.add(tf_sourcePath);
		tf_sourcePath.setColumns(20);

		JButton btnDurchsuchen = new JButton("Durchsuchen...");
		btnDurchsuchen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnDurchsuchen.setAction(action);
		panel_2.add(btnDurchsuchen);

		JButton btnBackupStarten = new JButton("Backup starten");
		btnBackupStarten.setAction(action_2);
		btnBackupStarten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		frmTotalbackup.getContentPane().add(btnBackupStarten, BorderLayout.SOUTH);

		ta_output = new JTextArea();
		frmTotalbackup.getContentPane().add(ta_output, BorderLayout.WEST);

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
			putValue(NAME, "Beenden");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	private class SA_opendialog_source extends AbstractAction {
		public SA_opendialog_source() {
			putValue(NAME, "Durchsuchen...");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int state = fc.showOpenDialog(null);

			if (state == JFileChooser.APPROVE_OPTION) {
				sourceFile = fc.getSelectedFile();
				tf_sourcePath.setText(sourceFile.getAbsolutePath());
			}
		}
	}

	private class SA_opendialog_destination extends AbstractAction {

		public SA_opendialog_destination() {
			putValue(NAME, "Durchsuchen...");
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
			putValue(NAME, "Backup starten");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {

			if (tf_sourcePath.getText().isEmpty()) {
				ta_output.append("Fehler: Kein Quellverzeichnis angegeben\n");
			} else if (tf_destinationPath.getText().isEmpty()) {
				ta_output.append("Fehler: Kein Zielverzeichnis angegeben\n");
			} else {
				//TODO: Pfad auf gültigkeit prüfen
				
				if (!checkPathValidity(tf_sourcePath.getText())) {
					ta_output.append("Ungültiger Quellpfad\n");
				} else if (!checkPathValidity(tf_destinationPath.getText())) {
					ta_output.append("Ungültiger Zielpfad\n");
				} else {
					ta_output.append("Backup wird erstellt...\n");
					controller.startBackup(tf_sourcePath.getText(), tf_destinationPath.getText());
					ta_output.append("Backup erfolgreich erstellt");
				}
			}
		}
	}

	/**
	 * Gibt einen String auf der GUI (im Textfeld) aus.
	 * ACHTUNG: Innerhalb von Mainframe.java direkt per ta_output.append() zugreifen!
	 * @param output String welcher auf der GUI angezeigt werden soll.
	 */
	public void addToOutput(String output) {
		if (output == null) {
			throw new NullPointerException();
		}
		window.ta_output.append(output + "\n");
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
}
