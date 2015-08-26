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

package nz.govt.natlib.adapter.bmp;

import java.io.File;
import java.io.IOException;

import nz.govt.natlib.adapter.AdapterUtils;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.CompoundElement;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.Element;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.FileDataSource;
import nz.govt.natlib.fx.FixedLengthStringElement;
import nz.govt.natlib.fx.IntegerElement;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * Adapter for BMP images. This adapter serves as an excellent example for
 * extracting metadata out of binary files.
 * 
 * It calls the standard DataAdapter.writeFileInfo(File, ParserContext) to
 * extract basic file information such as the last modified date and filename.
 * Any new adapter will likely rely on this method to extract basic metadata.
 * 
 * To extract specific metadata, this adapter relies on the Element
 * architecture. Each Element is capable of reading a stream of bytes and
 * converting it into a value such as a String or Integer. The Element
 * implementations are also responsible for firing events to the ParserListener,
 * thereby simplifying the code inside each adapter.
 * 
 * In addition to extracting simple information blocks, this adapter also
 * demonstrates the possibility of extracting repeated blocks of data such as
 * color tables. This adapter uses metadata from the header block to determine
 * whether a color table is present, where it is located, and how many colors it
 * contains.
 * 
 * @author Nic Evans
 * @version 1.0
 */
public class BitmapAdapter extends DataAdapter {

	/**
	 * Defines the format of the bitmap's header. The header element is the
	 * block of bytes at the beginning of the file:
	 * 
	 * Signature: The signature bytes will always be "BM". This is part of the
	 * magic bytes string. FileSize: Tells us how large the file is in bytes.
	 * This will be exactly the same as the filesize read from the filesystem.
	 * reserved: A reserved 4 byte (standard INT size) integer. DataOffset: A
	 * pointer to the data.
	 */
	private Element headerElement = new CompoundElement(new String[] {
			"Signature", "FileSize", "reserved", "DataOffset" }, new Element[] {
			new FixedLengthStringElement(2),
			new IntegerElement(IntegerElement.INT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.INT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.INT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT), });

	/**
	 * Defines the format of the bitmap's info header. This section begins
	 * immediately after the header element. As can be seen from the
	 * definitions, this is a collection of INT and SHORT values describing
	 * everything from width and height through to the colors and compression of
	 * the file.
	 */
	private Element infoHeaderElement = new CompoundElement(new String[] {
			"Length", "width", "height", "planes", "bitcount", "compression",
			"imagesize", "Xresolution", "Yresolution", "colors",
			"importantcolors" }, new Element[] {
			new IntegerElement(IntegerElement.INT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.INT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.INT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.INT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.INT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.INT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.INT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.INT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.INT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT), });

	/**
	 * Defines the format of the Bitmap's color table. The presence of this
	 * metadata can be determined based on attributes read in the information
	 * header. If this block is present, the adaptFile method will seek to the
	 * appropriate position in the file and read the color table, which is an
	 * array of these elements.
	 */
	private Element colorTableElement = new CompoundElement(new String[] {
			"red", "green", "blue", "reserved" }, new Element[] {
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT), });

	/**
	 * This method tests whether this is adapter wants to attempt to extract
	 * metadata from this file. The standard adapters test for a file extension
	 * match, and then make additional tests to increase the confidence of
	 * getting the right type. These extra tests may involve checking header
	 * bytes or looking for particular sequences of characters in the file.
	 * 
	 * This method may make use of the checkFileHeader(File,String) helper
	 * method.
	 * 
	 * @param file
	 *            The file to test.
	 * @return True if this adapter will attempt to extract the metadata from
	 *         the file.
	 */
	public boolean acceptsFile(File file) {

		// Start by assuming that this is not a bitmap.		
		boolean bmp = false;
		DataSource ftk = null;
		
		try {
			// Start to read the bitmap, and make sure that the first
			// two bytes are "BM", which is the standard "magic bytes" for
			// a bitmap file.
			ftk = new FileDataSource(file);
			String header = FXUtil.getFixedStringValue(ftk, 2);
			bmp = header.equalsIgnoreCase("bm");
		} catch (Exception ex) {
			// We got an exception trying to read the expected characters.
			// Perhaps the file doesn't have two bytes. Whatever the
			// problem, this can't be a bitmap file.
			bmp = false;
		}
		finally {
			AdapterUtils.close(ftk);
		}

		// Log a message if this isn't a real bitmap file.
		if (!bmp) {
			LogManager.getInstance().logMessage(LogMessage.INFO,
					file.getName() + " is not a bitmap file");
		}

		// Return true if we believe this is a bitmap; otherwise false.
		return bmp;
	}

	/**
	 * Return the DTD of the native metadata file this DataAdapter produces.
	 * This file produces native metadata in the format of "bmp.dtd".
	 * 
	 * @return The filename of the DTD.
	 */
	public String getOutputType() {
		return "bmp.dtd";
	}

	/**
	 * Return the MimeType of the type of file this adapter "adapts".
	 * 
	 * This adapter is used for extracting metadata from "image/bmp" files.
	 * 
	 * @return the MIME Type.
	 */
	public String getInputType() {
		return "image/bmp";
	}

	/**
	 * Returns the version of the adapter. This is the first release of the
	 * bitmpa adapter.
	 * 
	 * @return The version of the adapter.
	 */
	public String getVersion() {
		return "1.0";
	}

	public void adapt(File file, ParserContext ctx) throws IOException {
		DataSource ftk = new FileDataSource(file);

		// Open the BMP section of the metadata file. Part of the
		// bmp.dtd defintion is that the native format starts with an
		// opening <BMP> tag.
		ctx.fireStartParseEvent("BMP");

		// Extract the basic file metadata. This includes things like
		// last modified date and so forth.
		writeFileInfo(file, ctx);

		// Extract information from the header element.
		ctx.fireStartParseEvent("header");
		headerElement.read(ftk, ctx);
		ctx.fireEndParseEvent("header");

		// Extract information from the "information" element.
		ctx.fireStartParseEvent("information");
		infoHeaderElement.read(ftk, ctx);
		ctx.fireEndParseEvent("information");

		// is there a color map? We can check from the "bitcount" attribute
		// found in the "information" element.
		if (ctx.getIntAttribute("BMP.information.bitcount") <= 8) {

			// Using metadata already extracted, we can move to the right place
			// in the file...
			long length = ctx.getIntAttribute("BMP.information.length");
			long colors = ctx.getIntAttribute("BMP.information.colors");
			ftk.setPosition(ftk.getPosition() + (40 - length));

			// Now iterate through each color and write out a color index
			// metadata section.
			for (int i = 0; i < colors; i++) {
				ctx.fireStartParseEvent("color");
				ctx.fireParseEvent("index", i);
				colorTableElement.read(ftk, ctx);
				ctx.fireEndParseEvent("color");
			}
		}

		// We must now close the root <BMP> tag.
		ctx.fireEndParseEvent("BMP");

		// Close the datasource.
		ftk.close();
	}

	/**
	 * Get the name of the adapter. This is the "Bitmap Adapter".
	 * 
	 * @return The name of the adapter.
	 */
	public String getName() {
		return "Bitmap Adapter";
	}

	/**
	 * Gets a simple description of the Bitmap adapter.
	 * 
	 * @return The description of the adapter.
	 */
	public String getDescription() {
		return "Handles Bitmap files";
	}

}