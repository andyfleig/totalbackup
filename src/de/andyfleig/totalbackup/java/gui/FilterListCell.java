package gui;

import data.Filter;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class FilterListCell extends ListCell<Filter> {
	private GridPane gridPane = new GridPane();
	private Label lb_path = new Label();
	private Label lb_mode = new Label();

	public FilterListCell() {
		gridPane.add(lb_path, 0, 0);
		gridPane.add(lb_mode, 0 ,1);
	}

	@Override
	public void updateItem(Filter content, boolean empty) {
		super.updateItem(content, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setText(null);
			lb_path.setText(content.getPath());
			if (content.getMode() == 0) {
				lb_mode.setText("Exclusion-Filter");
			} else if (content.getMode() == 1) {
				lb_mode.setText("MD5-Filter");
			}
			setGraphic(gridPane);
		}
	}
}
