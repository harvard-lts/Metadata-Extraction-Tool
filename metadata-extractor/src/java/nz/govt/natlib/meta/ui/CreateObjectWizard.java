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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;

import javax.swing.JFrame;

import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.ui.tree.ObjectFolderNode;
import nz.govt.natlib.meta.ui.tree.PropertyContainer;
import nz.govt.natlib.meta.ui.tree.PropertyEditor;
import nz.govt.natlib.meta.ui.wizard.Wizard;
import nz.govt.natlib.meta.ui.wizard.WizardItem;

/**
 * @author unascribed
 * @version 1.0
 */

public class CreateObjectWizard {

	Wizard wizard;

	File[] files;

	boolean recurse = true;

	boolean flatten = false;

	String currentDirectory;

	Main controller;

	ObjectFolderNode node;

	public CreateObjectWizard(Main controller, ObjectFolderNode node) {
		this.controller = controller;
		this.node = node;
		if (node.getType() == ObjectFolderNode.COMPLEX) {
			recurse = true;
			flatten = false;
		}
		if (node.getType() == ObjectFolderNode.SIMPLE) {
			recurse = true;
			flatten = true;
		}

		wizard = new Wizard((JFrame) controller, "Create Object");

		wizard.addWizardItem(new SelectFileWizardItem(controller, node));
		wizard.addWizardItem(new PropertyWizardItem(node));

		wizard.start();
		wizard.pack();

		// set the location.
		Point p1 = controller.getLocation();
		Dimension d1 = controller.getSize();
		Dimension d2 = wizard.getSize();
		wizard.setLocation((p1.x + (d1.width / 2)) - (d2.width / 2),
				(p1.y + (d1.height / 2)) - (d2.height / 2));

		wizard.show();
	}

	private void setFiles(File[] files) {
		this.files = files;
	}

	private void setCurrentDirectory(String directory) {
		this.currentDirectory = directory;
	}

	private void setRecurse(boolean recurse) {
		this.recurse = recurse;
	}

	private void setFlatten(boolean flatten) {
		this.flatten = flatten;
	}

	private void endWizard() {
		try {
			controller
					.openFile(files, currentDirectory, recurse, flatten, node);
		} catch (Exception ex) {
			controller.error(ex);
		}
	}

	private class SelectFileWizardItem extends WizardItem implements
			FileDialogUser {

		FileDialog fileDialog = null;

		SelectFileWizardItem(Main controller, ObjectFolderNode node) {
			fileDialog = new FileDialog(controller, this, false, recurse,
					flatten, false);
			fileDialog.setCurrentDirectory(Config.getInstance()
					.getCurrentProfile().getInputDirectory());
		}

		public void saveTo(File file) {
		}

		public void openFile(File[] file, FileDialog dialog) {
			gatherDetails();
			endWizard();
		}

		public void error(Throwable t) {
			controller.error(t);
		}

		private void gatherDetails() {
			setFiles(fileDialog.getSelectedFiles());
			setRecurse(fileDialog.shouldRecurse());
			setFlatten(fileDialog.shouldFlatten());
			setCurrentDirectory(fileDialog.getCurrentDirectory());
		}

		public Component getWizardComponent() {
			return fileDialog;
		}

		public String getName() {
			return "Select Files";
		}

		public void finish() {
			gatherDetails();
			endWizard();
		}

		public void next() {
			// fileDialog.ok();
			gatherDetails();
		}

		public void cancel() {
			fileDialog.cancel();
		}

		public String getDescription() {
			return "Select the files you wish to add to the new Object";
		}

	}

	private class PropertyWizardItem extends WizardItem {

		PropertyEditor editor = null;

		PropertyWizardItem(PropertyContainer properties) {
			editor = new PropertyEditor(properties);
		}

		public Component getWizardComponent() {
			return editor;
		}

		public String getName() {
			return "Properties";
		}

		public void finish() {
			editor.ok();
			endWizard();
		}

		public void cancel() {
			editor.cancel();
		}

		public String getDescription() {
			return "Set the properties of the new Object";
		}

		public String getHelp() {
			return "You can change these later by editing the Object properties";
		}

	}

}