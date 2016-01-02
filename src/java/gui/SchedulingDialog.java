/*
 * Copyright 2014 - 2016 Andreas Fleig (andy DOT fleig AT gmail DOT com)
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
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import listener.ISchedulingDialogListener;
import main.BackupHelper;

import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.awt.event.ActionEvent;

public class SchedulingDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private ISchedulingDialogListener listener;

	/**
	 * Create the dialog.
	 */
	public SchedulingDialog(ISchedulingDialogListener schedulingListener) {
		this.listener = schedulingListener;
		setModal(true);
		setResizable(false);
		setAlwaysOnTop(true);
		setBounds(100, 100, 174, 160);
		setIconImage(Toolkit.getDefaultToolkit().getImage(BackupHelper.ICON_LOCATION));

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		ButtonGroup group = new ButtonGroup();

		JPanel panel = new JPanel();
		contentPanel.add(panel);

		JRadioButton radioButton_skipNext = new JRadioButton(
				ResourceBundle.getBundle("messages").getString("GUI.SchedulingDialog.skipNext"));
		radioButton_skipNext.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(radioButton_skipNext);
		group.add(radioButton_skipNext);

		JRadioButton radioButton_postpone = new JRadioButton(
				ResourceBundle.getBundle("messages").getString("GUI.SchedulingDialog.postpone"));
		radioButton_postpone.setSelected(true);
		radioButton_postpone.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(radioButton_postpone);
		group.add(radioButton_postpone);

		JComboBox comboBox = new JComboBox();
		comboBox.setPreferredSize(new Dimension(80, 25));
		comboBox.setMinimumSize(comboBox.getPreferredSize());
		comboBox.setMaximumSize(comboBox.getPreferredSize());
		comboBox.setModel(new DefaultComboBoxModel(new String[]{"5min", "15min", "1h", "2h", "6h", "12h", "24h"}));
		contentPanel.add(comboBox);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (radioButton_postpone.isSelected()) {
					LocalDateTime nextExecutionTime = LocalDateTime.now();
					switch (comboBox.getSelectedItem().toString()) {
						case "5min":
							nextExecutionTime = nextExecutionTime.plusMinutes(5);
							break;
						case "15min":
							nextExecutionTime = nextExecutionTime.plusMinutes(15);
							break;
						case "1h":
							nextExecutionTime = nextExecutionTime.plusHours(1);
							break;
						case "2h":
							nextExecutionTime = nextExecutionTime.plusHours(2);
							break;
						case "12h":
							nextExecutionTime = nextExecutionTime.plusHours(12);
							break;
						case "24h":
							nextExecutionTime = nextExecutionTime.plusDays(1);
							break;
					}
					listener.scheduleBackup(nextExecutionTime);
				} else {
					listener.rescheduleTask();
				}
				SchedulingDialog.this.dispose();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

	}
}
