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

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * 
 * @author unascribed
 * @version 1.0
 */

public class ToolBar extends JToolBar {
	JButton create = new JButton();

	JButton add = new JButton();

	JButton remove = new JButton();

	JButton help = new JButton();

	JButton exit = new JButton();

	JButton properties = new JButton();

	JButton schedule = new JButton();

	JButton process = new JButton();

	Main controller;

	JButton log = new JButton();

	JButton admin = new JButton();

	JButton removeAll = new JButton();

	Color over = null;

	Color normal = null;

	public ToolBar(Main controller) {
		this.controller = controller;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		Insets margin = null;
		normal = create.getBackground();
		over = new Color(normal.getRed() - 25, normal.getGreen() - 25, normal
				.getBlue() + 5);
		Icon createIcon = new ImageIcon(ImagePanel
				.resolveImage("button_create_object.gif"));
		Icon addIcon = new ImageIcon(ImagePanel.resolveImage("button_add.gif"));
		Icon removeIcon = new ImageIcon(ImagePanel
				.resolveImage("button_remove.gif"));
		Icon propsIcon = new ImageIcon(ImagePanel
				.resolveImage("button_properties.gif"));
		Icon processIcon = new ImageIcon(ImagePanel
				.resolveImage("button_process.gif"));
		Icon helpIcon = new ImageIcon(ImagePanel
				.resolveImage("button_help.gif"));
		Icon exitIcon = new ImageIcon(ImagePanel
				.resolveImage("button_exit.gif"));
		Icon removeaAllIcon = new ImageIcon(ImagePanel
				.resolveImage("button_remove_all.gif"));
		Icon logIcon = new ImageIcon(ImagePanel.resolveImage("button_log.gif"));
		Icon adminIcon = new ImageIcon(ImagePanel
				.resolveImage("button_admin.gif"));
		Icon scheduleIcon = new ImageIcon(ImagePanel
				.resolveImage("button_schedule.gif"));
		create.setIcon(createIcon);
		add.setIcon(addIcon);
		remove.setIcon(removeIcon);
		properties.setIcon(propsIcon);
		help.setIcon(helpIcon);
		exit.setIcon(exitIcon);
		process.setIcon(processIcon);
		removeAll.setIcon(removeaAllIcon);
		log.setIcon(logIcon);
		admin.setIcon(adminIcon);
		schedule.setIcon(scheduleIcon);

		// get the border stuff
		margin = new Insets(2, 2, 2, 2);
		Border blankBorder = null;

		removeAll.setBorder(blankBorder);
		removeAll.setToolTipText("Remove All Files/Folders/Objects");
		removeAll.setMargin(margin);
		removeAll.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeAll_actionPerformed(e);
			}
		});
		log.setBorder(blankBorder);
		log.setToolTipText("View Logs");
		log.setMargin(margin);
		log.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log_actionPerformed(e);
			}
		});
		admin.setBorder(blankBorder);
		admin.setToolTipText("View Administration");
		admin.setMargin(margin);
		admin.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				admin_actionPerformed(e);
			}
		});
		schedule.setBorder(blankBorder);
		schedule.setToolTipText("Schedule Harvest");
		schedule.setMargin(margin);
		schedule.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schedule_actionPerformed(e);
			}
		});
		help.setBorder(blankBorder);
		help.setToolTipText("View Help");
		help.setMargin(margin);
		help.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				help_actionPerformed(e);
			}
		});
		process.setBorder(blankBorder);
		process.setToolTipText("Process all Objects");
		process.setMargin(margin);
		process.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				process_actionPerformed(e);
			}
		});
		create.setBorder(blankBorder);
		create.setToolTipText("Create a New Object");
		create.setMargin(margin);
		create.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				create_actionPerformed(e);
			}
		});
		add.setBorder(blankBorder);
		add.setToolTipText("Add a File or Folder to the Selected Object");
		add.setMargin(margin);
		add.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				add_actionPerformed(e);
			}
		});
		remove.setBorder(blankBorder);
		remove.setToolTipText("Remove the Selected File/Folder/Object");
		remove.setMargin(margin);
		remove.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove_actionPerformed(e);
			}
		});
		properties.setBorder(blankBorder);
		properties
				.setToolTipText("Edit the Properties of the Currently Selected Object");
		properties.setMargin(margin);
		properties.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				properties_actionPerformed(e);
			}
		});
		exit.setBorder(blankBorder);
		exit.setToolTipText("Exit");
		exit.setMargin(margin);
		exit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit_actionPerformed(e);
			}
		});

		this.setFloatable(false);
		add(create);
		add(add);
		add(remove);
		add(removeAll);
		addSeparator();
		add(properties);
		addSeparator();
		add(process);
		addSeparator();
		add(log);
		add(admin);
		add(schedule);
		add(help);
		add(exit);

		create.setVerticalTextPosition(SwingConstants.BOTTOM);
		add.setVerticalTextPosition(SwingConstants.BOTTOM);
		remove.setVerticalTextPosition(SwingConstants.BOTTOM);
		properties.setVerticalTextPosition(SwingConstants.BOTTOM);
		help.setVerticalTextPosition(SwingConstants.BOTTOM);
		exit.setVerticalTextPosition(SwingConstants.BOTTOM);
		process.setVerticalTextPosition(SwingConstants.BOTTOM);
		removeAll.setVerticalTextPosition(SwingConstants.BOTTOM);
		log.setVerticalTextPosition(SwingConstants.BOTTOM);
		admin.setVerticalTextPosition(SwingConstants.BOTTOM);
		schedule.setVerticalTextPosition(SwingConstants.BOTTOM);

		BorderControl bcontrol = new BorderControl();
		create.addMouseListener(bcontrol);
		add.addMouseListener(bcontrol);
		remove.addMouseListener(bcontrol);
		properties.addMouseListener(bcontrol);
		help.addMouseListener(bcontrol);
		exit.addMouseListener(bcontrol);
		process.addMouseListener(bcontrol);
		removeAll.addMouseListener(bcontrol);
		log.addMouseListener(bcontrol);
		admin.addMouseListener(bcontrol);
		schedule.addMouseListener(bcontrol);

		this.setBorder(new HorizLineBorder());
	}

	private class BorderControl extends MouseAdapter {
		public void mouseEntered(MouseEvent e) {
			JButton source = (JButton) e.getSource();
			if (source.isEnabled()) {
				source.setBackground(over);
			}
		}

		public void mouseExited(MouseEvent e) {
			JButton source = (JButton) e.getSource();
			source.setBackground(normal);
		}
	}

	public void setCreateEnabled(boolean enable) {
		create.setEnabled(enable);
	}

	public void setAddEnabled(boolean enable) {
		add.setEnabled(enable);
	}

	public void setRemoveEnabled(boolean enable) {
		remove.setEnabled(enable);
	}

	public void setRemoveAllEnabled(boolean enable) {
		removeAll.setEnabled(enable);
	}

	public void setPropertiesEnabled(boolean enable) {
		properties.setEnabled(enable);
	}

	public void setProcessEnabled(boolean enable) {
		process.setEnabled(enable);
	}

	void create_actionPerformed(ActionEvent e) {
		controller.popupNewObjectMenu(create);
	}

	void add_actionPerformed(ActionEvent e) {
		controller.add();
	}

	void remove_actionPerformed(ActionEvent e) {
		controller.removeSelected();
	}

	void properties_actionPerformed(ActionEvent e) {
		controller.showPropertyEditor();
	}

	void process_actionPerformed(ActionEvent e) {
		controller.harvest();

	}

	void help_actionPerformed(ActionEvent e) {
		controller.showHelp();
	}

	void exit_actionPerformed(ActionEvent e) {
		controller.exit();
	}

	void log_actionPerformed(ActionEvent e) {
		controller.showLogWindow();
	}

	void admin_actionPerformed(ActionEvent e) {
		controller.showAdminWindow();
	}

	void schedule_actionPerformed(ActionEvent e) {
		controller.showScheduleWindow();
	}

	void removeAll_actionPerformed(ActionEvent e) {
		controller.removeAllNodes();
	}
}