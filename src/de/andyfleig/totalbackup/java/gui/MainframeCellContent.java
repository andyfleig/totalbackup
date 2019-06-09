package gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CellContent elements for the list of BackupTasks within the Mainframe.
 *
 * @author Andreas Fleig
 */
public class MainframeCellContent {
	private String taskName;
	private String taskStaus;
	private String taskNextExecutionStatus;

	/**
	 * Creates a new ContentCell for the list of BackupTasks within the Mainframe.
	 *
	 * @param taskName                  name of the BackupTask
	 * @param taskStaus                 status of the BackupTask
	 * @param taskNextExecutionDateTime next-execution-time of the BackupTask (may be null)
	 */
	public MainframeCellContent(String taskName, String taskStaus, LocalDateTime taskNextExecutionDateTime) {
		this.taskName = taskName;
		this.taskStaus = "Status: " + taskStaus;
		if (taskNextExecutionDateTime != null) {
			this.taskNextExecutionStatus = buildNextExecutionStatusString(taskNextExecutionDateTime);
		}
	}

	public String getTaskName() {
		return taskName;
	}

	public String getTaskStaus() {
		return taskStaus;
	}

	public String getTaskNextExecutionStatus() {
		return taskNextExecutionStatus;
	}

	public void setTaskStaus(String taskStaus) {
		this.taskStaus = "Status: " + taskStaus;
	}

	public void setTaskNextExecutionTimeStatus(LocalDateTime taskNextExecutionDateTime) {
		if (taskNextExecutionDateTime != null) {
			this.taskNextExecutionStatus = buildNextExecutionStatusString(taskNextExecutionDateTime);
		}
	}

	/**
	 * Correctly formats the string for the NextExecutionStatus.
	 *
	 * @param taskNextExecutionDateTime DataTime object of the next execution
	 * @return string for the NextExecutionStatus
	 */
	private String buildNextExecutionStatusString(LocalDateTime taskNextExecutionDateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String dataTime = taskNextExecutionDateTime.format(formatter);
		return "Next Execution: " + dataTime;
	}
}
