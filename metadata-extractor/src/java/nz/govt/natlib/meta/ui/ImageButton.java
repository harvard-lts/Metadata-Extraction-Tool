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
 * Created on 8/06/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.govt.natlib.meta.ui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author aparker
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ImageButton extends JButton {
	private Color over;

	private Color normal;

	public ImageButton(String text, Icon icon) {
		super(text, icon);
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		// this.setMargin(new Insets(10,10,10,10));
		setFocusPainted(false);
		normal = getBackground();
		over = new Color(normal.getRed() - 25, normal.getGreen() - 25, normal
				.getBlue() + 5);

		MouseAdapter borderControl = new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				JButton source = (JButton) e.getSource();
				if (source.isEnabled()) {
					source.setBackground(over);
				}
			}

			public void mouseExited(MouseEvent e) {
				JButton source = (JButton) e.getSource();
				source.setBackground(normal);
			}
		};
		addMouseListener(borderControl);
	}

	public ImageButton(Icon icon) {
		this("", icon);
	}

	public static void setAttributes(JButton butt, Icon icon) {
		butt.setIcon(icon);
		butt.setBorder(null);// BorderFactory.createEmptyBorder(2,2,2,2));
		butt.setFocusPainted(false);
		final Color normal = butt.getBackground();
		final Color over = new Color(normal.getRed() - 25,
				normal.getGreen() - 25, normal.getBlue() + 5);
		MouseAdapter borderControl = new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				JButton source = (JButton) e.getSource();
				if (source.isEnabled()) {
					source.setBackground(over);
				}
			}

			public void mouseExited(MouseEvent e) {
				JButton source = (JButton) e.getSource();
				source.setBackground(normal);
			}
		};
		butt.addMouseListener(borderControl);
	}
}
