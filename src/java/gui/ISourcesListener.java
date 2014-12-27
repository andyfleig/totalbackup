package gui;

import main.Source;

public interface ISourcesListener {
	//TODO: JavaDoc
	public boolean isAlreadySourcePath(String path);
	public void addSource(Source source);
	public void deleteSource(String path);
}
