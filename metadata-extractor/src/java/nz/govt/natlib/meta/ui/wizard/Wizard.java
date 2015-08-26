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

package nz.govt.natlib.meta.ui.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
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

public class Wizard extends JDialog {

	private int current = 0;

	private ArrayList items = null;

	Border lineBorder = new HorizLineBorder();

	GridBagLayout gridBagLayout1 = new GridBagLayout();

	JPanel jPanel1 = new JPanel();

	ImageButton next;// = new JButton();

	ImageButton finish;// = new JButton();

	ImageButton previous;// = new JButton();

	ImageButton cancel;// = new JButton();

	JPanel itemPanel = new JPanel();

	CardLayout cardLayout1 = new CardLayout();

	Border border1;

	String title;

	TitleBlock titlePanel = new TitleBlock("xp_folder.gif", "", "", "");

	JFrame parent;

	GridBagLayout gridBagLayout3 = new GridBagLayout();

	public Wizard(JFrame frame, String title) {
		super(frame, title, true);
		this.parent = frame;
		setResizable(false);
		this.title = title;
		items = new ArrayList();
		try {
			next = new ImageButton("Next", new ImageIcon(ImagePanel
					.resolveImage("button_next.gif")));
			finish = new ImageButton("Finish", new ImageIcon(ImagePanel
					.resolveImage("button_ok.gif")));
			previous = new ImageButton("Prev", new ImageIcon(ImagePanel
					.resolveImage("button_prev.gif")));
			cancel = new ImageButton("Cancel", new ImageIcon(ImagePanel
					.resolveImage("button_cancel.gif")));
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		WizardItem item = getWizardItem(0);
		cardLayout1.show(itemPanel, item.getName());
		current = 0;
		itemChanged(item);
	}

	public void addWizardItem(WizardItem item) {
		items.add(item);
		JPanel newPanel = new JPanel(new BorderLayout(2, 0));
		newPanel.add(BorderLayout.CENTER, item.getWizardComponent());
		itemPanel.add(item.getName(), newPanel);
		item.addNotify(this);
	}

	public void itemChanged(WizardItem item) {
		// validate title...
		setTitle(item.getName());
		setDescription(item.getDescription());
		setHelp(item.getHelp());

		// can any additional buttons be utilised
		setButtons();
	}

	public void setTitle(String st) {
		super.setTitle(title + ": " + st);
		titlePanel.setTitle(st);
	}

	public void setDescription(String st) {
		titlePanel.setDescription(st);
	}

	public void setHelp(String st) {
		titlePanel.setHelp(st);
	}

	private void setButtons() {
		WizardItem item = getWizardItem(current);

		cancel.setVisible(item.showCancel());
		finish.setVisible(item.showFinish());
		previous.setVisible(item.showMoveBack());
		next.setVisible(item.showMoveForward());
		cancel.setEnabled(true);
		finish.setEnabled(item.canFinish());
		previous.setEnabled(item.canMoveBack() && !isStart(current));
		next.setEnabled(item.canMoveForward() && !isEnd(current));
	}

	private WizardItem getWizardItem(int index) {
		if ((index < 0) || (index >= items.size())) {
			index = current;
		}
		return (WizardItem) items.get(index);
	}

	private boolean isStart(int index) {
		return index == 0;
	}

	private boolean isEnd(int index) {
		return index + 1 == items.size();
	}

	private void next() {
		WizardItem item = getWizardItem(current);
		item.next();

		if (!isEnd(current)) {
			item = getWizardItem(current + 1);
			cardLayout1.show(itemPanel, item.getName());
			current++;
		}
		itemChanged(item);
	}

	private void previous() {
		WizardItem item = getWizardItem(current);
		item.previous();

		if (!isStart(current)) {
			item = getWizardItem(current - 1);
			cardLayout1.show(itemPanel, item.getName());
			current--;
		}
		itemChanged(item);
	}

	private void cancel() {
		WizardItem item = getWizardItem(current);
		item.cancel();
		this.setVisible(false);
	}

	private void finish() {
		WizardItem item = getWizardItem(current);
		item.finish();

		/* presumably do something at this point */

		this.setVisible(false);
	}

	private void jbInit() throws Exception {
		border1 = BorderFactory.createEtchedBorder(Color.white, new Color(148,
				145, 140));
		this.getContentPane().setLayout(gridBagLayout1);
		next.setMnemonic('N');
		next.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				next_actionPerformed(e);
			}
		});
		finish.setMnemonic('F');
		finish.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				finish_actionPerformed(e);
			}
		});
		previous.setMnemonic('P');
		previous.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				previous_actionPerformed(e);
			}
		});
		cancel.setMnemonic('C');
		cancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel_actionPerformed(e);
			}
		});
		jPanel1.setLayout(gridBagLayout3);
		itemPanel.setLayout(cardLayout1);
		JPanel buttonPnl = new JPanel(new GridLayout(1, 4));
		buttonPnl.add(previous);
		buttonPnl.add(finish);
		buttonPnl.add(next);
		buttonPnl.add(cancel);
		this.getContentPane().add(
				buttonPnl,
				new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
						GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
						new Insets(4, 5, 2, 5), 0, 0));
		this.getContentPane().add(
				itemPanel,
				new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 2, 2, 2), 0, 0));
		this.getContentPane().add(
				titlePanel,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0),
						0, 0));
		itemPanel.setBorder(lineBorder);
	}

	void next_actionPerformed(ActionEvent e) {
		next();
	}

	void cancel_actionPerformed(ActionEvent e) {
		cancel();
	}

	void previous_actionPerformed(ActionEvent e) {
		previous();
	}

	void finish_actionPerformed(ActionEvent e) {
		finish();
	}
}