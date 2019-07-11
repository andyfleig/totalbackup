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

import listener.INextExecutionChooserListener;
import main.BackupHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class NextExecutionChooser extends JDialog {
	private INextExecutionChooserListener listener;

	private JPanel contentPane = new JPanel();
	private JButton btn_ok = new JButton("OK");
	private JLabel label_message = new JLabel();
	private JRadioButton rButton_tryAgain = new JRadioButton();
	private JRadioButton rButton_skip = new JRadioButton();
	private JRadioButton rButton_postpone = new JRadioButton();
	private JPanel panel_rbuttons = new JPanel();
	private JPanel panel_postponeOptions = new JPanel();
	private JPanel panel_options = new JPanel();
	private JPanel panel_button = new JPanel();
	private JComboBox comboBox_postpone = new JComboBox();
	private ButtonGroup group = new ButtonGroup();

	public NextExecutionChooser(INextExecutionChooserListener l) {
		this.listener = l;

		contentPane.setLayout(new BorderLayout());
		getContentPane().add(contentPane, BorderLayout.NORTH);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setContentPane(contentPane);
		setAlwaysOnTop(true);
		setBounds(100, 100, 550, 210);
		setIconImage(Toolkit.getDefaultToolkit().getImage(BackupHelper.TB_LOGO));

		panel_options.setLayout(new BorderLayout());
		panel_postponeOptions.setLayout(new BorderLayout());
		panel_options.add(panel_rbuttons, BorderLayout.NORTH);
		panel_options.add(panel_postponeOptions, BorderLayout.SOUTH);

		getContentPane().add(label_message, BorderLayout.NORTH);
		getContentPane().add(panel_options, BorderLayout.CENTER);
		panel_button.setLayout(new FlowLayout());
		panel_button.add(btn_ok);
		getContentPane().add(panel_button, BorderLayout.SOUTH);

		label_message.setText("Destination not found. How to proceed?");

		panel_rbuttons.setLayout(new BoxLayout(panel_rbuttons, BoxLayout.Y_AXIS));
		panel_rbuttons.add(rButton_tryAgain);
		panel_rbuttons.add(rButton_skip);
		panel_rbuttons.add(rButton_postpone);

		rButton_tryAgain.setText("try again");
		rButton_tryAgain.setSelected(true);
		rButton_skip.setText("skip");
		rButton_skip.setSelected(false);
		rButton_postpone.setText("postpone");
		rButton_postpone.setSelected(false);

		group.add(rButton_tryAgain);
		group.add(rButton_skip);
		group.add(rButton_postpone);

		comboBox_postpone.setPreferredSize(new Dimension(80, 25));
		comboBox_postpone.setMinimumSize(comboBox_postpone.getPreferredSize());
		comboBox_postpone.setMaximumSize(comboBox_postpone.getPreferredSize());
		comboBox_postpone.setModel(new DefaultComboBoxModel(new String[]{"2min", "5min", "10min", "15min", "20min", "30min", "60min"}));
		comboBox_postpone.setVisible(false);

		panel_postponeOptions.add(comboBox_postpone, BorderLayout.WEST);

		getRootPane().setDefaultButton(btn_ok);

		rButton_tryAgain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				comboBox_postpone.setVisible(false);
			}
		});
		rButton_skip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				comboBox_postpone.setVisible(false);
			}
		});
		rButton_postpone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				comboBox_postpone.setVisible(true);
			}
		});

		btn_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
	}

	private void onOK() {
		if (rButton_tryAgain.isSelected()) {
			listener.retry();
		} else if (rButton_skip.isSelected()) {
			listener.skipBackup();
		} else {
			String timeToPostpone = comboBox_postpone.getItemAt(comboBox_postpone.getSelectedIndex()).toString();
			String sub = timeToPostpone.substring(0, (timeToPostpone.length() - 3));


			int minutesToPostpone = Integer.valueOf(sub);
			listener.postponeBackupTo(LocalDateTime.now().plus(minutesToPostpone, ChronoUnit.MINUTES));
		}
		NextExecutionChooser.this.dispose();
	}
}
