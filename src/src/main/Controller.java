package main;

import gui.Mainframe;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Controller {
	
	Mainframe mainframe;

	public void startController() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				mainframe = new Mainframe(Controller.this);
				mainframe.frmTotalbackup.setVisible(true);
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
	}
}
