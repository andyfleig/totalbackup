/*
 * Copyright 2014 - 2019 Andreas Fleig (github AT andyfleig DOT de)
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

import data.BackupTask;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import listener.IBackupTaskDialogListener;
import listener.IFxMainframeListener;
import listener.IGUIControllerListener;
import main.Backupable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Controller of the JavaFx based GUI of TotalBackup.
 *
 * @author Andreas Fleig
 */
public class GuiController {

	private IGUIControllerListener guiControllerListener;
	private FxMainframe fxMainframe;
	private BackupTaskDialog backupTaskDialog;
	private AboutDialog aboutDialog;
	private Parent root;
	private Stage root_stage;
	public Stage backupTaskDialogStage;

	private TrayIcon trayIcon;
	private Process trayProcess;
	// defines whether the QtTray (true) or the Java-Tray (false) is used
	private boolean isQTTray;

	private Image image;

	private Socket socket = null;
	private Socket clientSocket = null;

	/**
	 * Creates a GuiController with the given corresponding listener and the given instance of the mainframe.
	 *
	 * @param guiControllerListener given instance of GuiControllerListener
	 * @param fxMainframe           given mainframe instance
	 */
	public GuiController(IGUIControllerListener guiControllerListener, FxMainframe fxMainframe) {
		this.guiControllerListener = guiControllerListener;
		this.fxMainframe = fxMainframe;
		fxMainframe.init(new IFxMainframeListener() {
			@Override
			public void startMainframe(Stage stage) {
				root_stage = stage;
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("FxMainframe.fxml"));
					loader.setController(fxMainframe);
					root = loader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}
				root_stage.setTitle("TotalBackup");

				root_stage.setScene(new Scene(root));
				root_stage.show();
			}

			@Override
			public void startBackupTaskDialog(String taskName) {
				backupTaskDialogStage = new Stage(StageStyle.UTILITY);
				IBackupTaskDialogListener backupTaskDialogListener = new IBackupTaskDialogListener() {
					@Override
					public void addBackupTask(BackupTask task) {
						guiControllerListener.addBackupTask(task);
					}

					@Override
					public void saveProperties() {
						guiControllerListener.saveProperties();
					}

					@Override
					public void scheduleBackupTask(BackupTask task) {
						guiControllerListener.scheduleBackupTask(task);
					}

					@Override
					public boolean backupTaskWithNameExisting(String taskName) {
						if (guiControllerListener.getBackupTaskWithName(taskName) == null) {
							return false;
						} else {
							return true;
						}
					}

					@Override
					public void deleteBackupTaskWithName(String taskName) {
						guiControllerListener.deleteBackupTaskWithName(taskName);
					}
				};

				backupTaskDialog = new BackupTaskDialog(guiControllerListener.getBackupTaskWithName(taskName));
				backupTaskDialog.setBackupTaskDialogListener(backupTaskDialogListener);
				backupTaskDialogStage.initModality(Modality.APPLICATION_MODAL);
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("BackupTaskDialog.fxml"));
					loader.setController(backupTaskDialog);
					Scene scene = new Scene(loader.load());
					backupTaskDialogStage.setScene(scene);

					backupTaskDialog.setStage(backupTaskDialogStage);
					backupTaskDialogStage.showAndWait();
				} catch (IOException e) {
					System.err.println(e.toString());
					System.err.println("IOException while starting BackupTaskDialog");
				}

			}

			@Override
			public void startAboutDialog() {
				aboutDialog = new AboutDialog();
				final Stage aboutDialogStage = new Stage(StageStyle.UTILITY);
				aboutDialogStage.initModality(Modality.APPLICATION_MODAL);
				try {
					aboutDialogStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("AboutDialog.fxml"))));
					aboutDialog.setStage(aboutDialogStage);
					aboutDialogStage.showAndWait();
				} catch (IOException e) {
					System.err.println("IOException while starting AboutDialog");
				}
			}

			@Override
			public void saveProperties() {
				guiControllerListener.saveProperties();
			}

			@Override
			public void deleteBackupTaskWithName(String taskName) {
				guiControllerListener.deleteBackupTaskWithName(taskName);
			}

			@Override
			public void deleteEmptyBackupFolders(BackupTask task) {
				guiControllerListener.deleteEmptyBackupFolders(task);
			}

			@Override
			public void runBackupTaskWithName(String taskName) {
				BackupTask task = guiControllerListener.getBackupTaskWithName(taskName);
				guiControllerListener.scheduleBackupTaskNow(task);

			}

			@Override
			public void taskFinished(BackupTask task, boolean schedule) {
				guiControllerListener.taskFinished(task, schedule);
			}
		});
		URL url = Thread.currentThread().getContextClassLoader().getResource("TB_logo.png");
		image = Toolkit.getDefaultToolkit().getImage(url);
	}

	/**
	 * Initializes the GuiController.
	 */
	public void initialize() {
		// Make a decision whether the QtTray or the Java-Tray is used
		// Java-Tray is used if supported by the platform and QtTray is not forced
		if (SystemTray.isSupported() && !guiControllerListener.argsContains("force_qt")) {
			SystemTray systemTray = SystemTray.getSystemTray();
			PopupMenu trayPopupMenu = new PopupMenu();
			MenuItem action = new MenuItem(ResourceBundle.getBundle("messages").getString("GUI.button_show"));
			action.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					//ToDo: show TotalBackup

				}
			});
			trayPopupMenu.add(action);

			MenuItem close = new MenuItem(ResourceBundle.getBundle("messages").getString("GUI.button_close"));
			close.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					guiControllerListener.quitTotalBackup();
				}
			});
			trayPopupMenu.add(close);
			int trayIconWidth = new TrayIcon(image).getSize().width;

			trayIcon = new TrayIcon(image.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH),
					ResourceBundle.getBundle("messages").getString("GUI.Mainframe.title"), trayPopupMenu);

			try {
				systemTray.add(trayIcon);
			} catch (AWTException e) {
				e.printStackTrace();
			}
			isQTTray = false;
		} else {

			// start QtTray
			ProcessBuilder builder = new ProcessBuilder("./totalbackuptray");
			try {
				trayProcess = builder.start();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null,
						ResourceBundle.getBundle("messages").getString("GUI.Mainframe.errStartingQtTray"),
						ResourceBundle.getBundle("messages").getString("GUI.errMsg"), JOptionPane.INFORMATION_MESSAGE);
			}

			// create and run thread for communication with QtTray
			Thread recvThread = new Thread(new Runnable() {

				@Override
				public void run() {
					recvLoop();
				}
			});
			recvThread.start();
			isQTTray = true;
		}
	}

	/**
	 * Returns whether QtTray or Java-Tray is used.
	 *
	 * @return QtTray = true, Java-Tray = false
	 */
	public boolean isQTTray() {
		return isQTTray;
	}

	/**
	 * Sends the given message to the QtTray. Message must not be longer than 999 characters.
	 *
	 * @param msg               message to send
	 * @param terminationSignal whether the message is a termination signal (true) or not (false)
	 */
	public void sendToQtTrayOverSocket(String msg, boolean terminationSignal) {
		char[] toSend;
		if (!terminationSignal) {
			if (msg == null) {
				msg = "";
			}
			int msgLenght = msg.length();
			toSend = new char[msgLenght + 3];
			// handle umlauts
			msg = msg.replace("ä", "ae");
			msg = msg.replace("ö", "oe");
			msg = msg.replace("ü", "ue");

			if (msgLenght < 10) {
				toSend[0] = "0".charAt(0);
				toSend[1] = "0".charAt(0);
				toSend[2] = String.valueOf(msgLenght).charAt(0);
			} else if (msgLenght < 100) {
				toSend[0] = "0".charAt(0);
				toSend[1] = String.valueOf(msgLenght).charAt(0);
				toSend[2] = String.valueOf(msgLenght).charAt(1);
			} else if (msgLenght < 1000) {
				toSend[0] = String.valueOf(msgLenght).charAt(0);
				toSend[1] = String.valueOf(msgLenght).charAt(1);
				toSend[2] = String.valueOf(msgLenght).charAt(2);
			} else {
				return;
			}
			for (int i = 3; i < msgLenght + 3; i++) {
				toSend[i] = msg.charAt(i - 3);
			}
		} else {
			toSend = new char[3];
			toSend[0] = "0".charAt(0);
			toSend[1] = "0".charAt(0);
			toSend[2] = "0".charAt(0);
		}
		// 1. create socket and connect
		try {
			clientSocket = new Socket("127.0.0.1", 1235);
			clientSocket.setReuseAddress(true);
		} catch (IOException e) {
			System.err.println("Error: IOException in Mainframe in sendToQtTrayOverSocket while creating Socket and " +
					"setReuseAddress");
			return;
		}

		PrintWriter out;
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			System.err.println("Error: IOException in Mainframe in sendToQtTrayOverSocket while creating PrintWriter");
			try {
				clientSocket.close();
			} catch (IOException ex) {
				System.out.println("Error: IOException in Mainframe in sendToQtTrayOverSocket while closing Socket");
			}
			return;
		}
		out.write(toSend);
		out.flush();
		try {
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("Error: IOException in Mainframe in sendToQtTrayOverSocket while closing " +
					"PrintWriter and Socket");
		}
	}

	/**
	 * Calls the destruction of the QtTray.
	 */
	public void destroyTrayProcess() {
		if (trayProcess != null) {
			trayProcess.destroy();
		}
	}

	/**
	 * Loop for the communication with the QtTray (via local TCP socket).
	 */
	private void recvLoop() {
		while (true) {
			// 1. create socket
			ServerSocket server = null;
			try {
				server = new ServerSocket(1234);
			} catch (IOException e) {
				System.err.println("Error: IOException in Mainframe in recvLoop while creating new ServerSocket");
				return;
			}

			// 2. connect
			try {
				socket = server.accept();
			} catch (IOException e) {
				System.err.println("Error: IOException in Mainframe in recvLoop while server.accept;");
			}

			try {
				DataInputStream in = new DataInputStream(socket.getInputStream());

				int msg = in.readInt();
				if (msg == 0) {
					// quit TotalBackup
					guiControllerListener.saveProperties();
					guiControllerListener.quitTotalBackup();
					in.close();
				} else if (msg == 1) {
					// hide/ show TotalBackup
					// ToDo: implement
					in.close();
				}

			} catch (IOException e) {
				System.err.println("Error: IOException in Mainframe in recvLoop while creating DataInputStream and " +
						"reading int");
			}

			// close socket
			try {
				socket.close();
				server.close();
				server = null;
			} catch (IOException e) {
				System.err.println("Error: IOException in Mainframe in recvLoop while closing Socket and ServerSocket");
			}
		}
	}

	/**
	 * Shows the given message on the Java-Tray.
	 *
	 * @param msg message to show
	 */
	public void showTrayPopupMessage(String msg) {
		trayIcon.displayMessage(null, msg, TrayIcon.MessageType.INFO);
	}

	/**
	 * Adds the given name of a BackupTask to the list of BackupTasks.
	 *
	 * @param task BackupTask to add
	 */
	public void addBackupTask(BackupTask task) {
		fxMainframe.addBackupTask(task);
	}

	/**
	 * Removes the BackupTask with the given name from the list of BackupTasks.
	 *
	 * @param taskName name of the BackupTask to remove
	 */
	public void removeBackupTask(String taskName) {
		fxMainframe.removeBackupTask(taskName);
	}

	/**
	 * Triggers the disposing of the PreparingDialog (if any)
	 */
	public void disposePreparingDialogIfNotNull() {
		fxMainframe.disposePreparingDialogIfNotNull();
	}

	/**
	 * Opens the SummaryDialog window giving an overview over some stats of the BackupTask (like number of files to
	 * copy)
	 *
	 * @param task   corresponding BackupTask
	 * @param backup Backup object of the BackupTask
	 */
	public void showSummaryDialog(final BackupTask task, final Backupable backup) {
		fxMainframe.showSummaryDialog(task, backup);
	}

	/**
	 * Opens the PreparingDialog window.
	 */
	public void showPreparingDialog(BackupTask task, Backupable backup) {
		fxMainframe.showPreparingDialog(task, backup);
	}

	/**
	 * Sets the status for the BackupTask with the given name.
	 *
	 * @param taskName   name of the BackupTask to set status
	 * @param error      whether it is an error status or not
	 * @param taskStatus status to set
	 */
	public void setStatusOfBackupTask(String taskName, boolean error, String taskStatus) {
		fxMainframe.setStatusOfBackupTask(taskName, error, taskStatus);
	}

	/**
	 * Sets the next-execution part of the status for the BackupTask with the given name.
	 *
	 * @param taskName                    name of the BackupTask to set next-execution part of the status
	 * @param taskNextExecutionTimeStatus next-execution status to set
	 */
	public void setNextExecutionTimeStatus(String taskName, LocalDateTime taskNextExecutionTimeStatus) {
		fxMainframe.setNextExecutionTimeStatusOfBackupTask(taskName, taskNextExecutionTimeStatus);
	}
}