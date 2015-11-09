package listener;

import java.time.LocalDateTime;

public interface ISchedulingDialogListener {

	/**
	 * Schedult den betreffenden BackupTask neu auf die gegebene Zeit.
	 *
	 * @param time Zeit auf die der BackupTask geschedult werden soll
	 */
	public void scheduleBackup(LocalDateTime time);

	/**
	 * Reschedult den betreffenden BackupTask.
	 */
	public void rescheduleTask();
}
