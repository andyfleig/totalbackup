package gui;

import java.awt.BorderLayout;
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

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

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
	private JList<String> list_Filter;
	private DefaultListModel<String> listModel;

	private boolean inEditMode;
	private String originalPath;

	private ISourcesListener sourcesListener;
	private Filter filterDialog;

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
		this.sourcesListener = sourcesListener;
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

			listModel = new DefaultListModel<String>();
			list_Filter = new JList<String>(listModel);
			list_Filter.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list_Filter.setSelectedIndex(0);
			list_Filter.setVisibleRowCount(6);
			JScrollPane listScroller = new JScrollPane(list_Filter);
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
					// Button Add:
					JButton button = new JButton(ResourceBundle.getBundle("gui.messages")
							.getString("Edit.btn_Add.text"));
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							filterDialog = new Filter(new IFilterListener() {

								@Override
								public void addFilter(String path) {
									listModel.addElement(path);
								}

								@Override
								public boolean isUnderSourceRoot(String path) {
									return Sources.this.isUnderSourceRoot(path);
								}

								@Override
								public void deleteFilter(String path) {
									Sources.this.deleteFilter(path);
								}
							});
							filterDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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

							filterDialog = new Filter(new IFilterListener() {

								@Override
								public void addFilter(String path) {
									listModel.addElement(path);
								}

								@Override
								public boolean isUnderSourceRoot(String path) {
									return Sources.this.isUnderSourceRoot(path);
								}

								@Override
								public void deleteFilter(String path) {
									Sources.this.deleteFilter(path);
								}
							});
							filterDialog.setFilter(listModel.get(list_Filter.getSelectedIndex()));
							filterDialog.setEditMode(true);
							filterDialog.setOriginalPath(list_Filter.getSelectedValue());

							filterDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
							listModel.remove(list_Filter.getSelectedIndex());
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
					JButton cancelButton = new JButton("Cancel");
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

	// TODO: JavaDoc
	private boolean isAlreadySourcePath(String path) {
		return sourcesListener.isAlreadySourcePath(path);
	}

	private void addSource(Source source) {
		sourcesListener.addSource(source);
	}

	public void deleteSource(String path) {
		sourcesListener.deleteSource(path);
	}

	public void setSource(Source source) {
		tf_source.setText(source.getPath());
	}

	public void setEditMode(boolean inEditMode) {
		this.inEditMode = inEditMode;
	}

	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}

	private boolean isUnderSourceRoot(String path) {
		if (path.startsWith(tf_source.getText())) {
			return true;
		}
		return false;
	}

	private void deleteFilter(String path) {
		for (int i = 0; i < listModel.getSize(); i++) {
			if (listModel.get(i).equals(path)) {
				listModel.remove(i);
			}
		}
	}

	public void addFilter(String filter) {
		listModel.addElement(filter);
	}
}
