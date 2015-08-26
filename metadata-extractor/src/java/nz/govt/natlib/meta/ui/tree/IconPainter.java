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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

/**
 * 
 * @author unascribed
 * @version 1.0
 */

public class IconPainter implements Icon {

	private Icon overlay;

	private Icon underlay;

	private Composite composite = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 0.5f);

	public IconPainter() {
		this(null, null);
	}

	public IconPainter(Icon overlay, Icon underlay) {
		this.overlay = overlay;
		this.underlay = underlay;
	}

	public void setUnderlay(Icon icon) {
		this.underlay = icon;
	}

	public void setOverLay(Icon icon) {
		this.overlay = icon;
	}

	public int getIconHeight() {
		int uy = underlay == null ? 0 : underlay.getIconHeight();
		int oy = overlay == null ? 0 : overlay.getIconHeight();
		return Math.max(uy, oy);
	}

	public int getIconWidth() {
		int ux = underlay == null ? 0 : underlay.getIconWidth();
		int ox = overlay == null ? 0 : overlay.getIconWidth();
		return Math.max(ux, ox);
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = ((Graphics2D) g);
		if (underlay != null) {
			Composite cpstOld = g2.getComposite();
			if (overlay != null) {
				// if there IS an overlay then apply the 'fade' composite
				g2.setComposite(composite);
			}
			underlay.paintIcon(c, g2, x, y);
			g2.setComposite(cpstOld);
		}
		if (overlay != null) {
			overlay.paintIcon(c, g2, x, y);
		}
	}

}
