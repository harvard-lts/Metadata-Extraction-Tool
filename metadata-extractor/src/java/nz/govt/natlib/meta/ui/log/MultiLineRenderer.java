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
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;

import nz.govt.natlib.meta.log.LogManager;

/**
 * 
 * @author unascribed
 * @version 1.0
 */
public class MultiLineRenderer extends DefaultTableCellRenderer {

	JTextArea renderer = new JTextArea();

	int height = 0;

	NumberFormat numberFormatter = NumberFormat.getInstance();

	SimpleDateFormat shortDateformatter = new SimpleDateFormat(
			"H:mm:ss yyyy-m-d");;

	SimpleDateFormat longDateformatter = new SimpleDateFormat(
			"H:mm:ss.SSa (z)\nE, d-MMM-yyyy");;

	Color light = new Color(255, 255, 255);

	Color dark = new Color(230, 230, 255);

	public MultiLineRenderer() {
		JLabel lab = new JLabel();
		renderer.setText(null);
		renderer.setBorder(null);
		renderer.setFont(lab.getFont());
		height = Toolkit.getDefaultToolkit().getFontMetrics(lab.getFont())
				.getHeight();
		renderer.setTabSize(1);
	}

	private int getRows(String text) {
		int rows = 1;
		int index = text.indexOf('\n');
		while (index != -1) {
			rows++;
			index = text.indexOf('\n', index + 1);
		}
		return rows;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean selected, boolean hasFocus, int row, int col) {
		Color unselectedForeground = table.getForeground();
		String str = "";

		if (selected) {
			if (value instanceof Number) {
				str = numberFormatter.format(value);
			} else if (value instanceof Date) {
				str = longDateformatter.format(value);
			} else if (value instanceof Throwable) {
				str = LogManager.flattenStack((Throwable) value);
				unselectedForeground = Color.red.darker();
			} else {
				str = (value == null) ? "" : value.toString();
			}

			// sort out dimensions - so that the table will render properly
			Rectangle rect = table.getCellRect(row, col, true);
			int newh = height * (selected ? getRows(str) : 1);
			renderer.setSize(0, newh);

			renderer.setForeground(table.getSelectionForeground());
			renderer.setBackground(table.getSelectionBackground());
		} else {
			str = (value == null) ? "" : value.toString();
			renderer.setForeground(unselectedForeground);
			if (row % 2 == 0) {
				renderer.setBackground(dark);
			} else {
				renderer.setBackground(light);
			}
		}

		renderer.setText(str);
		return renderer;
	}
}
