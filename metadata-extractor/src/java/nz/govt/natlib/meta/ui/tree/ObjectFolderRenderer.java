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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;

import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.ImagePanel;

/**
 * 
 * @author unascribed
 * @version 1.0
 */

public class ObjectFolderRenderer extends FolderRenderer {

	Icon simpleFolder;

	Icon complexFolder;

	Icon errorIcon;

	Icon groupOverlay;

	IconPainter icon = new IconPainter();

	public ObjectFolderRenderer() {
		try {
			simpleFolder = new ImageIcon(ImagePanel
					.resolveImage("simple_folder.gif"));
			complexFolder = new ImageIcon(ImagePanel
					.resolveImage("complex_folder.gif"));
			groupOverlay = new ImageIcon(ImagePanel
					.resolveImage("group_box.gif"));
			errorIcon = new ImageIcon(ImagePanel
					.resolveImage("status_error.gif"));
		} catch (Exception ex) {
			LogManager.getInstance().logMessage(LogMessage.ERROR,
					"Tree staus icons not found");
		}
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		ObjectFolderNode folder = (ObjectFolderNode) value;
		boolean grouped = folder
				.getPropertyValue(ObjectFolderNode.partOfGroupProp) == Boolean.TRUE;
		boolean error = folder.hasError();

		JLabel statusLabel = (JLabel) super.getTreeCellRendererComponent(tree,
				value, selected, expanded, leaf, row, hasFocus);
		statusLabel.setText(folder.getName());

		if (folder.getType() == ObjectFolderNode.COMPLEX) {
			icon.setUnderlay(complexFolder);
		}
		if (folder.getType() == ObjectFolderNode.SIMPLE) {
			icon.setUnderlay(simpleFolder);
		}

		if (error) {
			icon.setOverLay(errorIcon);
		} else {
			if (grouped) {
				icon.setOverLay(groupOverlay);
			} else {
				icon.setOverLay(null);
			}
		}

		statusLabel.setIcon(icon);
		return statusLabel;
	}
}