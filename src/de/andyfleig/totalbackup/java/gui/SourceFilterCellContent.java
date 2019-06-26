package gui;

/**
 * ToDo
 *
 * @author Andreas Fleig
 * @deprecated
 */
public class SourceFilterCellContent {
	private String path;
	private int mode;

	public SourceFilterCellContent(String path, int mode) {
		this.path = path;
		this.mode = mode;
	}

	public String getFilterPath() {
		return path;
	}

	public int getFilerMode() {
		return mode;
	}
}
