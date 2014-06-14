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
import java.awt.event.ActionListener;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JTextPane;
import java.util.ResourceBundle;

public class About extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final Action action = new SA_ok();
	private JLabel lbl_Copyright;
	private JButton btn_Ok;
	private JPanel panel;
	private JLabel lbl_Version;
	private JPanel panel_1;
	private JTextPane txt_About;


	/**
	 * @deprecated
	 */
	public static void main(String[] args) {
		/*
		try {
			About dialog = new About();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}

	/**
	 * Create the dialog.
	 */
	public About() {
		setResizable(false);
		setTitle(ResourceBundle.getBundle("gui.messages").getString("About.this.title")); //$NON-NLS-1$ //$NON-NLS-2$
		setModal(true);
		setAlwaysOnTop(true);
		setBounds(100, 100, 365, 147);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			panel = new JPanel();
			contentPanel.add(panel, BorderLayout.SOUTH);
			panel.setLayout(new BorderLayout(0, 0));
			{
				btn_Ok = new JButton(ResourceBundle.getBundle("gui.messages").getString("About.okButton.text")); //$NON-NLS-1$ //$NON-NLS-2$
				panel.add(btn_Ok, BorderLayout.EAST);
				btn_Ok.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				btn_Ok.setAction(action);
				btn_Ok.setActionCommand(ResourceBundle.getBundle("gui.messages").getString("About.okButton.actionCommand")); //$NON-NLS-1$ //$NON-NLS-2$
				getRootPane().setDefaultButton(btn_Ok);
			}
			{
				panel_1 = new JPanel();
				panel.add(panel_1, BorderLayout.WEST);
				panel_1.setLayout(new BorderLayout(0, 0));
				{
					lbl_Copyright = new JLabel(ResourceBundle.getBundle("gui.messages").getString("About.label.text")); //$NON-NLS-1$ //$NON-NLS-2$
					panel_1.add(lbl_Copyright, BorderLayout.SOUTH);
					lbl_Copyright.setVerticalAlignment(SwingConstants.BOTTOM);
					lbl_Copyright.setHorizontalAlignment(SwingConstants.CENTER);
				}
				{
					lbl_Version = new JLabel(ResourceBundle.getBundle("gui.messages").getString("About.lblVAlpha.text")); //$NON-NLS-1$ //$NON-NLS-2$
					panel_1.add(lbl_Version, BorderLayout.NORTH);
					lbl_Version.setHorizontalAlignment(SwingConstants.LEFT);
				}
			}
		}
		{
			txt_About = new JTextPane();
			txt_About.setForeground(Color.RED);
			txt_About.setText(ResourceBundle.getBundle("gui.messages").getString("About.txtpnWarnungDieSoftware.text")); //$NON-NLS-1$ //$NON-NLS-2$
			contentPanel.add(txt_About, BorderLayout.CENTER);
		}
	}

	private class SA_ok extends AbstractAction {
		public SA_ok() {
			putValue(NAME, "OK");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			About.this.dispose();
		}
	}
}
