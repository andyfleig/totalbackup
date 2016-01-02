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

import java.awt.*;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javax.swing.JLabel;

import data.BackupTask;
import listener.ISummaryDialogListener;
import main.BackupHelper;
import main.Backupable;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Dialog welcher nach der Preparation die Zusammenfassung des bevorstehenden Backups anzeigt.
 *
 * @author Andreas Fleig
 */
public class SummaryDialog extends JDialog {

	private ISummaryDialogListener listener;
	private boolean backupCanceled;
	private boolean backupIsNotFinished;

	/**
	 * Dialog für die Zusammenfassung des Vorbereiteten Backups.
	 *
	 * @param listener SummaryDialogListener
	 * @param task     entsprechender BackupTask
	 * @param backup   entsprechendes Backup (Backupable)
	 */
	public SummaryDialog(ISummaryDialogListener listener, final BackupTask task, final Backupable backup) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				if (!backupCanceled && !backupIsNotFinished) {
					cancelBackup(task);
				}
			}
		});
		setTitle(ResourceBundle.getBundle("messages").getString("GUI.SummaryDialog.title"));
		this.listener = listener;
		setResizable(false);
		setAlwaysOnTop(true);
		setBounds(100, 100, 524, 370);
		setIconImage(Toolkit.getDefaultToolkit().getImage(BackupHelper.ICON_LOCATION));

		getContentPane().setLayout(new BorderLayout());
		JPanel panel_main = new JPanel();
		panel_main.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel_main, BorderLayout.CENTER);
		panel_main.setLayout(null);

		JLabel label_taskName = new JLabel(
				ResourceBundle.getBundle("messages").getString("GUI.SummaryDialog.label_taskName"));
		label_taskName.setBounds(12, 10, 267, 15);
		panel_main.add(label_taskName);

		JLabel label_taskNameDyn = new JLabel("");
		label_taskNameDyn.setBounds(307, 10, 78, 15);
		panel_main.add(label_taskNameDyn);

		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(panel_buttons, BorderLayout.SOUTH);

		JButton btn_ok = new JButton(ResourceBundle.getBundle("messages").getString("GUI.SummaryDialog.button_start"));
		btn_ok.addActionListener(new ActionListener() {
			// Button Start:
			public void actionPerformed(ActionEvent arg0) {
				backupIsNotFinished = true;
				synchronized (task) {
					task.notify();
					SummaryDialog.this.dispose();
				}
			}
		});
		panel_buttons.add(btn_ok);
		getRootPane().setDefaultButton(btn_ok);

		JButton btn_cancel = new JButton(ResourceBundle.getBundle("messages").getString("GUI.button_cancel"));
		btn_cancel.addActionListener(new ActionListener() {
			// Button Cancel:
			public void actionPerformed(ActionEvent e) {
				backupCanceled = true;
				backup.cancel();
				synchronized (task) {
					task.notify();
				}
				cancelBackup(task);
			}
		});
		btn_cancel.setActionCommand("Cancel");
		panel_buttons.add(btn_cancel);

		JLabel label_numberOfDirs = new JLabel(
				ResourceBundle.getBundle("messages").getString("GUI.SummaryDialog.label_numberOfDirs"));
		label_numberOfDirs.setBounds(12, 37, 267, 15);
		panel_main.add(label_numberOfDirs);

		JLabel label_numberOfDirsDyn = new JLabel((String) null);
		label_numberOfDirsDyn.setBounds(307, 37, 78, 15);
		panel_main.add(label_numberOfDirsDyn);

		JLabel label_numberToCopy = new JLabel(
				ResourceBundle.getBundle("messages").getString("GUI.SummaryDialog.label_numberOfFilesToCopy"));
		label_numberToCopy.setBounds(12, 64, 267, 15);
		panel_main.add(label_numberToCopy);

		JLabel label_numberToCopyDyn = new JLabel((String) null);
		label_numberToCopyDyn.setBounds(307, 64, 78, 15);
		panel_main.add(label_numberToCopyDyn);

		JLabel label_numberToLink = new JLabel(
				ResourceBundle.getBundle("messages").getString("GUI.SummaryDialog.label_numberOfFilesToLink"));
		label_numberToLink.setBounds(12, 91, 267, 15);
		panel_main.add(label_numberToLink);

		JLabel label_numberToLinkDyn = new JLabel("0");
		label_numberToLinkDyn.setBounds(307, 91, 78, 15);
		panel_main.add(label_numberToLinkDyn);

		JLabel label_sizeToCopy = new JLabel(
				ResourceBundle.getBundle("messages").getString("GUI.SummaryDialog.label_sizeToCopy"));
		label_sizeToCopy.setBounds(12, 118, 267, 15);
		panel_main.add(label_sizeToCopy);

		JLabel label_sizeToLink = new JLabel(
				ResourceBundle.getBundle("messages").getString("GUI.SummaryDialog.label_sizeToLink"));
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
		label_numberToCopyDyn.setText(String.valueOf(backup.getBackupInfos().getNumberOfFilesToCopy()).toString());
		label_numberToLinkDyn.setText(String.valueOf(backup.getBackupInfos().getNumberOfFilesToLink()).toString());
		label_numberOfDirsDyn.setText(String.valueOf(backup.getBackupInfos().getNumberOfDirectories()).toString());

		// Größe der zu kopierenden Dateien:
		label_sizeToCopyDyn.setText(formatSize(backup.getBackupInfos().getSizeToCopy()));
		// Größe der zu verlinkenden Dateien:
		label_sizeToLinkDyn.setText(formatSize(backup.getBackupInfos().getSizeToLink()));
	}

	/**
	 * Formatiert die gegeben Größe für die Anzeige im Summary-Dialog.
	 *
	 * @param size Größe (als double)
	 * @return Formatierter String der Größe
	 */
	private String formatSize(double size) {
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		if (size < 1000) {
			return String.valueOf(decimalFormat.format(size)) + "Byte";
		} else if (size < 1000000) {
			return String.valueOf(decimalFormat.format(size / 1000)) + "kB";
		} else if (size < 1000000000) {
			return String.valueOf(decimalFormat.format(size / 1000000)) + "MB";
		} else {
			return String.valueOf(decimalFormat.format(size / 1000000000)) + "GB";
		}
	}

	/**
	 * Bricht den gegebenen BackupTask ab.
	 *
	 * @param task abzubrechender BackupTask
	 */
	private void cancelBackup(BackupTask task) {
		outprintBackupCanceled(task);
		listener.taskFinished(task);
		deleteEmptyBackupFolders(task);
		SummaryDialog.this.dispose();
	}

	/**
	 * Löscht alle leeren Backup-Ordner eines Backuptasks (im Zielverzeichnis).
	 *
	 * @param task entsprechender BackupTask
	 */
	private void deleteEmptyBackupFolders(BackupTask task) {
		listener.deleteEmptyBackupFolders(task);
	}

	private void outprintBackupCanceled(BackupTask task) {
		listener.outprintBackupCanceled(task);
	}
}
