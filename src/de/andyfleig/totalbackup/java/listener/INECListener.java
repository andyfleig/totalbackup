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
package listener;

import java.time.LocalDateTime;

public interface INECListener {
	/**
	 * Überspringt diesen Backup-Vorgang. Das Backup wird je nach Einstellungen auf den nächsten geplanten Zeitpunkt
	 * gescheduled.
	 */
	public void skipBackup();

	/**
	 * Verschiebt diesen Backup-Vorgang auf den angegebenen Zeitpunkt, falls dieser vor dem nächsten planmäßigen
	 * Ausführungszeitpunkt liegt.
	 *
	 * @param nextExecutionTime Zeitpunkt auf den dieser Backup-Vorgang verschoben werden soll
	 */
	public void postponeBackup(LocalDateTime nextExecutionTime);

	/**
	 * Rescheduled diesen Backup-Vorgang sofort neu.
	 */
	public void retry();
}
