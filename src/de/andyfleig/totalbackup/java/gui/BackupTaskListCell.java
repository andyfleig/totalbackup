package gui;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class BackupTaskListCell extends ListCell<CellContent> {
	private GridPane gridPane = new GridPane();
	private Label taskName = new Label();
	private Label taskStatus = new Label();

	public BackupTaskListCell() {
		gridPane.add(taskName, 0, 0);
		gridPane.add(taskStatus, 0 ,1);
	}

	@Override
	public void updateItem(CellContent content, boolean empty) {
		super.updateItem(content, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setText(null);
			taskName.setText(content.getTaskName());
			taskStatus.setText(content.getTaskStaus());
			setGraphic(gridPane);
		}
	}
}
