/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package nz.govt.natlib.meta.ui.log;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import nz.govt.natlib.meta.log.LogLevel;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.ImagePanel;

/**
 * 
 * @author unascribed
 * @version 1.0
 */
public class LogLevelRenderer extends JPanel implements TableCellRenderer {

	Icon max;

	Icon error;

	Icon min;

	Color light = new Color(255, 255, 255);

	Color dark = new Color(230, 230, 255);

	Insets insets = new Insets(0, 0, 0, 0);

	private JLabel label = new JLabel();

	public LogLevelRenderer() {
		try {
			max = new ImageIcon(ImagePanel.resolveImage("log_error.gif"));
			error = new ImageIcon(ImagePanel.resolveImage("log_problem.gif"));
			min = new ImageIcon(ImagePanel.resolveImage("log_message.gif"));

			this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			this.add(label);
		} catch (Exception ex) {
			LogManager.getInstance().logMessage(LogMessage.ERROR,
					"Logging icons not found");
		}
	}

	public Insets getInsets() {
		return insets;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean selected, boolean hasFocus, int row, int col) {
		LogLevel level = (LogLevel) value;

		label.setText(" ");
		this.setToolTipText(level.getName());

		if (level.getLevel() >= LogMessage.ERROR.getLevel()) {
			label.setIcon(max);
		} else if (level.getLevel() >= LogMessage.INFO.getLevel()) {
			label.setIcon(error);
		} else {
			label.setIcon(min);
		}

		if (selected) {
			label.setForeground(table.getSelectionForeground());
			this.setBackground(table.getSelectionBackground());
		} else {
			label.setForeground(table.getForeground());
			if (row % 2 == 0) {
				this.setBackground(dark);
			} else {
				this.setBackground(light);
			}
		}

		return this;
	}
}
