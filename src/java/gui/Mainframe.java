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

import listener.IEditDialogListener;
import listener.IMainframeListener;
import listener.IPreparingDialogListener;
import listener.ISummaryDialogListener;
import main.Backupable;
import main.Controller;
import gui.AboutDialog;
import gui.EditDialog;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.NullPointerException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
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

import com.google.gson.Gson;

import data.BackupTask;
import data.BackupThreadContainer;
import data.Source;

/**
 * Mainframe von TotalBackup.
 * 
 * @author Andreas Fleig
 *
 */
public class Mainframe extends JDialog {

	// Für main benötigt:
	// private Mainframe window;

	public JFrame frmTotalbackup;

	private final Action action_about = new SA_About();
	private final Action action_quit = new SA_Quit();
	private JTextPane textpane_output;
	private JList<BackupTask> list_tasks;

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

	private ArrayList<BackupThreadContainer> backupThreads = new ArrayList<BackupThreadContainer>();

	private SummaryDialog summary;

	private PreparingDialog prep;

	private Socket socket = null;

	private Process trayProcess;

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
				Mainframe.this.savePropertiesGson();
			}

			@Override
			public void scheduleBackupTasks() {
				listener.scheduleBackupTasks();
			}

		};

		frmTotalbackup = new JFrame();
		frmTotalbackup.setTitle(ResourceBundle.getBundle("gui.messages").getString("GUI.Mainframe.title"));
		frmTotalbackup.setBounds(100, 100, 894, 569);
		frmTotalbackup.setMinimumSize(new Dimension(500, 400));
		frmTotalbackup.setPreferredSize(new Dimension(800, 500));
		frmTotalbackup.pack();

		frmTotalbackup.setIconImage(Toolkit.getDefaultToolkit().getImage("../resources/TB_logo.png"));

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
					// Prüfen ob der gewählte Task gerade ausgeführt wird:
					if (listener.getRunningBackupTasks().contains(list_tasks.getSelectedValue().getTaskName())) {
						JOptionPane.showMessageDialog(null,
								ResourceBundle.getBundle("gui.messages").getString("GUI.Mainframe.errTaskIsRunning"),
								ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
								JOptionPane.ERROR_MESSAGE);
						return;
					}
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
						editDialog.setAutostart(task.getAutostart());
						editDialog.setLocation(frmTotalbackup.getLocationOnScreen());
						editDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
						if (task.simpleAutoCleanIsEnabled()) {
							editDialog.setAutoCleanEnabled(true);
							editDialog.setNumberOfBackupsToKeep(task.getNumberOfBackupsToKeep());
						} else if (task.extendedAutoCleanIsEnabled()) {
							editDialog.setExtendedAutoCleanSettings(task.getNumberOfExtendedCleanRules(),
									task.getThreshold(), task.getThresholdUnits(), task.getBackupsToKeep());
						}
						int autoBackupMode = task.getAutoBackupMode();
						editDialog.setAutoBackupMode(autoBackupMode);
						if (autoBackupMode == 1) {
							editDialog.setBackupWeekdays(task.getBackupWeekdays());
							editDialog.setBackupStartTime(task.getStartTime());
						} else if (autoBackupMode == 2) {
							editDialog.setBackupDaysInMonth(task.getBackupDaysInMonth());
							editDialog.setBackupStartTime(task.getStartTime());
						} else if (autoBackupMode == 3) {
							editDialog.setIntervalTime(task.getIntervalTime());
							editDialog.setIntervalUnit(task.getIntervalUnit());
						}
						editDialog.setCatchUpEnabled(task.isCatchUpEnabled());
						String catchUpTime;
						if (task.isCatchUpEnabled()) {
							catchUpTime = task.getCatchUpTime();
						} else {
							catchUpTime = "5min";
						}
						editDialog.setCatchUpTime(catchUpTime);
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
				// Prüfen ob der gewählte Task gerade ausgeführt wird:
				if (listener.getRunningBackupTasks().contains(list_tasks.getSelectedValue().getTaskName())) {
					JOptionPane
							.showMessageDialog(null,
									ResourceBundle.getBundle("gui.messages")
											.getString("GUI.Mainframe.errTaskIsRunning"),
									ResourceBundle.getBundle("gui.messages").getString("GUI.errMsg"),
									JOptionPane.ERROR_MESSAGE);
					return;
				}
				int reply = JOptionPane.showConfirmDialog(null,
						ResourceBundle.getBundle("gui.messages").getString("Messages.DeleteTask"), null,
						JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					if (!list_tasks.isSelectionEmpty()) {
						BackupTask currentTask = listModel.getElementAt(list_tasks.getSelectedIndex());
						listener.removeBackupTask(currentTask);
						listener.removeBackupTaskScheduling(currentTask);
					}
					savePropertiesGson();
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
				if (list_tasks.getSelectedIndex() == -1) {
					return;
				}
				final BackupTask taskToRun = listModel.getElementAt(list_tasks.getSelectedIndex());
				listener.taskStarted(taskToRun.getTaskName());
				prep = new PreparingDialog(new IPreparingDialogListener() {

					@Override
					public void cancelBackup(String taskName) {
						int reply = JOptionPane.showConfirmDialog(null, ResourceBundle.getBundle("gui.messages")
								.getString("Messages.CancelBackup"), ResourceBundle.getBundle("gui.messages")
								.getString("Messages.Cancel"), JOptionPane.YES_NO_OPTION);
						if (reply == JOptionPane.YES_OPTION) {
							Mainframe.this.addToOutput(
									ResourceBundle.getBundle("gui.messages").getString("Messages.CancelingBackup"),
									false, taskToRun.getTaskName());
							button_cancel.setEnabled(false);
							for (BackupThreadContainer container : backupThreads) {
								if (container.getTaskName().equals(taskName)) {
									container.getBackupThread().interrupt();
									backupThreads.remove(container);
								}
							}
						}

					}
				}, taskToRun);
				prep.setLocation(frmTotalbackup.getLocationOnScreen());

				Thread backupThread = new Thread(new Runnable() {
					@Override
					public void run() {
						prepareBackup(taskToRun);
					}
				});
				BackupThreadContainer newContainer = new BackupThreadContainer(backupThread, taskToRun.getTaskName());
				backupThreads.add(newContainer);
				backupThread.start();
				prep.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
				prep.setVisible(true);

			}
		});
		panel_buttons.add(button_startSelected);

		// Button Backup Abbrechen:
		button_cancel = new JButton(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.Mainframe.button_cancelBackup"));
		button_cancel.setEnabled(false);

		button_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BackupTask taskToRun = listModel.getElementAt(list_tasks.getSelectedIndex());
				if (taskToRun == null) {
					return;
				}
				int reply = JOptionPane.showConfirmDialog(null,
						ResourceBundle.getBundle("gui.messages").getString("Messages.CancelBackup"), ResourceBundle
								.getBundle("gui.messages").getString("Messages.Cancel"), JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					Mainframe.this
							.addToOutput(
									ResourceBundle.getBundle("gui.messages").getString("Messages.CancelingBackup"),
									false, null);
					button_cancel.setEnabled(false);
					for (BackupThreadContainer container : backupThreads) {
						if (container.getTaskName().equals(taskToRun.getTaskName())) {
							container.getBackupThread().interrupt();
							backupThreads.remove(container);
						}
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
				for (int i = 0; i < listModel.getSize(); i++) {
					listener.scheduleBackupTaskNow(listModel.get(i));
				}
			}
		});
		panel_buttons.add(button_cancel);

		// Entscheidung für den Tray-Typ treffen:
		// TODO: verbessern
		String OS = System.getProperty("os.name").toLowerCase();
		if (!OS.contains("win")) {
			// QT-App starten:
			ProcessBuilder builder = new ProcessBuilder("../totalbackuptray");
			try {
				trayProcess = builder.start();
			} catch (IOException e1) {
				System.err.println("Error while starting totalbackuptray");
			}

			// Thread für recv anlegen und starten:
			Thread recvThread = new Thread(new Runnable() {
				@Override
				public void run() {
					recvLoop();
				}
			});
			recvThread.start();
		} else {
			if (SystemTray.isSupported()) {
				SystemTray systemTray = SystemTray.getSystemTray();

				Image image = Toolkit.getDefaultToolkit().getImage("../resources/TB_logo.png");

				PopupMenu trayPopupMenu = new PopupMenu();

				MenuItem action = new MenuItem(ResourceBundle.getBundle("gui.messages").getString("GUI.button_show"));
				action.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						frmTotalbackup.setVisible(true);

					}
				});
				trayPopupMenu.add(action);

				MenuItem close = new MenuItem(ResourceBundle.getBundle("gui.messages").getString("GUI.button_close"));
				close.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				trayPopupMenu.add(close);

				TrayIcon trayIcon = new TrayIcon(image, ResourceBundle.getBundle("gui.messages").getString(
						"GUI.Mainframe.title"), trayPopupMenu);

				trayIcon.setImageAutoSize(true);

				try {
					systemTray.add(trayIcon);
				} catch (AWTException e) {
					e.printStackTrace();
				}

			}
		}

		frmTotalbackup.setVisible(true);

	}

	/**
	 * Endlosschleife für die Kommunikation mit dem Qt-Tray (mit TCP-Socket).
	 */
	private void recvLoop() {
		while (true) {
			// 1. Socket aufbauen:
			ServerSocket server = null;
			try {
				server = new ServerSocket(1234);
			} catch (IOException e) {
				System.err.println("Error: Could not open TCP-socket");
			}

			// 2. Verbinden:
			try {
				socket = server.accept();
			} catch (IOException e) {
				System.err.println("Error: Could not connect to tray");
			}

			try {
				DataInputStream in = new DataInputStream(socket.getInputStream());

				int msg = in.readInt();
				if (msg == 0) {
					savePropertiesGson();
					System.exit(0);
					in.close();
					break;
				} else if (msg == 1) {
					if (frmTotalbackup.isVisible()) {
						frmTotalbackup.setVisible(false);
					} else {
						frmTotalbackup.setVisible(true);
					}
				}

			} catch (IOException e) {
				System.err.println("Error while sending message to Qt-Tray");
			}

			// Socket schließen:
			try {
				socket.close();
				server.close();
				server = null;
			} catch (IOException e) {
				System.err.println("Error: Could not close socket");
			}
		}
	}

	/**
	 * Bereitet den gegebenen Task auf ein Hardlink-Backup vor (analysiert die
	 * Quelle(n)).
	 * 
	 * @param task
	 *            vorzubereitender Task
	 */
	public void prepareBackup(BackupTask task) {

		// Testen ob Quell- und Zielpfad(e) existieren:
		ArrayList<Source> sources = task.getSources();
		for (int i = 0; i < sources.size(); i++) {
			if (!(new File(sources.get(i).getPath())).exists()) {
				String output = ResourceBundle.getBundle("gui.messages").getString(
						"GUI.Mainframe.errorSourceDontExists");
				listener.printOut(output, false, task.getTaskName());
				listener.log(output, task);
				prep.dispose();
				listener.removeBackupTaskFromRunningTasks(task);
				return;
			}
		}
		if (!(new File(task.getDestinationPath())).exists()) {
			String output = ResourceBundle.getBundle("gui.messages")
					.getString("GUI.Mainframe.errDestinationDontExists");
			listener.printOut(output, false, task.getTaskName());
			prep.dispose();
			listener.removeBackupTaskFromRunningTasks(task);
			return;
		}

		Backupable backup = listener.startPreparation(task);
		if (prep != null) {
			prep.dispose();
		}
		boolean isCanceled = false;
		for (BackupThreadContainer container : backupThreads) {
			if (container.getTaskName().equals(task.getTaskName())) {
				isCanceled = true;
			}
		}
		if (!isCanceled) {
			if (!task.getAutostart()) {
				showSummaryDialog(task, backup);
				synchronized (task) {
					try {
						task.wait();
						if (!backup.isCanceled()) {
							startBackupTask(task, backup);
						}
					} catch (InterruptedException e) {
						System.out.println("Backup-Thread was unexpectedly canceled");
					}
				}
			} else {
				startBackupTask(task, backup);
			}
		}
	}

	/**
	 * Öffnet einen neuen SummeryDialog.
	 * 
	 * @param task
	 *            entsprechender BackupTask
	 */
	private void showSummaryDialog(final BackupTask task, final Backupable backup) {
		summary = new SummaryDialog(new ISummaryDialogListener() {

			@Override
			public void startBackup() {
				startBackupTask(task, backup);
			}

			@Override
			public String getTaskName() {
				return task.getTaskName();
			}

			@Override
			public void deleteEmptyBackupFolders(BackupTask task) {
				listener.deleteEmptyBackupFolders(task.getDestinationPath(), task);
			}

			@Override
			public void outprintBackupCanceled(BackupTask task) {
				listener.outprintBackupCanceled(task);
			}

			@Override
			public void taskFinished(BackupTask task) {
				listener.taskFinished(task);

			}

		}, task, backup);
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

	private void startBackupTask(final BackupTask task, Backupable backup) {
		if (!task.getAutostart()) {
			summary.dispose();
		}
		listener.startBackupTask(task, backup);

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
			putValue(NAME, ResourceBundle.getBundle("gui.messages").getString("GUI.Mainframe.menu_quit"));
		}

		public void actionPerformed(ActionEvent e) {
			savePropertiesGson();
			if (trayProcess != null) {
				trayProcess.destroy();
			}
			System.exit(0);
		}
	}

	/**
	 * Serialisiert die Programm-Einstellungen (Backup-Taks)
	 */
	private void saveProperties() {
		File properties = new File("../properties.ser");
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
	 * Serialisiert die Programm-Einstellungen (Backup-Tasks) mit Gson
	 */
	private void savePropertiesGson() {
		Gson gson = new Gson();
		String settings = gson.toJson(listener.getBackupTasks());
		try {
			PrintWriter out = new PrintWriter("../properties");
			out.println(settings);
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error: FileNotException while writing properties");
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
	 * @param taskName
	 *            Name des aktuellen Tasks
	 */
	public synchronized void addToOutput(String output, boolean error, String taskName) {
		if (output == null) {
			throw new NullPointerException();
		}
		if (taskName != null) {
			output = "[" + taskName + "]" + output;
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
