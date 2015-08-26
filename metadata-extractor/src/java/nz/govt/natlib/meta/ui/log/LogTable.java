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

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;

/**
 * 
 * @author unascribed
 * @version 1.0
 */

public class LogTable extends JTable {

	private TableCellRenderer defaultRenderer = new MultiLineRenderer();

	public LogTable() {
		super();
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setShowHorizontalLines(false);
	}

	public void setColumnWidths() {
		getColumnModel().getColumn(0).setMaxWidth(30);
		getColumnModel().getColumn(1).setMaxWidth(75);
		getColumnModel().getColumn(2).setMinWidth(125);
		getColumnModel().getColumn(3).setMinWidth(200);
		getColumnModel().getColumn(4).setMinWidth(200);
		getColumnModel().getColumn(5).setMinWidth(200);
	}

	public TableCellRenderer getDefaultRenderer(Class renderer) {
		return defaultRenderer;
	}

	public void tableChanged(TableModelEvent e) {
		setRowHeight(16);
		super.tableChanged(e);
	}

	public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
			boolean extend) {
		int oldSelected = getSelectedRow();
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
		int newSelected = getSelectedRow();

		// reset the height of the old row
		if (oldSelected != -1) {
			setRowHeight(16);
		}

		// reset the height of the new row...
		if (newSelected != -1) {
			// find out what the biggest component is and reset the row size
			// accordingly...
			int rowMax = getRowHeight();
			for (int i = 0; i < getColumnCount(); i++) {
				TableCellRenderer renderer = getCellRenderer(rowIndex, i);
				Component c = prepareRenderer(renderer, rowIndex, i);
				int height = c.getHeight();
				if (height > rowMax)
					rowMax = height;
			}
			setRowHeight(rowIndex, rowMax);
		}
	}
}
