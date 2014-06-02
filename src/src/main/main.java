package main;

import gui.mainframe;

public class main {

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
	          public void run() {
	               mainframe.main(null);
	          }
	    });

	}

}
