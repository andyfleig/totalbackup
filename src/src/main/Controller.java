package main;

import gui.Mainframe;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Controller {
	
	Mainframe mainframe;

	public Controller() {
		mainframe = new Mainframe(this);
	}

	public void startController() {
		//mainframe.main(null);
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				mainframe.main(null);
			}
		});
		
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
		//System.out.println("Test: " + mainframe.getTfDestinationPath());
	}
}
