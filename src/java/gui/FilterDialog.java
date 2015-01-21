package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
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

import javax.swing.JRadioButton;

import listener.IFilterDialogListener;

public class FilterDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField tf_filter;

	private IFilterDialogListener listener;
	/**
	 * Legt fest, ob gerade ein existierender Filter bearbeitet, oder ein neuer
	 * erzeugt wird.
	 */
	private boolean inEditMode;
	/**
	 * Speichert den Originalpfad der Qulle.
	 */
	private String originalPath;
	// TODO: JavaDoc
	private JRadioButton rBtn_excludeFilter;
	private JRadioButton rBtn_useMD5;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FilterDialog dialog = new FilterDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public FilterDialog(IFilterDialogListener listener) {
		setTitle(ResourceBundle.getBundle("gui.messages").getString("Filter.title"));
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.listener = listener;
		setBounds(100, 100, 462, 148);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.NORTH);

			rBtn_excludeFilter = new JRadioButton(ResourceBundle.getBundle("gui.messages").getString(
					"Filter.rBtnexcludeFilter.text"));
			rBtn_excludeFilter.setSelected(true);
			rBtn_excludeFilter.setToolTipText(ResourceBundle.getBundle("gui.messages").getString("Filter.excludeToolTip"));
			panel.add(rBtn_excludeFilter);
			rBtn_useMD5 = new JRadioButton(ResourceBundle.getBundle("gui.messages").getString("Filter.rBtnuseMD5.text"));
			rBtn_useMD5.setToolTipText(ResourceBundle.getBundle("gui.messages").getString("Filter.md5ToolTip"));
			panel.add(rBtn_useMD5);

			ButtonGroup rBtnGroup = new ButtonGroup();
			rBtnGroup.add(rBtn_excludeFilter);
			rBtnGroup.add(rBtn_useMD5);
		}
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
						// Unterscheidung der verschiedenen Filter:
						if (rBtn_excludeFilter.isSelected()) {
							addFilter(tf_filter.getText(), 0);
						} else if (rBtn_useMD5.isSelected()) {
							addFilter(tf_filter.getText(), 1);
						}
						FilterDialog.this.dispose();
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
						FilterDialog.this.dispose();
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
	 * @param mode
	 *            Filter-Art - 0 = Exclusion-Filter; 1 = MD5Filter
	 */
	private void addFilter(String filter, int mode) {
		if (mode == 0) {
			listener.addFilter(filter, 0);
		} else if (mode == 1) {
			listener.addFilter(filter, 1);
		}
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
	
	/**
	 * Legt den gegebenen Modus in der GUI fest.
	 * @param mode festzulegender Modus
	 */
	public void setMode(int mode) {
		if (mode == 0) {
			rBtn_excludeFilter.setSelected(true);
			rBtn_useMD5.setSelected(false);
		} else if (mode == 1) {
			rBtn_excludeFilter.setSelected(false);
			rBtn_useMD5.setSelected(true);
		}
	}
}
