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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import nz.govt.natlib.meta.ProgressStopListener;

/**
 * 
 * @author Nic Evans
 * @version 1.0
 */

public class Progress extends JDialog {
	private JPanel content = new JPanel();

	private GridBagLayout gridBagLayout1 = new GridBagLayout();

	private JProgressBar progressBar = new JProgressBar();

	private JPanel jPanel1 = new JPanel();

	private JLabel description = new JLabel();

	private JLabel fileProgress = new JLabel();

	private JLabel byteProgress = new JLabel();

	private GridBagLayout gridBagLayout2 = new GridBagLayout();

	private JPanel jPanel2 = new JPanel();

	private ImageButton stop;

	private boolean stopable;

	private ProgressStopListener listener;

	public Progress(Frame parent) {
		this(parent, false);
	}

	public Progress(Frame parent, boolean stopable) {
		super(parent, "Progress", false);
		ImageIcon stopIcon = null;
		try {
			stopIcon = new ImageIcon(ImagePanel.resolveImage("button_stop.gif"));
		} catch (Exception e) {
		}
		stop = new ImageButton("Cancel", stopIcon);
		this.stopable = stopable;
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		content.setLayout(gridBagLayout1);
		description
				.setText("Preparing to process                                         ");
		fileProgress.setText(" ");
		byteProgress.setText(" ");
		jPanel1.setLayout(gridBagLayout2);
		progressBar.setStringPainted(true);
		stop.setText("Stop");
		stop.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop_actionPerformed(e);
			}
		});
		content.add(progressBar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		content.add(jPanel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));
		jPanel1.add(byteProgress, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						5, 2, 5), 0, 0));
		jPanel1.add(fileProgress, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						5, 2, 5), 0, 0));
		jPanel1.add(description, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						5, 2, 5), 0, 0));
		content.add(jPanel2, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 5, 2), 0, 0));
		jPanel2.add(stop, null);
		stop.setEnabled(stopable);
		this.getContentPane().add(content, null);
	}

	public void setProgress(String name, String fileProgress,
			String byteProgress, int prog) {
		this.description.setText(name);
		this.fileProgress.setText(fileProgress);
		this.byteProgress.setText(byteProgress);
		this.progressBar.setValue(prog);
	}

	public void setProgressStopListener(ProgressStopListener listener) {
		this.listener = listener;
	}

	void stop_actionPerformed(ActionEvent e) {
		if (listener != null) {
			listener.stop();
		}
	}
}