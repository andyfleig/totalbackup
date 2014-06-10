package main;

import gui.Mainframe;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;

public class Controller {

	Mainframe mainframe;
	private int numberOfSources = 0;
	private ProgramState programState = ProgramState.getInstance(this);
	private static final String PROPERTIES_PATH = "./properties.properties";

	public void startController() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				mainframe = new Mainframe(Controller.this);
				mainframe.frmTotalbackup.setVisible(true);
				loadProps();
			}
		});
	}

	public void loadProps() {
		programState.loadProperties(PROPERTIES_PATH);
	}

	public void saveProbs() {
		programState.saveProperties(PROPERTIES_PATH);
	}

	public void startBackup(String source, String destination) {
		Backup backup = new Backup(this, source, destination);
		try {
			backup.runBackup();
		} catch (FileNotFoundException ex) {
			System.out.println("Datei existiert nicht!");
		} catch (IOException ex) {
			System.out.println("IO-Fehler!");
		}
	}

	public void printOut(String s) {
		mainframe.addToOutput(s);
	}
	
	public String getDestinationPath() {
		return mainframe.getDestPath();
	}
	
	public void setDestinationPath(String path) {
		mainframe.setDestPath(path);
	}
	public int getNumberOfSources() {
		return numberOfSources;
	}
	
	public void setNumberOfSources(int number) {
		this.numberOfSources = number;
	}
	
	public ArrayList<String> getSourcePaths() {
		return mainframe.getSourcePaths();
	}
	
	public void setSourcePaths(ArrayList<String> sourcePaths) {
		mainframe.setSourcePaths(sourcePaths);
		numberOfSources = sourcePaths.size();
	}
}
