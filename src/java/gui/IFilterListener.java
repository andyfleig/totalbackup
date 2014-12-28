package gui;

import java.io.File;

public interface IFilterListener {
	public void addFilter(String filter);
	public boolean isUnderSourceRoot(String path);
	public File getSourceFile();
	public void deleteFilter(String path);
}
