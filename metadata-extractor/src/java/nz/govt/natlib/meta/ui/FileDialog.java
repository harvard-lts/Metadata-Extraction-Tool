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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import nz.govt.natlib.meta.log.LogManager;

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

public class FileDialog extends JPanel {
	JPanel panel1 = new JPanel();

	BorderLayout borderLayout1 = new BorderLayout();

	JFileChooser fileSelector = new JFileChooser();

	FileDialogUser controller;

	private static DirDialog saveDialog, selectDialog;

	JFrame parent;

	boolean save = false;

	boolean extraOptions = false;

	JPanel jPanel1 = new JPanel();

	JCheckBox recurse = new JCheckBox();

	JCheckBox flatten = new JCheckBox();

	GridBagLayout gridBagLayout1 = new GridBagLayout();

	TitledBorder titledBorder1;

	ImageIcon openIcon, closeIcon, saveIcon;

	FlowLayout flowLayout1 = new FlowLayout();

	public FileDialog(JFrame parent, FileDialogUser controller, boolean save,
			boolean recurseFolders, boolean flattenFolders) {
		this(parent, controller, save, recurseFolders, flattenFolders, true);
	}

	public FileDialog(JFrame parent, FileDialogUser controller, boolean save,
			boolean recurseFolders, boolean flattenFolders,
			boolean controlButtons) {
		this(parent, controller, save, recurseFolders, flattenFolders, true,
				true);
	}

	public FileDialog(JFrame parent, FileDialogUser controller, boolean save,
			boolean recurseFolders, boolean flattenFolders,
			boolean controlButtons, boolean extraOptions) {
		this.controller = controller;
		this.save = save;
		this.extraOptions = extraOptions;
		fileSelector.setControlButtonsAreShown(controlButtons);
		try {
			jbInit();
			recurse.setSelected(recurseFolders);
			flatten.setSelected(flattenFolders);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void getComponents(Container cont) {
		Component[] conts = cont.getComponents();
		for (int i = 0; i < conts.length; i++) {
			if (conts[i] instanceof JButton) {
				String text = ((JButton) conts[i]).getText();
				if ((text != null) && (text.length() > 0)) {
					ImageIcon icon = null;
					if (text.equals("Open")) {
						icon = openIcon;
					} else if (text.equals("Cancel")) {
						icon = closeIcon;
					} else if (text.equals("Save")) {
						icon = saveIcon;
					}
					ImageButton.setAttributes((JButton) conts[i], icon);
				}
			} else {
				if (conts[i] instanceof Container) {
					getComponents((Container) conts[i]);
				}
			}
		}
	}

	void jbInit() throws Exception {
		openIcon = new ImageIcon(ImagePanel.resolveImage("button_ok.gif"));
		closeIcon = new ImageIcon(ImagePanel.resolveImage("button_cancel.gif"));
		saveIcon = new ImageIcon(ImagePanel.resolveImage("button_save.gif"));
		titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(
				Color.white, new Color(148, 145, 140)), "Folder Options");
		panel1.setLayout(borderLayout1);
		fileSelector.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileSector_actionPerformed(e);
			}
		});
		fileSelector.setApproveButtonText(save ? "Save" : "Open");
		fileSelector.setMultiSelectionEnabled(true);
		fileSelector.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileSelector.setApproveButtonMnemonic(save ? 's' : 'o');
		recurse.setText("Recurse Sub-Folders");
		jPanel1.setLayout(gridBagLayout1);
		flatten.setText("Flatten Folder Structure");
		jPanel1.setBorder(titledBorder1);
		this.setLayout(flowLayout1);
		flowLayout1.setHgap(0);
		flowLayout1.setVgap(0);
		add(panel1);
		panel1.add(fileSelector, BorderLayout.CENTER);
		if (extraOptions) {
			panel1.add(jPanel1, BorderLayout.SOUTH);
			jPanel1.add(flatten,
					new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
							GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0,
									5), 0, 0));
			jPanel1.add(recurse,
					new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
							GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0,
									5), 0, 0));
		}
		getComponents(fileSelector);
	}

	public void setCurrentDirectory(String dir) {
		if (dir != null) {
			fileSelector.setCurrentDirectory(new File(dir));
		}
	}

	public String getCurrentDirectory() {
		return fileSelector.getCurrentDirectory().getPath();
	}

	public void closeWindow() {
		this.getTopLevelAncestor().setVisible(false);
	}

	public void cancel() {
	}

	public File[] getSelectedFiles() {
		return fileSelector.getSelectedFiles();
	}

	public boolean shouldFlatten() {
		return flatten.isSelected();
	}

	public boolean shouldRecurse() {
		return recurse.isSelected();
	}

	public void ok() {
		File[] file = getSelectedFiles();
		controller.openFile(file, this);
	}

	public void okSave() {
		File[] file = getSelectedFiles();
		if ((file != null) && (file.length == 1)) {
			controller.saveTo(file[0]);
		}
	}

	public void setSelectedFile(File file) {
		fileSelector.setSelectedFile(file);
	}

	public static File showSelectDialog(JFrame parent, String dir) {
		if (selectDialog == null) {
			selectDialog = new DirDialog(parent, false);
		}
		if (dir != null) {
			return selectDialog.showSelectDialog(new File(dir));
		} else {
			return selectDialog.showSelectDialog();
		}
	}

	void fileSector_actionPerformed(ActionEvent e) {
		closeWindow();
		if (e.getActionCommand().equals("CancelSelection")) {
			cancel();
		}

		if (e.getActionCommand().equals("ApproveSelection")) {
			if (save) {
				okSave();
			} else {
				ok();
			}
		}
	}

}

class DirDialog extends Dialog {
	private FileDialog filer;

	private File selected = null;

	public DirDialog(JFrame parent, boolean save) {
		super(parent, true);
		filer = new FileDialog(parent, new DirSelected(), save, true, false);
		Dimension mySize = filer.getPreferredSize();
		Dimension parentSize = parent.getSize();
		Dimension parentContentsSize = parent.getContentPane().getSize();
		mySize.height += parentSize.height - parentContentsSize.height;
		mySize.width += 8;
		setSize(mySize);
		this.add(filer);
	}

	public File showSelectDialog(File dir) {
		filer.setCurrentDirectory(dir.getAbsolutePath());
		filer.setSelectedFile(new File(""));
		return showSelectDialog();
	}

	public File showSelectDialog() {
		filer.setSelectedFile(new File(""));
		show();
		return selected;
	}

	class DirSelected implements FileDialogUser {
		/*
		 * (non-Javadoc)
		 * 
		 * @see nz.govt.natlib.meta.ui.FileDialogUser#error(java.lang.Throwable)
		 */
		public void error(Throwable thrown) {
			LogManager.getInstance().logMessage(thrown);
			thrown.printStackTrace();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nz.govt.natlib.meta.ui.FileDialogUser#openFile(java.io.File[],
		 *      nz.govt.natlib.meta.ui.FileDialog)
		 */
		public void openFile(File[] files, FileDialog from) {
			if ((files != null) && (files.length > 0)) {
				selected = files[0];
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nz.govt.natlib.meta.ui.FileDialogUser#saveTo(java.io.File)
		 */
		public void saveTo(File file) {
		}
	}
}
