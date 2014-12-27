package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class Filter extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField tf_filter;

	private IFilterListener listener;
	private boolean inEditMode;
	private String originalPath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Filter dialog = new Filter(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Filter(IFilterListener listener) {
		this.listener = listener;
		setBounds(100, 100, 400, 116);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			tf_filter = new JTextField();
			contentPanel.add(tf_filter);
			tf_filter.setColumns(10);
		}
		{
			JLabel lbl_source = new JLabel(ResourceBundle.getBundle("gui.messages").getString(
					"Mainframe.lblQuelle.text"));
			contentPanel.add(lbl_source, BorderLayout.NORTH);
		}
		{
			// Button Durchsuchen:
			JButton btn_Find = new JButton(ResourceBundle.getBundle("gui.messages").getString("Edit.btn_Find.text"));
			btn_Find.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					int state = fc.showOpenDialog(null);
					if (state == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fc.getSelectedFile();
						if (isUnderSourceRoot(selectedFile.getAbsolutePath())) {
							tf_filter.setText(selectedFile.getAbsolutePath());
						} else {
							JOptionPane.showMessageDialog(null,
									ResourceBundle.getBundle("gui.messages").getString("Filter.ErrNotUnderSourceRoot"),
									ResourceBundle.getBundle("gui.messages").getString("Edit.ErrMsg"),
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			});
			contentPanel.add(btn_Find, BorderLayout.EAST);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				// Button OK:
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//Pfad auf gültigkeit Prüfen:
						if (!(new File(tf_filter.getText()).exists())) {
							return;
						}
						if (inEditMode) {
							deleteFilter(originalPath);
						}
						
						addFilter(tf_filter.getText());
						Filter.this.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				// Button Abbrechen:
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Filter.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	// TODO: JavaDoc
	private void addFilter(String filter) {
		listener.addFilter(filter);
	}

	private boolean isUnderSourceRoot(String path) {
		return listener.isUnderSourceRoot(path);
	}
	
	public void setFilter(String filter) {
		tf_filter.setText(filter);
	}
	
	public void setEditMode(boolean editMode) {
		this.inEditMode = editMode;
	}
	
	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}
	private void deleteFilter(String path) {
		listener.deleteFilter(originalPath);
	}
}
