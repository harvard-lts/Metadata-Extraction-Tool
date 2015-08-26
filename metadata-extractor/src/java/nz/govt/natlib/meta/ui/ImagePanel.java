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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * 
 * @author Nic Evans
 * @version 1.0
 */
public class ImagePanel extends Canvas {

	private Image img;

	private Dimension imageSize = null;

	private static HashMap images = new HashMap();

	public ImagePanel(Dimension size) {
		this.imageSize = size;
		this.setSize(imageSize);
	}

	/**
	 * Note the file mentioned (image.gif or whatever) must be in the classpath
	 * 
	 * @param name
	 * @return The image loaded from the classpath.
	 * @throws IOException
	 */
	public static Image resolveImage(String name) throws IOException {
		Image img = (Image) images.get(name);
		if (img == null) {
			// load the image...
			InputStream in = ClassLoader.getSystemClassLoader()
					.getResourceAsStream(name);
			if (in == null)
				return null;
			int size = in.available();
			byte[] imgData = new byte[size];
			in.read(imgData);
			img = Toolkit.getDefaultToolkit().createImage(imgData);
			images.put(name, img);
		}
		return img;
	}

	public boolean imageUpdate(Image img, int info, int x, int y, int w, int h) {
		return super.imageUpdate(img, info, x, y, w, h);
	}

	public void setImage(Image img) {
		this.img = img;
		repaint();
	}

	public void setImage(String img) throws IOException {
		setImage(resolveImage(img));
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		// center on the canvas...
		Dimension canvasSize = getSize();
		g.drawImage(img, (canvasSize.width / 2) - (imageSize.width / 2),
				(canvasSize.height / 2) - (imageSize.height / 2),
				imageSize.width, imageSize.height, Color.white, this);
	}

}
