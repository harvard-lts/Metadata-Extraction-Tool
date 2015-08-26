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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import nz.govt.natlib.AdapterFactory;
import nz.govt.natlib.AdapterFactoryListener;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.ConfigMapEntry;
import nz.govt.natlib.meta.config.Configuration;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.ImageButton;
import nz.govt.natlib.meta.ui.ImagePanel;
import nz.govt.natlib.meta.ui.NLNZCombo;

/**
 * @author AParker
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MappingPanel extends JPanel {
	private JTable mappingTable;

	private MappingTableModel tableModel;

	private ImageButton addMap, delMap;

	private ConfigMapEntry[] maps;

	private ArrayList adapterOutputs;

	private ArrayList configArray;

	private ArrayList xsltMaps;

	private JFrame parent;

	private ImageIcon delPic, addPic, mapPic;

	private NLNZCombo inputDTDCbx = new NLNZCombo();

	private NLNZCombo mappingCbx = new NLNZCombo();

	private NLNZCombo outputDTDCbx = new NLNZCombo();

	private boolean refreshing = false;

	public MappingPanel(JFrame parent) {
		this.parent = parent;
		readAdapters();
		readMaps();
		maps = Config.getEditInstance().getMappings();
		tableModel = new MappingTableModel(maps);
		mappingTable = new JTable(tableModel);
		jbInit();
	}

	private void jbInit() {
		this.setLayout(new BorderLayout());
		JPanel cbxPnl = new JPanel(new GridLayout(1, 3, 5, 5));
		cbxPnl.add(inputDTDCbx);
		cbxPnl.add(mappingCbx);
		cbxPnl.add(outputDTDCbx);
		cbxPnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JScrollPane scroller = new JScrollPane(mappingTable);
		JPanel mainPnl = new JPanel();
		JPanel buttonPnl = new JPanel(new GridLayout(1, 2, 5, 5));
		JPanel buttonBfrPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		mainPnl.setLayout(new BorderLayout());
		mappingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		try {
			addPic = new ImageIcon(ImagePanel
					.resolveImage("button_add_mapping.gif"));
			delPic = new ImageIcon(ImagePanel
					.resolveImage("button_delete_mapping.gif"));
			mapPic = new ImageIcon(ImagePanel
					.resolveImage("mapping_screen.gif"));
		} catch (Exception e) {
			LogManager.getInstance().logMessage(
					new LogMessage(LogMessage.ERROR, e, "Image not found",
							"Installation may be corrupt?"));
		}
		Border mapBorder = new TitledBorder(BorderFactory.createEtchedBorder(
				Color.white, new Color(148, 145, 140)), "Add/Remove Mappings");
		addMap = new ImageButton("Add", addPic);
		addMap.addActionListener(new AddMapAction());
		delMap = new ImageButton("Delete", delPic);
		delMap.addActionListener(new DelMapAction());
		buttonPnl.add(addMap);
		buttonPnl.add(delMap);
		JPanel bottomPnl = new JPanel(new BorderLayout());
		buttonBfrPnl.add(buttonPnl);
		bottomPnl.add(cbxPnl, BorderLayout.NORTH);
		bottomPnl.add(buttonBfrPnl, BorderLayout.SOUTH);
		mainPnl.add(scroller, BorderLayout.CENTER);
		mainPnl.add(bottomPnl, BorderLayout.SOUTH);
		mainPnl.setBorder(mapBorder);
		this.add(mainPnl, BorderLayout.CENTER);
		mappingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mappingTable.setRowSelectionAllowed(true);
		mappingTable.getColumnModel().getColumn(0).setHeaderValue(
				"Adapter Output");
		mappingTable.getColumnModel().getColumn(1).setHeaderValue("Mapping");
		mappingTable.getColumnModel().getColumn(2).setHeaderValue(
				"Final Schema");
		if (tableModel.getRowCount() > 0) {
			mappingTable.setRowSelectionInterval(0, 0);
		}
		AdapterFactory.getInstance().addAdapterFactoryListener(
				new AdapterFactoryListener() {
					public void adapterAdded(DataAdapter fe) {
						readAdapters();
						readMaps();
					}

					public void adapterRemoved(DataAdapter fe) {
						readAdapters();
						readMaps();
						tableModel.setMaps(Config.getEditInstance()
								.getMappings());
					}
				});
	}

	public void refresh() {
		if (!refreshing) {
			refreshing = true;
			readAdapters();
			readMaps();
			tableModel.setMaps(Config.getEditInstance().getMappings());
			refreshing = false;
		}
	}

	private void readAdapters() {
		adapterOutputs = new ArrayList();
		DataAdapter[] adapters = AdapterFactory.getInstance().getAdapters();
		for (int i = 0; i < adapters.length; i++) {
			adapterOutputs.add(adapters[i].getOutputType());
		}
		inputDTDCbx
				.setModel(new DefaultComboBoxModel(adapterOutputs.toArray()));
		List configs = Config.getEditInstance().getAvailableConfigs();
		configArray = new ArrayList();
		for (int i = 0; i < configs.size(); i++) {
			String output = ((Configuration) configs.get(i)).getOutputDTD();
			if ((output != null) && (output.length() > 0)) {
				configArray.add(output);
			}
		}
		outputDTDCbx.setModel(new DefaultComboBoxModel(configArray.toArray()));
	}

	private void readMaps() {
		xsltMaps = new ArrayList();
		File xmlDir = new File(Config.getInstance().getXMLBaseURL());
		String[] mapFiles = xmlDir.list(new FilenameFilter() {
			public boolean accept(File f, String filename) {
				return filename.toLowerCase().endsWith("xslt");
			}
		});
		if (mapFiles != null) {
			for (int i = 0; i < mapFiles.length; i++) {
				xsltMaps.add(mapFiles[i]);
			}
		}
		mappingCbx.setModel(new DefaultComboBoxModel(xsltMaps.toArray()));
	}

	private void addNewMap() {
		int inputDTD = inputDTDCbx.getSelectedIndex();
		int mappingXSLT = mappingCbx.getSelectedIndex();
		int outputDTD = outputDTDCbx.getSelectedIndex();
		ConfigMapEntry map = new ConfigMapEntry((String) adapterOutputs
				.get(inputDTD), (String) configArray.get(outputDTD),
				(String) xsltMaps.get(mappingXSLT));
		if (isNewMap((String) adapterOutputs.get(inputDTD),
				(String) configArray.get(outputDTD))) {
			tableModel.addMap(map);
			Config.getEditInstance().addMapping(map);
			mappingTable.setRowSelectionInterval(tableModel.getRowCount() - 1,
					tableModel.getRowCount() - 1);
			LogManager.getInstance().logMessage(
					LogMessage.INFO,
					"Mapping added: " + map.getInputDTD() + " : "
							+ map.getXsltFunction() + " : "
							+ map.getOutputDTD());
		} else {
			LogManager.getInstance().logMessage(
					LogMessage.INFO,
					"Mapping not added - already exists: " + map.getInputDTD()
							+ " : " + map.getXsltFunction() + " : "
							+ map.getOutputDTD());
			JOptionPane.showMessageDialog(this,
					"A mapping already exists for the selected schemas",
					"Unable to create mapping", JOptionPane.WARNING_MESSAGE,
					mapPic);
		}
	}

	private boolean isNewMap(String input, String output) {
		// System.out.println("Checking map creation: "+input+" - "+output);
		int num = tableModel.getRowCount();
		for (int i = 0; i < num; i++) {
			ConfigMapEntry map = tableModel.getMap(i);
			// System.out.println("Checking : "+map.getInputDTD()+" -
			// "+map.getOutputDTD());
			if (map.getInputDTD().equals(input)
					&& map.getOutputDTD().equals(output)) {
				return false;
			}
		}
		return true;
	}

	private void delMap() {
		int idx = mappingTable.getSelectedRow();
		if (idx > -1) {
			ConfigMapEntry getMap = tableModel.getMap(idx);
			if (getMap.getInputDTD().equals("default.dtd")) {
				int res = JOptionPane
						.showConfirmDialog(
								this,
								"Are you sure you want to uninstall system mapping?\nthis could render the harvester inoperable!",
								"Remove mapping", JOptionPane.WARNING_MESSAGE);
				// JOptionPane.showConfirmDialog(this,
				// "Unable to uninstall system mapping",
				// "Unable to remove mapping",JOptionPane.WARNING_MESSAGE,
				// mapPic);
				if (res != JOptionPane.OK_OPTION) {
					return;
				}
			}
			ConfigMapEntry map = tableModel.removeMap(idx);
			Config.getEditInstance().removeMapping(map);
			if (tableModel.getRowCount() > 0) {
				int sel = Math.max(Math.min(idx, tableModel.getRowCount() - 1),
						0);
				mappingTable.setRowSelectionInterval(sel, sel);
			}
		}
	}

	private class AddMapAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			addNewMap();
		}
	}

	private class DelMapAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			delMap();
		}
	}

	public List getUsers() {
		ArrayList l = new ArrayList();
		// for(int i=0;i<listModel.getSize();i++){
		// l.add(listModel.get(i));
		// }
		return l;
	}

	// private class MappingTableModel implements ListSelectionModel{
	// }

	private class MappingTableModel extends AbstractTableModel {
		ArrayList mappings = new ArrayList();

		public MappingTableModel(ConfigMapEntry[] maps) {
			setMaps(maps);
		}

		public void setMaps(ConfigMapEntry[] maps) {
			mappings = new ArrayList();
			for (int i = 0; i < maps.length; i++) {
				mappings.add(maps[i]);
			}
			this.fireTableDataChanged();
		}

		public void addMap(ConfigMapEntry map) {
			mappings.add(map);
			this.fireTableDataChanged();
		}

		public List getMappings() {
			return (List) mappings.clone();
		}

		public ConfigMapEntry getMap(int index) {
			if (index < mappings.size()) {
				ConfigMapEntry map = (ConfigMapEntry) mappings.get(index);
				return map;
			}
			return null;
		}

		public ConfigMapEntry removeMap(int index) {
			if (index < mappings.size()) {
				ConfigMapEntry map = (ConfigMapEntry) mappings.get(index);
				mappings.remove(index);
				fireTableDataChanged();
				return map;
			}
			return null;
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public int getRowCount() {
			return mappings.size();
		}

		public int getColumnCount() {
			return 3;
		}

		public Object getValueAt(int row, int col) {
			ConfigMapEntry map = (ConfigMapEntry) mappings.get(row);
			switch (col) {
			case 0:
				return map.getInputDTD();
			case 1:
				return map.getXsltFunction();
			case 2:
				return map.getOutputDTD();
			default:
				return null;
			}
		}
	}

	private class ComponentRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable t, Object value,
				boolean selected, boolean focus, int row, int col) {
			if (value instanceof JComboBox) {
				return (JComboBox) value;
			} else {
				TableCellLabel comp;
				if (value != null) {
					comp = new TableCellLabel(value.toString());
				} else {
					comp = new TableCellLabel("");
				}
				comp.setSelected(selected);
				return comp;
			}
		}
	}

	private class TableCellLabel extends JTextField {
		public TableCellLabel(String s) {
			super(s);
		}

		void setSelected(boolean isSelected) {
			if (isSelected) {
				this.setBackground(Color.LIGHT_GRAY);
			} else {
				this.setBackground(Color.WHITE);
			}
		}
	}
}
