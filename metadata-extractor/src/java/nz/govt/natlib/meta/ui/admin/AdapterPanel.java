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

/*
 * Created on 22/04/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.govt.natlib.meta.ui.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import nz.govt.natlib.AdapterFactory;
import nz.govt.natlib.AdapterFactoryListener;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.Profile;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * @author AParker
 * 
 * The panel to enable switching on and off adapters for the currently selected
 * profile.
 */
public class AdapterPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTable adapterTable;

	private AdapterTableModel tableModel;

	private DataAdapter[] adapters;

	private HashMap adaptersOn = new HashMap();

	private static JCheckBox editCbx = new JCheckBox();

	private static final String HEADER_ENABLE = "Enable";

	private static final String HEADER_DESC = "Description";

	private static final String HEADER_SCHEMA = "Adapter Schema";

	public AdapterPanel(JFrame parent) {
		this.setLayout(new BorderLayout());
		tableModel = new AdapterTableModel();
		readAdapters();
		adapterTable = new JTable(tableModel);
		jbInit();
	}

	private void jbInit() {
		JPanel mainPnl = new JPanel();
		Border adapterBorder = new TitledBorder(BorderFactory
				.createEtchedBorder(Color.white, new Color(148, 145, 140)),
				"Turn on/off Adapters");
		JPanel buttonPnl = new JPanel(new GridLayout(1, 2));
		JPanel buttonBfrPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JScrollPane scroller = new JScrollPane(adapterTable);
		adapterTable.getColumnModel().getColumn(0)
				.setHeaderValue(HEADER_ENABLE);
		adapterTable.getColumnModel().getColumn(1).setHeaderValue(HEADER_DESC);
		adapterTable.getColumnModel().getColumn(2)
				.setHeaderValue(HEADER_SCHEMA);
		mainPnl.setLayout(new BorderLayout());
		buttonBfrPnl.add(buttonPnl);
		mainPnl.add(scroller, BorderLayout.CENTER);
		mainPnl.add(buttonBfrPnl, BorderLayout.SOUTH);
		mainPnl.setBorder(adapterBorder);
		this.add(mainPnl, BorderLayout.CENTER);
		editCbx.setHorizontalAlignment(JCheckBox.CENTER);
		adapterTable.getColumnModel().getColumn(0).setCellRenderer(
				new ComponentRenderer());
		JCheckBox comp = new JCheckBox();
		comp.setHorizontalAlignment(JCheckBox.CENTER);
		adapterTable.getColumnModel().getColumn(0).setCellEditor(
				new DefaultCellEditor(comp));
		adapterTable.getColumnModel().getColumn(0).setMaxWidth(50);
		AdapterFactory.getInstance().addAdapterFactoryListener(
				new AdapterFactoryListener() {
					public void adapterAdded(DataAdapter fe) {
						readAdapters();
					}

					public void adapterRemoved(DataAdapter fe) {
						readAdapters();
					}
				});
		this.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				readAdapters();
			}
		});
	}

	private void readAdapters() {
		adapters = AdapterFactory.getInstance().getAdapters();
		adaptersOn = new HashMap();
		Iterator it = Config.getEditInstance().getCurrentProfile()
				.getAdapterClasses();
		while (it.hasNext()) {
			Object adapterClass = it.next();
			adaptersOn.put(adapterClass, adapterClass);
		}
		tableModel.fireTableDataChanged();
	}

	public void saveChanges() {
		Profile curr = Config.getEditInstance().getCurrentProfile();
		for (int i = 0; i < adapters.length; i++) {
			String adapterClass = adapters[i].getClass().getName();
			curr.setAdapter(adapterClass, adaptersOn.containsKey(adapterClass));
		}
	}

	private class AdapterTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int col) {
			return (col == 0);
		}

		public int getRowCount() {
			return adapters.length;
		}

		public int getColumnCount() {
			return 3;
		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return new Boolean(adaptersOn.containsKey(adapters[row]
						.getClass().getName()));
			case 1:
				return adapters[row].getInputType();
			case 2:
				return adapters[row].getOutputType();
			default:
				return null;
			}
		}

		public void setValueAt(Object val, int row, int col) {
			boolean adapterOn = val.equals(Boolean.TRUE);
			String className = adapters[row].getClass().getName();
			if (!adapterOn) {
				LogManager.getInstance().logMessage(
						LogMessage.INFO,
						"Switching on "
								+ className
								+ " adapter for "
								+ Config.getEditInstance().getCurrentProfile()
										.getName() + " profile");
				adaptersOn.remove(className);
			} else {
				LogManager.getInstance().logMessage(
						LogMessage.INFO,
						"Switching on "
								+ className
								+ " adapter for "
								+ Config.getEditInstance().getCurrentProfile()
										.getName() + " profile");
				adaptersOn.put(className, className);
			}
			Config.getEditInstance().getCurrentProfile().setAdapter(className,
					adapterOn);
		}
	}

	private class ComponentRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable t, Object value,
				boolean selected, boolean focus, int row, int col) {
			JCheckBox comp = new JCheckBox("", ((Boolean) value).booleanValue());
			comp.setHorizontalAlignment(JCheckBox.CENTER);
			return comp;
		}
	}
}
