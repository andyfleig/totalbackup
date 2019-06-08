package gui;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class FilterListCell extends ListCell<SourceFilterCellContent> {
	private GridPane gridPane = new GridPane();
	private Label lb_path = new Label();
	private Label lb_mode = new Label();

	public FilterListCell() {
		gridPane.add(lb_path, 0, 0);
		gridPane.add(lb_mode, 0 ,1);
	}

	@Override
	public void updateItem(SourceFilterCellContent content, boolean empty) {
		super.updateItem(content, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setText(null);
			lb_path.setText(content.getFilterPath());
			if (content.getFilerMode() == 0) {
				lb_mode.setText("Exclusion-Filter");
			} else if (content.getFilerMode() == 1) {
				lb_mode.setText("MD5-Filter");
			}
			setGraphic(gridPane);
		}
	}
}
