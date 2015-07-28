package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import listener.ISchedulingDialogListener;

import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Component;
import java.awt.Dimension;
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
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		ButtonGroup group = new ButtonGroup();
		
		JPanel panel = new JPanel();
		contentPanel.add(panel);
		
				JRadioButton radioButton_skipNext = new JRadioButton(
						ResourceBundle.getBundle("gui.messages").getString("GUI.SchedulingDialog.skipNext"));
				radioButton_skipNext.setHorizontalAlignment(SwingConstants.CENTER);
				panel.add(radioButton_skipNext);
				group.add(radioButton_skipNext);
				
						JRadioButton radioButton_postpone = new JRadioButton(
								ResourceBundle.getBundle("gui.messages").getString("GUI.SchedulingDialog.postpone"));
						radioButton_postpone.setSelected(true);
						radioButton_postpone.setHorizontalAlignment(SwingConstants.CENTER);
						panel.add(radioButton_postpone);
						group.add(radioButton_postpone);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setPreferredSize(new Dimension(80, 25));
		comboBox.setMinimumSize(comboBox.getPreferredSize());
		comboBox.setMaximumSize(comboBox.getPreferredSize());
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"5min", "15min", "1h", "2h", "6h", "12h", "24h"}));
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
