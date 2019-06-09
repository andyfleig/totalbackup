/*
 * Copyright 2014 - 2016 Andreas Fleig (andy DOT fleig AT gmail DOT com)
 *
 * All rights reserved.
 *
 * This file is part of TotalBackup.
 *
 * TotalBackup is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TotalBackup is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TotalBackup.  If not, see <http://www.gnu.org/licenses/>.
 */
package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * AboutDialog containing general information about totalbackup like the version number.
 *
 * @author Andreas Fleig
 */
public class AboutDialog {
	static Stage stage;

	@FXML
	private Label lbl_versionAndCopyright;
	@FXML
	private ImageView if_logo;

	public void OKAction() {
		stage.close();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void initialize() {
		String infoText = "TotalBackup v0.1pre5 beta";
		infoText = infoText.concat("\nCopyright 2014-2019 Andreas Fleig - All rights reserved");
		infoText = infoText.concat("\nLicense: GPLv3+");
		infoText = infoText.concat("\n" + System.getProperty("java.runtime.name") + " " + System.getProperty("java.version"));


		lbl_versionAndCopyright.setText(infoText);
		try {
			FileInputStream input = new FileInputStream("./src/de/andyfleig/totalbackup/resources/TB_logo.png");
			Image image = new Image(input);
			if_logo.setImage(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
