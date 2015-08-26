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

package nz.govt.natlib.meta.ui.log;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.log.FileLogger;
import nz.govt.natlib.meta.log.LevelFilter;
import nz.govt.natlib.meta.log.LogFilter;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.FileDialog;
import nz.govt.natlib.meta.ui.FileDialogUser;
import nz.govt.natlib.meta.ui.HorizLineBorder;
import nz.govt.natlib.meta.ui.ImageButton;
import nz.govt.natlib.meta.ui.ImagePanel;
import nz.govt.natlib.meta.ui.NLNZCombo;
import nz.govt.natlib.meta.ui.TitleBlock;

/**
 * A window for log messages.
 * 
 * @author unascribed
 * @version 1.0
 */

public class LogWindow extends JFrame implements FileDialogUser {
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	JMenuBar mainMenu = new JMenuBar();

	JMenu jMenu1 = new JMenu();

	JMenuItem openMenu = new JMenuItem();

	JMenuItem closeMenu = new JMenuItem();

	ImageIcon closeIcon;

	LogModel liveModel;

	LogModel viewModel;

	JPanel jPanel1 = new JPanel();

	TitledBorder titledBorder1;

	Border border1;

	TitledBorder titledBorder2;

	JPanel jPanel3 = new JPanel();

	JLabel jLabel1 = new JLabel();

	NLNZCombo priorityFilter = new NLNZCombo(new LevelFilter[] {
			new LevelFilter("All", LogMessage.WORTHLESS_CHATTER, false, true),
			new LevelFilter(LogMessage.CRITICAL),
			new LevelFilter(LogMessage.ERROR),
			new LevelFilter(LogMessage.DEBUG),
			new LevelFilter(LogMessage.INFO),
			new LevelFilter(LogMessage.WORTHLESS_CHATTER) });

	JScrollPane jScrollPane1 = new JScrollPane();

	LogTable logTable;

	BorderLayout borderLayout1 = new BorderLayout();

	TitledBorder titledBorder3;

	FlowLayout flowLayout1 = new FlowLayout();

	Border lineBorder = new HorizLineBorder();

	private static final String NORMAL_HELP = "You can save and open log files from the file menu of this window";

	private static final String NORMAL_DESC = "Currently shows the log messages for this session";

	TitleBlock titlePanel = new TitleBlock("xp_log.gif", "Log", NORMAL_DESC,
			NORMAL_HELP);

	JMenuItem saveAs = new JMenuItem();

	JMenuItem viewLive = new JMenuItem();

	JMenuItem jMenuItem1 = new JMenuItem();

	public LogWindow(JFrame parent, LogModel model) {
		super("Log");
		setIconImage(parent.getIconImage());
		this.liveModel = model;
		try {
			jbInit();
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		logTable = new LogTable();
		setViewModel(liveModel);
		try {
			closeIcon = new ImageIcon(ImagePanel
					.resolveImage("button_close.gif"));
		} catch (Exception e) {
		}
		ImageButton close = new ImageButton("Close", closeIcon);

		titledBorder2 = new TitledBorder(BorderFactory.createEtchedBorder(
				Color.white, new Color(148, 145, 140)), "Log Messages");
		titledBorder3 = new TitledBorder(BorderFactory.createEtchedBorder(
				Color.white, new Color(148, 145, 140)), "Message Filter");
		this.getContentPane().setLayout(gridBagLayout1);
		jMenu1.setText("File");
		openMenu.setText("Open");
		openMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openMenu_actionPerformed(e);
			}
		});
		closeMenu.setText("Close");
		closeMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeMenu_actionPerformed(e);
			}
		});
		this.setJMenuBar(mainMenu);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				this_windowClosing(e);
			}
		});
		jPanel1.setLayout(borderLayout1);
		jLabel1.setText("Message Priority:");
		close.setMnemonic('C');
		close.setText("Close");
		close.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close_actionPerformed(e);
			}
		});
		jPanel3.setBorder(titledBorder3);
		jPanel3.setLayout(flowLayout1);
		flowLayout1.setAlignment(FlowLayout.LEFT);
		jPanel1.setBorder(titledBorder1);
		priorityFilter.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				priorityFilter_actionPerformed(e);
			}
		});
		saveAs.setText("Save As");
		saveAs.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAs_actionPerformed(e);
			}
		});
		viewLive.setText("View Current Log");
		viewLive.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewLive_actionPerformed(e);
			}
		});
		jMenuItem1.setText("Clear Current Log");
		jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuItem1_actionPerformed(e);
			}
		});
		mainMenu.add(jMenu1);
		jMenu1.add(saveAs);
		jMenu1.add(openMenu);
		jMenu1.addSeparator();
		jMenu1.add(viewLive);
		jMenu1.add(jMenuItem1);
		jMenu1.addSeparator();
		jMenu1.add(closeMenu);
		this.getContentPane().add(
				titlePanel,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(2, 0, 2, 0), 0, 0));
		this.getContentPane().add(
				jPanel1,
				new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(5, 2, 5, 2), 0, 0));
		this.getContentPane().add(
				jPanel3,
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL, new Insets(5, 2, 5, 2),
						0, 0));
		jPanel3.add(jLabel1, null);
		jPanel3.add(priorityFilter, null);
		this.getContentPane().add(
				close,
				new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
						new Insets(5, 5, 7, 5), 0, 0));
		jPanel1.add(jScrollPane1, BorderLayout.CENTER);
		jScrollPane1.getViewport().add(logTable);
	}

	private void close() {
		this.setVisible(false);
	}

	private void saveViewModel() {
		// create and open a fileDialog
		JDialog fileDialog = new JDialog(this, "Open File", true);
		FileDialog filePanel = new FileDialog(this, this, true, false, false);
		fileDialog.getContentPane().add(filePanel);
		fileDialog.pack();

		// set the location.
		Point p1 = this.getLocation();
		Dimension d1 = this.getSize();
		Dimension d2 = fileDialog.getSize();
		fileDialog.setLocation((p1.x + (d1.width / 2)) - (d2.width / 2),
				(p1.y + (d1.height / 2)) - (d2.height / 2));

		filePanel.setCurrentDirectory(Config.getInstance().getBaseHarvestDir());
		fileDialog.show();
	}

	public void saveTo(File file) {
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileLogger logger = new FileLogger(file);
			for (int i = 0; i < viewModel.getRowCount(); i++) {
				logger.logMessage((LogMessage) viewModel.getRow(i));
			}
			logger.close();
			LogMessage msg = new LogMessage(LogMessage.WORTHLESS_CHATTER, null,
					"Saved logfile: " + file, "");
			LogManager.getInstance().logMessage(msg);
		} catch (Exception ex) {
			LogMessage msg = new LogMessage(LogMessage.ERROR, ex,
					"Error saving logfile", "");
			LogManager.getInstance().logMessage(msg);
		}
	}

	private void setViewModel(LogModel model) {
		viewModel = model;
		TableSorter sorter = new TableSorter(viewModel);
		logTable.setModel(sorter);
		sorter.addMouseListenerToHeaderInTable(logTable);
		logTable.setColumnWidths();
		logTable.getColumnModel().getColumn(0).setCellRenderer(
				new LogLevelRenderer());
	}

	public void openFile(File file[], FileDialog dialog) {
		// check the file.
		if (file.length == 1) {

			try {
				LogMessage[] msg = FileLogger.readMessages(file[0]);
				LogModel newModel = new LogModel(msg,
						(LogFilter) priorityFilter.getSelectedItem());
				setViewModel(newModel);
				LogMessage logMsg = new LogMessage(
						LogMessage.WORTHLESS_CHATTER, null, "Opened logfile: "
								+ file[0], "");
				LogManager.getInstance().logMessage(logMsg);
				setTitle("Log: " + file[0]);
				titlePanel
						.setDescription("Note: This window currently shows the log messages from the file: "
								+ file[0]);
				titlePanel
						.setHelp("You can view the log for the current session by selecting 'View Current Log' from the 'File' menu");
			} catch (Exception ex) {
				LogMessage msg = new LogMessage(LogMessage.ERROR, ex,
						"Error reading logfile", "");
				LogManager.getInstance().logMessage(msg);
			}

		} else {
			LogManager.getInstance().logMessage(LogMessage.ERROR,
					"You must select only one file to open in the log window");
		}
	}

	public void error(Throwable t) {
		LogManager.getInstance().logMessage(t);
	}

	private void openFile() {
		// create and open a fileDialog
		JDialog fileDialog = new JDialog(this, "Select File", true);
		FileDialog filePanel = new FileDialog(this, this, false, false, false);
		fileDialog.getContentPane().add(filePanel);
		fileDialog.pack();

		// set the location.
		Point p1 = this.getLocation();
		Dimension d1 = this.getSize();
		Dimension d2 = fileDialog.getSize();
		fileDialog.setLocation((p1.x + (d1.width / 2)) - (d2.width / 2),
				(p1.y + (d1.height / 2)) - (d2.height / 2));

		filePanel.setCurrentDirectory(Config.getInstance().getBaseHarvestDir());
		fileDialog.show();
	}

	private void closeMenu_actionPerformed(ActionEvent e) {
		close();
	}

	private void close_actionPerformed(ActionEvent e) {
		close();
	}

	private void this_windowClosing(WindowEvent e) {
		close();
	}

	private void priorityFilter_actionPerformed(ActionEvent e) {
		viewModel.setFilter((LogFilter) priorityFilter.getSelectedItem());
	}

	void viewLive_actionPerformed(ActionEvent e) {
		setViewModel(liveModel);
		setTitle("Log");
		titlePanel.setDescription(NORMAL_DESC);
		titlePanel.setHelp(NORMAL_HELP);
	}

	void saveAs_actionPerformed(ActionEvent e) {
		saveViewModel();
	}

	void openMenu_actionPerformed(ActionEvent e) {
		openFile();
	}

	void jMenuItem1_actionPerformed(ActionEvent e) {
		liveModel.clear();
	}

}