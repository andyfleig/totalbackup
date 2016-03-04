package gui;

import data.BackupTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import listener.IFxMainframeListener;
import listener.IGUIControllerListener;
import main.BackupHelper;
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
import java.util.ResourceBundle;
import java.util.Set;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */


public class GuiController {

	private IGUIControllerListener guiControllerListener;
	private FxMainframe fxMainframe;
	private BackupTaskDialog backupTaskDialog;
	private Parent root;
	public Stage backupTaskDialogStage;

	//ToDo: setzen (isQTTray)!
	private TrayIcon trayIcon;
	private Process trayProcess;
	// Legt fest ob ein QT-Tray (true) oder ein Java-Tray (false) verwendet wird.
	private boolean isQTTray;

	private Image image;

	private Socket socket = null;
	private Socket clientSocket = null;

	public GuiController(IGUIControllerListener guiControllerListener, FxMainframe fxMainframe) {

		this.fxMainframe = fxMainframe;
		fxMainframe.init(new IFxMainframeListener() {
			@Override
			public void startMainframe(Stage stage) {
				try {
					root = FXMLLoader.load(getClass().getResource("FxMainframe.fxml"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				stage.setTitle("TotalBackup");
				stage.setScene(new Scene(root));
				stage.show();
			}

			@Override
			public void startBackupTaskDialog() {
				backupTaskDialogStage = new Stage(StageStyle.UTILITY);
				backupTaskDialog = new BackupTaskDialog();
				backupTaskDialogStage.initModality(Modality.APPLICATION_MODAL);
				try {
					backupTaskDialogStage.setScene(
							new Scene(FXMLLoader.load(getClass().getResource("BackupTaskDialog.fxml"))));
					backupTaskDialog.setStage(backupTaskDialogStage);
					backupTaskDialogStage.showAndWait();
				} catch (IOException e) {
					System.err.println("IOException while starting BackupTaskDialog");
				}
			}

			@Override
			public void startAboutDialog() {
				AboutDialog aboutDialog = new AboutDialog();
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
		});
		//ToDo: Wohin? (Konstruktor, initilazie oder wo anders?)
		image = Toolkit.getDefaultToolkit().getImage(BackupHelper.ICON_LOCATION);
	}

	public void initialize() {
		// Entscheidung für den Tray-Typ treffen:
		// Wird der Java-SystemTray unterstützt wird dieser auch verwendet:
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

			// QT-App starten:
			ProcessBuilder builder = new ProcessBuilder("./totalbackuptray");
			try {
				trayProcess = builder.start();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null,
						ResourceBundle.getBundle("messages").getString("GUI.Mainframe.errStartingQtTray"),
						ResourceBundle.getBundle("messages").getString("GUI.errMsg"), JOptionPane.INFORMATION_MESSAGE);
			}

			// Thread für recv anlegen und starten:
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
	 * Gibt zurück ob ein QT-Tray (true) oder ein Java-Tray (false) verwendet wird.
	 *
	 * @return QT-Tray (true) oder ein Java-Tray (false)
	 */
	public boolean isQTTray() {
		return isQTTray;
	}

	/**
	 * Sendet die gegebene Nachricht (String) an den QT-Tray. Dabei darf die Nachricht maximal 999 Zeichen lang sein!
	 *
	 * @param msg               zu sendende Nachricht
	 * @param terminationSignal wenn es sich bei der Nachricht um ein terminationSignal (true) und keine anzuzuegende
	 *                          Nachricht (false) handelt
	 */
	public void sendToQtTrayOverSocket(String msg, boolean terminationSignal) {
		char[] toSend;
		if (!terminationSignal) {
			if (msg == null) {
				msg = "";
			}
			int msgLenght = msg.length();
			toSend = new char[msgLenght + 3];
			// Anzuzeigende Nachricht:
			// Umlaute bearbeiten:
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
			// Signal:
			toSend[0] = "0".charAt(0);
			toSend[1] = "0".charAt(0);
			toSend[2] = "0".charAt(0);
		}
		// 1. Socket aufbauen & verbinden:
		try {
			clientSocket = new Socket("127.0.0.1", 1235);
			clientSocket.setReuseAddress(true);
		} catch (IOException e) {
			System.err.println("Error: IOExcpetion in Mainframe in sendToQtTrayOverSocket while creating Socket and " +
					"setReuseAddress");
			return;
		}

		PrintWriter out;
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			System.err.println("Error: IOExcpetion in Mainframe in sendToQtTrayOverSocket while creating PrintWriter");
			try {
				clientSocket.close();
			} catch (IOException ex) {
				System.out.println("Error: IOExcpetion in Mainframe in sendToQtTrayOverSocket while closing Socket");
			}
			return;
		}
		out.write(toSend);
		out.flush();
		try {
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("Error: IOExcpetion in Mainframe in sendToQtTrayOverSocket while closing " +
					"PrintWriter and Socket");
		}
	}

	/**
	 * Ruft destroy auf dem TrayProcess auf, wenn dieser nicht null ist.
	 */
	public void destroyTrayProcess() {
		if (trayProcess != null) {
			trayProcess.destroy();
		}
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
				System.err.println("Error: IOException in Mainframe in recvLoop while creating new ServerSocket");
			}

			// 2. Verbinden:
			try {
				socket = server.accept();
			} catch (IOException e) {
				System.err.println("Error: IOException in Mainframe in recvLoop while server.accept;");
			}

			try {
				DataInputStream in = new DataInputStream(socket.getInputStream());

				int msg = in.readInt();
				if (msg == 0) {
					// Programm beenden:
					guiControllerListener.saveProperties();
					guiControllerListener.quitTotalBackup();
					in.close();
				} else if (msg == 1) {
					// Programm zeigen/ verstecken:
					if (backupTaskDialogStage.isShowing()) {
						//ToDo: richtig?
						backupTaskDialogStage.hide();
					} else {
						//ToDo: richtig? - oder showAndWait()?
						backupTaskDialogStage.show();
					}
					in.close();
				}

			} catch (IOException e) {
				System.err.println("Error: IOException in Mainframe in recvLoop while creating DataInputStream and " +
						"rading int");
			}

			// Socket schließen:
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
	 * Gibt den gegebenen String als Tray-Popup-Message aus.
	 *
	 * @param msg anzuzeigender Text
	 */
	public void showTrayPopupMessage(String msg) {
		trayIcon.displayMessage(null, msg, TrayIcon.MessageType.INFO);
	}

	/**
	 * Fügt einen Task (Namen) in die Liste der Tasks in der GUI hinzu.
	 *
	 * @param taskName
	 */
	public void addBackupTask(String taskName) {
		fxMainframe.addBackupTask(taskName);
	}
	/**
	 * Fügt einen Task (Namen) in die Liste der Tasks in der GUI hinzu.
	 *
	 * @param taskName
	 */
	public void removeBackupTask(String taskName) {
		fxMainframe.removeBackupTask(taskName);
	}

	/**
	 * Ruft auf dem aktuellen PreparingDialog dispose() auf, wenn dieser nicht null ist.
	 */
	public void disposePreparingDialogIfNotNull() {
		//ToDo: Implementieren
		//if (prep != null) {
		//	prep.dispose();
		//}
	}

	/**
	 * Öffnet einen neuen SummeryDialog.
	 *
	 * @param task entsprechender BackupTask
	 */
	public void showSummaryDialog(final BackupTask task, final Backupable backup) {
		fxMainframe.showSummaryDialog(task, backup);
	}

	/**
	 * Setzt den Status für den Task mit dem gegebenen Namen (taskName).
	 *
	 * @param taskName   Task für den der Status gesetzt werden soll
	 * @param error      Ob es sich um einen Fehler oder um einen "normalen" Status handelt. true = Fehler, false =
	 *                   Status
	 * @param taskStatus zu setzender Status
	 */
	public void setStatusOfBackupTask(String taskName, boolean error, String taskStatus) {
		fxMainframe.setStatusOfBackupTask(taskName, error, taskStatus);
	}
}