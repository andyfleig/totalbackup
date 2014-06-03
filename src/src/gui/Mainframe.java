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
import main.Backup;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import java.awt.event.ActionListener;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Mainframe {

	private JFrame frmTotalbackup;
	private final Action action_about = new SA_About();
	private final Action action_quit = new SA_Quit();
	private JTextField tf_sourcePath;
	private JTextField tf_destinationPath;
	private final Action action = new SA_opendialog_source();
	
	File sourceFile;
	File destinationFile;
	private final Action action_1 = new SA_opendialog_destination();
	private final Action action_2 = new SA_runBackup();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Mainframe window = new Mainframe();
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
	public Mainframe() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTotalbackup = new JFrame();
		frmTotalbackup.setTitle("TotalBackup");
		frmTotalbackup.setBounds(100, 100, 556, 451);
		frmTotalbackup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmTotalbackup.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.setAction(action_quit);
		mnFile.add(mntmQuit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout_1 = new JMenuItem("About");
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
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
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
			putValue(NAME, "About");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			About.main(null);
		}
	}
	private class SA_Quit extends AbstractAction {
		public SA_Quit() {
			putValue(NAME, "Quit");
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
			int state = fc.showOpenDialog( null );
			
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
			int state = fc.showOpenDialog( null );
			
			if (state == JFileChooser.APPROVE_OPTION) {
				sourceFile = fc.getSelectedFile();
				tf_destinationPath.setText(sourceFile.getAbsolutePath());
			}
		}
	}
	private class SA_runBackup extends AbstractAction {
		public SA_runBackup() {
			putValue(NAME, "Backup starten");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			Backup backup = new Backup(tf_sourcePath.getText(), tf_destinationPath.getText());
			try {
				backup.runBackup();
			} catch (FileNotFoundException ex) {
				System.out.println("Datei existiert nicht!");
			} catch (IOException ex) {
				System.out.println("IO-Fehler!");
			}
			
		}
	}
}
