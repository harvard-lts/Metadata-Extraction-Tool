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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * 
 * @author unascribed
 * @version 1.0
 */

public class TitleBlock extends JPanel {
	Border lineBorder = new HorizLineBorder();

	GridBagLayout gridBagLayout2 = new GridBagLayout();

	JLabel titleLabel = new JLabel();

	JLabel descLabel = new JLabel();

	JPanel imageArea = new JPanel();

	JLabel helpLabel = new JLabel();

	BorderLayout borderLayout2 = new BorderLayout();

	JLabel imageLabel = new JLabel();

	String iconName = "xp_log.gif";

	public TitleBlock(String iconName, String title, String description,
			String help) {
		this.iconName = iconName;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		setTitle(title);
		setDescription(description);
		setHelp(help);
	}

	void jbInit() throws Exception {
		Image image = ImagePanel.resolveImage(iconName);
		imageLabel.setIcon(new ImageIcon(image));
		setLayout(gridBagLayout2);
		setFont(new java.awt.Font("Dialog", 1, 12));
		titleLabel.setFont(new java.awt.Font("Dialog", 1, 12));
		titleLabel.setText(" ");
		descLabel.setText(" ");
		helpLabel.setText(" ");
		imageArea.setLayout(borderLayout2);
		setBackground(Color.white);
		imageArea.setOpaque(false);
		add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(2, 5, 2, 5), 0, 0));
		add(descLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(0, 15, 2, 5), 0, 0));
		add(imageArea, new GridBagConstraints(1, 0, 1, 3, 1.0, 0.0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL,
				new Insets(2, 5, 2, 5), 0, 0));
		imageArea.add(imageLabel, BorderLayout.EAST);
		add(helpLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(0, 15, 5, 5), 0, 0));
		setBorder(lineBorder);
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	public void setHelp(String help) {
		helpLabel.setText(help);
	}

	public void setDescription(String description) {
		descLabel.setText(description);
	}
}