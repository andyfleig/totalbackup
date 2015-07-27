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

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

import data.BackupTask;

import java.awt.BorderLayout;
import java.util.ResourceBundle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import listener.IPreparingDialogListener;

/**
 * Minimaler Dialog welcher wärend der Preparation angezeigt wird und die
 * Möglichkeit zum Abbruch des Backups bietet.
 * 
 * @author Andreas Fleig
 *
 */
public class PreparingDialog extends JDialog {

	private IPreparingDialogListener listener;

	/**
	 * Create the panel.
	 */
	public PreparingDialog(IPreparingDialogListener listener, final BackupTask task) {
		setSize(220, 80);
		setResizable(false);
		this.listener = listener;

		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel_message = new JPanel();
		getContentPane().add(panel_message, BorderLayout.NORTH);
		JLabel label_preparing = new JLabel(
				ResourceBundle.getBundle("gui.messages").getString("GUI.PreparingDialog.label_preparing"));
		panel_message.add(label_preparing);

		JPanel panel_button = new JPanel();
		getContentPane().add(panel_button, BorderLayout.SOUTH);

		JButton button_cancelBackup = new JButton(
				ResourceBundle.getBundle("gui.messages").getString("GUI.PreparingDialog.button_cancelBackup"));
		button_cancelBackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelBackup(task.getTaskName());
			}
		});
		panel_button.add(button_cancelBackup);
	}

	private void cancelBackup(String taskName) {
		listener.cancelBackup(taskName);
	}
}
