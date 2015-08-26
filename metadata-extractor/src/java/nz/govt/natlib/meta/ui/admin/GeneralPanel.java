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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.Profile;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.FileDialog;
import nz.govt.natlib.meta.ui.ImageButton;
import nz.govt.natlib.meta.ui.ImagePanel;
import nz.govt.natlib.meta.ui.NLNZCombo;

/**
 * @author AParker
 * 
 * The general panel - the starting screen for administration
 */
public class GeneralPanel extends JPanel {
	private JFrame parent;

	private JTextField inputDir = new JTextField();

	private JTextField logDir = new JTextField();

	private NLNZCombo profileCmb;

	private Profile currentProfile;

	private ImageButton delConfigButt;

	private ImageButton createConfigButt;

	private boolean refreshing = false;

	private static final String BUTTON_DELETE = "button_delete_profile.gif";

	private static final String BUTTON_CREATE = "button_add_profile.gif";

	private static final String BUTTON_FOLDER = "xp_folder_small.gif";

	public GeneralPanel(JFrame parent) {
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
		try {
			delConfigButt = new ImageButton("Delete", new ImageIcon(ImagePanel
					.resolveImage(BUTTON_DELETE)));
			createConfigButt = new ImageButton("Create", new ImageIcon(
					ImagePanel.resolveImage(BUTTON_CREATE)));
			folder = new ImageIcon(ImagePanel.resolveImage(BUTTON_FOLDER));
		} catch (Exception e) {
			LogManager.getInstance().logMessage(
					new LogMessage(LogMessage.ERROR, e, "Image not found",
							"Installation may be corrupt?"));
		}
		Border configBorder = new TitledBorder(BorderFactory
				.createEtchedBorder(Color.white, new Color(148, 145, 140)),
				"Configuration Selection");
		Border settingBorder = new TitledBorder(BorderFactory
				.createEtchedBorder(Color.white, new Color(148, 145, 140)),
				"Options");
		JPanel bufferPnl = new JPanel(new BorderLayout());
		mainPnl.setLayout(new BorderLayout());
		JPanel configPnl = new JPanel(new GridBagLayout());
		configPnl.setBorder(configBorder);
		JPanel optionPnl = new JPanel(new GridBagLayout());
		optionPnl.setBorder(settingBorder);
		JLabel configLbl = new JLabel("Profile to edit:");
		profileCmb = new NLNZCombo();
		profileCmb.setEditable(true);
		createConfigButt.setEnabled(false);
		JLabel outputDirLbl = new JLabel("Input Directory:");
		JLabel logDirLbl = new JLabel("Log Directory:");
		ImageButton logDirButt = new ImageButton(folder);
		ImageButton inputDirButt = new ImageButton(folder);
		logDirButt.setToolTipText("Set Log Destination Folder");
		inputDirButt.setToolTipText("Set Input Directory Folder");

		delConfigButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (profileCmb.getSelectedIndex() > -1) {
					if (profileCmb.getModel().getSize() > 1) {
						delProfile((Profile) profileCmb.getSelectedItem());
					}
				}
			}
		});
		createConfigButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createProfile((String) profileCmb.getSelectedItem());
			}
		});
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
		profileCmb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (!refreshing) {
					if (profileCmb.getSelectedIndex() > -1) {
						createConfigButt.setEnabled(false);
						delConfigButt.setEnabled(true);
						loadProfile((Profile) profileCmb.getSelectedItem());
					} else {
						createConfigButt.setEnabled(true);
						delConfigButt.setEnabled(false);
					}
				}
			}
		});
		final JTextField editRegion = (JTextField) profileCmb.getEditor()
				.getEditorComponent();
		editRegion.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				delConfigButt.setEnabled(false);
				createConfigButt.setEnabled(editRegion.getText().length() > 0);
			}
		});
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.weightx = 0;
		configPnl.add(configLbl, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 3;
		gbc.gridx = 1;
		configPnl.add(profileCmb, gbc);
		gbc.weightx = 0;
		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.NONE;
		configPnl.add(delConfigButt, gbc);
		gbc.gridx = 3;
		configPnl.add(createConfigButt, gbc);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0;
		optionPnl.add(outputDirLbl, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		optionPnl.add(inputDir, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		optionPnl.add(inputDirButt, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		optionPnl.add(logDirLbl, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		optionPnl.add(logDir, gbc);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		optionPnl.add(logDirButt, gbc);
		mainPnl.add(configPnl, BorderLayout.NORTH);
		mainPnl.add(optionPnl, BorderLayout.SOUTH);
		bufferPnl.add(mainPnl, BorderLayout.NORTH);
		this.add(bufferPnl);
		refresh();
	}

	public void refresh() {
		ArrayList profiles = Config.getEditInstance().getAvailableProfiles();
		currentProfile = Config.getEditInstance().getDefaultProfile();
		profileCmb.setModel(new DefaultComboBoxModel(profiles.toArray()));
		populate();
	}

	private void createProfile(String name) {
		LogManager.getInstance().logMessage(LogMessage.INFO,
				"Creating profile [" + name + "]");
		Profile p = new Profile();
		p.setName(name);
		p.setInputDirectory(Config.getEditInstance().getBaseHarvestDir());
		p.setLogDirectory(Config.getEditInstance().getLogDirectory());
		Config.getEditInstance().addProfile(p);
		Config.getEditInstance().setCurrentProfile(p);
		refresh();
	}

	private void delProfile(Profile profile) {
		LogManager.getInstance().logMessage(LogMessage.INFO,
				"Deleting profile [" + profile.getName() + "]");
		Config.getEditInstance().removeProfile(profile);
		refresh();
	}

	private void loadProfile(Profile profile) {
		Config.getEditInstance().setCurrentProfile(profile);
		populate();
	}

	private void populate() {
		refreshing = true;
		this.logDir.setText(Config.getEditInstance().getCurrentProfile()
				.getLogDirectory());
		this.inputDir.setText(Config.getEditInstance().getCurrentProfile()
				.getInputDirectory());
		profileCmb
				.setSelectedItem(Config.getEditInstance().getCurrentProfile());
		refreshing = false;
	}

	public void saveChanges() {
		Config.getEditInstance().getCurrentProfile().setInputDirectory(
				inputDir.getText());
		Config.getEditInstance().getCurrentProfile().setLogDirectory(
				logDir.getText());
	}
}
