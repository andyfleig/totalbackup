package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javax.swing.JLabel;

import listener.ISummaryDialogListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SummaryDialog extends JDialog {

	private ISummaryDialogListener listener;
	private final JPanel panel_main = new JPanel();
	private boolean backupCanceled;
	private boolean backupIsNotFinished;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SummaryDialog dialog = new SummaryDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SummaryDialog(ISummaryDialogListener listener) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				if (!backupCanceled && !backupIsNotFinished) {
					cancelBackup();
				}
			}
		});
		setTitle(ResourceBundle.getBundle("gui.messages").getString("GUI.SummaryDialog.title"));
		this.listener = listener;
		setResizable(false);
		setAlwaysOnTop(true);
		setBounds(100, 100, 524, 370);
		getContentPane().setLayout(new BorderLayout());
		panel_main.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel_main, BorderLayout.CENTER);
		panel_main.setLayout(null);

		JLabel label_taskName = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.SummaryDialog.label_taskName"));
		label_taskName.setBounds(12, 10, 267, 15);
		panel_main.add(label_taskName);

		JLabel label_taskNameDyn = new JLabel("");
		label_taskNameDyn.setBounds(307, 10, 78, 15);
		panel_main.add(label_taskNameDyn);

		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(panel_buttons, BorderLayout.SOUTH);

		JButton btn_ok = new JButton(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.SummaryDialog.button_start"));
		btn_ok.addActionListener(new ActionListener() {
			// Button Start:
			public void actionPerformed(ActionEvent arg0) {
				backupIsNotFinished = true;
				clearBackupInfos();
				SummaryDialog.this.listener.startBackup();
			}
		});
		btn_ok.setActionCommand("OK");
		panel_buttons.add(btn_ok);
		getRootPane().setDefaultButton(btn_ok);

		JButton btn_cancel = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_cancel"));
		btn_cancel.addActionListener(new ActionListener() {
			// Button Cancel:
			public void actionPerformed(ActionEvent e) {
				backupCanceled = true;
				cancelBackup();
			}
		});
		btn_cancel.setActionCommand("Cancel");
		panel_buttons.add(btn_cancel);

		JLabel label_numberOfDirs = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.SummaryDialog.label_numberOfDirs"));
		label_numberOfDirs.setBounds(12, 37, 267, 15);
		panel_main.add(label_numberOfDirs);

		JLabel label_numberOfDirsDyn = new JLabel((String) null);
		label_numberOfDirsDyn.setBounds(307, 37, 78, 15);
		panel_main.add(label_numberOfDirsDyn);

		JLabel label_numberToCopy = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.SummaryDialog.label_numberOfFilesToCopy"));
		label_numberToCopy.setBounds(12, 64, 267, 15);
		panel_main.add(label_numberToCopy);

		JLabel label_numberToCopyDyn = new JLabel((String) null);
		label_numberToCopyDyn.setBounds(307, 64, 78, 15);
		panel_main.add(label_numberToCopyDyn);

		JLabel label_numberToLink = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.SummaryDialog.label_numberOfFilesToLink"));
		label_numberToLink.setBounds(12, 91, 267, 15);
		panel_main.add(label_numberToLink);

		JLabel label_numberToLinkDyn = new JLabel("0");
		label_numberToLinkDyn.setBounds(307, 91, 78, 15);
		panel_main.add(label_numberToLinkDyn);

		JLabel label_sizeToCopy = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.SummaryDialog.label_sizeToCopy"));
		label_sizeToCopy.setBounds(12, 118, 267, 15);
		panel_main.add(label_sizeToCopy);

		JLabel label_sizeToLink = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.SummaryDialog.label_sizeToLink"));
		label_sizeToLink.setBounds(12, 145, 267, 15);
		panel_main.add(label_sizeToLink);

		JLabel label_sizeToCopyDyn = new JLabel("0");
		label_sizeToCopyDyn.setBounds(307, 118, 78, 15);
		panel_main.add(label_sizeToCopyDyn);

		JLabel label_sizeToLinkDyn = new JLabel("0");
		label_sizeToLinkDyn.setBounds(307, 145, 78, 15);
		panel_main.add(label_sizeToLinkDyn);

		// Inhalte hinzufügen:
		label_taskNameDyn.setText(listener.getTaskName());
		label_numberToCopyDyn.setText(String.valueOf(listener.getNumberOfFilesToCopy()).toString());
		label_numberToLinkDyn.setText(String.valueOf(listener.getNumberOfFilesToLink()).toString());
		label_numberOfDirsDyn.setText(String.valueOf(listener.getNumberOfDirectories()).toString());

		DecimalFormat decimalFormat = new DecimalFormat("#0.00");

		// TODO: schöner: in eigene Methode auslagern
		// Größe der zu kopierenden Dateien:
		double size = listener.getSizeToCopy();
		if (size < 1000) {
			String result = String.valueOf(decimalFormat.format(size)) + "Byte";
			label_sizeToCopyDyn.setText(result);
		} else if (size < 1000000) {
			String result = String.valueOf(decimalFormat.format(size / 1000)) + "kB";
			label_sizeToCopyDyn.setText(result);
		} else if (size < 1000000000) {
			String result = String.valueOf(decimalFormat.format(size / 1000000)) + "MB";
			label_sizeToCopyDyn.setText(result);
		} else {
			String result = String.valueOf(decimalFormat.format(size / 1000000000)) + "GB";
			label_sizeToCopyDyn.setText(result);
		}

		// Größe der zu kopierenden Dateien:
		size = listener.getSizeToLink();
		if (size < 1000) {
			String result = String.valueOf(decimalFormat.format(size)) + "Byte";
			label_sizeToLinkDyn.setText(result);
		} else if (size < 1000000) {
			String result = String.valueOf(decimalFormat.format(size / 1000)) + "kB";
			label_sizeToLinkDyn.setText(result);
		} else if (size < 1000000000) {
			String result = String.valueOf(decimalFormat.format(size / 1000000)) + "MB";
			label_sizeToLinkDyn.setText(result);
		} else {
			String result = String.valueOf(decimalFormat.format(size / 1000000000)) + "GB";
			label_sizeToLinkDyn.setText(result);
		}
	}
	
	private void cancelBackup() {
		outprintBackupCanceled();
		listener.taskFinished(listener.getTaskName());
		deleteEmptyBackupFolders();
		clearBackupInfos();
		SummaryDialog.this.dispose();
	}

	private void clearBackupInfos() {
		listener.clearBackupInfos();
	}

	private void deleteEmptyBackupFolders() {
		listener.deleteEmptyBackupFolders();
	}

	private void outprintBackupCanceled() {
		listener.outprintBackupCanceled();
	}
}
