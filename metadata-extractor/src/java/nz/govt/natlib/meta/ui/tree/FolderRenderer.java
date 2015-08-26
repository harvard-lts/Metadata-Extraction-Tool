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
import javax.swing.tree.DefaultTreeCellRenderer;

import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.ImagePanel;

/**
 * @author unascribed
 * @version 1.0
 */

public class FolderRenderer extends DefaultTreeCellRenderer {

	IconPainter icon;

	Icon errorIcon;

	public FolderRenderer() {
		try {
			Icon folderNormal = new ImageIcon(ImagePanel
					.resolveImage("folder.gif"));
			errorIcon = new ImageIcon(ImagePanel
					.resolveImage("status_error.gif"));
			icon = new IconPainter(null, folderNormal);
		} catch (Exception ex) {
			LogManager.getInstance().logMessage(LogMessage.ERROR,
					"Status icons not found");
		}
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		FolderNode folder = (FolderNode) value;
		boolean error = folder.hasError();

		JLabel statusLabel = (JLabel) super.getTreeCellRendererComponent(tree,
				value, selected, expanded, leaf, row, hasFocus);
		statusLabel.setText(folder.getName());

		if (error) {
			icon.setOverLay(errorIcon);
		} else {
			icon.setOverLay(null);
		}

		statusLabel.setIcon(icon);
		return statusLabel;
	}
}