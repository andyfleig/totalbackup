package main;

import gui.Mainframe;

public class Main {

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
	          public void run() {
	               Mainframe.main(null);
	          }
	    });

	}

}
