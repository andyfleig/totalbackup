package gui;

import listener.IEditDialogListener;
import listener.IMainframeListener;
import listener.IPreparingDialogListener;
import listener.ISummaryDialogListener;
import main.Controller;
import gui.AboutDialog;
import gui.EditDialog;

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
import java.awt.Toolkit;
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

import data.BackupTask;
import data.Source;

public class Mainframe extends JDialog {

	// Für main benötigt:
	// private Mainframe window;

	public JFrame frmTotalbackup;
	private final Action action_about = new SA_About();
	private final Action action_quit = new SA_Quit();
	private JTextPane textpane_output;
	private JList<BackupTask> list_tasks;
	private BackupTask selectedTask;

	private JButton button_startAll;
	private JButton button_add;
	private JButton button_edit;
	private JButton button_delete;
	private JButton button_cancel;
	private JButton button_startSelected;
	private JTextField textfield_status;
	private JCheckBox checkbox_advancedOutput;

	private IEditDialogListener editListener;

	private EditDialog editDialog;

	private DefaultListModel<BackupTask> listModel;

	private IMainframeListener listener;

	private StyledDocument tpOutput_doc;

	private Thread backupThread;

	private SummaryDialog summary;

	private PreparingDialog prep;

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
		editListener = new IEditDialogListener() {

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
		frmTotalbackup.setTitle(ResourceBundle.getBundle("gui.messages").getString("GUI.Mainframe.title"));
		frmTotalbackup.setBounds(100, 100, 894, 569);
		frmTotalbackup.setMinimumSize(new Dimension(500, 400));
		frmTotalbackup.setPreferredSize(new Dimension(800, 500));
		frmTotalbackup.pack();
		frmTotalbackup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frmTotalbackup.setIconImage(Toolkit.getDefaultToolkit().getImage("TB_logo.png"));

		JMenuBar menuBar = new JMenuBar();
		frmTotalbackup.setJMenuBar(menuBar);

		JMenu mn_File = new JMenu(ResourceBundle.getBundle("gui.messages").getString("GUI.Mainframe.menu_file"));
		menuBar.add(mn_File);

		JMenuItem mntm_Quit = new JMenuItem(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.Mainframe.menu_quit"));
		mntm_Quit.setAction(action_quit);
		mn_File.add(mntm_Quit);

		JMenu mn_Help = new JMenu(ResourceBundle.getBundle("gui.messages").getString("GUI.Mainframe.menu_help"));
		menuBar.add(mn_Help);

		JMenuItem mntm_About = new JMenuItem(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.Mainframe.menu_about"));
		mntm_About.setAction(action_about);
		mn_Help.add(mntm_About);

		listModel = new DefaultListModel<BackupTask>();

		textpane_output = new JTextPane() {
			public boolean getScrollableTracksViewportWidth() {
				return getUI().getPreferredSize(this).width <= getParent().getSize().width;
			}
		};
		textpane_output.setEditable(false);

		DefaultCaret caret = (DefaultCaret) textpane_output.getCaret();
		// TODO: Kein Vertikales Autoscrollen
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		tpOutput_doc = textpane_output.getStyledDocument();

		JScrollPane scrollPane_output = new JScrollPane(textpane_output);

		JPanel panel_statusAndLog = new JPanel();

		frmTotalbackup.getContentPane().add(panel_statusAndLog, BorderLayout.CENTER);
		panel_statusAndLog.setLayout(new BorderLayout());
		panel_statusAndLog.add(scrollPane_output, BorderLayout.CENTER);

		// Checkbox erweiterte Ausgabe:
		checkbox_advancedOutput = new JCheckBox(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.Mainframe.checkbox_advancedOutput"));
		
		JPanel panel_options = new JPanel();
		panel_statusAndLog.add(panel_options, BorderLayout.SOUTH);
		panel_options.setLayout(new BorderLayout(0, 0));
		panel_options.add(checkbox_advancedOutput, BorderLayout.WEST);	

		JPanel panel_tasks = new JPanel();
		frmTotalbackup.getContentPane().add(panel_tasks, BorderLayout.NORTH);
		panel_tasks.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_status = new JPanel();
		panel_status.setLayout(new BorderLayout());
		panel_statusAndLog.add(panel_status, BorderLayout.NORTH);
		
		textfield_status = new JTextField();
		textfield_status.setEditable(false);
		textfield_status.setPreferredSize(new Dimension(0, 25));
		panel_status.add(textfield_status, BorderLayout.CENTER);

		JLabel label_status = new JLabel();
		label_status.setPreferredSize(new Dimension(0, 25));
		label_status.setText(" " + ResourceBundle.getBundle("gui.messages").getString("GUI.Mainframe.label_status"));
		panel_status.add(label_status, BorderLayout.NORTH);

		JLabel label_tasks = new JLabel(ResourceBundle.getBundle("gui.messages").getString("GUI.Mainframe.label_tasks"));
		panel_tasks.add(label_tasks, BorderLayout.NORTH);
		label_tasks.setPreferredSize(new Dimension(0, 25));

		JScrollPane listScroller_tasks = new JScrollPane();
		panel_tasks.add(listScroller_tasks, BorderLayout.WEST);

		// Button Hinzufügen:
		JPanel panel_configureTasks = new JPanel();
		panel_tasks.add(panel_configureTasks, BorderLayout.EAST);
		panel_configureTasks.setLayout(new BoxLayout(panel_configureTasks, BoxLayout.Y_AXIS));
		panel_configureTasks.setPreferredSize(new Dimension(140, 76));
		button_add = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_add"));
		panel_configureTasks.add(button_add);
		button_add.setAlignmentX(Component.CENTER_ALIGNMENT);
		button_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					editDialog = new EditDialog(editListener);
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
		button_edit = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_edit"));
		button_edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Prüfen ob ein Listenelement selektiert ist:
				if (!list_tasks.isSelectionEmpty()) {
					try {
						// Neuen Edit-Dialog erzeugen:
						editDialog = new EditDialog(editListener);
						editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						editDialog.setEditMode(true);
						// Gespeicherte Werte in den Edit-Dialog eintragen:
						BackupTask task = list_tasks.getSelectedValue();
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
		button_edit.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_configureTasks.add(button_edit);

		// Button Löschen:
		button_delete = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_delete"));
		panel_configureTasks.add(button_delete);
		button_delete.setAlignmentX(Component.CENTER_ALIGNMENT);

		button_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int reply = JOptionPane.showConfirmDialog(null,
						ResourceBundle.getBundle("gui.messages").getString("Messages.DeleteTask"), null,
						JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					if (!list_tasks.isSelectionEmpty()) {
						listener.removeBackupTask(listModel.getElementAt(list_tasks.getSelectedIndex()));
					}
					saveProperties();
				}
			}
		});

		list_tasks = new JList<BackupTask>(listModel);
		panel_tasks.add(list_tasks, BorderLayout.CENTER);
		list_tasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list_tasks.setSelectedIndex(0);
		list_tasks.setVisibleRowCount(6);

		JButton button_clearOutput = new JButton(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.Mainframe.button_clearOutput"));
		button_clearOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textpane_output.setText("");
			}
		});
		panel_options.add(button_clearOutput, BorderLayout.EAST);

		JPanel panel_buttons = new JPanel();
		frmTotalbackup.getContentPane().add(panel_buttons, BorderLayout.SOUTH);

		// Button Ausgewähltes Backup starten:
		button_startSelected = new JButton(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.Mainframe.button_startSelectedBackup"));

		button_startSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				backupThread = new Thread(new Runnable() {
					@Override
					public void run() {
						prepareBackup(listModel.getElementAt(list_tasks.getSelectedIndex()));
					}
				});
				backupThread.start();

				// Warten bis der Thread sich beendet:
				try {
					backupThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				prep.dispose();
				showSummaryDialog();

			}
		});
		panel_buttons.add(button_startSelected);

		// Button Backup Abbrechen:
		button_cancel = new JButton(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.Mainframe.button_cancelBackup"));
		button_cancel.setEnabled(false);

		button_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int reply = JOptionPane.showConfirmDialog(null,
						ResourceBundle.getBundle("gui.messages").getString("Messages.CancelBackup"), ResourceBundle
								.getBundle("gui.messages").getString("Messages.Cancel"), JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					Mainframe.this.addToOutput(
							ResourceBundle.getBundle("gui.messages").getString("Messages.CancelingBackup"), false);
					button_cancel.setEnabled(false);
					if (backupThread != null) {
						backupThread.interrupt();
					}
				}
			}
		});

		// Button Alle-Backups-Starten:
		button_startAll = new JButton(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.Mainframe.button_startAllBackups"));
		panel_buttons.add(button_startAll);

		button_startAll.addActionListener(new ActionListener() {
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

				// Warten bis der Thread sich beendet:
				try {
					backupThread.join();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					return;
				}
				prep.dispose();
				showSummaryDialog();
			}
		});
		panel_buttons.add(button_cancel);
	}

	/**
	 * Bereitet den gegebenen Task auf ein Hardlink-Backup vor (analysiert die
	 * Quelle(n)).
	 * 
	 * @param task
	 *            vorzubereitender Task
	 */
	private void prepareBackup(BackupTask task) {
		selectedTask = task;

		// Testen ob Quell- und Zielpfad(e) existieren:
		ArrayList<Source> sources = selectedTask.getSources();
		for (int i = 0; i < sources.size(); i++) {
			if (!(new File(sources.get(i).getPath())).exists()) {
				String output = ResourceBundle.getBundle("gui.messages").getString(
						"GUI.Mainframe.errorSourceDontExists");
				listener.printOut(output, false);
				listener.log(output, selectedTask);
				return;
			}
		}
		if (!(new File(selectedTask.getDestinationPath())).exists()) {
			String output = ResourceBundle.getBundle("gui.messages")
					.getString("GUI.Mainframe.errDestinationDontExists");
			listener.printOut(output, false);
			return;
		}

		prep = new PreparingDialog(new IPreparingDialogListener() {

			@Override
			public void cancelBackup() {
				int reply = JOptionPane.showConfirmDialog(null,
						ResourceBundle.getBundle("gui.messages").getString("Messages.CancelBackup"), ResourceBundle
								.getBundle("gui.messages").getString("Messages.Cancel"), JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					Mainframe.this.addToOutput(
							ResourceBundle.getBundle("gui.messages").getString("Messages.CancelingBackup"), false);
					button_cancel.setEnabled(false);
					if (backupThread != null) {
						backupThread.interrupt();
					}
				}

			}
		});
		prep.setLocation(frmTotalbackup.getLocationOnScreen());
		// TODO: Langfristige Lösung?
		// prep.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		prep.setVisible(true);

		listener.startPreparation(selectedTask);
	}

	/**
	 * Öffnet einen neuen SummeryDialog.
	 */
	private void showSummaryDialog() {
		summary = new SummaryDialog(new ISummaryDialogListener() {

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
			public long getNumberOfFilesToCopy() {
				return listener.getNumberOfFilesToCopy();
			}

			@Override
			public long getNumberOfFilesToLink() {
				return listener.getNumberOfFilesToLink();
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
		summary.dispose();

		backupThread = new Thread(new Runnable() {
			@Override
			public void run() {
				listener.startBackupTask(selectedTask);
			}
		});
		backupThread.start();

	}

	private class SA_About extends AbstractAction {
		public SA_About() {
			putValue(NAME, ResourceBundle.getBundle("gui.messages").getString("GUI.AboutDialog.title"));
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				AboutDialog dialog = new AboutDialog();
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
			// TODO: unnötig?
			putValue(NAME, ResourceBundle.getBundle("gui.messages").getString("GUI.Mainframe.menu_quit"));
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			saveProperties();
			System.exit(0);
		}
	}

	/**
	 * Seriallisiert die Programm-Einstellungen (Backup-Taks)
	 */
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
		textfield_status.setText(status);
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
		button_cancel.setEnabled(!noBackupRunning);
		button_startSelected.setEnabled(noBackupRunning);
		button_startAll.setEnabled(noBackupRunning);
		button_add.setEnabled(noBackupRunning);
		button_edit.setEnabled(noBackupRunning);
		button_delete.setEnabled(noBackupRunning);
	}

	/**
	 * Gibt zurück ob die erweiterte Ausgabe aktiviert ist.
	 * 
	 * @return Status der erweiterten Ausgabe
	 */
	public boolean advancedOutputIsEnabled() {
		return checkbox_advancedOutput.isSelected();
	}
}
