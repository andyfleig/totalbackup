package main;

import java.awt.Toolkit;
import java.awt.Dimension;

import java.util.Properties;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.ArrayList;

public class ProgramState {
	
	private Properties properties = new Properties();
	private static ProgramState programState = null;
	private Controller controller;
	
	/**
	 * Erzeugt einen ProgramState mit dem ünbergebenen Controller.
	 * @param c Controller für den ProgramState
	 */
	public ProgramState(Controller c) {
		this.controller = c;
	}
	
	/**
	 * Gibt die einzige Instanz des ProgramState zurück.
	 * @param c Controller für den ProgramState
	 * @return Gibt den ProgramState zurück
	 */
	public static ProgramState getInstance(Controller c) {
		if (programState == null) {
			programState = new ProgramState(c);
		}
		return programState;
	}
	
	/**
	 * Läd die, in der Properties-Datei gespeicherten Programm-Einstellugen.
	 * @param path Pfad der Properties-Datei
	 */
	public void loadProperties(String path) {
		File prop = new File(path);
		try {
			properties.load(new FileInputStream(prop));
			controller.setDestinationPath(properties.getProperty("destination"));
			int i = 0;
			ArrayList<String> sourcePaths = new ArrayList<String>();
			while (properties.getProperty("source." + i) != null) {
				sourcePaths.add(properties.getProperty("source." + i));
				i++;
			}
			controller.setSourcePaths(sourcePaths);
		} catch (FileNotFoundException e) {
			System.out.println("Fehler: Datei nicht gefunden");
		} catch (IOException e) {
			System.out.println("Fehler: IO-Fehler");
		}
	}
	
	/**
	 * Speichert die Programm-Einstellungen in die Properties-Datei.
	 * @param path Pfad zur Properties-Datei
	 */
	public void saveProperties(String path) {
		if (path == null) {
			throw new IllegalArgumentException();
		}
		File prop = new File(path);
		try {
			properties.load(new FileInputStream(prop));
			properties.setProperty("destination", controller.getDestinationPath());
			ArrayList<String> sources = controller.getSourcePaths();
			for (int i = 0; i < controller.getNumberOfSources(); i++) {
				properties.setProperty("source." + i, sources.get(i));
			}
			properties.store(new FileOutputStream(prop), "");
		} catch (FileNotFoundException e) {
			try {
                prop.createNewFile();
                saveProperties(path);
            } catch (IOException ex) {
                System.out.println("Fehler beim Erzeugen von " + path);
            }
		} catch (IOException e) {
			System.out.println("Fehler beim Schreiben von " + path);
		}
	}
}
