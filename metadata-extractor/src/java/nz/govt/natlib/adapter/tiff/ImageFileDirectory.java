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

package nz.govt.natlib.adapter.tiff;

import java.io.IOException;

import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.Element;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.IntegerElement;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.fx.RationalElement;
import nz.govt.natlib.fx.StringElement;

/**
 * @author unascribed
 * @version 1.0
 */
public class ImageFileDirectory extends Element {

	private boolean bigEndian;

	private Element sElement;

	private Element iElement;

	private Element rElement;

	private Element stElement;

	public ImageFileDirectory(boolean bigEndian) {
		this.bigEndian = bigEndian;
		sElement = new IntegerElement(IntegerElement.SHORT_SIZE, bigEndian,
				IntegerElement.DECIMAL_FORMAT);
		iElement = new IntegerElement(IntegerElement.INT_SIZE, bigEndian,
				IntegerElement.DECIMAL_FORMAT);
		rElement = new RationalElement();
		stElement = new StringElement();
	}

	// a flexible variable length record that delves into other parts of the
	// file.
	public void read(DataSource data, ParserContext ctx) throws IOException {
		// read how many entries there are...
		int ifdEntryCount = (int) FXUtil.getNumericalValue(data,
				IntegerElement.SHORT_SIZE, bigEndian);
		Object[] imgLen = null;
		Object[] imgData = null;

		for (int i = 0; i < ifdEntryCount; i++) {
			int tag = (int) FXUtil.getNumericalValue(data,
					IntegerElement.SHORT_SIZE, bigEndian);
			int type = (int) FXUtil.getNumericalValue(data,
					IntegerElement.SHORT_SIZE, bigEndian);
			int length = (int) FXUtil.getNumericalValue(data,
					IntegerElement.INT_SIZE, bigEndian);
			int value = (int) FXUtil.getNumericalValue(data,
					IntegerElement.INT_SIZE, bigEndian); // which may be an
															// offset to the
															// actual data
															// value...

			String name = TIFFUtil.getTagName(tag);
			Object result = null;
			if ((TIFFUtil.getUnitLength(type) * length) > 4) {
				Element tiffElement = null;
				// need to go into the file to get the value.
				// Note: some other special types may be compounded into this
				// one...
				if (tag == 32932) {
					tiffElement = new MSAnnotationBlock(length);
				} else {
					switch (type) {
					case 0x02:
						tiffElement = stElement;
						break;
					case 0x03:
						tiffElement = new IntegerElement(2, (int) length,
								bigEndian, IntegerElement.DECIMAL_FORMAT);
						break;
					case 0x04:
						tiffElement = new IntegerElement(4, (int) length,
								bigEndian, IntegerElement.DECIMAL_FORMAT);
						break;
					case 0x05:
						tiffElement = rElement;
						break;
					}
				}

				if (tiffElement != null) {
					long p = data.getPosition();
					data.setPosition(value);
					result = FXUtil.readElement(data, tiffElement);
					data.setPosition(p); // put it back again
				}
			} else {
				result = new Long(value);
			}

			if (name == null) {
				name = "Custom";
				result = null;
			}

			// not all tags should make it into the IFD Entry Directory
			// results...
			if (tag == 273) {
				// if the entry if for actual image data then put that in too...
				// it's a huge array of image data...

				// of (course there might be only one element...
				if (result.getClass().isArray()) {
					imgData = (Object[]) result;
				} else {
					imgData = new Object[] { result };
				}
				continue; // don't put this in - it's meaningless until the
							// data is stored along with it.
			}

			if (tag == 279) {
				// it's an array of image data length...

				// of (course there might be only one element...
				if (result.getClass().isArray()) {
					imgLen = (Object[]) result;
				} else {
					imgLen = new Object[] { result };
				}
				
				// We don't want this in the output either.
				continue;
			}
			

			// render the results...
			ctx.fireStartParseEvent("element");
			ctx.fireParseEvent("name", name);
			ctx.fireParseEvent("tag", tag);
			ctx.fireParseEvent("length", length);
			ctx.fireParseEvent("type", type);

			// handle arrays of results...
			if ((result != null) && (result.getClass().isArray())) {
				Object[] valArray = (Object[]) result;
				for (int vi = 0; vi < valArray.length; vi++) {
					ctx.fireParseEvent("value", valArray[vi]);
				}
			} else {
				ctx.fireParseEvent("value", result);
			}
			ctx.fireEndParseEvent("element");
		}
	}

}