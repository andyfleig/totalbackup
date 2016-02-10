package gui;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class cellContent {
	private String taskName;
	private String taskStaus;

	public cellContent(String taskName, String taskStaus) {
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
