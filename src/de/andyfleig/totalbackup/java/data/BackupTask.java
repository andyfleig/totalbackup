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
package data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

/**
 * One backup task containing the configuration information of the backup like source and destination path or
 * information about the configuration of auto-clean or auto-backup.
 *
 * @author Andreas Fleig
 */
public class BackupTask implements Serializable {

	/**
	 * Version number for serialization.
	 */
	private static final long serialVersionUID = 1212577706810419845L;
	private String taskName;
	private ArrayList<Source> sources;
	private String destinationPath;
	private int backupMode;
	private boolean basicAutoCleanIsEnabled;
	private boolean advancedAutoCleanIsEnabled;
	private int numberOfExtendedCleanRules;
	private int numberOfBackupsToKeep;
	private boolean isPrepared = false;
	private boolean autostart = false;
	private boolean destVerification = false;

	// For the advanced auto-clean feature:
	private int[] threshold = new int[5];
	private String[] thresholdUnits = new String[5];
	private int[] backupsToKeep = new int[5];

	// For the auto-backup feature:
	private int autoBackupMode;
	private boolean[] weekdays = new boolean[7];
	private boolean[] backupDaysInMonth = new boolean[31];
	private LocalTime backupStartTime;
	private int intervalTime;
	private String intervalUnit;
	private transient ScheduledFuture<?> scheduledFuture;
	private transient ScheduledFuture<?> popupScheduledFuture;
	private LocalDateTime nextExecutionTime;
	/**
	 * Number of minutes until next scheduled backup so it is reasonably the catch up the missed one (default is 10
	 * minutes)
	 */
	private String catchUpTime;
	private boolean catchUpEnabled;
	/**
	 * DestinationPath for the one time execution of a backup with another destination path.
	 */
	private String realDestinationPath = null;

	/**
	 * Creates a new BackupTask.
	 *
	 * @param name name of the BackupTask
	 */
	public BackupTask(String name) {
		this.taskName = name;
		sources = new ArrayList<>();
	}

	/**
	 * Returns the name of the BackupTask.
	 *
	 * @return name of the BackupTask
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * Returns all the sources of this BackupTask.
	 *
	 * @return list of all the sources
	 */
	public ArrayList<Source> getSources() {
		return sources;
	}

	/**
	 * Adds the given source to the list of sources of this BackupTask.
	 *
	 * @param source source to add
	 */
	public void addSource(Source source) {
		sources.add(source);
	}

	/**
	 * Sets the destination path of this BackupTask to the given one.
	 *
	 * @param path destination path to set
	 */
	public void setDestinationPath(String path) {
		this.destinationPath = path;
	}

	/**
	 * Returns the destination path of this BackupTask.
	 *
	 * @return destination path
	 */
	public String getDestinationPath() {
		return this.destinationPath;
	}

	@Override
	public String toString() {
		return taskName;
	}

	/**
	 * Sets the mode of the backup of this BackupTask.
	 *
	 * @param mode backup-mode to set (0 = normal, 1 = hardlink)
	 */
	public void setBackupMode(int mode) {
		backupMode = mode;
	}

	/**
	 * Returns the backup-mode of this BackupTask.
	 *
	 * @return backup-mode (0 = normal, 1 = hardlink)
	 */
	public int getBackupMode() {
		return backupMode;
	}

	/**
	 * Sets the basic auto-clean feature to the given value.
	 *
	 * @param enabled enable basic auto-clean
	 */
	public void setBasicAutoCleanEnabled(boolean enabled) {
		basicAutoCleanIsEnabled = enabled;
	}

	/**
	 * Sets the advanced auto-clean feature to the given value.
	 *
	 * @param enabled enable advanced auto-clean
	 */
	public void setAdvancedAutoCleanEnabled(boolean enabled) {
		advancedAutoCleanIsEnabled = enabled;
	}

	/**
	 * Returns whether basic auto-clean feature is enabled for this BackupTask.
	 *
	 * @return whether basic auto-clean is enabled or not
	 */
	public boolean basicAutoCleanIsEnabled() {
		return basicAutoCleanIsEnabled;
	}

	/**
	 * Returns whether advanced auto-clean feature is enabled for this BackupTask.
	 *
	 * @return whether advanced auto-clean is enabled or not.
	 */
	public boolean advancedAutoCleanIsEnabled() {
		return advancedAutoCleanIsEnabled;
	}

	/**
	 * Returns the advanced auto-clean threshold of this BackupTask.
	 *
	 * @return threshold
	 */
	public int[] getThreshold() {
		return threshold;
	}

	/**
	 * Returns the unit of the advanced auto-clean threshold of this BackupTask.
	 *
	 * @return unit of the threshold
	 */
	public String[] getThresholdUnits() {
		return thresholdUnits;
	}

	/**
	 * Sets the advanced auto-clean threshold of this BackupTask.
	 *
	 * @param thresholdToSet threshold to set
	 */
	public void setThreshold(int[] thresholdToSet) {
		this.threshold = thresholdToSet;
	}

	/**
	 * Sets the unit of the advanced auto-clean threshold of this BackupTask.
	 *
	 * @param thresholdUnitsToSet unit to set
	 */
	public void setThresholdUnits(String[] thresholdUnitsToSet) {
		this.thresholdUnits = thresholdUnitsToSet;
	}

	/**
	 * Returns the number of backup-sets to keep for each rule for advanced auto-clean.
	 *
	 * @return the number of backup-sets to keep for each rule
	 */
	public int[] getBackupsToKeep() {
		return backupsToKeep;
	}

	/**
	 * Sets the number of backup-sets to keep for each rule for advanced auto-clean.
	 *
	 * @param backupsToKeep number of backup-sets to keep for each rule
	 */
	public void setBackupsToKeep(int[] backupsToKeep) {
		this.backupsToKeep = backupsToKeep;
	}

	/**
	 * Returns the number of backup-sets to keep for basic auto-clean.
	 *
	 * @return number of backup-sets to keep
	 */
	public int getNumberOfBackupsToKeep() {
		return numberOfBackupsToKeep;
	}

	/**
	 * Sets the number of backup-sets to keep for basic auto-clean.
	 *
	 * @param numberOfBackupsToKeep number of backup-sets to keep
	 */
	public void setNumberOfBackupsToKeep(int numberOfBackupsToKeep) {
		this.numberOfBackupsToKeep = numberOfBackupsToKeep;
	}

	/**
	 * Returns whether this BackupTask is ready for execution (which means preparation was completed).
	 *
	 * @return whether prepared for execution or not
	 */
	public boolean isPrepared() {
		return isPrepared;
	}

	/**
	 * Marks this BackupTask as prepared and therefore ready for execution.
	 */
	public void setPrepared() {
		this.isPrepared = true;
	}

	/**
	 * Sets the auto-start option to the given value.
	 *
	 * @param autostart auto-start option to set (on = true, off = false)
	 */
	public void setAutostart(boolean autostart) {
		this.autostart = autostart;
	}

	/**
	 * Sets the destination-verification option to the given value.
	 *
	 * @param destVerification destination-verification option to set (on = true, off = false)
	 */
	public void setDestinationVerification(boolean destVerification) {
		this.destVerification = destVerification;
	}

	/**
	 * Returns whether the auto-start option is activated.
	 *
	 * @return whether auto-start is activated (true) or not (false)
	 */
	public boolean autostartIsEnabled() {
		return autostart;
	}

	/**
	 * Returns whether the destination-verification option is activated.
	 *
	 * @return whether destination-verification is activated (true) or not (false)
	 */
	public boolean destinationVerificationIsEnabled() {
		return destVerification;
	}

	/**
	 * Returns the number of rules for advanced auto-clean.
	 *
	 * @return number of auto-clean rules
	 */
	public int getNumberOfExtendedCleanRules() {
		return this.numberOfExtendedCleanRules;
	}

	/**
	 * Determines the number of rules for advanced auto-clean.
	 *
	 * @param numberOfRules number of auto-clean rules to set
	 */
	public void setNumberOfAdvancedAutoCleanRules(int numberOfRules) {
		this.numberOfExtendedCleanRules = numberOfRules;
	}

	/**
	 * Returns a list of the boundaries of advanced auto-clean as formatted strings.
	 *
	 * @return list of formated strings of boundaries
	 */
	public String[] getFormattedBoundaries() {
		String[] result = new String[numberOfExtendedCleanRules - 1];
		for (int i = 0; i < (numberOfExtendedCleanRules - 1); i++) {
			result[i] = threshold[i] + "_" + thresholdUnits[i];
		}
		return result;
	}

	/**
	 * Returns the mode of the auto-backup feature, where 0 = no auto-backup 1 = weekdays 2 = days in months 3 =
	 * interval
	 *
	 * @return auto-backup mode
	 */
	public int getAutoBackupMode() {
		return autoBackupMode;
	}

	/**
	 * Determines the mode of the auto-backup feature, where 0 = no auto-backup 1 = weekdays 2 = days in months 3 =
	 * interval
	 *
	 * @param mode auto-backup mode to set
	 */
	public void setAutoBackupMode(int mode) {
		this.autoBackupMode = mode;
	}

	/**
	 * Returns the weekdays of the auto-backup feature, where 0 = monday, 1 = tuesday, * ... 6 = sunday.
	 *
	 * @return weekdays for the auto-backup feature
	 */
	public boolean[] getBackupWeekdays() {
		return weekdays;
	}

	/**
	 * Determines the weekdays of the auto-backup feature, where 0 = monday, 1 = tuesday, ... 6 = sunday.
	 *
	 * @param weekdays weekdays for the auto-backup feature to set
	 */
	public void setBackupWeekdays(boolean[] weekdays) {
		this.weekdays = weekdays;
	}

	/**
	 * Returns the days in month of the auto-backup feature, where 0 = 1. 1 = 2. ... 30 = 31.
	 *
	 * @return days in month for the auto-backup feature to set
	 */
	public boolean[] getBackupDaysInMonth() {
		return backupDaysInMonth;
	}

	/**
	 * Determines the days in month of the auto-backup feature, where 0 = 1. 1 = 2. ... 30 = 31.
	 *
	 * @param daysInMonth days in month for the auto-backup feature
	 */
	public void setBackupDaysInMonth(boolean[] daysInMonth) {
		this.backupDaysInMonth = daysInMonth;
	}

	/**
	 * Returns the start-time of the auto-backup feature.
	 *
	 * @return start-time for the auto-backup feature
	 */
	public LocalTime getBackupStartTime() {
		return backupStartTime;
	}

	/**
	 * Determines the start-time of the auto-backup feature.
	 *
	 * @param startTime start-time for the auto-backup feature to set
	 */
	public void setBackupStartTime(LocalTime startTime) {
		this.backupStartTime = startTime;
	}

	/**
	 * Returns the time interval of the auto-backup feature.
	 *
	 * @return time interval for the auto-backup feature
	 */
	public int getIntervalTime() {
		return intervalTime;
	}

	/**
	 * Determines the time interval of the auto-backup feature.
	 *
	 * @param time time interval for the auto-backup feature to set
	 */
	public void setIntervalTime(int time) {
		this.intervalTime = time;
	}

	/**
	 * Returns the time unit of the interval of the auto-backup feature.
	 *
	 * @return time unit of the interval of the auto-backup feature
	 */
	public String getIntervalUnit() {
		return intervalUnit;
	}

	/**
	 * Determines the time unit of the interval of the auto-backup feature.
	 *
	 * @param intervalUnit time unit of the interval of the auto-backup feature to set
	 */
	public void setIntervalUnit(String intervalUnit) {
		this.intervalUnit = intervalUnit;
	}

	/**
	 * Returns the ScheduledFuture object for the next scheduled execution.
	 *
	 * @return ScheduledFuture object of the next scheduled execution
	 */
	public ScheduledFuture getScheduledFuture() {
		return scheduledFuture;
	}

	/**
	 * Sets the ScheduledFuture object for the next scheduled execution.
	 *
	 * @param scheduledFuture ScheduledFuture object of the next scheduled execution to set
	 */
	public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
		this.scheduledFuture = scheduledFuture;
	}

	/**
	 * Returns the ScheduledFuture object for the next scheduled popup.
	 *
	 * @return popupScheduledFuture object of the next scheduled popup
	 */
	public ScheduledFuture getPopupScheduledFuture() {
		return popupScheduledFuture;
	}

	/**
	 * Sets the ScheduledFuture object for the next scheduled popup.
	 *
	 * @param popupScheduledFuture ScheduledFuture object of the next scheduled popup to set
	 */
	public void setPopupScheduledFuture(ScheduledFuture<?> popupScheduledFuture) {
		this.popupScheduledFuture = popupScheduledFuture;
	}

	/**
	 * Returns the date and time of the next scheduled execution.
	 *
	 * @return date and time of the next scheduled execution
	 */
	public LocalDateTime getLocalDateTimeOfNextBackup() {
		return nextExecutionTime;
	}

	/**
	 * Sets the date and time of the next scheduled execution.
	 *
	 * @param nextExecutionTime date and time of the next scheduled execution to set
	 */
	public void setLocalDateTimeOfNextBackup(LocalDateTime nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}

	/**
	 * Resettet den nächsten Ausführungszeitpunkt (LocalDateTime). Achtung: Hierbei wird nicht das scheduling an sich
	 * resettet sondern nur die zusätzliche Variable für das Nachholen versäumter Backups. Diese Methode ist nur gefolgt
	 * von task.getScheduledFuture().cancel(false) zu benutzen! ToDo: create one joint method?
	 */
	public void resetLocalDateTimeOfNextExecution() {
		this.nextExecutionTime = null;
	}

	/**
	 * Returns the number of minutes until next scheduled backup so it is reasonably the catch up the missed one
	 * (default is 10 minutes)
	 *
	 * @return threshold in minutes
	 */
	public int getProfitableTimeUntilNextExecution() {
		if (this.catchUpTime == null) {
			return 10;
		}
		switch (catchUpTime) {
			case "10min":
				return 10;
			case "15min":
				return 15;
			case "30min":
				return 30;
			case "1h":
				return 60;
			case "2h":
				return 120;
			case "6h":
				return 360;
			case "12h":
				return 720;
			case "24h":
				return 1440;
			default:
				return 10;
		}
	}

	/**
	 * Sets the catch-up feature enabled.
	 *
	 * @param enabled catch-up feature enabled (true) or disabled (false)
	 */
	public void setCatchUpEnabled(boolean enabled) {
		this.catchUpEnabled = enabled;
	}

	/**
	 * Returns whether the catch-up feature is enabled for this BackupTask.
	 *
	 * @return whether catch-up feature is enabled (true) or not (false)
	 */
	public boolean catchUpIsEnabled() {
		return catchUpEnabled;
	}

	/**
	 * Returns the catch-up time.
	 *
	 * @return catch-up time as string
	 */
	public String getCatchUpTime() {
		return catchUpTime;
	}

	/**
	 * Determines the catch-up time.
	 *
	 * @param catchUpTime catch-up time as string
	 */
	public void setCatchUpTime(String catchUpTime) {
		this.catchUpTime = catchUpTime;
	}


	/**
	 * Returns the the real destination path of the BackupTask which will become the actual destination path after the
	 * execution. This is necessary when a backup is executed once with a divergent destination (e.g. for
	 * destination-verification)
	 *
	 * @return real destination path
	 */
	public String getRealDestinationPath() {
		return this.realDestinationPath;
	}

	/**
	 * Determines the the real destination path of the BackupTask which will become the actual destination path after
	 * the execution. This is necessary when a backup is executed once with a divergent destination (e.g. for
	 * destination-verification)
	 *
	 * @param path real destination path to set
	 */
	public void setRealDestinationPath(String path) {
		this.realDestinationPath = path;
	}
}
