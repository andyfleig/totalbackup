/*
 * Copyright 2014 - 2019 Andreas Fleig (github AT andyfleig DOT de)
 *
 * All rights reserved.
 *
 * This file is part of TotalBackup.
 *
 * TotalBackup is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TotalBackup is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TotalBackup.  If not, see <http://www.gnu.org/licenses/>.
 */
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

	/**
	 * Sets the next-execution-time-status to the given time.
	 *
	 * @param taskNextExecutionDateTime next-execution-time-status to set, sets no next-execution-time-status if
	 *                                  taskNextExecutionDateTime is null
	 */
	public void setTaskNextExecutionTimeStatus(LocalDateTime taskNextExecutionDateTime) {
		if (taskNextExecutionDateTime != null) {
			this.taskNextExecutionStatus = buildNextExecutionStatusString(taskNextExecutionDateTime);
		} else {
			this.taskNextExecutionStatus = "";
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
