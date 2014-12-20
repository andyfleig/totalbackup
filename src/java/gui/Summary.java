package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Summary extends JDialog {

	private ISummaryListener listener;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Summary dialog = new Summary();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Summary(ISummaryListener listener) {
		this.listener = listener;
		setResizable(false);
		setAlwaysOnTop(true);
		setBounds(100, 100, 524, 370);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		{
			JLabel lbl_taskName = new JLabel("Aktueller Backup-Task:");
			lbl_taskName.setBounds(12, 10, 267, 15);
			contentPanel.add(lbl_taskName);
		}
		
		JLabel lbl_taskNameDyn = new JLabel("");
		lbl_taskNameDyn.setBounds(307, 10, 78, 15);
		contentPanel.add(lbl_taskNameDyn);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Start");
				okButton.addActionListener(new ActionListener() {
					// Button Start:
					public void actionPerformed(ActionEvent arg0) {
						Summary.this.listener.startBackup();
						clearBackupInfos();
						Summary.this.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Abbrechen");
				cancelButton.addActionListener(new ActionListener() {
					// Button Cancel:
					public void actionPerformed(ActionEvent e) {
						clearBackupInfos();
						Summary.this.dispose();
					}
				});
				//TODO: angelegten Backup-Ordner löschen
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		JLabel lbl_numberOfDirs = new JLabel("Anzahl der zu kopierenden Ordner:");
		lbl_numberOfDirs.setBounds(12, 37, 267, 15);
		contentPanel.add(lbl_numberOfDirs);

		JLabel lbl_numberOfDirsDyn = new JLabel((String) null);
		lbl_numberOfDirsDyn.setBounds(307, 37, 78, 15);
		contentPanel.add(lbl_numberOfDirsDyn);

		JLabel lbl_numberOfFiles = new JLabel("Anzahl der zu kopierenden Dateien:");
		lbl_numberOfFiles.setBounds(12, 64, 267, 15);
		contentPanel.add(lbl_numberOfFiles);

		JLabel lbl_numberOfFilesDyn = new JLabel((String) null);
		lbl_numberOfFilesDyn.setBounds(307, 64, 78, 15);
		contentPanel.add(lbl_numberOfFilesDyn);
		
		JLabel lbl_sizeToCopy = new JLabel("Größe der zu kopierenden Dateien:");
		lbl_sizeToCopy.setBounds(12, 91, 267, 15);
		contentPanel.add(lbl_sizeToCopy);
		
		JLabel lbl_sizeToLink = new JLabel("Größe der zu verlinkenden Dateien:");
		lbl_sizeToLink.setBounds(12, 118, 267, 15);
		contentPanel.add(lbl_sizeToLink);
		
		JLabel lbl_sizeToCopyDyn = new JLabel("0");
		lbl_sizeToCopyDyn.setBounds(307, 91, 78, 15);
		contentPanel.add(lbl_sizeToCopyDyn);
		
		JLabel lbl_sizeToLinkDyn = new JLabel("0");
		lbl_sizeToLinkDyn.setBounds(307, 118, 78, 15);
		contentPanel.add(lbl_sizeToLinkDyn);
		
		
		
		
		
		//Inhalte hinzufügen:
		lbl_taskNameDyn.setText(listener.getTaskName());
		lbl_numberOfFilesDyn.setText(String.valueOf(listener.getNumberOfFiles()).toString());
		lbl_numberOfDirsDyn.setText(String.valueOf(listener.getNumberOfDirectories()).toString());
		
		//Größe der zu kopierenden Dateien:
		double size = listener.getSizeToCopy();
		if (size < 1000) {
			String result = String.valueOf(listener.getSizeToCopy()) + "Byte";
			lbl_sizeToCopyDyn.setText(result);
		} else if (size < 1000000) {
			String result = String.valueOf(listener.getSizeToCopy()/1000) + "kB";
			lbl_sizeToCopyDyn.setText(result);
		} else if (size < 1000000000) {
			String result = String.valueOf(listener.getSizeToCopy()/1000000) + "MB";
			lbl_sizeToCopyDyn.setText(result);
		} else {
			String result = String.valueOf(listener.getSizeToCopy()/1000000000) + "GB";
			lbl_sizeToCopyDyn.setText(result);
		}
		
		
		//Größe der zu kopierenden Dateien:
				size = listener.getSizeToLink();
				if (size < 1000) {
					String result = String.valueOf(listener.getSizeToLink()) + "Byte";
					lbl_sizeToLinkDyn.setText(result);
				} else if (size < 1000000) {
					String result = String.valueOf(listener.getSizeToLink()/1000) + "kB";
					lbl_sizeToLinkDyn.setText(result);
				} else if (size < 1000000000) {
					String result = String.valueOf(listener.getSizeToLink()/1000000) + "MB";
					lbl_sizeToLinkDyn.setText(result);
				} else {
					String result = String.valueOf(listener.getSizeToLink()/1000000000) + "GB";
					lbl_sizeToLinkDyn.setText(result);
				}
	}
	
	private void clearBackupInfos() {
		listener.clearBackupInfos();
	}
}
