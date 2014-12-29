package gui;

import main.Controller;
import main.BackupTask;
import main.Source;
import gui.About;
import gui.Edit;
import gui.IEditListener;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.lang.NullPointerException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultCaret;

public class Mainframe extends JDialog {

	// Für main benötigt:
	// private Mainframe window;

	public JFrame frmTotalbackup;
	private final Action action_about = new SA_About();
	private final Action action_quit = new SA_Quit();
	private JTextPane tp_Output;
	private JList<BackupTask> list_Tasks;
	private BackupTask selectedTask;

	private JButton btn_StartAll;
	private JButton btn_Add;
	private JButton btn_Edit;
	private JButton btn_Delete;
	private JButton btnCancel;
	private JButton btnStartSelected;
	private JTextField tF_status;
	private JCheckBox cb_advancedOutput;

	private IEditListener editListener;

	private Edit editDialog;

	private DefaultListModel<BackupTask> listModel;

	private IMainframeListener listener;

	private StyledDocument tpOutput_doc;

	private Thread backupThread;

	File sourceFile;
	File destinationFile;

	SimpleAttributeSet blackAS;
	SimpleAttributeSet redAS;

	/**
	 * Launch the application.
	 * 
	 * @deprecated
	 */
	// public void main(String[] args) {
	//
	// Mainframe window = new Mainframe(listener);
	// window.frmTotalbackup.setVisible(true);
	//
	// EventQueue.invokeLater(new Runnable() { public void run() { try {
	// window = new Mainframe(listener);
	// window.frmTotalbackup.setVisible(true); } catch (Exception e) {
	// e.printStackTrace(); } } });
	//
	// }

	/**
	 * Create the application.
	 */
	public Mainframe(IMainframeListener listener) {
		this.listener = listener;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {

		// AttributeSets erstellen:
		blackAS = new SimpleAttributeSet();
		redAS = new SimpleAttributeSet();
		StyleConstants.setForeground(blackAS, Color.BLACK);
		StyleConstants.setForeground(redAS, Color.RED);

		// Edit-Listener anlegen:
		editListener = new IEditListener() {

			@Override
			public BackupTask getBackupTaskWithName(String name) {
				return listener.getBackupTaskWithName(name);
			}

			@Override
			public void removeBackupTask(BackupTask task) {
				listener.removeBackupTask(task);

			}

			@Override
			public ArrayList<String> getBackupTaskNames() {
				return listener.getBackupTaskNames();
			}

			@Override
			public void addBackupTask(BackupTask task) {
				listener.addBackupTask(task);
			}

			@Override
			public void saveProperties() {
				Mainframe.this.saveProperties();
			}

		};

		frmTotalbackup = new JFrame();
		frmTotalbackup.setTitle(ResourceBundle.getBundle("gui.messages").getString("Mainframe.frmTotalbackup.title")); //$NON-NLS-1$ //$NON-NLS-2$
		frmTotalbackup.setBounds(100, 100, 894, 569);
		frmTotalbackup.setMinimumSize(new Dimension(500, 400));
		frmTotalbackup.setPreferredSize(new Dimension(800, 500));
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

		tp_Output = new JTextPane() {
			public boolean getScrollableTracksViewportWidth() {
				return getUI().getPreferredSize(this).width <= getParent().getSize().width;
			}
		};
		tp_Output.setEditable(false);

		DefaultCaret caret = (DefaultCaret) tp_Output.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		// TODO: Kein Vertikales Autoscrollen

		tpOutput_doc = tp_Output.getStyledDocument();

		JScrollPane scrollPane = new JScrollPane(tp_Output);

		JPanel panel_4 = new JPanel();

		frmTotalbackup.getContentPane().add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new BorderLayout());
		panel_4.add(scrollPane, BorderLayout.CENTER);

		// Checkbox erweiterte Ausgabe:
		cb_advancedOutput = new JCheckBox(ResourceBundle.getBundle("gui.messages").getString(
				"Mainframe.cb_advancedOutput.text"));
		panel_4.add(cb_advancedOutput, BorderLayout.SOUTH);

		JPanel panel_5 = new JPanel();
		panel_5.setLayout(new BorderLayout());
		panel_4.add(panel_5, BorderLayout.NORTH);

		tF_status = new JTextField();
		tF_status.setEditable(false);
		tF_status.setPreferredSize(new Dimension(0, 25));
		panel_5.add(tF_status, BorderLayout.CENTER);

		JLabel lbl_status = new JLabel();
		lbl_status.setPreferredSize(new Dimension(0, 25));
		lbl_status.setText(" " + ResourceBundle.getBundle("gui.messages").getString("Mainframe.lbl_status.text"));
		panel_5.add(lbl_status, BorderLayout.NORTH);

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

		panel_2.add(listScroller, BorderLayout.WEST);

		// Button Hinzufügen:
		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3, BorderLayout.EAST);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		panel_3.setPreferredSize(new Dimension(140, 76));
		btn_Add = new JButton(ResourceBundle.getBundle("gui.messages").getString("Mainframe.btnHinzufuegen.text")); //$NON-NLS-1$ //$NON-NLS-2$
		panel_3.add(btn_Add);
		btn_Add.setAlignmentX(Component.CENTER_ALIGNMENT);
		btn_Add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					editDialog = new Edit(editListener);
					editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					editDialog.setLocation(frmTotalbackup.getLocationOnScreen());
					editDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
					editDialog.setVisible(true);
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		// Button Bearbeiten:
		btn_Edit = new JButton(ResourceBundle.getBundle("gui.messages").getString("Mainframe.btnBearbeiten.text")); //$NON-NLS-1$ //$NON-NLS-2$
		btn_Edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Prüfen ob ein Listenelement selektiert ist:
				if (!list_Tasks.isSelectionEmpty()) {
					try {
						// Neuen Edit-Dialog erzeugen:
						editDialog = new Edit(editListener);
						editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						editDialog.setEditMode(true);
						// Gespeicherte Werte in den Edit-Dialog eintragen:
						BackupTask task = list_Tasks.getSelectedValue();
						editDialog.setBackupTaskName(task.getTaskName());
						editDialog.setEditable(false);
						editDialog.setSourcePaths(task.getSources());
						editDialog.setDestinationPath(task.getDestinationPath());
						editDialog.setBackupMode(task.getBackupMode());
						editDialog.setAutoCleanEnabled(task.autoCleanIsEnabled());
						editDialog.setNumberOfBackupsToKeep(task.getNumberOfBackupsToKeep());
						editDialog.setLocation(frmTotalbackup.getLocationOnScreen());
						editDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
						editDialog.setVisible(true);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		btn_Edit.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_3.add(btn_Edit);

		// Button Löschen:
		btn_Delete = new JButton(ResourceBundle.getBundle("gui.messages").getString("Mainframe.btnLoeschen.text")); //$NON-NLS-1$ //$NON-NLS-2$
		panel_3.add(btn_Delete);
		btn_Delete.setAlignmentX(Component.CENTER_ALIGNMENT);

		btn_Delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!list_Tasks.isSelectionEmpty()) {
					listener.removeBackupTask(listModel.getElementAt(list_Tasks.getSelectedIndex()));
				}
				saveProperties();
			}
		});

		list_Tasks = new JList<BackupTask>(listModel);
		panel_2.add(list_Tasks, BorderLayout.CENTER);
		list_Tasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list_Tasks.setSelectedIndex(0);
		list_Tasks.setVisibleRowCount(6);

		JPanel panel_1 = new JPanel();
		frmTotalbackup.getContentPane().add(panel_1, BorderLayout.SOUTH);

		// Button Ausgewähltes Backup starten:
		btnStartSelected = new JButton(ResourceBundle
				.getBundle("gui.messages").getString("Mainframe.btnStartSelected.text")); //$NON-NLS-1$ //$NON-NLS-2$

		btnStartSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				backupThread = new Thread(new Runnable() {
					@Override
					public void run() {
						prepareBackup(listModel.getElementAt(list_Tasks.getSelectedIndex()));
					}
				});
				backupThread.start();
			}
		});
		panel_1.add(btnStartSelected);

		// Button Backup Abbrechen:
		btnCancel = new JButton(ResourceBundle.getBundle("gui.messages").getString("Mainframe.btnCancel.text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnCancel.setEnabled(false);

		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int reply = JOptionPane.showConfirmDialog(null,
						ResourceBundle.getBundle("gui.messages").getString("Messages.CancelBackup"), ResourceBundle
								.getBundle("gui.messages").getString("Messages.Cancel"), JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					Mainframe.this.addToOutput(
							ResourceBundle.getBundle("gui.messages").getString("Messages.CancelingBackup"), false);
					btnCancel.setEnabled(false);
					if (backupThread != null) {
						backupThread.interrupt();
					}
				}
			}
		});

		// Button Alle-Backups-Starten:
		btn_StartAll = new JButton(ResourceBundle.getBundle("gui.messages")
				.getString("Mainframe.btnBackupStarten.text"));
		panel_1.add(btn_StartAll);
		// btn_StartAll.setEnabled(false);

		btn_StartAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backupThread = new Thread(new Runnable() {
					@Override
					public void run() {
						ArrayList<BackupTask> backupTasks = listener.getBackupTasks();
						for (int i = 0; i < backupTasks.size(); i++) {
							prepareBackup(backupTasks.get(i));
						}
					}
				});
				backupThread.start();

			}
		});
		panel_1.add(btnCancel);
	}

	//TODO: JavaDoc
	private void prepareBackup(BackupTask task) {
		selectedTask = task;

		// Testen ob Quell- und Zielpfad(e) existieren:
		ArrayList<Source> sources = selectedTask.getSources();
		for (int i = 0; i < sources.size(); i++) {
			if (!(new File(sources.get(i).getPath())).exists()) {
				String output = ResourceBundle.getBundle("gui.messages").getString("Mainframe.ErrorSourceDontExists");
				listener.printOut(output, false);
				listener.log(output, selectedTask);
				return;
			}
		}
		if (!(new File(selectedTask.getDestinationPath())).exists()) {
			String output = ResourceBundle.getBundle("gui.messages").getString("Mainframe.ErrorDestDontExists");
			listener.printOut(output, false);
			return;
		}
		
		Preparing prep = new Preparing(new IPreparingListener() {
			
			@Override
			public void cancelBackup() {
				int reply = JOptionPane.showConfirmDialog(null,
						ResourceBundle.getBundle("gui.messages").getString("Messages.CancelBackup"), ResourceBundle
								.getBundle("gui.messages").getString("Messages.Cancel"), JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					Mainframe.this.addToOutput(
							ResourceBundle.getBundle("gui.messages").getString("Messages.CancelingBackup"), false);
					btnCancel.setEnabled(false);
					if (backupThread != null) {
						backupThread.interrupt();
					}
				}
				
			}
		});
		
		prep.setLocation(frmTotalbackup.getLocationOnScreen());
		prep.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		prep.setVisible(true);
		
		listener.startPreparation(selectedTask);
		prep.dispose();
		Summary summary = new Summary(new ISummaryListener() {

			@Override
			public void startBackup() {
				startBackupTask();
			}

			@Override
			public String getTaskName() {
				// TODO: schön?
				return selectedTask.getTaskName();
			}

			@Override
			public long getNumberOfDirectories() {
				return listener.getNumberOfDirectories();
			}

			@Override
			public long getNumberOfFiles() {
				return listener.getNumberOfFiles();
			}

			@Override
			public double getSizeToCopy() {
				return listener.getSizeToCopy();
			}

			@Override
			public double getSizeToLink() {
				return listener.getSizeToLink();
			}

			@Override
			public void clearBackupInfos() {
				listener.clearBackupInfos();
			}

			@Override
			public void deleteEmptyBackupFolders() {
				listener.deleteEmptyBackupFolders(selectedTask.getDestinationPath());
			}

			@Override
			public void outprintBackupCanceled() {
				listener.outprintBackupCanceled();
			}

		});
		summary.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		summary.setLocation(frmTotalbackup.getLocationOnScreen());
		summary.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		summary.setVisible(true);
	}

	/**
	 * Initialisiert das Mainframe.
	 * 
	 * @deprecated
	 * 
	 * @param c
	 *            Controller
	 */
	public void init(Controller c) {
		// this.controller = c;
	}

	private void startBackupTask() {
		listener.startBackupTask(selectedTask);
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
				dialog.setLocation(frmTotalbackup.getLocationOnScreen());
				dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
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
			saveProperties();
			System.exit(0);
		}
	}

	private void saveProperties() {
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

			o.writeObject(listener.getBackupTasks());
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
	}

	/**
	 * Gibt einen String auf der GUI (im Textfeld) aus.
	 * 
	 * @param output
	 *            String welcher auf der GUI angezeigt werden soll.
	 * @param error
	 *            true = Fehlermeldung (schrift rot); false = Normale Ausgabe
	 *            (schrift schwarz)
	 */
	public void addToOutput(String output, boolean error) {
		if (output == null) {
			throw new NullPointerException();
		}

		if (!error) {
			try {
				if (tpOutput_doc.getLength() < 1) {
					tpOutput_doc.insertString(tpOutput_doc.getLength(), output, blackAS);
				} else {
					tpOutput_doc.insertString(tpOutput_doc.getLength(), "\n" + output, blackAS);
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		} else {
			try {
				if (tpOutput_doc.getLength() < 1) {
					tpOutput_doc.insertString(tpOutput_doc.getLength(), output, redAS);
				} else {
					tpOutput_doc.insertString(tpOutput_doc.getLength(), "\n" + output, redAS);
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	/**
	 * Gibt den gegebenen String auf dem Status-Textfeld auf der GUI aus.
	 * 
	 * @param status
	 *            auszugebender String
	 */
	public void setStatus(String status) {
		tF_status.setText(status);
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

	/**
	 * Sperrt bzw. entsperrt die Buttons der GUI.
	 * 
	 * @param noBackupRunning
	 *            false = "sperrt" die Buttons der GUI (während laufendem
	 *            Backup), true = entsperrt
	 */
	public void setButtonsToBackupRunning(boolean noBackupRunning) {
		btnCancel.setEnabled(!noBackupRunning);
		btnStartSelected.setEnabled(noBackupRunning);
		btn_StartAll.setEnabled(noBackupRunning);
		btn_Add.setEnabled(noBackupRunning);
		btn_Edit.setEnabled(noBackupRunning);
		btn_Delete.setEnabled(noBackupRunning);
	}

	/**
	 * Gibt zurück ob die erweiterte Ausgabe aktiviert ist.
	 * 
	 * @return Status der erweiterten Ausgabe
	 */
	public boolean advancedOutputIsEnabled() {
		return cb_advancedOutput.isSelected();
	}
}
