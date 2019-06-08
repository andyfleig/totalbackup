package gui;

import data.Source;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class SourceListCell extends ListCell<Source> {
	private GridPane gridPane = new GridPane();
	private Label lb_path = new Label();

	public SourceListCell() {
		gridPane.add(lb_path, 0, 0);
	}

	@Override
	public void updateItem(Source source, boolean empty) {
		super.updateItem(source, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setText(null);
			lb_path.setText(source.getPath());
			setGraphic(gridPane);
		}
	}
}
