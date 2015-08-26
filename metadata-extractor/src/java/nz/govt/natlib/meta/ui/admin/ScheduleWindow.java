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
 * Created on 28/05/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.govt.natlib.meta.ui.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;

import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.ui.Main;
import nz.govt.natlib.meta.ui.TitleBlock;

/**
 * @author aparker
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ScheduleWindow extends JDialog {
	private SchedulingPanel schedulePnl;

	private Main controller;

	private static final String NORMAL_HELP = "You can schedule harvesting for a future time using this tool";

	private static final String NORMAL_DESC = "Schedule harvesting to occur later";

	TitleBlock titlePanel = new TitleBlock("icon_schedule.gif", "Scheduling",
			NORMAL_DESC, NORMAL_HELP);

	public ScheduleWindow(Main controller, String model) {
		super(controller, "Schedule Harvesting", true);
		this.controller = controller;
		schedulePnl = new SchedulingPanel(controller, this);
		// Just initialise the settings
		Config.getEditInstance(true);
		try {
			jbInit();
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void jbInit() {
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(schedulePnl, BorderLayout.CENTER);
		this.getContentPane().add(titlePanel, BorderLayout.NORTH);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(360, 260);
		this.setLocation((screenSize.width - 360) / 2,
				(screenSize.height - 260) / 2);
		this.setResizable(false);
	}
}
