package gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

import java.awt.Color;
import java.io.File;

import javax.swing.JTextPane;

import java.util.ResourceBundle;

public class AboutDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final Action action = new SA_ok();
	private JButton btn_Ok;
	private JLabel txt_About;

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
		setTitle(ResourceBundle.getBundle("gui.messages").getString("About.this.title"));
		setModal(true);
		setAlwaysOnTop(true);
		setBounds(100, 100, 479, 589);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));

		txt_About = new JLabel();
		txt_About.setText(ResourceBundle.getBundle("gui.messages").getString("About.txtpnWarnungDieSoftware.text"));
		contentPanel.add(txt_About, BorderLayout.CENTER);

		btn_Ok = new JButton(ResourceBundle.getBundle("gui.messages").getString("About.okButton.text"));
		contentPanel.add(btn_Ok, BorderLayout.SOUTH);
		btn_Ok.setAction(action);
		btn_Ok.setActionCommand(ResourceBundle.getBundle("gui.messages").getString("About.okButton.actionCommand"));
		getRootPane().setDefaultButton(btn_Ok);

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
