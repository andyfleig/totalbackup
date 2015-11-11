package listener;

import java.time.LocalDateTime;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public interface INECListener {
	/**
	 * Überspringt diesen Backup-Vorgang. Das Backup wird je nach Einstellungen auf den nächsten geplanten Zeitpunkt gescheduled.
	 */
	public void skipBackup();

	/**
	 * Verschiebt diesen Backup-Vorgang auf den angegebenen Zeitpunkt, falls dieser vor dem nächsten planmäßigen Ausführungszeitpunkt liegt.
	 *
	 * @param nextExecutionTime Zeitpunkt auf den dieser Backup-Vorgang verschoben werden soll
	 */
	public void postponeBackup(LocalDateTime nextExecutionTime);

	/**
	 * Rescheduled diesen Backup-Vorgang sofort neu.
	 */
	public void retry();
}
