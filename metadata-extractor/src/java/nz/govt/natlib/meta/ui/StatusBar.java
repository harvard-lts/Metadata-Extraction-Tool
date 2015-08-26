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
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import nz.govt.natlib.meta.HarvestStatus;

/**
 * 
 * @author unascribed
 * @version 1.0
 */

public class StatusBar extends JPanel {
	BorderLayout borderLayout1 = new BorderLayout();

	JLabel statusIcon = new JLabel();

	JLabel statusText = new JLabel();

	Icon ok;

	Icon error;

	Icon blank;

	public StatusBar() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		ok = new ImageIcon(ImagePanel.resolveImage("status_ok.gif"));
		error = new ImageIcon(ImagePanel.resolveImage("status_error.gif"));
		blank = new ImageIcon(ImagePanel.resolveImage("status_blank.gif"));

		Border border = new ShortLowBevel();
		statusIcon.setBorder(border);
		this.setLayout(borderLayout1);
		statusText.setBorder(border);
		borderLayout1.setHgap(4);
		this.add(statusIcon, BorderLayout.WEST);
		this.add(statusText, BorderLayout.CENTER);

		setStatus(HarvestStatus.BLANK);

		BorderFactory.createBevelBorder(2);
	}

	public void setStatus(HarvestStatus status) {
		Icon icon = null;
		String text = null;
		if (status == HarvestStatus.BLANK) {
			icon = blank;
			text = "Ready to harvest";
		}

		if (status == HarvestStatus.ERROR) {
			icon = error;
			text = "An error occurred while extracting metadata";
		}

		if (status == HarvestStatus.OK) {
			icon = ok;
			text = "Metadata was successfully extracted";
		}
		setStatus(icon, text);
	}

	public void setStatus(Icon icon, String text) {
		statusIcon.setIcon(icon);
		statusText.setText(" " + text);
	}

	private class ShortLowBevel extends BevelBorder {

		public ShortLowBevel() {
			super(BevelBorder.LOWERED);
		}

		protected void paintLoweredBevel(Component c, Graphics g, int x, int y,
				int width, int height) {
			Color oldColor = g.getColor();
			int h = height;
			int w = width;

			g.translate(x, y);

			g.setColor(getShadowInnerColor(c));
			g.drawLine(0, 0, 0, h - 1);
			g.drawLine(1, 0, w - 1, 0);

			// g.setColor(getShadowOuterColor(c));
			// g.drawLine(1, 1, 1, h-2);
			// g.drawLine(2, 1, w-2, 1);

			g.setColor(getHighlightOuterColor(c));
			g.drawLine(1, h - 1, w - 1, h - 1);
			g.drawLine(w - 1, 1, w - 1, h - 2);

			// g.setColor(getHighlightInnerColor(c));
			// g.drawLine(2, h-2, w-2, h-2);
			// g.drawLine(w-2, 2, w-2, h-3);

			g.translate(-x, -y);
			g.setColor(oldColor);

		}
	}
}