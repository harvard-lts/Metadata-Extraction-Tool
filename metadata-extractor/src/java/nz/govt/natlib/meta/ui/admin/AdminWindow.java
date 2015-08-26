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
 * Created on 21/04/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.govt.natlib.meta.ui.admin;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.ImageButton;
import nz.govt.natlib.meta.ui.ImagePanel;
import nz.govt.natlib.meta.ui.TitleBlock;

/**
 * @author AParker
 * 
 * The main window that manages all admin tab panels
 */
public class AdminWindow extends JDialog {
	private static final long serialVersionUID = 1L;

	private JTabbedPane mainTabs = new JTabbedPane();

	private JTabbedPane adminTabs = new JTabbedPane();

	private GeneralPanel generalPanel;

	private AdapterPanel adaptersPanel;

	private DefaultsPanel defaultsPanel;

	private ConfigurationPanel configPanel;

	private UserPanel userPanel;

	private MappingPanel mappingPanel;

	private MaintenancePanel adapterMaintenancePanel;

	private JFrame parent;

	private ImageButton applyBtn, cancelBtn;

	private JPanel adminPanel;

	private Image icon;

	private ImageIcon applyIcon, cancelIcon, configIcon, defaultIcon; // ,
																		// securityIcon;

	private ImageIcon userIcon, mappingIcon, adapterIcon, adaptersIcon,
			adminIcon;

	// private boolean unlocked = false;
	private String model = "";

	private static final String NORMAL_HELP = "You can administer all configuration elements from this window";

	private static final String NORMAL_DESC = "Shows all configurable administration elements";

	private static final String OK_ICON = "button_ok.gif";

	private static final String CANCEL_ICON = "button_cancel.gif";

	private static final String DEFAULTS_ICON = "icon_default.gif";

	private static final String CONFIG_ICON = "icon_config.gif";

	private static final String ADAPTER_ICON = "icon_adapter.gif";

	private static final String USERS_ICON = "icon_user.gif";

	private static final String MAINTENANCE_ICON = "icon_adapters.gif";

	private static final String ADMIN_ICON = "icon_admin.gif";

	private static final String MAPPINGS_ICON = "icon_mapping.gif";

	TitleBlock titlePanel = new TitleBlock("admin_icon.gif", "Administration",
			NORMAL_DESC, NORMAL_HELP);

	public AdminWindow(JFrame parent, String model) {
		super(parent, "Administration", true);
		this.parent = parent;
		// Just initialise the settings
		Config.getEditInstance(true);
		icon = parent.getIconImage();
		// setIconImage(icon);
		this.model = model;
		try {
			jbInit();
			pack();
		} catch (Exception e) {
			LogManager.getInstance().logMessage(e);
		}
	}

	public void show() {
		generalPanel.refresh();
		defaultsPanel.refresh();
		configPanel.refresh();
		userPanel.refresh();
		mappingPanel.refresh();
		adapterMaintenancePanel.refresh();
		super.show();
	}

	private void jbInit() throws Exception {
		applyIcon = new ImageIcon(ImagePanel.resolveImage(OK_ICON));
		cancelIcon = new ImageIcon(ImagePanel.resolveImage(CANCEL_ICON));
		configIcon = new ImageIcon(ImagePanel.resolveImage(CONFIG_ICON));
		defaultIcon = new ImageIcon(ImagePanel.resolveImage(DEFAULTS_ICON));
		userIcon = new ImageIcon(ImagePanel.resolveImage(USERS_ICON));
		mappingIcon = new ImageIcon(ImagePanel.resolveImage(MAPPINGS_ICON));
		adapterIcon = new ImageIcon(ImagePanel.resolveImage(ADAPTER_ICON));
		adaptersIcon = new ImageIcon(ImagePanel.resolveImage(MAINTENANCE_ICON));
		adminIcon = new ImageIcon(ImagePanel.resolveImage(ADMIN_ICON));
		applyBtn = new ImageButton("OK", applyIcon);
		cancelBtn = new ImageButton("Cancel", cancelIcon);
		this.getContentPane().setLayout(new GridBagLayout());
		generalPanel = new GeneralPanel(parent);
		mainTabs.addTab("General", new ImageIcon(icon), generalPanel,
				"General Config Options");
		adaptersPanel = new AdapterPanel(parent);
		mainTabs.addTab("Adapters", adaptersIcon, adaptersPanel,
				"Turn adapters on and off");
		adminPanel = new JPanel(new BorderLayout());
		mainTabs.addTab("Admin", adminIcon, adminPanel, "Admin Configuration");
		defaultsPanel = new DefaultsPanel(parent);
		userPanel = new UserPanel(parent);
		mappingPanel = new MappingPanel(parent);
		configPanel = new ConfigurationPanel(parent);
		adapterMaintenancePanel = new MaintenancePanel(parent);

		adminTabs.addTab("Defaults", defaultIcon, defaultsPanel,
				"Default Options");
		adminTabs.addTab("Users", userIcon, userPanel, "Add and Remove Users");
		adminTabs.addTab("Mappings", mappingIcon, mappingPanel,
				"Add and Remove Adapter Mappings");
		adminTabs.addTab("Configuration", configIcon, configPanel,
				"Configuration Maintenance Settings");
		adminTabs.addTab("Adapter Maintenance", adapterIcon,
				adapterMaintenancePanel, "Install and Uninstall Adapters");
		adminPanel.add(adminTabs);

		// mainTabs.addChangeListener(new ChangeListener(){
		// public void stateChanged(ChangeEvent ce) {
		// if(mainTabs.getSelectedComponent().equals(adminPanel)){
		// if((!unlocked) && (adminTries<3)){
		// showPasswordDialog();
		// }
		// }else{
		// }
		// }
		// });
		JPanel btnPnl = new JPanel(new GridLayout(1, 2, 5, 5));
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				cancel();
			}
		});
		applyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				save();
			}
		});
		btnPnl.add(cancelBtn);
		btnPnl.add(applyBtn);
		this.getContentPane().add(
				titlePanel,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(2, 0, 2, 0), 0, 0));
		this.getContentPane().add(
				mainTabs,
				new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(5, 2, 5, 2), 0, 0));
		this.getContentPane().add(
				btnPnl,
				new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(5, 2, 5, 2), 0, 0));
	}

	// /** Admin password has been entered and other tabs are now shown */
	// private void unlock(){
	// LogManager.getInstance().logMessage(LogMessage.INFO,"Admin logged in");
	// adminTabs.addTab("Defaults",defaultIcon,defaultsPanel,"Default Options");
	// adminTabs.addTab("Users",userIcon,userPanel,"Add and Remove Users");
	// adminTabs.addTab("Mappings",mappingIcon,mappingPanel,"Add and Remove
	// Adapter Mappings");
	// adminTabs.addTab("Configuration",configIcon,configPanel,"Configuration
	// Maintenance Settings");
	// adminTabs.addTab("Adapter
	// Maintenance",adapterIcon,adapterMaintenancePanel,"Install and Uninstall
	// Adapters");
	// adminPanel.add(adminTabs);
	// unlocked = true;
	// }

	// private void showPasswordDialog(){
	// final JDialog d = new JDialog(this,"Enter Admin Password",true){
	// public Dimension getPreferredSize(){
	// Dimension d = super.getPreferredSize();
	// d.width = Math.max(d.width,300);
	// return d;
	// }
	// public Dimension getMinimumSize(){
	// return getPreferredSize();
	// }
	// };
	//				
	// final JPasswordField p = new JPasswordField();
	// JPanel butts = new JPanel (new GridLayout(1,2,5,5));
	// ImageButton cancelButt = new ImageButton("Cancel",cancelIcon);
	// ImageButton logonButt = new ImageButton("Logon",applyIcon);
	// butts.add(cancelButt);
	// butts.add(logonButt);
	// ActionListener al = new ActionListener(){
	// public void actionPerformed(ActionEvent ae){
	// if(Config.getInstance().checkAdminPassword(new
	// String(p.getPassword()).trim())){
	// unlock();
	// d.setVisible(false);
	// }else{
	// JOptionPane.showMessageDialog(AdminWindow.this,
	// "Invalid Password",
	// "Invalid Password",
	// JOptionPane.WARNING_MESSAGE,
	// securityIcon);
	// LogManager.getInstance().logMessage(LogMessage.ERROR,"Admin password
	// incorrect ["+new String(p.getPassword()).trim()+"]");
	// adminTries++;
	// p.setText("");
	// if(adminTries>2){
	// JOptionPane.showMessageDialog(AdminWindow.this,
	// "Maximum Password Tries Exceeded - Contact Your Administrator",
	// "Password tries exceeded",
	// JOptionPane.WARNING_MESSAGE,
	// securityIcon);
	// d.setVisible(false);
	// LogManager.getInstance().logMessage(LogMessage.ERROR,"Admin password
	// limit exceeded");
	// }
	// }
	// }
	// };
	// logonButt.addActionListener(al);
	// cancelButt.addActionListener(new ActionListener(){
	// public void actionPerformed(ActionEvent e){
	// mainTabs.setSelectedIndex(0);
	// d.setVisible(false);
	// }
	// });
	// p.addActionListener(al);
	// JPanel nthPnl = new JPanel(new BorderLayout(5,5));
	// d.getContentPane().setLayout(new BorderLayout(5,5));
	// nthPnl.add(p,BorderLayout.CENTER);
	// nthPnl.add(new JLabel(" Enter Password: "),BorderLayout.WEST);
	// JPanel sthPnl = new JPanel(new BorderLayout());
	// sthPnl.add(butts,BorderLayout.EAST);
	// nthPnl.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
	// sthPnl.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	// d.getContentPane().add(sthPnl,BorderLayout.SOUTH);
	// d.getContentPane().add(nthPnl,BorderLayout.NORTH);
	// d.pack();
	// Dimension dlgSize = d.getPreferredSize();
	// Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	// int wid = (scrSize.width-dlgSize.width)/2;
	// int hgt = (scrSize.height-dlgSize.height)/2;
	// d.setLocation(wid,hgt);
	// d.setResizable(false);
	// d.show();
	// }

	/**
	 * The whole administration experience is cancelled - just revert back to
	 * old edit instance
	 */
	private void cancel() {
		LogManager.getInstance().logMessage(LogMessage.INFO,
				"Cancelling Administration Changes and closing admin console");
		setVisible(false);
		Config.getEditInstance(true);
	}

	/** Save the saveable changes back to the main Configuration repository */
	private void save() {
		LogManager.getInstance().logMessage(LogMessage.INFO,
				"Saving Administration Changes and closing admin console");
		generalPanel.saveChanges();
		adaptersPanel.saveChanges();
		Config.saveEdit();
		setVisible(false);
	}
}
