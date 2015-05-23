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

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import java.util.ResourceBundle;

/**
 * Über-Dialog mit Informationen zu TotalBackup.
 * 
 * @author Andreas Fleig
 *
 */
public class AboutDialog extends JDialog {

	private final JPanel panel_main = new JPanel();
	private final Action action = new SA_ok();
	private JButton button_ok;
	private JLabel label_about;

	/**
	 * @deprecated
	 */
	public static void main(String[] args) {
		/*
		 * try { About dialog = new About();
		 * dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		 * dialog.setVisible(true); } catch (Exception e) { e.printStackTrace();
		 * }
		 */
	}

	/**
	 * Create the dialog.
	 */
	public AboutDialog() {
		setResizable(false);
		setTitle(ResourceBundle.getBundle("gui.messages").getString("GUI.AboutDialog.title"));
		setModal(true);
		setAlwaysOnTop(true);
		setBounds(100, 100, 479, 532);
		getContentPane().setLayout(new BorderLayout());
		panel_main.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel_main, BorderLayout.CENTER);
		panel_main.setLayout(new BorderLayout(0, 0));

		label_about = new JLabel();
		label_about.setText(ResourceBundle.getBundle("gui.messages").getString("GUI.AboutDialog.text"));
		panel_main.add(label_about, BorderLayout.NORTH);

		button_ok = new JButton(ResourceBundle.getBundle("gui.messages").getString("GUI.button_ok"));
		panel_main.add(button_ok, BorderLayout.SOUTH);
		button_ok.setAction(action);
		button_ok.setActionCommand(ResourceBundle.getBundle("gui.messages").getString("GUI.button_ok"));
		getRootPane().setDefaultButton(button_ok);

	}

	private class SA_ok extends AbstractAction {
		public SA_ok() {
			putValue(NAME, "OK");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			AboutDialog.this.dispose();
		}
	}
}
