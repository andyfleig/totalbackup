package gui;

import main.Controller;
import main.BackupTask;
import gui.About;
import gui.Edit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.lang.NullPointerException;
import java.util.ResourceBundle;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.BoxLayout;
import javax.swing.JSplitPane;

public class Mainframe {

	private Mainframe window;

	// private mit setter?
	public JFrame frmTotalbackup;
	private final Action action_about = new SA_About();
	private final Action action_quit = new SA_Quit();
	private JTextArea ta_Output;
	private JList<BackupTask> list_Tasks;

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
		frmTotalbackup.setPreferredSize(new Dimension(500, 400));
		frmTotalbackup.setMinimumSize(frmTotalbackup.getPreferredSize());
		frmTotalbackup.pack();
		frmTotalbackup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmTotalbackup.setJMenuBar(menuBar);

		JMenu mn_File = new JMenu(ResourceBundle.getBundle("gui.messages").getString("Mainframe.mnFile.text")); //$NON-NLS-1$ //$NON-NLS-2$
		menuBar.add(mn_File);

		JMenuItem mntm_Quit = new JMenuItem(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.mntmQuit.text")); //$NON-NLS-1$ //$NON-NLS-2$
		mntm_Quit.setAction(action_quit);
		mn_File.add(mntm_Quit);

		JMenu mn_Help = new JMenu(ResourceBundle.getBundle("gui.messages").getString("Mainframe.mnHelp.text")); //$NON-NLS-1$ //$NON-NLS-2$
		menuBar.add(mn_Help);

		JMenuItem mntm_About = new JMenuItem(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.mntmAbout_1.text")); //$NON-NLS-1$ //$NON-NLS-2$
		mntm_About.setAction(action_about);
		mn_Help.add(mntm_About);

		listModel = new DefaultListModel<BackupTask>();

		JButton btn_StartAll = new JButton(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.btnBackupStarten.text")); //$NON-NLS-1$ //$NON-NLS-2$
		btn_StartAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.startAllBackups();
			}
		});

		ta_Output = new JTextArea();

		JScrollPane scrollPane = new JScrollPane(ta_Output);
		frmTotalbackup.getContentPane().add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		frmTotalbackup.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new BorderLayout(0, 0));

		JLabel lbl_Tasks = new JLabel(ResourceBundle.getBundle("gui.messages").getString("Mainframe.lblTask.text")); //$NON-NLS-1$ //$NON-NLS-2$
		lbl_Tasks.setPreferredSize(new Dimension(0, 25));
		panel_2.add(lbl_Tasks, BorderLayout.NORTH);
		JScrollPane listScroller = new JScrollPane();

		// TODO
		// listScroller.setMaximumSize(new Dimension(2000, 2000));
		// listScroller.setMinimumSize(new Dimension(2000, 2000));

		panel_2.add(listScroller, BorderLayout.WEST);

		// Button Hinzufügen:
		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3, BorderLayout.EAST);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		panel_3.setPreferredSize(new Dimension(140, 76));
		JButton btn_Add = new JButton(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.btnHinzufuegen.text")); //$NON-NLS-1$ //$NON-NLS-2$
		panel_3.add(btn_Add);
		btn_Add.setAlignmentX(Component.CENTER_ALIGNMENT);
		btn_Add.addActionListener(new ActionListener() {
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
		JButton btn_Edit = new JButton(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.btnBearbeiten.text")); //$NON-NLS-1$ //$NON-NLS-2$
		btn_Edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Prüfen ob ein Listenelement selektiert ist:
				if (!list_Tasks.isSelectionEmpty()) {
					try {
						// Neuen Edit-Dialog erzeugen:
						editDialog = new Edit(controller);
						editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						editDialog.setVisible(true);

						// Gespeicherte werte in den Edit-Dialog eintragen:
						BackupTask task = list_Tasks.getSelectedValue();
						editDialog.setBackupTaskName(task.getTaskName());
						editDialog.setSourcePaths(task.getSourcePaths());
						editDialog.setDestinationPath(task.getDestinationPath());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		btn_Edit.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_3.add(btn_Edit);

		// Button Löschen:
		JButton btn_Delete = new JButton(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.btnLoeschen.text")); //$NON-NLS-1$ //$NON-NLS-2$
		panel_3.add(btn_Delete);
		btn_Delete.setAlignmentX(Component.CENTER_ALIGNMENT);

		list_Tasks = new JList<BackupTask>(listModel);
		panel_2.add(list_Tasks, BorderLayout.CENTER);
		list_Tasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list_Tasks.setSelectedIndex(0);
		list_Tasks.setVisibleRowCount(6);

		btn_Delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!list_Tasks.isSelectionEmpty()) {
					controller.removeBackupTask(listModel.getElementAt(list_Tasks.getSelectedIndex()));
					// listModel.remove(list_Tasks.getSelectedIndex());
					controller.setNumberOfBackupTasks(controller.getNumberOfBackupTasks() - 1);
				}
			}
		});
		
		

		/*
		 * JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		 * splitPane.setTopComponent(panel);
		 * splitPane.setBottomComponent(scrollPane);
		 * splitPane.setOneTouchExpandable(true);
		 * splitPane.setDividerLocation(150);
		 */
	}

	/**
	 * Initialisiert das Mainframe.
	 * 
	 * @param c
	 *            Controller
	 */
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
			File properties = new File("./properties.ser");
			if (!properties.exists()) {
				try {
					properties.createNewFile();
				} catch (IOException ex) {
					System.err.println(ex);
				}
			}

			OutputStream fos = null;
			ObjectOutputStream o = null;

			try {
				fos = new FileOutputStream(properties);
				o = new ObjectOutputStream(fos);

				o.writeObject(controller.getBackupTasks());
			} catch (IOException ex) {
				System.out.println(ex);
			} finally {
				if (o != null)
					try {
						o.close();
					} catch (IOException ex) {
						System.err.println(ex);
					}
				if (fos != null)
					try {
						fos.close();
					} catch (IOException ex) {
						System.err.println(ex);
					}
			}
			System.exit(0);
		}
	}

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
		ta_Output.append(output + "\n");
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

	/**
	 * Fügt der Liste der Backup-Tasks einen Backup-Task hinzu.
	 * 
	 * @param task
	 *            hinzuzufügender Backup-Task
	 */
	public void addBackupTaskToList(BackupTask task) {
		listModel.addElement(task);
	}

	/**
	 * Löscht einen Backup-Task aus der Liste der Backup-Tasks.
	 * 
	 * @param task
	 *            zu löschender Backup-Task
	 */
	public void removeBackupTaskFromList(BackupTask task) {
		listModel.removeElement(task);
	}

	/**
	 * Gibt den Zielpfad zurück.
	 * 
	 * @return Zielpfad
	 */
	public String getDestPath() {
		if (editDialog != null) {
			return editDialog.getDestinationPath();
		}
		return null;
		// TODO: schöner!?
	}

	/**
	 * Legt den Zielpfad fest.
	 * 
	 * @param path
	 *            festzulegender Zielpfad
	 */
	public void setDestPath(String path) {
		if (editDialog != null) {
			editDialog.setDestinationPath(path);
		}
	}
}
