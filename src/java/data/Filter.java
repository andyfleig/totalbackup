package data;

import java.io.Serializable;

public class Filter implements Serializable{
	private static final long serialVersionUID = -1492728068400793184L;
	// TODO: JavaDoc
	private String path;
	private int mode;

	public Filter(String path, int mode) {
		this.path = path;
		this.mode = mode;
	}

	public String getPath() {
		return path;
	}

	public int getMode() {
		return mode;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public String toString() {
		if (mode == 0) {
			return "Ex: " + path;
		} else if (mode == 1) {
			return "MD5: " + path;
		}
		return null;
	}
}
