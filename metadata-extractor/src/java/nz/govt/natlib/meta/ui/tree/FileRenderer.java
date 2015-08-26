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

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import nz.govt.natlib.meta.HarvestStatus;
import nz.govt.natlib.meta.MetaUtil;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.ImagePanel;

/**
 * @author unascribed
 * @version 1.0
 */

public class FileRenderer extends DefaultTreeCellRenderer {

	Icon ok;

	Icon error;

	Icon blank;

	IconPainter paintError = new IconPainter();

	IconPainter paintOK = new IconPainter();

	private StringBuffer toolTipText = new StringBuffer("");

	public FileRenderer() {
		try {
			ok = new ImageIcon(ImagePanel.resolveImage("status_ok.gif"));
			error = new ImageIcon(ImagePanel.resolveImage("status_error.gif"));
			blank = new ImageIcon(ImagePanel.resolveImage("status_blank.gif"));

			paintOK.setOverLay(ok);
			paintError.setOverLay(error);
		} catch (Exception ex) {
			LogManager.getInstance().logMessage(LogMessage.ERROR,
					"Tree staus icons not found");
		}
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		FileNode fileNode = (FileNode) value;
		HarvestStatus status = fileNode.getStatus();
		File file = fileNode.getFile();
		JLabel statusLabel = (JLabel) super.getTreeCellRendererComponent(tree,
				value, selected, expanded, leaf, row, hasFocus);

		Icon fileViewIcon = blank;
		toolTipText.delete(0, toolTipText.length());
		toolTipText.append("<html>");
		Color backdrop = statusLabel.getBackground();

		String systemDesc = fileNode.getDescription();
		fileViewIcon = fileNode.getIcon();
		if (systemDesc != null) {
			toolTipText.append("&nbsp;<b>");
			toolTipText.append(systemDesc == null ? "" : systemDesc);
			toolTipText.append("</b><br>");
		}

		toolTipText.append("&nbsp;<b>Name :</b>");
		toolTipText.append(file.getName());
		toolTipText.append("<br>");
		toolTipText.append("&nbsp;<b>Size :</b>");
		toolTipText.append(MetaUtil.formatBytes(file.length()));

		if (status == HarvestStatus.OK) {
			paintOK.setOverLay(ok);
			paintOK.setUnderlay(fileViewIcon);
			fileViewIcon = paintOK;
		}
		if (status == HarvestStatus.ERROR) {
			paintError.setOverLay(error);
			paintError.setUnderlay(fileViewIcon);
			fileViewIcon = paintError;
			toolTipText.append("<br><br>&nbsp;<font color=\"#FF0000\">");
			toolTipText.append(fileNode.getStatusMessage());
			toolTipText.append("</font>");
		}
		toolTipText.append("</html>");

		statusLabel.setText(file.getName()); // +" ("+file.length()+"bytes)"
		statusLabel.setIcon(fileViewIcon);
		statusLabel.setToolTipText(toolTipText.toString());
		statusLabel.setBackground(backdrop);
		return statusLabel;
	}

}