/*
 * Copyright 2014 - 2019 Andreas Fleig (github AT andyfleig DOT de)
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

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Static helper containing various general methods for the JavaFx GUI.
 *
 * @author Andreas Fleig
 */
public final class GuiHelper {

	/**
	 * Opens an Error-Window with the given header and content.
	 *
	 * @param header  header message of the error
	 * @param content content message of the error
	 */
	public static void showErrorWindow(String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.setResizable(true);
		alert.showAndWait();
	}

	/**
	 * Creates an Confirmation-Window with the given header and content. Provides a YES and a NO button.
	 *
	 * @param header  header message of the error
	 * @param content content message of the error
	 * @return pressed button Optional
	 */
	public static Optional<ButtonType> showConfirmationWindows(String header, String content) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO);
		alert.setTitle("Warning");
		alert.setHeaderText(header);
		alert.setResizable(true);
		return alert.showAndWait();
	}

}
