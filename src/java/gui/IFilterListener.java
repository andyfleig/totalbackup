package gui;

public interface IFilterListener {
	public void addFilter(String filter);
	public boolean isUnderSourceRoot(String path);
	public void deleteFilter(String path);
}
