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

package nz.govt.natlib.adapter.jpg;

import java.io.File;
import java.io.IOException;

import nz.govt.natlib.adapter.AdapterUtils;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.adapter.exif.EXIFElement;
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
 * Adapter for JPEG images.
 * 
 * @author Simon Reed
 * @version 1.0
 */

public class JpgAdapter extends DataAdapter {

	// The compound element "parser" to read a jfif header
	private Element jfifElement = new CompoundElement(new String[] {
			"Identifier", "MajorVersion", "MinorVersion", "DensityUnits",
			"XDensity", "YDensity", "ThumbnailWidth", "ThumbnailHeight" },
			new Element[] {
					new FixedLengthStringElement(5, true),
					new IntegerElement(IntegerElement.BYTE_SIZE, false,
							IntegerElement.DECIMAL_FORMAT),
					new IntegerElement(IntegerElement.BYTE_SIZE, false,
							IntegerElement.DECIMAL_FORMAT),
					new IntegerElement(IntegerElement.BYTE_SIZE, false,
							IntegerElement.DECIMAL_FORMAT),
					new IntegerElement(IntegerElement.SHORT_SIZE, true,
							IntegerElement.DECIMAL_FORMAT),
					new IntegerElement(IntegerElement.SHORT_SIZE, true,
							IntegerElement.DECIMAL_FORMAT),
					new IntegerElement(IntegerElement.BYTE_SIZE, false,
							IntegerElement.DECIMAL_FORMAT),
					new IntegerElement(IntegerElement.BYTE_SIZE, false,
							IntegerElement.DECIMAL_FORMAT), });

	// The compound element "parser" to read a frame n header
	private Element jpgElement = new CompoundElement(new String[] {
			"Precision", "ImageHeight", "ImageWidth", "Components" },
			new Element[] {
					new IntegerElement(IntegerElement.BYTE_SIZE, false,
							IntegerElement.DECIMAL_FORMAT),
					new IntegerElement(IntegerElement.SHORT_SIZE, true,
							IntegerElement.DECIMAL_FORMAT),
					new IntegerElement(IntegerElement.SHORT_SIZE, true,
							IntegerElement.DECIMAL_FORMAT),
					new IntegerElement(IntegerElement.BYTE_SIZE, false,
							IntegerElement.DECIMAL_FORMAT), });

	// The compound element "parser" to read the components of a frame n header
	private Element jpgComponentElement = new CompoundElement(new String[] {
			"ComponentId", "ComponentSamplingFactors",
			"ComponentQuantizationTable" }, new Element[] {
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT), });

	public String getVersion() {
		return "1.1";
	}

	public boolean acceptsFile(File file) {
		boolean jpg = false;
		DataSource ftk = null;
		try {
			// Read the header and see if this appears to be a JPG.
			ftk = new FileDataSource(file);
			// Header and default information
			JpgMarker marker = null;
			marker = readMarker(ftk);
			if (marker.type == 0xd8 && marker.length == 0) {
				jpg = true;
			} else {
				LogManager.getInstance().logMessage(
						LogMessage.WORTHLESS_CHATTER,
						file.getName() + " is not a JPG file");
			}
		} catch (IOException ex) {
			LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
					"IO Exception determining JPG file type");
		}
		finally {
			AdapterUtils.close(ftk);
		}
		return jpg;
	}

	public String getOutputType() {
		return "jpg.dtd";
	}

	public String getInputType() {
		return "image/jpeg";
	}

	public String getName() {
		return "JPEG Graphics Adapter";
	}

	public String getDescription() {
		return "Adapts JPEG Graphics Files, including any EXIF data found";
	}

	public void adapt(File oFile, ParserContext ctx) throws IOException {
		// Add the MetaData to the tree!
		DataSource ftk = new FileDataSource(oFile);
		// Header and default information
		ctx.fireStartParseEvent("JPG");
		writeFileInfo(oFile, ctx);
		try {
			JpgMarker marker = null;

			// watchdog variable - for untydy or invalid jpgs
			long posWatchDog = ftk.getPosition();
			long posWatchDogLoops = 0;
			boolean sos = false;
			boolean jfif = false;
			boolean exif = false;
			boolean loop = true;
			
			while (loop) {
				marker = readMarker(ftk);
				long iPos = ftk.getPosition(); // get this now, so whatever the
												// 'readers' do we can always
												// move to the next marker

				// System.out.println(iPos+":"+marker);
				// readers of particular markers...
				if (marker.delim == 0xFF) {
					switch ((int) marker.type) {
					case 0xe0: {
						readJFIF(ftk, ctx, marker);
						jfif = true;
						break;
					}
					case 0xe1: {
						if (!exif) {
							readEXIF(ftk, ctx, marker);
						}
						exif = true;
						break;
					}
					case 0xda: {
						readScan(ftk, ctx, marker);
						sos = true;
						break;
					}
					case 0xc0:
						readFrame(ftk, ctx, marker);
						break;
					case 0xfe:
						readComment(ftk, ctx, marker);
						break;
					case 0xd8:
						readStartOfImage(ftk, ctx, marker);
						break;
					case 0xd9:
						readEndOfImage(ftk, ctx, marker);
						break;
					default:
						String name = JpgUtil.getJpgMarkerName(marker.delim,
								marker.type);
						ctx.fireStartParseEvent(name);
						ctx.fireEndParseEvent(name);
						break;
					}
				}

				if (marker.type == 0xda) {
					// after a 'Start Of Scan' the rest is the IMAGE itself, so
					// move the file pos to the end of the file (less 2 bytes),
					// to pick up the EOF
					ftk.setPosition(oFile.length() - 2);
				} else {
					ftk.setPosition(marker.length + iPos);
				}

				if (marker.type == 0xd9) {
					break; // this is the EOF marker, we are done!
				}

				// WATCH DOG LOOP CHECK FOR JPGS THAT DON'T GO ANYWHERE
				if (posWatchDog == ftk.getPosition()) {
					posWatchDogLoops++;
				} else {
					posWatchDogLoops = 0;
				}
				if (posWatchDogLoops == 2) {
					if (sos && (jfif || exif)) {
						loop = false; // just finish and tidy up - you got
										// everything you could...
						LogManager.getInstance()
								.logMessage(
										LogMessage.INFO,
										"Early termination of "
												+ oFile.getName()
												+ " after Scan Data, "
												+ (exif ? "EXIF"
														: (jfif ? "JFIF" : ""))
												+ " data WAS harvested");
					} else {
						LogManager
								.getInstance()
								.logMessage(
										LogMessage.ERROR,
										"Early termination of "
												+ oFile.getName()
												+ " before Scan Data, no data was harvested");
						throw new RuntimeException(
								"Endless Loop detected in JPG Data of "
										+ oFile.getName() + "");
					}
				}
				posWatchDog = ftk.getPosition();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			ctx.fireEndParseEvent("JPG");
			ftk.close();
		}
	}

	/**
	 * Reads a JPEG marker block.
	 * 
	 * @param ftk
	 * @return
	 * @throws IOException
	 */
	private JpgMarker readMarker(DataSource ftk) throws IOException {
		JpgMarker marker = new JpgMarker();
		marker.delim = FXUtil.getNumericalValue(ftk, 1, true);
		marker.type = FXUtil.getNumericalValue(ftk, 1, true);

		// some of the markers have no length.
		if (marker.delim == 0xFF) {
			if ((marker.type == 0xd8) || (marker.type == 0xd9)) {
				marker.length = 0;
			} else {
				// give the length as a length from the current files marker -
				// JPEG block lengths INCLUDE the two bytes of the length itself
				marker.length = FXUtil.getNumericalValue(ftk, 2, true) - 2;
			}
		}

		return marker;
	}

	public void readFrame(DataSource ftk, ParserContext ctx, JpgMarker marker)
			throws IOException {
		ctx.fireStartParseEvent("IMAGE");
		jpgElement.read(ftk, ctx);
		long components = ctx.getIntAttribute("JPG.IMAGE.COMPONENTS");
		for (int i = 1; i <= components; i++) {
			ctx.fireStartParseEvent("Component");
			jpgComponentElement.read(ftk, ctx);
			ctx.fireEndParseEvent("Component");
		}
		ctx.fireEndParseEvent("IMAGE");
	}

	public void readStartOfImage(DataSource ftk, ParserContext ctx,
			JpgMarker marker) throws IOException {
	}

	public void readEndOfImage(DataSource ftk, ParserContext ctx,
			JpgMarker marker) throws IOException {
	}

	public void readComment(DataSource ftk, ParserContext ctx, JpgMarker marker)
			throws IOException {
		ctx.fireStartParseEvent("Comment");
		String comment = FXUtil.getFixedStringValue(ftk, (int) marker.length);
		ctx.fireParseEvent(comment);
		ctx.fireEndParseEvent("Comment");
	}

	public void readScan(DataSource ftk, ParserContext ctx, JpgMarker marker)
			throws IOException {
		ctx.fireStartParseEvent("ScanData");
		long scanComponents = FXUtil.getNumericalValue(ftk, 1, true);
		ctx.fireParseEvent("Components", scanComponents);
		ctx.fireEndParseEvent("ScanData");
	}

	public void readJFIF(DataSource ftk, ParserContext ctx, JpgMarker marker)
			throws IOException {
		ctx.fireStartParseEvent("JFIF");
		jfifElement.read(ftk, ctx);
		ctx.fireEndParseEvent("JFIF");
	}

	public void readEXIF(DataSource ftk, ParserContext ctx, JpgMarker marker)
			throws IOException {
		if (marker.type != 0xE1) {
			throw new RuntimeException("Not an EXIF block");
		}
		LogManager.getInstance().logMessage(LogMessage.INFO, "Starting EXIF Element");
		LogManager.getInstance().logMessage(LogMessage.INFO, "Calling Constructor");
		EXIFElement exifReader = new EXIFElement();
		LogManager.getInstance().logMessage(LogMessage.INFO, "End of EXIF Constructor");
		
		exifReader.read(ftk, ctx);
		LogManager.getInstance().logMessage(LogMessage.INFO, "Ended EXIF Element");		
	}

}