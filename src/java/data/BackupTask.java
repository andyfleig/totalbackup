package data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Eine Backup-Aufgabe.
 * 
 * @author andy
 *
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

	// Für das erweiterte AutoClean:
	private int[] threshold = new int[5];
	private String[] thresholdUnits = new String[5];
	private String[] backupsToKeep = new String[5];

	/**
	 * Erzeugt einen BackupTask
	 * 
	 * @param name
	 *            Name des Backup-Tasks
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
	 * @param source
	 *            hinzuzufügende Quelle
	 */
	public void addSourcePath(Source source) {
		sources.add(source);
	}

	/**
	 * Legt den Zielpfad auf den übergebenen Pfad fest.
	 * 
	 * @param path
	 *            festzulegender Pfad
	 */
	public void setDestinationPath(String path) {
		this.destinationPath = path;
	}

	/**
	 * Legt alle Quellpfade auf die übergebenen Quellpfade fest. Achtung, alle
	 * existierenden Quellpfade werden überschrieben!
	 * 
	 * @param sources
	 *            festzulegende Quellen
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
	 * @param path
	 *            zu löschender Pfad
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
	 * @param path
	 *            Pfad zu welchem der Index gesucht wird
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
	 * @param mode
	 *            festzulegender Backup-Modus
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
	 * @param enabled
	 *            Auto-Clean aktivieren
	 */
	public void setSimpleAutoCleanEnabled(boolean enabled) {
		simpleAutoCleanIsEnabled = enabled;
	}

	/**
	 * Aktiviert bzw. Deaktiviert die erweiterte Auto-Clean Funktion.
	 * 
	 * @param enabled
	 *            Auto-Clean aktivieren
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

	// TODO: JavaDoc (ganze Klasse)
	public void setThreshold(int[] thresholdToSet) {
		this.threshold = thresholdToSet;
	}

	public void setThresholdUnits(String[] thresholdUnitsToSet) {
		this.thresholdUnits = thresholdUnitsToSet;
	}

	public void setBackupsToKeep(String[] backupsToKeep) {
		this.backupsToKeep = backupsToKeep;
	}
	
	public int[] getThreshold() {
		return threshold;
	}
	
	public String[] getThresholdUnits() {
		return thresholdUnits;
	}
	
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
	 * @param numberOfBackupsToKeep
	 *            Anzahl der zu behaltenden Backup-Sätze
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
	 * @param prepared
	 *            true = vorbereitet, false = nicht vorbereitet
	 */
	public void setPrepared(boolean prepared) {
		this.isPrepared = prepared;
	}

	/**
	 * Setzt die Autostart-Option.
	 * 
	 * @param autostart
	 *            zu setzenden Autostart-Option
	 */
	public void setAutostart(boolean autostart) {
		this.autostart = autostart;
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
	 * Setzt die Anzhal der Regeln des erweiterten AutoClean.
	 * @param numberOfRules Anzahl der Regeln
	 */
	public void setNumberOfExtendedAutoCleanRules(int numberOfRules) {
		this.numberOfExtendedCleanRules = numberOfRules;
	}
	
	/**
	 * Gibt die Anzahl der Regeln des erweiterten AutoClean zurück.
	 * @return Anzhal der Regeln
	 */
	public int getNumberOfExtendedCleanRules() {
		return this.numberOfExtendedCleanRules;
	}
}
