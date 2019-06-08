package gui;

import javafx.css.PseudoClass;
import javafx.scene.control.ListCell;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class MainframeCellContent {
	private String taskName;
	private String taskStaus;

	public MainframeCellContent(String taskName, String taskStaus) {
		this.taskName = taskName;
		this.taskStaus = taskStaus;
	}

	public String getTaskName() {
		return taskName;
	}

	public String getTaskStaus() {
		return taskStaus;
	}

	public void setTaskStaus(String taskStaus) {
		this.taskStaus = taskStaus;
	}
}
