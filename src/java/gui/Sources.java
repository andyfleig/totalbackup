package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

public class Sources extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField tf_source;
	private JList<String> list_SourcePaths;
	private DefaultListModel<String> listModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Sources dialog = new Sources();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Sources() {
		setTitle("Quellen");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.NORTH);
			panel.setLayout(new BorderLayout(0, 0));
			{
				JLabel lbl_source = new JLabel("Quellpfad:");
				panel.add(lbl_source, BorderLayout.WEST);
			}
			{
				tf_source = new JTextField();
				panel.add(tf_source);
				tf_source.setColumns(10);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			
			listModel = new DefaultListModel<String>();
			list_SourcePaths = new JList<String>(listModel);
			list_SourcePaths.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list_SourcePaths.setSelectedIndex(0);
			list_SourcePaths.setVisibleRowCount(6);
			JScrollPane listScroller = new JScrollPane(list_SourcePaths);
			panel.add(listScroller);
			listScroller.setMaximumSize(new Dimension(200, 200));
			listScroller.setMinimumSize(new Dimension(200, 200));
			{
			
			{
				JLabel lbl_filter = new JLabel("Filter:");
				panel.add(lbl_filter, BorderLayout.NORTH);
			}
		}
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1, BorderLayout.EAST);
				panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
				{
					JButton button = new JButton("Hinzufügen");
					button.setAlignmentX(0.5f);
					panel_1.add(button);
				}
				{
					JButton button = new JButton("Bearbeiten");
					button.setAlignmentX(0.5f);
					panel_1.add(button);
				}
				{
					JButton button = new JButton("Löschen");
					button.setAlignmentX(0.5f);
					panel_1.add(button);
				}
			}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	}	
}
