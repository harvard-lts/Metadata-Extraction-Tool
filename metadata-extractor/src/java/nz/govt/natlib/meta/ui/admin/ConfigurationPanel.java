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
 * Created on 19/05/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.govt.natlib.meta.ui.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import nz.govt.natlib.meta.Harvester;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.Configuration;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.FileDialog;
import nz.govt.natlib.meta.ui.ImageButton;
import nz.govt.natlib.meta.ui.ImagePanel;

/**
 * @author aparker
 * 
 * Panel to maintain configurations
 */
public class ConfigurationPanel extends JPanel {
	private JFrame parent;

	private JTable configTable;

	private ConfigurationTableModel tableModel;

	private ImageButton addConfig, delConfig;

	private ArrayList configs;

	private ImageIcon addPic, folderPic;

	private ImageIcon delPic, cancelPic, configPic;

	private static final String ICON_FOLDER = "xp_folder_small.gif";

	private static final String ICON_ADD_CONFIG = "button_add_config.gif";

	private static final String ICON_DEL_CONFIG = "button_del_config.gif";

	private static final String ICON_CANCEL = "button_cancel.gif";

	private static final String ICON_CONFIG = "icon_config_32.gif";

	public ConfigurationPanel(JFrame parent) {
		this.parent = parent;
		configs = Config.getEditInstance().getAvailableConfigs();
		tableModel = new ConfigurationTableModel(configs);
		configTable = new JTable(tableModel);
		jbInit();
	}

	public void refresh() {
		configs = Config.getEditInstance().getAvailableConfigs();
		tableModel.populate(configs);
	}

	private void jbInit() {
		try {
			folderPic = new ImageIcon(ImagePanel.resolveImage(ICON_FOLDER));
			addPic = new ImageIcon(ImagePanel.resolveImage(ICON_ADD_CONFIG));
			delPic = new ImageIcon(ImagePanel.resolveImage(ICON_DEL_CONFIG));
			cancelPic = new ImageIcon(ImagePanel.resolveImage(ICON_CANCEL));
			configPic = new ImageIcon(ImagePanel.resolveImage(ICON_CONFIG));
		} catch (Exception e) {
			LogManager.getInstance().logMessage(e);
		}
		this.setLayout(new BorderLayout());
		JScrollPane scroller = new JScrollPane(configTable);
		JPanel mainPnl = new JPanel();
		JPanel buttonPnl = new JPanel(new GridLayout(1, 2, 5, 5));
		JPanel buttonBfrPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		mainPnl.setLayout(new BorderLayout());
		configTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Border configBorder = new TitledBorder(BorderFactory
				.createEtchedBorder(Color.white, new Color(148, 145, 140)),
				"Add/Remove Configurations");
		addConfig = new ImageButton("Add", addPic);
		addConfig.addActionListener(new AddConfigAction());
		delConfig = new ImageButton("Delete", delPic);
		delConfig.addActionListener(new DelConfigAction());
		buttonPnl.add(addConfig);
		buttonPnl.add(delConfig);
		JPanel bottomPnl = new JPanel(new BorderLayout());
		buttonBfrPnl.add(buttonPnl);
		bottomPnl.add(buttonBfrPnl, BorderLayout.SOUTH);
		mainPnl.add(scroller, BorderLayout.CENTER);
		mainPnl.add(bottomPnl, BorderLayout.SOUTH);
		mainPnl.setBorder(configBorder);
		this.add(mainPnl, BorderLayout.CENTER);
		configTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		configTable.setRowSelectionAllowed(true);
		configTable.getColumnModel().getColumn(0).setHeaderValue("Name");
		configTable.getColumnModel().getColumn(1).setHeaderValue(
				"Harvester Class");
		configTable.getColumnModel().getColumn(2).setHeaderValue("Output Dir");
		configTable.getColumnModel().getColumn(3).setHeaderValue("Output DTD");
		if (tableModel.getRowCount() > 0) {
			configTable.setRowSelectionInterval(0, 0);
		}
	}

	private void addNewConfig() {
		NewConfigDlg dlg = new NewConfigDlg();
		if (dlg.showDialog()) {
			String name = dlg.getConfigurationName();
			String className = dlg.getConfigurationClass();
			String outputDTD = dlg.getOutputDtd();
			String outputDir = dlg.getOutputDir();
			Configuration config = new Configuration(name, className,
					outputDir, outputDTD);
			tableModel.addConfiguration(config);
			Config.getEditInstance().addConfig(config);
			configTable.setRowSelectionInterval(tableModel.getRowCount() - 1,
					tableModel.getRowCount() - 1);
			LogManager.getInstance().logMessage(LogMessage.INFO,
					"Configuration added: " + name);
		}
	}

	private void delConfig() {
		int idx = configTable.getSelectedRow();
		if (idx > -1) {
			Configuration config = tableModel.removeConfiguration(idx);
			Config.getEditInstance().removeConfig(config);
			if (tableModel.getRowCount() > 0) {
				int sel = Math.max(Math.min(idx, tableModel.getRowCount() - 1),
						0);
				configTable.setRowSelectionInterval(sel, sel);
			}
			LogManager.getInstance().logMessage(LogMessage.INFO,
					"Configuration removed: " + config.getName());
		}
	}

	private class AddConfigAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			addNewConfig();
		}
	}

	private class DelConfigAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			delConfig();
		}
	}

	private class ConfigurationTableModel extends AbstractTableModel {
		ArrayList configList;

		public ConfigurationTableModel(ArrayList configs) {
			populate(configs);
		}

		public void populate(ArrayList configs) {
			configList = new ArrayList();
			for (int i = 0; i < configs.size(); i++) {
				configList.add(configs.get(i));
			}
			this.fireTableDataChanged();
		}

		public void addConfiguration(Configuration config) {
			configList.add(config);
			this.fireTableDataChanged();
		}

		public Configuration removeConfiguration(int index) {
			if (index < configList.size()) {
				Configuration config = (Configuration) configList.get(index);
				configList.remove(index);
				fireTableDataChanged();
				return config;
			}
			return null;
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public int getRowCount() {
			return configList.size();
		}

		public int getColumnCount() {
			return 4;
		}

		public Object getValueAt(int row, int col) {
			Configuration config = (Configuration) configList.get(row);
			switch (col) {
			case 0:
				return config.getName();
			case 1:
				return config.getClassName();
			case 2:
				return config.getOutputDirectory();
			case 3:
				return config.getOutputDTD();
			default:
				return null;
			}
		}
	}

	private class NewConfigDlg extends JDialog {
		private JTextField configName = new JTextField();

		private ImageButton cancel = new ImageButton("Cancel", cancelPic);

		private ImageButton ok = new ImageButton("Add", addPic);

		private ImageButton folder = new ImageButton(folderPic);

		private JTextField configClass = new JTextField();

		private JTextField outputDir = new JTextField();

		private JTextField outputDTD = new JTextField();

		private boolean okResult = false;

		public NewConfigDlg() {
			super(parent, true);
			outputDTD.setEditable(false);
			outputDTD.setFocusable(false);
			JPanel mainPnl = new JPanel(new GridBagLayout());
			mainPnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			mainPnl.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			Insets labelInsets = new Insets(5, 5, 5, 5);
			Insets fieldInsets = new Insets(2, 2, 2, 2);
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = labelInsets;
			mainPnl.add(new JLabel("Name:"), gbc);
			gbc.insets = fieldInsets;
			gbc.gridx = 1;
			gbc.gridwidth = 2;
			gbc.weightx = 3;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			mainPnl.add(configName, gbc);
			gbc.fill = GridBagConstraints.NONE;
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 1;
			gbc.weightx = 0;
			gbc.insets = labelInsets;
			mainPnl.add(new JLabel("Harvester Class:"), gbc);
			gbc.insets = fieldInsets;
			gbc.gridx = 1;
			gbc.gridwidth = 2;
			gbc.weightx = 3;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			mainPnl.add(configClass, gbc);

			gbc.fill = GridBagConstraints.NONE;
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.gridwidth = 1;
			gbc.weightx = 0;
			gbc.insets = labelInsets;
			mainPnl.add(new JLabel("Output DTD:"), gbc);
			gbc.insets = fieldInsets;
			gbc.gridx = 1;
			gbc.gridwidth = 2;
			gbc.weightx = 3;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			mainPnl.add(outputDTD, gbc);

			gbc.fill = GridBagConstraints.NONE;
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.gridwidth = 1;
			gbc.weightx = 0;
			gbc.insets = labelInsets;
			mainPnl.add(new JLabel("Output Dir:"), gbc);
			gbc.insets = fieldInsets;
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 3;
			mainPnl.add(outputDir, gbc);
			gbc.fill = GridBagConstraints.NONE;
			gbc.gridx = 2;
			gbc.weightx = 0;
			mainPnl.add(folder, gbc);

			JPanel buttonPnl = new JPanel(new BorderLayout());
			buttonPnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			JPanel buttons = new JPanel(new GridLayout(1, 2, 5, 5));
			buttonPnl.add(buttons, BorderLayout.EAST);
			buttons.add(cancel);
			buttons.add(ok);

			gbc.gridx = 0;
			gbc.gridy = 4;
			gbc.anchor = GridBagConstraints.EAST;
			gbc.gridwidth = 3;
			gbc.fill = GridBagConstraints.NONE;
			mainPnl.add(buttonPnl, gbc);
			getContentPane().add(mainPnl);

			ActionListener okListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if ((configName.getText().length() == 0)
							|| (configClass.getText().length() == 0)
							|| (outputDir.getText().length() == 0)) {
						JOptionPane.showMessageDialog(parent,
								"Not all fields are completed",
								"Configuration not added",
								JOptionPane.ERROR_MESSAGE, configPic);
						okResult = false;
					} else if ("CLASS ERROR".equals(outputDTD.getText())) {
						JOptionPane
								.showMessageDialog(
										parent,
										"The Harvester class is not found or is incorrect",
										"Configuration not added",
										JOptionPane.ERROR_MESSAGE, configPic);
						okResult = false;
					} else if (!new File(outputDir.getText()).isDirectory()) {
						JOptionPane.showMessageDialog(parent,
								"The output directory does not exist",
								"Configuration not added",
								JOptionPane.ERROR_MESSAGE, configPic);
						okResult = false;
					} else {
						okResult = true;
					}
					setVisible(!okResult);
				}
			};
			ok.addActionListener(okListener);
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					okResult = false;
					setVisible(false);
				}
			});
			folder.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File selected = FileDialog.showSelectDialog(parent,
							new File(outputDir.getText()).getAbsolutePath());
					if (selected != null) {
						outputDir.setText(selected.getAbsolutePath());
					}
				}
			});

			configClass.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent fe) {
					String har = configClass.getText();
					String outDTD = "";
					if ((har != null) && (har.length() > 0)) {
						try {
							Harvester h = (Harvester) Class.forName(har)
									.newInstance();
							if (h.getOutputType() != null) {
								outDTD = h.getOutputType();
							} else {
								outDTD = "NONE";
							}
						} catch (InstantiationException e) {
							outDTD = "CLASS ERROR";
						} catch (ClassNotFoundException e1) {
							outDTD = "CLASS ERROR";
						} catch (IllegalAccessException e2) {
							outDTD = "CLASS ERROR";
						} catch (Exception e3) {
							outDTD = "CLASS ERROR";
						} catch (Throwable e3) {
							outDTD = "CLASS ERROR";
						}
					}
					outputDTD.setText(outDTD);
				}
			});

			this.setTitle("Add New Configuration");
			this.setSize(430, 180);
			this.setResizable(false);
			Dimension size = this.getSize();
			Dimension parentSize = parent.getSize();
			Point parentLoc = parent.getLocation();
			int x = ((parentSize.width - size.width) / 2) + parentLoc.x;
			int y = ((parentSize.height - size.height) / 2) + parentLoc.y;
			this.setLocation(x, y);

		}

		public boolean showDialog() {
			show();
			return okResult;
		}

		public String getConfigurationName() {
			return configName.getText();
		}

		public String getConfigurationClass() {
			return configClass.getText();
		}

		public String getOutputDir() {
			return outputDir.getText();
		}

		public String getOutputDtd() {
			return outputDTD.getText();
		}
	}

}
