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

package nz.govt.natlib.meta.ui.help;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import nz.govt.natlib.meta.ui.HorizLineBorder;
import nz.govt.natlib.meta.ui.ImageButton;
import nz.govt.natlib.meta.ui.ImagePanel;
import nz.govt.natlib.meta.ui.TitleBlock;

/**
 * 
 * @author unascribed
 * @version 1.0
 */

public class HelpWindow extends JFrame {
	ImageIcon closeIcon;

	BorderLayout borderLayout1 = new BorderLayout();

	JPanel jPanel1 = new JPanel();

	FlowLayout flowLayout1 = new FlowLayout();

	TitleBlock titlePanel = new TitleBlock("xp_help.gif", "Help",
			"Simple help for extracting metadata is given below",
			"For more detailed help please consult the manual");

	JPanel jPanel2 = new JPanel();

	JScrollPane jScrollPane1 = new JScrollPane();

	JEditorPane jEditorPane1 = new JEditorPane();

	BorderLayout borderLayout2 = new BorderLayout();

	public HelpWindow(JFrame parent) {
		super("Harvester Help");
		setIconImage(parent.getIconImage());
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		this.getContentPane().setLayout(borderLayout1);
		try {
			closeIcon = new ImageIcon(ImagePanel
					.resolveImage("button_close.gif"));
		} catch (Exception e) {
		}
		ImageButton closeHelp = new ImageButton("Close", closeIcon);
		closeHelp.setMnemonic('C');
		closeHelp.setText("Close");
		closeHelp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeHelp_actionPerformed(e);
			}
		});
		jPanel1.setLayout(flowLayout1);
		jEditorPane1.setBackground(Color.WHITE);
		flowLayout1.setAlignment(FlowLayout.RIGHT);
		jPanel2.setLayout(borderLayout2);
		borderLayout2.setHgap(5);
		borderLayout2.setVgap(5);
		borderLayout1.setHgap(5);
		borderLayout1.setVgap(5);
		this.getContentPane().add(titlePanel, BorderLayout.NORTH);
		this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
		jPanel1.add(closeHelp, null);
		this.getContentPane().add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(jScrollPane1, BorderLayout.CENTER);
		jPanel2.setBorder(new HorizLineBorder());
		jEditorPane1.setEditable(false);
		jScrollPane1.getViewport().add(jEditorPane1);

		HTMLEditorKit doc = new HTMLEditorKit();
		jEditorPane1.setEditorKit(doc);
		URL url = ClassLoader.getSystemResource("help.html");
		jEditorPane1.setPage(url);
	}

	void closeHelp_actionPerformed(ActionEvent e) {
		setVisible(false);
	}
}