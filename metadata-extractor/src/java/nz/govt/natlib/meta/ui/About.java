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

package nz.govt.natlib.meta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import nz.govt.natlib.meta.config.Config;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */

public class About extends JDialog {
	JPanel contentPane;

	JPanel jPanel1 = new JPanel();

	ImageButton okButton;

	GridBagLayout gridBagLayout1 = new GridBagLayout();

	JTextArea aboutText = new JTextArea();

	GridLayout gridLayout1 = new GridLayout();

	ImagePanel imagePanel = new ImagePanel(new Dimension(71, 115));

	Main controller;

	BorderLayout borderLayout1 = new BorderLayout();

	Icon okIcon;

	public About(Main controller) {
		super(controller, true);
		this.controller = controller;
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		okButton = new ImageButton("Ok", new ImageIcon(ImagePanel
				.resolveImage("button_ok.gif")));
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(gridBagLayout1);
		okButton.setMnemonic('O');
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});
		jPanel1.setLayout(gridLayout1);
		aboutText.setEnabled(false);
		aboutText.setFont(new java.awt.Font("SansSerif", 0, 12));
		aboutText.setBorder(null);
		aboutText.setOpaque(false);
		aboutText.setDisabledTextColor(Color.black);
		aboutText.setEditable(false);
		aboutText.setText(Config.getInstance().getApplicationName() + "\n\n"
				+ Config.getInstance().getCopyright());
		gridLayout1.setColumns(1);
		this.setModal(true);
		this.setResizable(false);
		this.setTitle("About");
		contentPane.add(jPanel1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				new Insets(10, 5, 2, 5), 0, 0));
		jPanel1.add(okButton, null);
		contentPane.add(aboutText, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						1, 5, 1, 5), 0, 0));
		contentPane.add(imagePanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 10), 0, 0));

		imagePanel.setImage("nav_logo.jpg");

		this.pack();
	}

	private void okButton_actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}

}