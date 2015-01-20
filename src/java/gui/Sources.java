package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import main.Filter;
import main.Source;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ResourceBundle;

public class Sources extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8855971977478046562L;
	private final JPanel contentPanel = new JPanel();
	private JTextField tf_source;
	private JList<Filter> list_Filter;
	private DefaultListModel<Filter> listModel;

	private ISourcesListener sourcesListener;
	private FilterDialog filterDialog;

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
			Sources dialog = new Sources(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Sources(ISourcesListener sourcesListener) {
		setResizable(false);
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.sourcesListener = sourcesListener;
		setTitle(ResourceBundle.getBundle("gui.messages").getString("Sources.title"));
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
				JLabel lbl_source = new JLabel(ResourceBundle.getBundle("gui.messages").getString("Sources.sourcePath"));
				panel.add(lbl_source, BorderLayout.WEST);
			}
			{
				tf_source = new JTextField();
				panel.add(tf_source);
				tf_source.setColumns(10);
			}
			{
				// Button Druchsuchen:
				JButton button = new JButton(ResourceBundle.getBundle("gui.messages").getString("Edit.btn_Find.text"));
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fc = new JFileChooser();
						fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						int state = fc.showOpenDialog(null);

						if (state == JFileChooser.APPROVE_OPTION) {
							File sourceFile = fc.getSelectedFile();
							if (!isAlreadySourcePath(sourceFile.getAbsolutePath())) {
								tf_source.setText(sourceFile.getAbsolutePath());
							} else {
								JOptionPane.showMessageDialog(null,
										ResourceBundle.getBundle("gui.messages").getString("Edit.ErrSamePath"),
										ResourceBundle.getBundle("gui.messages").getString("Edit.ErrMsg"),
										JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}
				});
				button.setAlignmentX(0.5f);
				panel.add(button, BorderLayout.EAST);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));

			listModel = new DefaultListModel<Filter>();
			list_Filter = new JList<Filter>(listModel);
			list_Filter.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list_Filter.setSelectedIndex(0);
			list_Filter.setVisibleRowCount(6);
			JScrollPane listScroller = new JScrollPane(list_Filter);
			panel.add(listScroller);
			listScroller.setMaximumSize(new Dimension(200, 200));
			listScroller.setMinimumSize(new Dimension(200, 200));
			{

				{
					JLabel lbl_filter = new JLabel(ResourceBundle.getBundle("gui.messages").getString("Sources.filter"));
					panel.add(lbl_filter, BorderLayout.NORTH);
				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1, BorderLayout.EAST);
				panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
				{
					// Button Add:
					JButton button = new JButton(ResourceBundle.getBundle("gui.messages")
							.getString("Edit.btn_Add.text"));
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							filterDialog = new FilterDialog(new IFilterDialogListener() {

								@Override
								public void addFilter(String path, int mode) {
									listModel.addElement(new Filter(path, mode));
								}

								@Override
								public boolean isUnderSourceRoot(String path) {
									return Sources.this.isUnderSourceRoot(path);
								}

								@Override
								public void deleteFilter(String path) {
									Sources.this.deleteFilter(path);
								}

								@Override
								public File getSourceFile() {
									return Sources.this.getSourceFile();
								}
							});
							filterDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
							filterDialog.setLocation(Sources.this.getLocationOnScreen());
							filterDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
							filterDialog.setVisible(true);
						}
					});
					button.setAlignmentX(0.5f);
					panel_1.add(button);
				}
				{
					// Button Bearbeiten:
					JButton button = new JButton(ResourceBundle.getBundle("gui.messages").getString(
							"Mainframe.btnBearbeiten.text"));
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (list_Filter.isSelectionEmpty()) {
								return;
							}

							filterDialog = new FilterDialog(new IFilterDialogListener() {

								@Override
								public void addFilter(String path, int mode) {
									listModel.addElement(new Filter(path, mode));
								}

								@Override
								public boolean isUnderSourceRoot(String path) {
									return Sources.this.isUnderSourceRoot(path);
								}

								@Override
								public void deleteFilter(String path) {
									Sources.this.deleteFilter(path);
								}

								@Override
								public File getSourceFile() {
									return Sources.this.getSourceFile();
								}
							});
							filterDialog.setFilter(listModel.get(list_Filter.getSelectedIndex()).getPath());
							filterDialog.setEditMode(true);
							filterDialog.setOriginalPath(list_Filter.getSelectedValue().getPath());
							filterDialog.setMode(listModel.get(list_Filter.getSelectedIndex()).getMode());

							filterDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
							filterDialog.setLocation(Sources.this.getLocationOnScreen());
							filterDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
							filterDialog.setVisible(true);
						}
					});
					button.setAlignmentX(0.5f);
					panel_1.add(button);
				}
				{
					JButton button = new JButton(ResourceBundle.getBundle("gui.messages").getString(
							"Edit.btn_Delete.text"));
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int reply = JOptionPane.showConfirmDialog(null, ResourceBundle.getBundle("gui.messages")
									.getString("Messages.DeleteFilter"), null, JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION) {
								listModel.remove(list_Filter.getSelectedIndex());
							}
						}
					});
					button.setAlignmentX(0.5f);
					panel_1.add(button);
				}
			}
			{
				JPanel buttonPane = new JPanel();
				buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
				getContentPane().add(buttonPane, BorderLayout.SOUTH);
				{
					// Button OK:
					JButton okButton = new JButton(ResourceBundle.getBundle("gui.messages").getString(
							"Edit.btn_Ok.text"));
					okButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// Quellpfad prüfen:
							File source = new File(tf_source.getText());
							if (!source.exists()) {
								JOptionPane.showMessageDialog(null,
										ResourceBundle.getBundle("gui.messages").getString("Sources.ErrIllegalSource"),
										ResourceBundle.getBundle("gui.messages").getString("Edit.ErrMsg"),
										JOptionPane.INFORMATION_MESSAGE);
								return;
							}

							// Prüfen ob der gewählte Pfad bereits Quellpfad
							// ist:
							if (isAlreadySourcePath(tf_source.getText()) && !inEditMode) {
								JOptionPane.showMessageDialog(null,
										ResourceBundle.getBundle("gui.messages").getString("Edit.ErrSamePath"),
										ResourceBundle.getBundle("gui.messages").getString("Edit.ErrMsg"),
										JOptionPane.INFORMATION_MESSAGE);
								return;
							}

							// Quellobjekt erzeugen und hinzufügen:
							Source newSource = new Source(tf_source.getText());

							// Filter hinzufügen:
							for (int i = 0; i < listModel.getSize(); i++) {
								newSource.addFilter(listModel.get(i));
							}

							if (inEditMode) {
								deleteSource(originalPath);
							}

							addSource(newSource);
							Sources.this.dispose();

						}
					});
					okButton.setActionCommand("OK");
					buttonPane.add(okButton);
					getRootPane().setDefaultButton(okButton);
				}
				{
					JButton cancelButton = new JButton(ResourceBundle.getBundle("gui.messages").getString(
							"Summary.btn_cancel"));
					cancelButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Sources.this.dispose();
						}
					});
					cancelButton.setActionCommand("Cancel");
					buttonPane.add(cancelButton);
				}
			}
		}

	}

	/**
	 * Prüft für den gegebenen String ob dieser bereits Quellpfad ist.
	 * 
	 * @param path
	 *            zu prüfender Quellpfad
	 * @return ob der Pfad bereits Quellpfad ist
	 */
	private boolean isAlreadySourcePath(String path) {
		return sourcesListener.isAlreadySourcePath(path);
	}

	/**
	 * Fügt eine Quelle hinzu.
	 * 
	 * @param source
	 *            hinzuzufügende Quelle.
	 */
	private void addSource(Source source) {
		sourcesListener.addSource(source);
	}

	/**
	 * Sucht nach einer Quelle mit dem gegebenen Pfad. Wird diese gefunden wird
	 * sie gelöscht.
	 * 
	 * @param path
	 *            zu löschende Quelle
	 */
	public void deleteSource(String path) {
		sourcesListener.deleteSource(path);
	}

	/**
	 * Setzt den Pfad der im Quellpfad-Textfeld angezeigt wird.
	 * 
	 * @param source
	 *            zu setzende Quelle
	 */
	public void setSource(Source source) {
		tf_source.setText(source.getPath());
	}

	/**
	 * Schaltet den EditMode an bzw. aus.
	 * 
	 * @param editMode
	 *            true = an, false = aus
	 */
	public void setEditMode(boolean inEditMode) {
		this.inEditMode = inEditMode;
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
	 * Prüft ob der gegebene Pfad unter dem Rootpfad der gewählten Quelle ist.
	 * 
	 * @param path
	 *            zu prüfender Pfad
	 * @return ob der gegebene Pfad unter dem Rootpfad der Quelle ist
	 */
	private boolean isUnderSourceRoot(String path) {
		if (path.startsWith(tf_source.getText())) {
			return true;
		}
		return false;
	}

	/**
	 * Gibt die Quelldatei zurück.
	 * 
	 * @return Quelldatei
	 */
	private File getSourceFile() {
		return new File(tf_source.getText());
	}

	/**
	 * Durchsucht die Liste der Filter nach dem gegebenen Pfad. Wird ein Filter
	 * mit diesem Pfad gefunden wird dieser gelöscht.
	 * 
	 * @param path
	 *            zu löschender Filterpfad
	 */
	private void deleteFilter(String path) {
		for (int i = 0; i < listModel.getSize(); i++) {
			if (listModel.get(i).equals(path)) {
				listModel.remove(i);
			}
		}
	}

	/**
	 * Fügt einen Filter zur Liste der Filter hinzu.
	 * 
	 * @param filter
	 *            hinzuzufügender Filter
	 */
	public void addFilter(String filter, int mode) {
		listModel.addElement(new Filter(filter, mode));
	}
}
