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

import data.Filter;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

/**
 * ToDo
 *
 * @author Andreas Fleig
 */
public class FilterListCell extends ListCell<Filter> {
	private GridPane gridPane = new GridPane();
	private Label lb_path = new Label();
	private Label lb_mode = new Label();

	public FilterListCell() {
		gridPane.add(lb_path, 0, 0);
		gridPane.add(lb_mode, 0 ,1);
	}

	@Override
	public void updateItem(Filter content, boolean empty) {
		super.updateItem(content, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setText(null);
			lb_path.setText(content.getPath());
			if (content.getMode() == 0) {
				lb_mode.setText("Exclusion-Filter");
			} else if (content.getMode() == 1) {
				lb_mode.setText("MD5-Filter");
			}
			setGraphic(gridPane);
		}
	}
}
