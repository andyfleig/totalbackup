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
		JLabel label_preparing = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.PreparingDialog.label_preparing"));
		panel_message.add(label_preparing);

		JPanel panel_button = new JPanel();
		getContentPane().add(panel_button, BorderLayout.SOUTH);

		JButton button_cancelBackup = new JButton(ResourceBundle.getBundle("gui.messages").getString(
				"GUI.PreparingDialog.button_cancelBackup"));
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
