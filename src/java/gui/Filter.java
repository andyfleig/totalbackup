package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class Filter extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField tf_filter;

	private IFilterListener listener;
	/**
	 * Legt fest, ob gerade ein existierender Filter bearbeitet, oder ein neuer
	 * erzeugt wird.
	 */
	private boolean inEditMode;
	/**
	 * Speichert den Originalpfad der Qulle.
	 */
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
		setTitle(ResourceBundle.getBundle("gui.messages").getString("Filter.title"));
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
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
					// Beschränkung des FC auf den Sourceroot:
					FileSystemView fsv = new DirectoryRestrictedFileSystemView(getSourceFile());

					JFileChooser fc = new JFileChooser(fsv.getHomeDirectory(), fsv);
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
				JButton okButton = new JButton(ResourceBundle.getBundle("gui.messages").getString("Edit.btn_Ok.text"));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Pfad auf gültigkeit Prüfen:
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
				JButton cancelButton = new JButton(ResourceBundle.getBundle("gui.messages").getString(
						"Summary.btn_cancel"));
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

	/**
	 * Fügt einen Filter zur Liste der Filter hinzu.
	 * 
	 * @param filter
	 *            hinzuzufügender Filter
	 */
	private void addFilter(String filter) {
		listener.addFilter(filter);
	}

	/**
	 * Prüft ob der gegebene Pfad unter dem Rootpfad der gewählten Quelle ist.
	 * 
	 * @param path
	 *            zu prüfender Pfad
	 * @return ob der gegebene Pfad unter dem Rootpfad der Quelle ist
	 */
	private boolean isUnderSourceRoot(String path) {
		return listener.isUnderSourceRoot(path);
	}

	/**
	 * Gibt die Quelldatei zurück.
	 * 
	 * @return Quelldatei
	 */
	private File getSourceFile() {
		return listener.getSourceFile();
	}

	/**
	 * Setzt das Textfeld für den Filter-Pfad auf den gegebenen Pfad.
	 * 
	 * @param filter
	 *            Pfad des Filters
	 */
	public void setFilter(String filter) {
		tf_filter.setText(filter);
	}

	/**
	 * Schaltet den EditMode an bzw. aus.
	 * 
	 * @param editMode
	 *            true = an, false = aus
	 */
	public void setEditMode(boolean editMode) {
		this.inEditMode = editMode;
	}

	/**
	 * Legt den Originalpfad fest.
	 * 
	 * @param originalPath
	 *            festzulegender Originalpfad
	 */
	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}

	/**
	 * Durchsucht die Liste der Filter nach dem gegebenen Pfad. Wird ein Filter
	 * mit diesem Pfad gefunden wird dieser gelöscht.
	 * 
	 * @param path
	 *            zu löschender Filterpfad
	 */
	private void deleteFilter(String path) {
		listener.deleteFilter(originalPath);
	}
}
