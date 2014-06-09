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
	private JLabel label;
	private JButton okButton;
	private JPanel panel;
	private JLabel lblVAlpha;
	private JPanel panel_1;
	private JTextPane txtpnWarnungDieSoftware;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			About dialog = new About();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				okButton = new JButton(ResourceBundle.getBundle("gui.messages").getString("About.okButton.text")); //$NON-NLS-1$ //$NON-NLS-2$
				panel.add(okButton, BorderLayout.EAST);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				okButton.setAction(action);
				okButton.setActionCommand(ResourceBundle.getBundle("gui.messages").getString("About.okButton.actionCommand")); //$NON-NLS-1$ //$NON-NLS-2$
				getRootPane().setDefaultButton(okButton);
			}
			{
				panel_1 = new JPanel();
				panel.add(panel_1, BorderLayout.WEST);
				panel_1.setLayout(new BorderLayout(0, 0));
				{
					label = new JLabel(ResourceBundle.getBundle("gui.messages").getString("About.label.text")); //$NON-NLS-1$ //$NON-NLS-2$
					panel_1.add(label, BorderLayout.SOUTH);
					label.setVerticalAlignment(SwingConstants.BOTTOM);
					label.setHorizontalAlignment(SwingConstants.CENTER);
				}
				{
					lblVAlpha = new JLabel(ResourceBundle.getBundle("gui.messages").getString("About.lblVAlpha.text")); //$NON-NLS-1$ //$NON-NLS-2$
					panel_1.add(lblVAlpha, BorderLayout.NORTH);
					lblVAlpha.setHorizontalAlignment(SwingConstants.LEFT);
				}
			}
		}
		{
			txtpnWarnungDieSoftware = new JTextPane();
			txtpnWarnungDieSoftware.setForeground(Color.RED);
			txtpnWarnungDieSoftware.setText(ResourceBundle.getBundle("gui.messages").getString("About.txtpnWarnungDieSoftware.text")); //$NON-NLS-1$ //$NON-NLS-2$
			contentPanel.add(txtpnWarnungDieSoftware, BorderLayout.CENTER);
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
