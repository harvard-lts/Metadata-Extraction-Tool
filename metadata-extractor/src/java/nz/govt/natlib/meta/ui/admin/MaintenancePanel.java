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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import nz.govt.natlib.AdapterFactory;
import nz.govt.natlib.FileUtil;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.ConfigMapEntry;
import nz.govt.natlib.meta.config.JarResources;
import nz.govt.natlib.meta.config.Loader;
import nz.govt.natlib.meta.config.Profile;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.FileDialog;
import nz.govt.natlib.meta.ui.ImageButton;
import nz.govt.natlib.meta.ui.ImagePanel;

/**
 * @author AParker
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MaintenancePanel extends JPanel {
	private JTable adapterTable;

	private AdapterTableModel tableModel;

	private ArrayList adapterList = new ArrayList();

	private ImageButton addAdapter, delAdapter;

	private ImageIcon addPic, delPic, folder, cancelPic;

	private JFrame parent;

	private static final String BUTTON_DELETE_ADAPTER = "button_delete_adapter.gif";

	private static final String BUTTON_ADD_ADAPTER = "button_add_adapter.gif";

	private static final String BUTTON_CANCEL = "button_cancel.gif";

	private static final String BUTTON_FOLDER = "xp_folder_small.gif";

	public MaintenancePanel(JFrame parent) {
		this.parent = parent;
		this.setLayout(new BorderLayout());
		readAdapters();
		tableModel = new AdapterTableModel();
		adapterTable = new JTable(tableModel);
		jbInit();
	}

	private void jbInit() {
		JPanel mainPnl = new JPanel();
		Border adapterBorder = new TitledBorder(BorderFactory
				.createEtchedBorder(Color.white, new Color(148, 145, 140)),
				"Install/Uninstall Adapters");
		JPanel buttonPnl = new JPanel(new GridLayout(1, 2));
		JPanel buttonBfrPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JScrollPane scroller = new JScrollPane(adapterTable);
		adapterTable.getColumnModel().getColumn(0).setHeaderValue("Name");
		adapterTable.getColumnModel().getColumn(1).setHeaderValue(
				"Adapter Schema");
		adapterTable.getColumnModel().getColumn(2).setHeaderValue("Jar File");
		try {
			cancelPic = new ImageIcon(ImagePanel.resolveImage(BUTTON_CANCEL));
			addPic = new ImageIcon(ImagePanel.resolveImage(BUTTON_ADD_ADAPTER));
			delPic = new ImageIcon(ImagePanel
					.resolveImage(BUTTON_DELETE_ADAPTER));
			folder = new ImageIcon(ImagePanel.resolveImage(BUTTON_FOLDER));
		} catch (Exception e) {
			LogManager.getInstance().logMessage(
					new LogMessage(LogMessage.ERROR, e, "Image not found",
							"Installation may be corrupt?"));
		}
		Border mapBorder = new TitledBorder(BorderFactory.createEtchedBorder(
				Color.white, new Color(148, 145, 140)), "Add/Remove Mappings");
		addAdapter = new ImageButton("Add", addPic);
		addAdapter.addActionListener(new AddAdapterAction());
		delAdapter = new ImageButton("Delete", delPic);
		delAdapter.addActionListener(new DelAdapterAction());
		buttonPnl.add(addAdapter);
		buttonPnl.add(delAdapter);
		mainPnl.setLayout(new BorderLayout());
		buttonBfrPnl.add(buttonPnl);
		mainPnl.add(scroller, BorderLayout.CENTER);
		mainPnl.add(buttonBfrPnl, BorderLayout.SOUTH);
		mainPnl.setBorder(adapterBorder);
		this.add(mainPnl, BorderLayout.CENTER);
		this.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent fe) {
				refresh();
			}
		});
	}

	public void refresh() {
		readAdapters();
		tableModel.fireTableDataChanged();
	}

	private void addNewAdapter() {
		NewAdapterDlg dlg = new NewAdapterDlg();
		if (dlg.showDialog()) {
			String jarName = dlg.getJarName();
			String adapter = dlg.getAdapterClass();
			LogManager.getInstance().logMessage(LogMessage.INFO,
					"Installing " + adapter);

			File jarFile = new File(jarName);
			// Get other xslt/xml/xsd/dtd stuff
			Iterator it = dlg.getXmlFiles();
			JarResources jarRes = new JarResources(jarFile.getAbsolutePath());
			while (it.hasNext()) {
				String xmlFile = (String) it.next();
				LogManager.getInstance().logMessage(LogMessage.INFO,
						"Installing " + xmlFile + " in xml directory");
				byte[] res = jarRes.getResource(xmlFile);
				File newRes = new File(Config.getInstance().getXMLBaseURL(),
						new File(xmlFile).getName());
				try {
					if (newRes.createNewFile()) {
						FileOutputStream fos = new FileOutputStream(newRes);
						fos.write(res);
						fos.close();
					}
				} catch (Exception e) {
					LogManager.getInstance().logMessage(
							new LogMessage(LogMessage.ERROR, e,
									"File error transferring xml file "
											+ newRes.getAbsolutePath(),
									"Adapter may be corrupt?"));
				}
			}
			if (!jarFile.getParent().equals(
					Config.getInstance().getJarBaseURL())) {
				File newJarFile = new File(
						Config.getInstance().getJarBaseURL(), jarFile.getName());
				FileUtil.copy(jarFile.getAbsolutePath(), newJarFile
						.getAbsolutePath());
				jarFile = newJarFile;
			}
			Loader.loadJar(jarFile.getAbsolutePath());
			try {
				DataAdapter adapterImpl = (DataAdapter) ClassLoader
						.getSystemClassLoader().loadClass(adapter)
						.newInstance();
				AdapterFactory.getInstance().addAdapter(adapterImpl);
				ArrayList profiles = Config.getInstance()
						.getAvailableProfiles();
				Iterator itp = profiles.iterator();
				while (itp.hasNext()) {
					Profile p = (Profile) itp.next();
					p.setAdapter(adapterImpl.getClass().getName(), true);
				}
				ArrayList editprofiles = Config.getEditInstance()
						.getAvailableProfiles();
				Iterator itp2 = editprofiles.iterator();
				while (itp2.hasNext()) {
					Profile p = (Profile) itp2.next();
					p.setAdapter(adapterImpl.getClass().getName(), true);
				}
				Config.getEditInstance().setJarForAdapter(adapterImpl,
						jarFile.getName());
				Config.getInstance().setJarForAdapter(adapterImpl,
						jarFile.getName());
				refresh();
			} catch (Exception e) {
				LogManager.getInstance().logMessage(
						new LogMessage(LogMessage.ERROR, e,
								"Problem loading adapter class",
								"Adapter may be corrupt?"));
			}
		}
	}

	private void delAdapter() {
		int idx = adapterTable.getSelectedRow();
		if (idx > -1) {
			DataAdapter adapter = tableModel.getAdapter(idx);
			if (Config.getEditInstance().getJarForAdapter(adapter).equals(
					Config.SYSTEM_ADAPTER)) {
				JOptionPane.showMessageDialog(this,
						"Unable to uninstall system adapter "
								+ adapter.getName(),
						"Unable to remove adapter",
						JOptionPane.WARNING_MESSAGE, delPic);
			} else {
				int result = JOptionPane.showConfirmDialog(this,
						"Are you sure you wish to uninstall "
								+ adapter.getName(), "Remove Adapter",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
						delPic);
				if (result == JOptionPane.OK_OPTION) {
					removeAdapter(adapter);
				}
			}
		}
	}

	private void removeAdapter(DataAdapter adapter) {
		LogManager.getInstance().logMessage(LogMessage.INFO,
				"Removing adapter " + adapter.getName());
		ConfigMapEntry[] maps = Config.getEditInstance().getMappings();
		HashMap mapSet = new HashMap();
		for (int i = 0; i < maps.length; i++) {
			mapSet.put(maps[i].getXsltFunction(), maps[i]);
		}
		String jar = Config.getEditInstance().getJarForAdapter(adapter);
		if (jar != null) {
			File jarFile = new File(Config.getInstance().getJarBaseURL(), jar);
			JarResources jarRes = new JarResources(jarFile.getAbsolutePath());
			Set jarFiles = jarRes.listContents();
			Iterator it = jarFiles.iterator();
			while (it.hasNext()) {
				String fileName = (String) it.next();
				if (fileName.endsWith(".xslt") || fileName.endsWith(".dtd")
						|| fileName.endsWith(".xml")
						|| fileName.endsWith(".xsd")) {
					LogManager.getInstance().logMessage(LogMessage.INFO,
							"Removing " + fileName + " from xml directory");
					File file = new File(Config.getInstance().getXMLBaseURL(),
							fileName);
					if (file.exists()) {
						if (!file.delete()) {
							LogManager.getInstance().logMessage(
									LogMessage.ERROR,
									"Problem removing " + fileName
											+ " from xml directory");
						}
						// Remove mappings associated with this file
						ConfigMapEntry map = (ConfigMapEntry) mapSet
								.get(fileName);
						if (map != null) {
							LogManager.getInstance().logMessage(
									LogMessage.ERROR,
									"Removing map using "
											+ map.getXsltFunction()
											+ " from mappings table");
							Config.getEditInstance().removeMapping(map);
							Config.getInstance().removeMapping(map);
						}
					}
				}
			}
			// Actually remove adapter
			AdapterFactory.getInstance().removeAdapter(adapter);
			File jarBackup = new File(Config.getInstance().getJarBaseURL(), jar
					+ ".bak");
			if (jarBackup.exists()) {
				jarBackup.delete();
			}
			LogManager.getInstance().logMessage(LogMessage.INFO,
					"Renaming adapter jar to " + adapter.getName() + ".bak");
			FileUtil.copy(jarFile.getAbsolutePath(), jarBackup
					.getAbsolutePath());
			jarFile.deleteOnExit();
		}
		Config.saveAdapterEdit();
		refresh();
	}

	private class AddAdapterAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			addNewAdapter();
		}
	}

	private class DelAdapterAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			delAdapter();
		}
	}

	private void readAdapters() {
		DataAdapter[] adapters = AdapterFactory.getInstance().getAdapters();
		adapterList.removeAll(adapterList);
		for (int i = 0; i < adapters.length; i++) {
			this.adapterList.add(adapters[i]);
		}
	}

	public void saveChanges() {
	}

	private class NewAdapterDlg extends JDialog {
		private JTextField jarNameTxt = new JTextField();

		private ImageButton cancel = new ImageButton("Cancel", cancelPic);

		private ImageButton findJarBtn = new ImageButton(folder);

		private ImageButton ok = new ImageButton("Install", addPic);

		private JLabel adapterTxt = new JLabel("Select Adapter");

		private String jarName;

		private String adapterClass;

		private ArrayList xmlFiles;

		private boolean canceled = false;

		public NewAdapterDlg() {
			super(parent, true);
			JPanel jarPnl = new JPanel(new BorderLayout());
			JPanel centerPnl = new JPanel(new GridLayout(2, 1));
			jarPnl.add(jarNameTxt, BorderLayout.CENTER);
			jarPnl.add(findJarBtn, BorderLayout.EAST);
			centerPnl.add(jarPnl);
			centerPnl.add(adapterTxt);
			getContentPane().setLayout(new BorderLayout());
			JPanel mainPnl = new JPanel(new BorderLayout());
			getContentPane().add(mainPnl, BorderLayout.CENTER);
			mainPnl.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
			mainPnl.add(centerPnl, BorderLayout.NORTH);
			JPanel buttons = new JPanel(new GridLayout(1, 2));
			buttons.add(cancel);
			buttons.add(ok);
			JPanel buttBuffer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			buttBuffer.add(buttons);
			mainPnl.add(buttBuffer, BorderLayout.SOUTH);
			ActionListener okListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			};
			jarNameTxt.addActionListener(okListener);
			ok.addActionListener(okListener);
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					canceled = true;
					setVisible(false);
				}
			});
			findJarBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File selected = FileDialog.showSelectDialog(parent, Config
							.getEditInstance().getJarBaseURL());
					if (selected != null) {
						jarNameTxt.setText(selected.getAbsolutePath());
						analyseTHIS();
					}
				}
			});
			ok.setEnabled(false);
			adapterTxt.setForeground(Color.DARK_GRAY);
			this.setTitle("Add New Adpater from Jar");
			this.setSize(450, 120);
			// this.setResizable(false);
			Dimension size = this.getSize();
			Dimension parentSize = parent.getSize();
			Point parentLoc = parent.getLocation();
			int x = ((parentSize.width - size.width) / 2) + parentLoc.x;
			int y = ((parentSize.height - size.height) / 2) + parentLoc.y;
			this.setLocation(x, y);
		}

		public String getAdapterClass() {
			return adapterClass;
		}

		public Iterator getXmlFiles() {
			return xmlFiles.iterator();
		}

		private void analyseTHIS() {
			adapterTxt.setText("Analysing...");
			adapterTxt.setForeground(Color.BLUE);
			JarResources jar = new JarResources(jarNameTxt.getText());
			Set contents = jar.listContents();
			Iterator it = contents.iterator();
			xmlFiles = new ArrayList();
			adapterClass = null;
			Loader miniLoader = new Loader();
			while (it.hasNext()) {
				String path = (String) it.next();
				if (path.endsWith(".class")) {
					if (adapterClass == null) {
						String className = path.substring(0,
								path.indexOf(".class")).replace('/', '.');
						byte[] classBytes = null;
						classBytes = jar.getResource(path);
						if (classBytes != null) {
							Class cl = null;
							try {
								cl = miniLoader.returnClass(className,
										classBytes, 0, classBytes.length);
							} catch (NoClassDefFoundError cnf) {
								// LogManager.getInstance().logMessage(new
								// LogMessage(LogMessage.WORTHLESS_CHATTER,cnf,"Adapter
								// class load ignored","Adapter may be corrupt,
								// but more likely a subclass has been
								// ignored?"));
								continue;
							} catch (IllegalAccessError illegalAccessError) {
								// LogManager.getInstance().logMessage(new
								// LogMessage(LogMessage.WORTHLESS_CHATTER,cnf,"Adapter
								// class load ignored","Adapter may be corrupt,
								// but more likely a subclass has been
								// ignored?"));
								continue;
							} catch (Throwable e) {
								LogManager
										.getInstance()
										.logMessage(
												new LogMessage(
														LogMessage.INFO,
														e,
														"Adapter class failure",
														"Adapter may be corrupt, attempting to proceede"));
								continue;
							}
							if (cl != null) {
								if (DataAdapter.class
										.equals(cl.getSuperclass())) {
									adapterClass = cl.getName();
								}
							}
						}
					}
				} else if (path.endsWith(".xslt") || path.endsWith(".dtd")
						|| path.endsWith(".xml") || path.endsWith(".xsd")) {
					xmlFiles.add(path);
				}
			}
			if ((adapterClass != null) && (adapterClass.length() > 0)) {
				if (AdapterFactory.getInstance().isAdapterLoaded(adapterClass)) {
					adapterTxt.setForeground(Color.RED);
					adapterTxt.setText("Adapter " + adapterClass
							+ " already loaded!!");
					LogManager
							.getInstance()
							.logMessage(
									LogMessage.INFO,
									adapterClass
											+ " adapter attempted to be installed again");
					ok.setEnabled(false);
				} else {
					adapterTxt.setForeground(Color.BLUE);
					adapterTxt.setText("Adapter: " + adapterClass);
					ok.setEnabled(true);
				}
			} else {
				adapterTxt.setForeground(Color.RED);
				adapterTxt.setText("No Adapter Class Found!");
				LogManager.getInstance().logMessage(LogMessage.INFO,
						"No adapter class found in " + jarNameTxt.getText());
				ok.setEnabled(false);
			}
		}

		public boolean showDialog() {
			canceled = false;
			show();
			jarName = jarNameTxt.getText();
			return (adapterClass != null) && (adapterClass.length() > 0)
					&& !canceled;
		}

		public String getJarName() {
			return jarName;
		}
	}

	private class AdapterTableModel extends AbstractTableModel {
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public int getRowCount() {
			return adapterList.size();
		}

		public int getColumnCount() {
			return 3;
		}

		public DataAdapter getAdapter(int index) {
			DataAdapter res = null;
			if (index < adapterList.size()) {
				res = (DataAdapter) adapterList.get(index);
			}
			return res;
		}

		public Object getValueAt(int row, int col) {
			DataAdapter adapter = getAdapter(row);
			switch (col) {
			case 0:
				return adapter.getName();
			case 1:
				return adapter.getOutputType();
			case 2:
				return Config.getEditInstance().getJarForAdapter(adapter);
			default:
				return null;
			}
		}
	}

	private class ComponentRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable t, Object value,
				boolean selected, boolean focus, int row, int col) {
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

	private class TableCellLabel extends JTextField {
		public TableCellLabel(String s) {
			super(s);
			setEditable(false);
			this.setHorizontalAlignment(JTextField.RIGHT);
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
