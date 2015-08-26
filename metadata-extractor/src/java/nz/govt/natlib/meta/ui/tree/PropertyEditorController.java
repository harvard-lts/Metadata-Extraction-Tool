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

package nz.govt.natlib.meta.ui.tree;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import nz.govt.natlib.meta.ui.HorizLineBorder;
import nz.govt.natlib.meta.ui.ImageButton;
import nz.govt.natlib.meta.ui.ImagePanel;
import nz.govt.natlib.meta.ui.TitleBlock;

/**
 * 
 * @author unascribed
 * @version 1.0
 */

public class PropertyEditorController extends JPanel {

	private PropertyEditor view;

	ImageButton ok;

	ImageButton cancel;

	JPanel jPanel2 = new JPanel();

	GridLayout gridLayout1 = new GridLayout();

	GridBagLayout gridBagLayout1 = new GridBagLayout();

	Border lineBorder = new HorizLineBorder();

	TitleBlock titlePanel = new TitleBlock("xp_props.gif", "Edit Properties",
			"Edit/Alter the properties for an Object",
			"Use the fields below to edit the properties for this Object");

	JLabel imageLabel = new JLabel();

	public PropertyEditorController(PropertyContainer properties) {

		view = new PropertyEditor(properties);
		try {
			ok = new ImageButton("Ok", new ImageIcon(ImagePanel
					.resolveImage("button_ok.gif")));
			cancel = new ImageButton("Close", new ImageIcon(ImagePanel
					.resolveImage("button_close.gif")));
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void jbInit() throws Exception {
		jPanel2.setLayout(gridLayout1);
		gridLayout1.setHgap(5);

		ok.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok_actionPerformed(e);
			}
		});
		cancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel_actionPerformed(e);
			}
		});
		this.setLayout(gridBagLayout1);
		view.setBorder(lineBorder);
		jPanel2.add(ok, null);
		jPanel2.add(cancel, null);
		this.add(titlePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						2, 0, 2, 0), 0, 0));

		add(view, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 2, 2, 5), 0, 0));
		add(jPanel2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				new Insets(2, 5, 5, 2), 0, 0));
		add(titlePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(2, 0, 2, 0), 0, 0));
	}

	public void closeWindow() {
		this.getTopLevelAncestor().setVisible(false);
	}

	private void ok_actionPerformed(ActionEvent e) {
		view.ok();
		closeWindow();
	}

	private void cancel_actionPerformed(ActionEvent e) {
		view.cancel();
		closeWindow();
	}
}