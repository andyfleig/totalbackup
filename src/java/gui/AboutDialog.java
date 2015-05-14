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
