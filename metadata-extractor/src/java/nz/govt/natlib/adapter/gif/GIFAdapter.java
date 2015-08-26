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

package nz.govt.natlib.adapter.gif;

import java.io.File;
import java.io.IOException;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ByteChomperElement;
import nz.govt.natlib.fx.CompoundBitElement;
import nz.govt.natlib.fx.CompoundElement;
import nz.govt.natlib.fx.ConstantElement;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.Element;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.FileDataSource;
import nz.govt.natlib.fx.FixedLengthStringElement;
import nz.govt.natlib.fx.GifAspectRatioIntegerElement;
import nz.govt.natlib.fx.IntegerElement;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * Adapter for GIF images.
 * 
 * @author Simon Reed
 * @version 1.0
 */

public class GIFAdapter extends DataAdapter {

	// The compound element "parser" to read a GIF header
	private Element gifHeaderElement = new CompoundElement(new String[] {
			"identifier", "version" }, new Element[] {
			new FixedLengthStringElement(3, true),
			new FixedLengthStringElement(3, true), });

	private Element gifImageElement = new CompoundElement(new String[] {
			"left-margin", "top-margin", "width", "height", "PACKED",
			"encoding", "compressed" }, new Element[] {
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new CompoundBitElement(new String[] { "has-local-map",
					"interlaced", "", "image-bits-per-pixel" },
					new CompoundBitElement.BitElement[] {
							new CompoundBitElement.BooleanBitReader(1),
							new CompoundBitElement.BooleanBitReader(1),
							new CompoundBitElement.BitChomper(3),
							new CompoundBitElement.AddingBitReader(3, 1), }),
			new ConstantElement("\"Raster\""),
			new ConstantElement("\"false\""), });

	private Element gifApplicationElement = new CompoundElement(new String[] {
			"", "identifier", "auth-code" }, new Element[] {
			new ByteChomperElement(IntegerElement.BYTE_SIZE),
			new FixedLengthStringElement(8), new FixedLengthStringElement(3),
	// followed by heaps of sub-blocks...
			});

	private Element gifPlainTextElement89a = new CompoundElement(new String[] {
			"", "text-left-pos", "text-top-pos", "grid-height", "grid-width",
			"cell-width", "cell-height", "foreground-color-index",
			"background-color-index" }, new Element[] {
			new ByteChomperElement(IntegerElement.BYTE_SIZE),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT), });

	private Element gifControlElement89a = new CompoundElement(new String[] {
			"", "PACKED", "delay", "transparent-colour", "" }, new Element[] {
			new ByteChomperElement(IntegerElement.BYTE_SIZE),
			new CompoundBitElement(new String[] { "", "disposal-method",
					"user-input", "transparent" },
					new CompoundBitElement.BitElement[] {
							new CompoundBitElement.BitChomper(3),
							new CompoundBitElement.EnumeratedBitReader(3,
									new String[] { "0", "1", "2", "3" },
									new String[] { "not specified",
											"do not dispose",
											"restore to background",
											"restore to previous" }, "n/a"),
							new CompoundBitElement.BooleanBitReader(1),
							new CompoundBitElement.BooleanBitReader(1), }),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new ByteChomperElement(IntegerElement.BYTE_SIZE), });

	private Element gifScreenElement87a = new CompoundElement(
			new String[] { "screen-width", "screen-height", "PACKED",
					"background-colour", "" },
			new Element[] {
					new IntegerElement(IntegerElement.SHORT_SIZE, false,
							IntegerElement.DECIMAL_FORMAT),
					new IntegerElement(IntegerElement.SHORT_SIZE, false,
							IntegerElement.DECIMAL_FORMAT),
					new CompoundBitElement(
							new String[] { "global-map", "colour-resolution",
									"", "bits-per-pixel" },
							new CompoundBitElement.BitElement[] {
									new CompoundBitElement.BooleanBitReader(1),
									new CompoundBitElement.AddingBitReader(3, 1),
									new CompoundBitElement.BitChomper(1),
									new CompoundBitElement.AddingBitReader(3, 1), }),
					new IntegerElement(IntegerElement.BYTE_SIZE, false,
							IntegerElement.DECIMAL_FORMAT),
					new ByteChomperElement(1), });

	private Element gifScreenElement89a = new CompoundElement(
			new String[] { "screen-width", "screen-height", "PACKED",
					"background", "aspect-ratio" },
			new Element[] {
					new IntegerElement(IntegerElement.SHORT_SIZE, false,
							IntegerElement.DECIMAL_FORMAT),
					new IntegerElement(IntegerElement.SHORT_SIZE, false,
							IntegerElement.DECIMAL_FORMAT),
					new CompoundBitElement(
							new String[] { "global-map", "colour-resolution",
									"colour-map-sorted", "bits-per-pixel" },
							new CompoundBitElement.BitElement[] {
									new CompoundBitElement.BooleanBitReader(1),
									new CompoundBitElement.AddingBitReader(3, 1),
									new CompoundBitElement.BooleanBitReader(1),
									new CompoundBitElement.AddingBitReader(3, 1), }),
					new IntegerElement(IntegerElement.BYTE_SIZE, false,
							IntegerElement.DECIMAL_FORMAT),
					new GifAspectRatioIntegerElement(IntegerElement.BYTE_SIZE,
							false), });

	public GIFAdapter() {
	}

	public String getVersion() {
		return "1.0";
	}

	public String getName() {
		return "GIF Graphics Adapter";
	}

	public String getDescription() {
		return "Adapts Interlaced and NonInterlaced GIF87a and 89a images";
	}

	public boolean acceptsFile(File file) {
		boolean gif = false;
		
		try {
			// Read the header bytes to check if this really is a GIF.
			DataSource ftk = new FileDataSource(file);
			// Header and default information
			String head = FXUtil.getFixedStringValue(ftk, 6);
			if ((head.toLowerCase().equals("gif87a"))
					|| (head.toLowerCase().equals("gif89a"))) {
				gif = true;
			}
			ftk.close();
		} catch (IOException ex) {
			LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
					"IO Exception determining GIF file type");
		}
		return gif;
	}

	public String getOutputType() {
		return "gif.dtd";
	}

	public String getInputType() {
		return "image/gif";
	}

	public void adapt(File oFile, ParserContext ctx) throws IOException {
		// Add the MetaData to the tree!
		DataSource ftk = new FileDataSource(oFile);
		// Header and default information
		ctx.fireStartParseEvent("GIF");
		writeFileInfo(oFile, ctx);
		try {
			gifHeaderElement.read(ftk, ctx);

			String version = ((String) ctx.getAttribute("GIF.version"))
					.toLowerCase();
			// this is where the standards file formats diverge
			if (version.equals("87a")) {
				gifScreenElement87a.read(ftk, ctx);
			} else if (version.equals("89a")) {
				gifScreenElement89a.read(ftk, ctx);
			} else {
				throw new RuntimeException("Unknown GIF Version :" + version);
			}

			// now jump over the global colour map.
			boolean hasGlobalMap = ctx
					.getBooleanAttribute("GIF.PACKED.global-map");
			// System.out.println(hasGlobalMap);
			if (hasGlobalMap) {
				int bitsPerPixel = (int) ctx
						.getIntAttribute("GIF.PACKED.bits-per-pixel");
				readColorTable(ftk, ctx, bitsPerPixel);
			}

			// start reading all the blocks...
			int imgC = 1;
			boolean clean = false;
			while (true) {
				long b = FXUtil.getNumericalValue(ftk.getData(1), false);

				// System.out.println("Block head "+b+"
				// ("+Integer.toHexString((int)b)+")");
				if (b == 0x21) {
					/* Control Block */
					// there are different kinds of control block...
					long sb = FXUtil.getNumericalValue(ftk.getData(1), false);
					if (sb == 0xf9) {
						ctx.fireStartParseEvent("control-block");
						ctx.fireParseEvent("sequence", imgC);
						gifControlElement89a.read(ftk, ctx);
						ctx.fireEndParseEvent("control-block");
					} else if (sb == 0x01) {
						// plain text
						ctx.fireStartParseEvent("text");
						gifPlainTextElement89a.read(ftk, ctx);
						int blocks = readSubBlocks(ftk, ctx, false);
						ctx.fireEndParseEvent("text");

					} else if (sb == 0xfe) {
						// plain text
						ctx.fireStartParseEvent("comment");
						int blocks = readSubBlocks(ftk, ctx, false);
						ctx.fireEndParseEvent("comment");
					} else if (sb == 0xff) {
						ctx.fireStartParseEvent("application");
						gifApplicationElement.read(ftk, ctx);
						int blocks = readSubBlocks(ftk, ctx, true);
						ctx.fireParseEvent("data-blocks", blocks);
						ctx.fireEndParseEvent("application");
					} else {
						// System.out.println("unknown extension block "+sb+" at
						// "+(ftk.getPosition()-1));
						// can we recover and move on... No!
						throw new RuntimeException("unknown extension block "
								+ sb + " at " + (ftk.getPosition()));
					}
				} else if (b == 0x3b) {
					/* Terminator */
					clean = true;
					break;
				} else if (b == 0x2c) {
					/* The Image... */
					ctx.fireStartParseEvent("image-info");
					ctx.fireParseEvent("sequence", imgC);
					gifImageElement.read(ftk, ctx);

					// move the pointer along to the next block (skip local
					// color table and image data)
					boolean hasLocalMap = ctx
							.getBooleanAttribute("GIF.image-info.PACKED.has-local-map");
					if (hasLocalMap) {
						int bitsPerPixel = (int) ctx
								.getIntAttribute("GIF.PACKED.image-bits-per-pixel");
						readColorTable(ftk, ctx, bitsPerPixel);
					}

					// ...and the image data itself...
					byte lzwMinCodeSize = ftk.getData(1)[0];
					int blocks = readSubBlocks(ftk, ctx, true);
					ctx.fireParseEvent("data-blocks", blocks);

					// done!
					ctx.fireEndParseEvent("image-info");
					imgC++;
				} else {
					/* Unknown */
					clean = false;
					break;
				}
			}

			ctx.fireParseEvent("frames", imgC - 1);
			ctx.fireParseEvent("clean-termination", clean);
			ctx.fireParseEvent("animated", imgC > 2 ? "true" : "false");

		} catch (Exception ex) {
			ex.printStackTrace();
			ex.fillInStackTrace();
			throw new RuntimeException(ex);
		} finally {
			ctx.fireEndParseEvent("GIF");
			ftk.close();
		}
	}

	/*
	 * The methods below are generally to skip over the data itself without
	 * extracting much about it...
	 */
	private void readColorTable(DataSource ftk, ParserContext ctx,
			int bitsPerPixel) throws IOException {
		int colorEntries = (int) Math.pow(2, bitsPerPixel) * 3;
		ftk.setPosition(ftk.getPosition() + colorEntries);
	}

	private int readSubBlocks(DataSource ftk, ParserContext ctx, boolean skip)
			throws IOException {
		// read until terminated...
		boolean hasMoreBlocks = true;
		int i = 0;
		while (hasMoreBlocks) {
			hasMoreBlocks = readSubBlock(ftk, ctx, skip);
			if (hasMoreBlocks)
				i++;
		}
		return i;
	}

	private boolean readSubBlock(DataSource ftk, ParserContext ctx, boolean skip)
			throws IOException {
		byte[] b = ftk.getData(1);
		long size = FXUtil.getNumericalValue(b, false);
		if (size == 0x00) {
			return false;
		}
		if (skip) {
			ftk.setPosition(ftk.getPosition() + size);
		} else {
			String value = FXUtil.getFixedStringValue(ftk, (int) size);
			ctx.fireParseEvent(value);
		}
		return true;
	}

}