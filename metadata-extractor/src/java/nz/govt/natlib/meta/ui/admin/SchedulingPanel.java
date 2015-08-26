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
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.ImageButton;
import nz.govt.natlib.meta.ui.ImagePanel;
import nz.govt.natlib.meta.ui.Main;

/**
 * @author AParker
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SchedulingPanel extends JPanel {
	private JDialog parent;

	private ElapsedTimeWidget tw;

	private Main controller;

	private ImageIcon schedulePic = null;

	private ImageIcon closePic = null;

	public SchedulingPanel(Main controller, JDialog parent) {
		this.controller = controller;
		this.parent = parent;
		this.setLayout(new BorderLayout());
		jbInit();
	}

	private void jbInit() {
		JPanel mainPnl = new JPanel();
		ImageIcon goPic = null;
		try {
			schedulePic = new ImageIcon(ImagePanel
					.resolveImage("icon_schedule.gif"));
			closePic = new ImageIcon(ImagePanel
					.resolveImage("button_cancel.gif"));
			goPic = new ImageIcon(ImagePanel
					.resolveImage("button_schedule.gif"));
		} catch (Exception e) {
		}

		Border mainBorder = BorderFactory.createEmptyBorder(0, 10, 0, 0);
		Border scheduleBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border elapsedBorder = new TitledBorder(BorderFactory
				.createEtchedBorder(Color.white, new Color(148, 145, 140)),
				"Elapsed Time");
		Border startBorder = new TitledBorder(BorderFactory.createEtchedBorder(
				Color.white, new Color(148, 145, 140)), "Start Time");
		BorderLayout bLay = new BorderLayout();
		JPanel bufferPnl = new JPanel(new BorderLayout());
		JPanel elapsedPnl = new JPanel(bLay);
		JPanel startPnl = new JPanel(new BorderLayout());
		JPanel buttonPnl = new JPanel(new BorderLayout());
		JLabel label1 = new JLabel("Schedule process to run in");
		bLay.setHgap(30);
		elapsedPnl.add(label1, BorderLayout.WEST);
		tw = new ElapsedTimeWidget();
		startPnl.add(tw.getStartLabel());
		startPnl.setBorder(startBorder);
		tw.addVetoableChangeListener(new VetoableChangeListener() {
			public void vetoableChange(PropertyChangeEvent pce) {
				tw.getStartLabel();
			}
		});
		elapsedPnl.add(tw, BorderLayout.EAST);
		mainPnl.setLayout(new GridLayout(2, 1));
		elapsedPnl.setBorder(elapsedBorder);
		ImageButton applyButt = new ImageButton("Schedule", goPic);
		ImageButton closeButton = new ImageButton("Close", closePic);
		JPanel bufButPnl = new JPanel(new BorderLayout());
		bufButPnl.add(buttonPnl, BorderLayout.SOUTH);
		JPanel buttImplPnl = new JPanel(new GridLayout(1, 2, 5, 5));
		buttonPnl.add(buttImplPnl, BorderLayout.EAST);
		buttImplPnl.add(closeButton);
		buttImplPnl.add(applyButt);
		buttonPnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPnl.add(elapsedPnl);
		mainPnl.add(startPnl);
		mainPnl.setBorder(mainBorder);
		bufferPnl.add(mainPnl, BorderLayout.NORTH);
		bufferPnl.add(bufButPnl, BorderLayout.SOUTH);
		this.setBorder(scheduleBorder);
		this.add(bufferPnl, BorderLayout.CENTER);
		applyButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				schedule();
			}
		});
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				parent.setVisible(false);
			}
		});
	}

	private void schedule() {
		LogManager.getInstance().logMessage(LogMessage.INFO,
				"Harvest Scheduled for " + new Date(tw.getScheduledTime()));
		new RunningDlg();
	}

	class RunningDlg extends JDialog {
		private String LABEL_TEXT = "Harvest starting in ";

		JLabel messageLbl = new JLabel(LABEL_TEXT, JLabel.CENTER);

		long dday = 0;

		public RunningDlg() {
			super(parent, "Harvest Scheduled", true);
			setSize(230, 90);
			Border mainBorder = BorderFactory.createEmptyBorder(0, 10, 0, 0);
			Border scheduleBorder = BorderFactory.createEmptyBorder(10, 10, 10,
					10);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((screenSize.width - 230) / 2,
					(screenSize.height - 100) / 2);
			this.setResizable(false);
			ImageButton cancelButton = new ImageButton("CANCEL", closePic);
			JLabel scheduleLbl = new JLabel(schedulePic);
			scheduleLbl.setBorder(null);
			JPanel wrapPanel = new JPanel(new BorderLayout());
			wrapPanel.setBorder(scheduleBorder);
			JPanel mainPnl = new JPanel(new GridLayout(2, 1, 10, 10));
			mainPnl.add(messageLbl);
			mainPnl.add(cancelButton);
			wrapPanel.add(mainPnl, BorderLayout.CENTER);
			wrapPanel.add(scheduleLbl, BorderLayout.WEST);
			this.getContentPane().add(wrapPanel, BorderLayout.CENTER);
			mainPnl.setBorder(scheduleBorder);
			dday = tw.getScheduledTime();
			final ScheduleRunner scheduler = new ScheduleRunner();
			Thread t = new Thread(scheduler);
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					LogManager.getInstance().logMessage(LogMessage.INFO,
							"Scheduled harvest cancelled ");
					scheduler.cancel();
				}
			});
			t.start();
			show();
		}

		class ScheduleRunner implements Runnable {
			private boolean active = true;

			public void run() {
				while (active) {
					if (dday < System.currentTimeMillis()) {
						break;
					}
					long time = dday - System.currentTimeMillis();
					int hours = (int) (time / (60 * 60 * 1000));
					time = time - 60 * 60 * 1000 * hours;
					int minutes = (int) (time / (60 * 1000));
					time = time - 60 * 1000 * minutes;
					int seconds = (int) (time / 1000);
					messageLbl.setText(LABEL_TEXT + hours + "h " + minutes
							+ "m " + seconds + "s");
					RunningDlg.this.pack();
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
				if (active) {
					harvest();
				}
			}

			public void cancel() {
				RunningDlg.this.setVisible(false);
				active = false;
			}

			private void harvest() {
				RunningDlg.this.setVisible(false);
				parent.setVisible(false);
				controller.harvest();
			}
		}
	}

	class ElapsedTimeWidget extends JPanel {
		JLabel label = new JLabel("");

		SpinnerNumberModel hours = new SpinnerNumberModel(1, 0, 99, 1);

		JSpinner hourWidget = new JSpinner(hours);

		SpinnerNumberModel minutes = new SpinnerNumberModel(0, 0, 59, 1);

		JSpinner minuteWidget = new JSpinner(minutes);

		public ElapsedTimeWidget() {
			initialise();
		}

		private void initialise() {
			this.setLayout(new GridLayout(1, 4));
			this.add(hourWidget);
			this.add(new JLabel("hours", JLabel.CENTER));
			this.add(minuteWidget);
			this.add(new JLabel("minutes", JLabel.CENTER));
			ChangeListener changeListener = new ChangeListener() {
				public void stateChanged(ChangeEvent ce) {
					getStartLabel();
				}
			};
			hourWidget.addChangeListener(changeListener);
			minuteWidget.addChangeListener(changeListener);
		}

		public long getScheduledTime() {
			long time = ((Integer) hourWidget.getValue()).intValue() * 60;
			time += ((Integer) minuteWidget.getValue()).intValue();
			time = System.currentTimeMillis() + time * 60 * 1000;
			return time;
		}

		private JLabel getStartLabel() {
			long time = getScheduledTime();
			// System.out.println("Changed Label!!!");
			Date d = new Date(time);
			label.setText("Scheduled for: " + d);
			return label;
		}
	}
}
