package gui;

public interface ISummaryListener {
	
	//TODO: JavaDoc
	public void startBackup();
	
	public String getTaskName();
	
	public void clearBackupInfos();
	
	public long getNumberOfDirectories();
	public long getNumberOfFiles();
	public double getSizeToCopy();
	public double getSizeToLink();
}