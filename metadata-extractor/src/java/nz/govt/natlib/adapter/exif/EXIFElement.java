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

package nz.govt.natlib.adapter.exif;

import java.io.IOException;

import nz.govt.natlib.fx.ConstantElement;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.Element;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.FixedLengthStringElement;
import nz.govt.natlib.fx.IntegerElement;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.fx.RationalElement;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * @author Nic Evans
 * @version 1.0
 */

public class EXIFElement extends Element {

	/**
	 * The main method used by parsers top execute the functionality of the
	 * Element.
	 * 
	 * @param ftk
	 *            the source of the data to be parsed by this element
	 * @param ctx
	 *            the context of the parser, within which this element is being
	 *            executed. Elements should output all significant parse events
	 *            into this context.
	 * @throws IOException
	 */
	public void read(DataSource ftk, ParserContext ctx) throws IOException {
		
		LogManager.getInstance().logMessage(LogMessage.INFO, "Checking EXIF Marker");
		
		String exifMarker = FXUtil.getFixedStringValue(ftk, 6);
		long tagStart = ftk.getPosition();
		if (!exifMarker.substring(0, 4).equalsIgnoreCase("EXIF")) {
			throw new RuntimeException("Not an EXIF block");
		}

		ctx.fireStartParseEvent("EXIF");
		// read the endian and tiff markers
		
		LogManager.getInstance().logMessage(LogMessage.INFO, "Attempting to read markers");
		
		long endianMarker = FXUtil.getNumericalValue(ftk, 2, true);
		boolean bigEndian = endianMarker != 0x4949;
		long tiffMarker = FXUtil.getNumericalValue(ftk, 2, bigEndian);
		
		LogManager.getInstance().logMessage(LogMessage.INFO, "Finished Read markers");
		

		if (tiffMarker != 0x002A) {
			throw new RuntimeException("EXIF block not in TIFF Format");
		}

		// IFD0 (Main Image)
		long ifdOffset = FXUtil.getNumericalValue(ftk, 4, bigEndian);
		ftk.setPosition(ftk.getPosition() + (ifdOffset - 0x8));
		readIFD(ftk, ctx, tagStart, bigEndian);

		// IFD1 (Thumbnail)
		// ifdOffset = FXUtil.getNumericalValue(ftk,4,bigEndian);
		// ftk.setPosition(ifdOffset);
		// readIFD(ftk,ctx,bigEndian);

		ctx.fireEndParseEvent("EXIF");
	}

	public void readIFD(DataSource ftk, ParserContext ctx, long offset,
			long tagStart, boolean bigEndian) throws IOException {
		long p = ftk.getPosition();
		ftk.setPosition(offset);
		readIFD(ftk, ctx, tagStart, bigEndian);
		ftk.setPosition(p); // put it back again
	}

	public void readIFD(DataSource ftk, ParserContext ctx, long tagStart,
			boolean bigEndian) throws IOException {
		long count = FXUtil.getNumericalValue(ftk, 2, bigEndian);
		
		LogManager.getInstance().logMessage(LogMessage.INFO, "Count of EXIF: " + count);
		
		int UNIT_LENGTH[] = new int[] { 0 /* not used */, 1, 1, 2, 4, 8, /*
																		 * not
																		 * used
																		 */
				1, 1, 2, 4, 8, 4, 8 };
		for (int i = 0; i < count; i++) {
			LogManager.getInstance().logMessage(LogMessage.INFO, "In read EXIF: " + i);
			
			int tag = (int) FXUtil.getNumericalValue(ftk,
					IntegerElement.SHORT_SIZE, bigEndian);
			int type = (int) FXUtil.getNumericalValue(ftk,
					IntegerElement.SHORT_SIZE, bigEndian);
			int length = (int) FXUtil.getNumericalValue(ftk,
					IntegerElement.INT_SIZE, bigEndian);
			String name = EXIFUtil.getTagName(tag);
			// System.out.println("["+i+"] Tag :"+tag+"("+name+") type :"+type+"
			// length :"+length);
			// if (type>UNIT_LENGTH.length) type=0; // unknown types not welcome
			boolean offset = UNIT_LENGTH[type] * length > 4;
			long val = 0;
			if ((!offset) && (tag != 0x9000)) { // i.e. exif version is special
				val = FXUtil.getNumericalValue(ftk, UNIT_LENGTH[type],
						bigEndian); // which may be an offset to the actual data
									// value...
				// must move file pointer 4 regardless of actual amount read
				int advance = 4 - UNIT_LENGTH[type];
				ftk.setPosition(ftk.getPosition() + advance);
			} else {
				// if it is an offset, then read the 4 byte offset value
				// (nothing to do with the real value)
				val = FXUtil.getNumericalValue(ftk, IntegerElement.INT_SIZE,
						bigEndian); // which may be an offset to the actual data
									// value...
			}

			Object result = null;
			if (offset) {
				Element tiffElement = null;
				switch (type) {
				case 0x01:
					tiffElement = new IntegerElement(1, (int) length,
							bigEndian, IntegerElement.DECIMAL_FORMAT);
					break;
				case 0x02:
					tiffElement = new FixedLengthStringElement(length, true);
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
					tiffElement = new RationalElement(bigEndian);
					break;
				case 0x06:
					tiffElement = new IntegerElement(1, (int) length,
							bigEndian, IntegerElement.DECIMAL_FORMAT);
					break;
				case 0x07:
					tiffElement = new ConstantElement("Manufacturer Defn");
					break;
				case 0x08:
					tiffElement = new IntegerElement(2, (int) length,
							bigEndian, IntegerElement.DECIMAL_FORMAT);
					break;
				case 0x09:
					tiffElement = new IntegerElement(4, (int) length,
							bigEndian, IntegerElement.DECIMAL_FORMAT);
					break;
				case 0x10:
					tiffElement = new RationalElement();
					break;
				}

				// again with the special cases
				if (tag == 0x9286) {
					// read a comment from the IFDd
					tiffElement = new FixedLengthStringElement(length, true);
				}

				if (tiffElement != null) {
					long p = ftk.getPosition();
					ftk.setPosition(val + tagStart);
					result = FXUtil.readElement(ftk, tiffElement);
					ftk.setPosition(p); // put it back again
				}
			} else {

				switch (type) {
				case 0x02:
					result = "n/a string";
					break;
				default:
					result = new Long(val);
					break;
				}

				// special cases...
				if (tag == 0x9000) {
					// EXIF version...
					// cunning way to read 4 bytes from a long...
					byte c[] = new byte[5];
					c[0] = (byte) ((val & 0xFF000000) / 0x00FFFFFF);
					c[1] = (byte) ((val & 0x00FF0000) / 0x0000FFFF);
					c[2] = '.';
					c[3] = (byte) ((val & 0x0000FF00) / 0x000000FF);
					c[4] = (byte) ((val & 0x000000FF));
					result = new String(c);
				}
			}

			// read it and the special cases (sigh.. don't you love 'special'
			// cases?)
			if (tag == 0x8769) {
				// special IFD EXIF TAG
				readIFD(ftk, ctx, val + tagStart, tagStart, bigEndian);
			} else if (tag == 0x927c) {
				// maker note - stored as an IFD...
				long p = ftk.getPosition();
				// don't do this - as camera manufacturers do different things
				// readIFD(ftk,ctx,val+12,bigEndian);
			} else {
				String tagName = EXIFUtil.getTagName(tag);
				String tagValue = result == null ? "" : result.toString();
				if (tagName != null) {
					ctx.fireStartParseEvent(tagName);
					ctx.fireParseEvent("TIFF_TAG", tag);
					ctx.fireParseEvent("TIFF_TYPE", type);
					ctx.fireParseEvent("VALUE", tagValue);
					ctx.fireEndParseEvent(tagName);
				} else {
					// do something about unknowns
					// System.out.println("Unknown tag
					// :"+Long.toHexString(tag)+" = "+result);
				}
			}
		}
	}
}