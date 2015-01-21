package gui;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

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
	public PreparingDialog(IPreparingDialogListener listener) {
		setSize(220, 80);
		setResizable(false);
		this.listener = listener;
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		JLabel lbl_preparing = new JLabel(ResourceBundle.getBundle("gui.messages").getString("Preparing.lbl_preparing.text"));
		panel.add(lbl_preparing);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton(ResourceBundle.getBundle("gui.messages").getString("Preparing.btn_cancel.text"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelBackup();
			}
		});
		panel_1.add(btnNewButton);

	}
	
	private void cancelBackup() {
		listener.cancelBackup();
	}
}
