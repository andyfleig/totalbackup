package gui;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class BackupTaskListCell extends ListCell<MainframeCellContent> {
	private GridPane gridPane = new GridPane();
	private Label taskName = new Label();
	private Label taskStatus = new Label();
	private Label taskNextExecution = new Label();

	public BackupTaskListCell() {
		gridPane.add(taskName, 0, 0);
		gridPane.add(taskStatus, 0 ,1);
		gridPane.add(taskNextExecution, 0 ,2);
	}

	@Override
	public void updateItem(MainframeCellContent content, boolean empty) {
		super.updateItem(content, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setText(null);
			taskName.setText(content.getTaskName());
			taskName.setStyle("-fx-font-weight: bold");
			taskStatus.setText(content.getTaskStaus());
			taskNextExecution.setText(content.getTaskNextExecutionStatus());
			setGraphic(gridPane);
		}
	}
}
