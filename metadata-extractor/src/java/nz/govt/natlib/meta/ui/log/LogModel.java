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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import nz.govt.natlib.meta.log.Log;
import nz.govt.natlib.meta.log.LogFilter;
import nz.govt.natlib.meta.log.LogLevel;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * UI Model for the log window.
 * 
 * @author unascribed
 * @version 1.0
 */

public class LogModel extends AbstractTableModel implements Log {

	private LogFilter filter;

	private ArrayList messages;

	private boolean suspendEvents = false;

	// this makes it a lot faster - with the propogation of events etc...
	private ArrayList filteredMessages;

	protected LogModel(LogMessage[] msg, LogFilter filter) {
		this();
		for (int i = 0; i < msg.length; i++) {
			messages.add(msg[i]);
		}
		setFilter(filter);
	}

	public LogModel() {
		messages = new ArrayList();
		filteredMessages = new ArrayList();
	}

	public LogMessage getRow(int i) {
		return (LogMessage) messages.get(i);
	}

	public int getColumnCount() {
		return 6;
	}

	public String getColumnName(int col) {
		String value = null;

		switch (col) {
		case 0:
			value = "Level";
			break;
		case 1:
			value = "ID";
			break;
		case 2:
			value = "Date";
			break;
		case 3:
			value = "Message";
			break;
		case 4:
			value = "Source";
			break;
		case 5:
			value = "Comment";
			break;
		}

		return value;
	}

	public Class getColumnClass(int col) {
		Class value = Object.class;

		switch (col) {
		case 0:
			value = LogLevel.class;
			break;
		case 1:
			value = Long.class;
			break;
		case 2:
			value = Date.class;
			break;
		case 3:
			value = String.class;
			break;
		case 4:
			value = Object.class;
			break;
		case 5:
			value = String.class;
			break;
		}

		return value;
	}

	public Object getValueAt(int row, int col) {
		LogMessage message = (LogMessage) filteredMessages.get(row);
		Object value = null;

		switch (col) {
		case 0:
			value = message.getLevel();
			break;
		case 1:
			value = new Long(message.getId());
			break;
		case 2:
			value = message.getDate();
			break;
		case 3:
			value = message.getMessage();
			break;
		case 4:
			value = message.getSource();
			break;
		case 5:
			value = message.getComment();
			break;
		}

		return value;
	}

	public int getRowCount() {
		// work out what passes in the filter...
		return filteredMessages.size();
	}

	public LogFilter getFilter() {
		return filter;
	}

	public void setFilter(LogFilter filter) {
		this.filter = filter;

		// refilter everything
		Iterator it = messages.iterator();
		while (it.hasNext()) {
			LogMessage msg = (LogMessage) it.next();
			if (filter.filter(msg)) {
				if (!filteredMessages.contains(msg))
					filteredMessages.add(msg);
			} else {
				if (filteredMessages.contains(msg))
					filteredMessages.remove(msg);
			}
		}

		this.fireTableDataChanged();
	}

	public void logMessage(LogMessage msg) {
		messages.add(msg);
		if ((filter == null) || (filter.filter(msg))) {
			filteredMessages.add(msg);
		}
		if (!suspendEvents)
			this.fireTableDataChanged();
	}

	public void close() {
		filteredMessages.clear();
		messages.clear();
		this.fireTableDataChanged();
	}

	public void suspendEvents(boolean suspendEvents) {
		this.suspendEvents = suspendEvents;
		if (suspendEvents == false) {
			this.fireTableDataChanged();
		}
	}

	public void clear() {
		filteredMessages.clear();
		messages.clear();
		this.fireTableDataChanged();
	}

}