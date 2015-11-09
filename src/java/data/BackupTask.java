/*
 * Copyright 2014, 2015 Andreas Fleig (andy DOT fleig AT gmail DOT com)
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
 * Eine Backup-Aufgabe. Enthält die Einstellungen dieses Backup Tasks wie Quell-
 * und Zielpfad aber auch Informationen über autoClean usw.
 *
 * @author Andreas Fleig
 */
public class BackupTask implements Serializable {

	/**
	 * Versionsnummer für die Seriallisierung.
	 */
	private static final long serialVersionUID = 1212577706810419845L;
	private String taskName;
	private ArrayList<Source> sources;
	private String destinationPath;
	private int backupMode;
	private boolean simpleAutoCleanIsEnabled;
	private boolean extendedAutoCleanIsEnabled;
	private int numberOfExtendedCleanRules;
	private int numberOfBackupsToKeep;
	private boolean isPrepared = false;
	private boolean autostart = false;
	private boolean destVerification = false;

	// Für das erweiterte AutoClean:
	private int[] threshold = new int[5];
	private String[] thresholdUnits = new String[5];
	private String[] backupsToKeep = new String[5];

	// Für AutoBackup:
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
	 * Gibt an wie weit ein Backup sich in der Zukunft befinden darft sodass
	 * sich das nachholen eines versäumten Backups noch lohnt. Angabe in
	 * Minuten.
	 */
	private String catchUpTime;
	private boolean catchUpEnabled;
	// Für die einmalige Ausführung eines Backups mit einem anderen Zielpfad:
	private String realDestinationPath = null;

	/**
	 * Erzeugt einen BackupTask
	 *
	 * @param name Name des Backup-Tasks
	 */
	public BackupTask(String name) {
		this.taskName = name;
		sources = new ArrayList<Source>();
	}

	/**
	 * Gibt den Namen des Tasks zurück.
	 *
	 * @return Task-Name
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * Gibt alle Quellen zurück.
	 *
	 * @return alle Quellen
	 */
	public ArrayList<Source> getSources() {
		return sources;
	}

	/**
	 * Fügt der Listen der zu sichernden Quellpfade einen Pfad hinzu.
	 *
	 * @param source hinzuzufügende Quelle
	 */
	public void addSourcePath(Source source) {
		sources.add(source);
	}

	/**
	 * Legt den Zielpfad auf den übergebenen Pfad fest.
	 *
	 * @param path festzulegender Pfad
	 */
	public void setDestinationPath(String path) {
		this.destinationPath = path;
	}

	/**
	 * Legt alle Quellpfade auf die übergebenen Quellpfade fest. Achtung, alle
	 * existierenden Quellpfade werden überschrieben!
	 *
	 * @param sources festzulegende Quellen
	 */
	public void setSources(ArrayList<Source> sources) {
		this.sources = sources;
	}

	/**
	 * Gibt den Zielpfad zurück.
	 *
	 * @return Zielpfad
	 */
	public String getDestinationPath() {
		return this.destinationPath;
	}

	/**
	 * Löscht den gegebenen Pfad aus der Liste der zu sichernden Quellpfade.
	 *
	 * @param path zu löschender Pfad
	 */
	public void deletePath(String path) {
		int index = getIndexOfPath(path);
		if (index != -1) {
			sources.remove(index);
		}

	}

	/**
	 * Sucht den Index zum gegebenen Pfad.
	 *
	 * @param path Pfad zu welchem der Index gesucht wird
	 * @return Index, -1 falls der gesuchte Pfad nicht gefunden wurde
	 */
	private int getIndexOfPath(String path) {
		for (int i = 0; i < sources.size(); i++) {
			if (sources.get(i).equals(path)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gibt den Namen des Backup-Tasks als String zurück. Wird für das korrekte
	 * Anzeigen des Namens in der Liste (GUI) benötigt.
	 */
	@Override
	public String toString() {
		return taskName;
	}

	/**
	 * Löscht alle Quell- und Zielpfade.
	 */
	public void resetPaths() {
		sources.clear();
		destinationPath = null;
	}

	/**
	 * Legt den Backup-Modus fest.
	 *
	 * @param mode festzulegender Backup-Modus
	 */
	public void setBackupMode(int mode) {
		backupMode = mode;
	}

	/**
	 * Gibt den gewählten Backup-Modus zurück. 0 = normal, 1 = hardlink.
	 *
	 * @return gewälter Backup-Modus
	 */
	public int getBackupMode() {
		return backupMode;
	}

	/**
	 * Aktiviert bzw. Deaktiviert die einfache Auto-Clean Funktion.
	 *
	 * @param enabled Auto-Clean aktivieren
	 */
	public void setSimpleAutoCleanEnabled(boolean enabled) {
		simpleAutoCleanIsEnabled = enabled;
	}

	/**
	 * Aktiviert bzw. Deaktiviert die erweiterte Auto-Clean Funktion.
	 *
	 * @param enabled Auto-Clean aktivieren
	 */
	public void setExtendedAutoCleanEnabled(boolean enabled) {
		extendedAutoCleanIsEnabled = enabled;
	}

	/**
	 * Gibt zurück ob einfaches Auto-Clean aktiviert (true) oder deaktiviert
	 * (false) ist.
	 *
	 * @return Zustand der Auto-Clean Funktion
	 */
	public boolean simpleAutoCleanIsEnabled() {
		return simpleAutoCleanIsEnabled;
	}

	/**
	 * Gibt zurück ob erweiterte Auto-Clean aktiviert (true) oder deaktiviert
	 * (false) ist.
	 *
	 * @return Zustand der Auto-Clean Funktion
	 */
	public boolean extendedAutoCleanIsEnabled() {
		return extendedAutoCleanIsEnabled;
	}

	/**
	 * Legt den Threshold des BackupTasks fest.
	 *
	 * @param thresholdToSet festzulegender Threshold
	 */
	public void setThreshold(int[] thresholdToSet) {
		this.threshold = thresholdToSet;
	}

	/**
	 * Legt die Threshold-Einheiten des BackupTasks fest.
	 *
	 * @param thresholdUnitsToSet festzulegende Threshold-Einheiten
	 */
	public void setThresholdUnits(String[] thresholdUnitsToSet) {
		this.thresholdUnits = thresholdUnitsToSet;
	}

	/**
	 * Legt die Anzahl der zu behaltenden Backupsätze für die einzelnen Regeln
	 * fest.
	 *
	 * @param backupsToKeep festzulegende Werte
	 */
	public void setBackupsToKeep(String[] backupsToKeep) {
		this.backupsToKeep = backupsToKeep;
	}

	/**
	 * Gibt den Threshold zurück
	 *
	 * @return Threshold
	 */
	public int[] getThreshold() {
		return threshold;
	}

	/**
	 * Gibt die Threshold-Einheiten zurück.
	 *
	 * @return Threshold-Einheiten
	 */
	public String[] getThresholdUnits() {
		return thresholdUnits;
	}

	/**
	 * Gibt die Anzahl der zu behaltenden Backupsätze für die einzelnen Regeln
	 * zurück.
	 *
	 * @return Anzahl der zu behaltenden Backupsätze
	 */
	public String[] getBackupsToKeep() {
		return backupsToKeep;
	}

	/**
	 * Löscht alle gesetzten Einstellungen/ Informationen des
	 * AutoClean-Features.
	 */
	public void clearAutoCleanInformations() {
		simpleAutoCleanIsEnabled = false;
		extendedAutoCleanIsEnabled = false;
		numberOfBackupsToKeep = 0;
		threshold = null;
		thresholdUnits = null;
		backupsToKeep = null;
	}

	/**
	 * Legt die Anzahl der beim Auto-Clean zu behaltenden Backup-Sätze fest
	 *
	 * @param numberOfBackupsToKeep Anzahl der zu behaltenden Backup-Sätze
	 */
	public void setNumberOfBackupsToKeep(int numberOfBackupsToKeep) {
		this.numberOfBackupsToKeep = numberOfBackupsToKeep;
	}

	/**
	 * Gibt die Anzahl der beim Auto-Clean zu behaltenden Backup-Sätze zurück
	 *
	 * @return Anzahl der beim Auto-Clean zu behaltenden Backup-Sätze
	 */
	public int getNumberOfBackupsToKeep() {
		return numberOfBackupsToKeep;
	}

	/**
	 * Gibt zurück ob dieser BackupTask vorbereitet (zur Ausführung bereit) ist
	 *
	 * @return true wenn die Vorbereitungen getroffen wurden, false sonst
	 */
	public boolean isPrepered() {
		return isPrepared;
	}

	/**
	 * Markiert diesen BackupTask als für das Backup vorbereitet/ nicht
	 * vorbereitet.
	 *
	 * @param prepared true = vorbereitet, false = nicht vorbereitet
	 */
	public void setPrepared(boolean prepared) {
		this.isPrepared = prepared;
	}

	/**
	 * Setzt die Autostart-Option.
	 *
	 * @param autostart zu setzende Autostart-Option
	 */
	public void setAutostart(boolean autostart) {
		this.autostart = autostart;
	}

	/**
	 * Setzt die DestinationVerification-Option.
	 *
	 * @param destVerificaion zu setzende DestinationVerification-Option
	 */
	public void setDestinationVerification(boolean destVerificaion) {
		this.destVerification = destVerificaion;
	}

	/**
	 * Gibt zurück ob die Autostart-Option aktiviert ist.
	 *
	 * @return ob die Autostart-Option aktiviert ist
	 */
	public boolean getAutostart() {
		return autostart;
	}

	/**
	 * Gibt zurück ob die DestinationVerification-Option aktiviert ist.
	 *
	 * @return ob die DestinationVerification-Option aktiviert ist
	 */
	public boolean getDestinationVerification() {
		return destVerification;
	}

	/**
	 * Setzt die Anzahl der Regeln des erweiterten AutoClean.
	 *
	 * @param numberOfRules Anzahl der Regeln
	 */
	public void setNumberOfExtendedAutoCleanRules(int numberOfRules) {
		this.numberOfExtendedCleanRules = numberOfRules;
	}

	/**
	 * Gibt die Anzahl der Regeln des erweiterten AutoClean zurück.
	 *
	 * @return Anzahl der Regeln
	 */
	public int getNumberOfExtendedCleanRules() {
		return this.numberOfExtendedCleanRules;
	}

	/**
	 * Gibt eine Liste von Strings zurück, wobei die Strings für die
	 * verschiedenen Regeln eine Kombination von Zahl_Zeiteinheit ist.
	 *
	 * @return Liste von Strings welche die Grenzwerte beschreiben
	 */
	public String[] getBoundaries() {
		String[] result = new String[numberOfExtendedCleanRules - 1];
		for (int i = 0; i < (numberOfExtendedCleanRules - 1); i++) {
			result[i] = threshold[i] + "_" + thresholdUnits[i];
		}
		return result;
	}

	/**
	 * Legt den Backup-Modus fest. 0 = Auto-Backup deaktiviert, 1 =
	 * Zeitpunkt-Wochentag, 2 = Zeitpunkt-TagImMonat, 3 = Intervall
	 *
	 * @param mode Backup-Modus
	 */
	public void setAutoBackupMode(int mode) {
		this.autoBackupMode = mode;
	}

	/**
	 * Gibt den Backup-Modus zurück. 0 = Auto-Backup deaktiviert, 1 =
	 * Zeitpunkt-Wochentag, 2 = Zeitpunkt-TagImMonat, 3 = Intervall
	 *
	 * @return Backup-Modus
	 */
	public int getAutoBackupMode() {
		return autoBackupMode;
	}

	/**
	 * Legt die Wochentage fest an denen das Backup ausgeführt werden soll. Die
	 * Array-Felder entsprechen den Wochentagen von [0] = Montag bis [6] =
	 * Sonntag.
	 *
	 * @param weekdays Wochentage an denen gesichert werden soll
	 */
	public void setBackupWeekdays(boolean[] weekdays) {
		this.weekdays = weekdays;
	}

	/**
	 * Gibt die Wochentage zurück an denen das Backup ausgeführt werden soll.
	 * Die Array-Felder entsprechen den Wochentagen von [0] = Montag bis [6] =
	 * Sonntag.
	 *
	 * @return Wochentage an denen gesichert werden soll
	 */
	public boolean[] getBackupWeekdays() {
		return weekdays;
	}

	/**
	 * Legt die Tage im Monat fest an denen das Backup ausgefürt werden soll.
	 * Die Array-Felder entsprechen den Tagen im Monat von [0] = 1. bis [30] =
	 * 31.
	 *
	 * @param daysInMonth Tage im Monat an denen das Backup ausgeführt werden soll.
	 */
	public void setBackupDaysInMonth(boolean[] daysInMonth) {
		this.backupDaysInMonth = daysInMonth;
	}

	/**
	 * Gibt die Tage im Monat zurück an denen das Backup ausgeführt werden soll.
	 * Die Array-Felder entsprechen den Tagen im Monat von [0] = 1. bis [30] =
	 * 31.
	 *
	 * @return Tage im Monat an denen das Backup ausgeführt werden soll.
	 */
	public boolean[] getBackupDaysInMonth() {
		return backupDaysInMonth;
	}

	/**
	 * Legt die Startzeit für das AutoBackup fest.
	 *
	 * @param startTime festzulegende Startzeit
	 */
	public void setBackupStartTime(LocalTime startTime) {
		this.backupStartTime = startTime;
	}

	/**
	 * Gibt die Startzeit des AutoBackups zurück.
	 *
	 * @return Startzeit des AutoBackups
	 */
	public LocalTime getStartTime() {
		return backupStartTime;
	}

	/**
	 * Legt die Intervallzeit fest.
	 *
	 * @param time Intervallzeit
	 */
	public void setIntervalTime(int time) {
		this.intervalTime = time;
	}

	/**
	 * Gibt die Intervallzeit zurück.
	 *
	 * @return Intervallzeit
	 */
	public int getIntervalTime() {
		return intervalTime;
	}

	/**
	 * Legt die Intervalleinheit fest.
	 *
	 * @param intervalUnit Intervalleinheit
	 */
	public void setIntervalUnit(String intervalUnit) {
		this.intervalUnit = intervalUnit;
	}

	/**
	 * Gibt die Intervalleinheit zurück.
	 *
	 * @return Intervalleinheit
	 */
	public String getIntervalUnit() {
		return intervalUnit;
	}

	/**
	 * Gibt das ScheduledFuture für die nächste geschedulte Ausführung zurück.
	 *
	 * @return ScheduledFuture der nächsten Ausführung
	 */
	public ScheduledFuture getScheduledFuture() {
		return scheduledFuture;
	}

	/**
	 * Setzt das ScheduledFuture für die nächste Ausführung.
	 *
	 * @param scheduledFuture ScheduledFuture der nächsten Ausführung
	 */
	public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
		this.scheduledFuture = scheduledFuture;
	}

	/**
	 * Gibt das ScheduledFuture für den nächsten geschedulte Popup zurück.
	 *
	 * @return ScheduledFuture des nächsten Popups
	 */
	public ScheduledFuture getPopupScheduledFuture() {
		return popupScheduledFuture;
	}

	/**
	 * Setzt das ScheduledFuture für den nächsten Popup.
	 *
	 * @param scheduledFuture ScheduledFuture des nächsten Popups
	 */
	public void setPopupScheduledFuture(ScheduledFuture<?> popupScheduledFuture) {
		this.popupScheduledFuture = popupScheduledFuture;
	}

	/**
	 * Speichert den nächsten geschedulten Ausführungszeitpunkt.
	 *
	 * @param nextExecutionTime nächster geschedulter Ausführungszeitpunkt als LocalDateTime
	 */
	public void setLocalDateTimeOfNextBackup(LocalDateTime nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}

	/**
	 * Gibt den nächsten geschedulten Ausführungszeitpunkt zurück.
	 *
	 * @return nächster geplanter Ausführungszeitpunkt
	 */
	public LocalDateTime getLocalDateTimeOfNextBackup() {
		return nextExecutionTime;
	}

	/**
	 * Resettet den nächsten Ausführungszeitpunkt (LocalDateTime). Achtung:
	 * Hierbei wird nicht das scheduling an sich resettet sondern nur die
	 * zusätzliche Variable für das Nachholen versäumter Backups. Diese Methode
	 * ist nur gefolgt von task.getScheduledFuture().cancel(false) zu benutzen!
	 */
	public void resetLocalDateTimeOfNextExecution() {
		this.nextExecutionTime = null;
	}

	/**
	 * Gibt die Dauer zum nächsten geplanten Backup, so dass sich das nachholen
	 * eines versäumten Backups noch lohnt, zurück.
	 *
	 * @return Dauer zum nächsten Backup
	 */
	public int getProfitableTimeUntilNextExecution() {
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
				return 5;
		}
	}

	/**
	 * Aktiviert/ Deaktiviert die Backup-Nachholen-Funktikon.
	 *
	 * @param enabled zu setzender Wert (akt./deakt.)
	 */
	public void setCatchUpEnabled(boolean enabled) {
		this.catchUpEnabled = enabled;
	}

	/**
	 * Legt die catchUp-Zeit (als String) fest.
	 *
	 * @param catchUpTime festzulegende catchUp-Zeit (als String)
	 */
	public void setCatchUpTime(String catchUpTime) {
		this.catchUpTime = catchUpTime;
	}

	/**
	 * Gibt die catchUp-Zeit (als String) zurück.
	 *
	 * @return catchUp-Zeit (als String)
	 */
	public String getCatchUpTime() {
		return catchUpTime;
	}

	/**
	 * Gibt zurück ob catchUp aktiviert ist.
	 *
	 * @return ob catchUp aktiviert ist
	 */
	public boolean isCatchUpEnabled() {
		if (catchUpEnabled) {
			return true;
		}
		return false;
	}

	/**
	 * Legt den "richtigen" Zielpfad fest. Dieser wird nach dem Backup zum
	 * Zielpfad. Dies ist nötig wenn einmalig ein Backup mit einem anderen
	 * Zielpfad ausgeführt werden soll (z.B. für DestinationVerification).
	 *
	 * @param path festzulegender "richtiger" Zielpfad
	 */
	public void setRealDestinationPath(String path) {
		this.realDestinationPath = path;
	}

	/**
	 * Gibt den "richtigen" Zielpfad zurück. Dieser wird nach dem Backup zum
	 * Zielpfad. Dies ist nötig wenn einmalig ein Backup mit einem anderen
	 * Zielpfad ausgeführt werden soll (z.B. für DestinationVerification).
	 *
	 * @return "richtiger" Zielpfad
	 */
	public String getRealDestinationPath() {
		return this.realDestinationPath;
	}
}
