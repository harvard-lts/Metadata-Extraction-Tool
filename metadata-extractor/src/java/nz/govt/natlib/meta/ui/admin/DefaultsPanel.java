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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.User;
import nz.govt.natlib.meta.config.UserListener;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.FileDialog;
import nz.govt.natlib.meta.ui.ImageButton;
import nz.govt.natlib.meta.ui.NLNZCombo;

/**
 * @author AParker
 * 
 * A Panel with some application defaults
 */
public class DefaultsPanel extends JPanel implements UserListener {
	private JFrame parent;

	private JTextField inputDir = new JTextField();

	private JTextField logDir = new JTextField();

	private NLNZCombo defaultUser = new NLNZCombo();

	private NLNZCombo configCmb = new NLNZCombo();

	private boolean refreshing = false;

	private ImageIcon passwordIcon, passwordButtonIcon;

	private static final String ICON_SECURITY = "security.gif";

	private static final String BUTTON_SECURITY = "button_security.gif";

	private static final String BUTTON_FOLDER = "xp_folder_small.gif";

	public DefaultsPanel(JFrame parent) {
		this.parent = parent;
		jbInit();
	}

	private void jbInit() {
		JPanel mainPnl = new JPanel() {
			public Dimension getMinimumSize() {
				Dimension d = super.getMinimumSize();
				d.width = Math.max(d.width, 400);
				return d;
			}

			public Dimension getPreferredSize() {
				return getMinimumSize();
			}
		};
		ImageIcon folder = null;
		// try{
		// passwordIcon = new ImageIcon(ImagePanel.resolveImage(ICON_SECURITY));
		// passwordButtonIcon = new
		// ImageIcon(ImagePanel.resolveImage(BUTTON_SECURITY));
		// folder = new ImageIcon(ImagePanel.resolveImage(BUTTON_FOLDER));
		// }catch(Exception e){
		// LogManager.getInstance().logMessage(new
		// LogMessage(LogMessage.ERROR,e,"Image not found","Installation may be
		// corrupt?"));
		// }
		Border defaultsBorder = new TitledBorder(BorderFactory
				.createEtchedBorder(Color.white, new Color(148, 145, 140)),
				"Default Options");
		Border passwordBorder = new TitledBorder(BorderFactory
				.createEtchedBorder(Color.white, new Color(148, 145, 140)),
				"Change Admin Password");
		JPanel bufferPnl = new JPanel(new BorderLayout());
		mainPnl.setLayout(new BorderLayout());
		JPanel optionPnl = new JPanel(new GridBagLayout());
		// JPanel passwordPnl = new JPanel(new GridBagLayout());
		// passwordPnl.setBorder(passwordBorder);
		optionPnl.setBorder(defaultsBorder);
		JLabel userLbl = new JLabel("Default User:");
		refresh();
		configCmb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				Config.getEditInstance().setDefaultUser(
						(User) configCmb.getSelectedItem());
			}
		});
		JLabel outputDirLbl = new JLabel("Input Directory:");
		JLabel logDirLbl = new JLabel("Log Directory:");
		ImageButton logDirButt = null;
		ImageButton inputDirButt = null;
		logDirButt = new ImageButton(folder);
		inputDirButt = new ImageButton(folder);
		logDirButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File selected = FileDialog.showSelectDialog(parent, new File(
						logDir.getText()).getAbsolutePath());
				if ((selected != null) && (selected.isDirectory())) {
					logDir.setText(selected.getAbsolutePath());
				}
			}
		});
		inputDirButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File selected = FileDialog.showSelectDialog(parent, new File(
						inputDir.getText()).getAbsolutePath());
				if ((selected != null) && (selected.isDirectory())) {
					inputDir.setText(selected.getAbsolutePath());
				}
			}
		});
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.weightx = 0;
		optionPnl.add(userLbl, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 3;
		gbc.gridx = 1;
		optionPnl.add(configCmb, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0;
		optionPnl.add(outputDirLbl, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		optionPnl.add(inputDir, gbc);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		optionPnl.add(inputDirButt, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;
		optionPnl.add(logDirLbl, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		optionPnl.add(logDir, gbc);
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		optionPnl.add(logDirButt, gbc);

		JLabel oldPasswordLbl = new JLabel("Old Password:");
		JLabel newPasswordLbl = new JLabel("New Password:");
		JLabel confirmPasswordLbl = new JLabel("Confirm Password:");
		final JPasswordField oldPasswordTxt = new JPasswordField();
		final JPasswordField newPasswordTxt = new JPasswordField();
		final JPasswordField confirmPasswordTxt = new JPasswordField();
		ImageButton changeBtn = new ImageButton("Change Password!",
				passwordButtonIcon);

		// GridBagConstraints pgbc = new GridBagConstraints();
		// pgbc.gridx = 0;
		// pgbc.gridy = 0;
		// pgbc.insets = new Insets(5,5,5,5);
		// pgbc.weightx = 0;
		// passwordPnl.add(oldPasswordLbl,pgbc);
		// pgbc.gridx = 1;
		// pgbc.weightx = 3;
		// pgbc.fill = GridBagConstraints.HORIZONTAL;
		// passwordPnl.add(oldPasswordTxt,pgbc);
		// pgbc.gridx = 0;
		// pgbc.gridy = 1;
		// pgbc.weightx = 0;
		// pgbc.fill = GridBagConstraints.NONE;
		// passwordPnl.add(newPasswordLbl,pgbc);
		// pgbc.gridx = 1;
		// pgbc.weightx = 3;
		// pgbc.fill = GridBagConstraints.HORIZONTAL;
		// passwordPnl.add(newPasswordTxt,pgbc);
		// pgbc.gridx = 0;
		// pgbc.gridy = 2;
		// pgbc.weightx = 0;
		// pgbc.fill = GridBagConstraints.NONE;
		// passwordPnl.add(confirmPasswordLbl,pgbc);
		// pgbc.gridx = 1;
		// pgbc.weightx = 3;
		// pgbc.fill = GridBagConstraints.HORIZONTAL;
		// passwordPnl.add(confirmPasswordTxt,pgbc);
		// pgbc.gridx = 2;
		// pgbc.gridy = 2;
		// pgbc.weightx = 0;
		// pgbc.fill = GridBagConstraints.NONE;
		// passwordPnl.add(changeBtn,pgbc);

		changeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String oldPassword = new String(oldPasswordTxt.getPassword());
				String newPassword = new String(newPasswordTxt.getPassword());
				String confirmPassword = new String(confirmPasswordTxt
						.getPassword());
				if (Config.getInstance().checkAdminPassword(oldPassword)) {
					if (newPassword.equals(confirmPassword)) {
						Config.getInstance().setAdminPassword(oldPassword,
								newPassword);
						JOptionPane.showMessageDialog(parent,
								"Password changed", "Password changed",
								JOptionPane.INFORMATION_MESSAGE, passwordIcon);
						LogManager.getInstance().logMessage(LogMessage.INFO,
								"Admin password changed");
					} else {
						JOptionPane.showMessageDialog(parent,
								"Confirm password differs from new password",
								"Password not changed",
								JOptionPane.ERROR_MESSAGE, passwordIcon);
					}
				} else {
					JOptionPane.showMessageDialog(parent,
							"Old password is incorrect",
							"Password not changed", JOptionPane.ERROR_MESSAGE,
							passwordIcon);
					LogManager
							.getInstance()
							.logMessage(LogMessage.INFO,
									"Admin password not changed due to incorrect old password");
				}
			}
		});

		mainPnl.add(optionPnl, BorderLayout.SOUTH);
		bufferPnl.add(mainPnl, BorderLayout.NORTH);
		// bufferPnl.add(passwordPnl,BorderLayout.SOUTH);
		this.add(bufferPnl);
		refresh();
		Config.getEditInstance().addUserListener(this);
	}

	public void refresh() {
		refreshing = true;
		User[] users = Config.getEditInstance().getUsers();
		configCmb.setModel(new DefaultComboBoxModel(users));
		configCmb.setSelectedItem(Config.getEditInstance().getDefaultUser());
		this.logDir.setText(Config.getEditInstance().getLogDirectory());
		this.inputDir.setText(Config.getEditInstance().getBaseHarvestDir());
		refreshing = false;
	}

	public void userAdded(User user) {
		User oldUser = (User) configCmb.getSelectedItem();
		configCmb.setModel(new DefaultComboBoxModel(Config.getEditInstance()
				.getUsers()));
		configCmb.setSelectedItem(oldUser);
	}

	public void userRemoved(User user) {
		User oldUser = (User) configCmb.getSelectedItem();
		configCmb.setModel(new DefaultComboBoxModel(Config.getEditInstance()
				.getUsers()));
		if (!oldUser.equals(user) && configCmb.getModel().getSize() > 0) {
			configCmb.setSelectedIndex(0);
		} else {
			configCmb.setSelectedItem(oldUser);
		}
	}

	public void saveChanges() {
		Config.getEditInstance().setDefaultUser(
				(User) defaultUser.getSelectedItem());
		Config.getEditInstance().setBaseHarvestDir(inputDir.getText());
		Config.getEditInstance().setLogDirectory(logDir.getText());
	}

}
