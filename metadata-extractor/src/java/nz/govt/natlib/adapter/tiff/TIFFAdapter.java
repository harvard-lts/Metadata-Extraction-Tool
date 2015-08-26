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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import nz.govt.natlib.adapter.AdapterUtils;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.Element;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.FileDataSource;
import nz.govt.natlib.fx.IntegerElement;
import nz.govt.natlib.fx.ParserContext;

/**
 * Adapter for TIFF images.
 * 
 * @author unascribed
 * @version 1.0
 */
public class TIFFAdapter extends DataAdapter {

	public static String TIFF_HEADER_1 = "49 49 2A 00";

	public static String TIFF_HEADER_2 = "4D 4D 00 2A";

	public boolean acceptsFile(File file) {
		return checkFileHeader(file, TIFF_HEADER_1) || 
		       checkFileHeader(file, TIFF_HEADER_2);
	}

	public String getVersion() {
		return "1.2";
	}

	public String getOutputType() {
		return "tiff_6_0.dtd";
	}

	public String getInputType() {
		return "image/tiff";
	}

	public String getStyleSheetName() {
		return "tiff_6_0.xslt";
	}

	public String getName() {
		return "Tagged Image File Format, ";
	}

	public String getDescription() {
		return "TIFF file adapter, reads all version from 1.0 to 6.0";
	}

	public void adapt(File file, ParserContext ctx) throws IOException {
		HashMap results = new HashMap();
		// add the MetaData to the tree!
		DataSource ftk = null;
		
		try {
			ftk = new FileDataSource(file);
			ctx.fireStartParseEvent("tiff");
			writeFileInfo(file, ctx);
	
			// work out the endian...
			long endianIndicator = FXUtil.getNumericalValue(ftk,
					IntegerElement.SHORT_SIZE, false);
			boolean bigEndian = endianIndicator == 0x4D4D;
			long version = FXUtil.getNumericalValue(ftk, IntegerElement.SHORT_SIZE,
					bigEndian);
	
			ctx.fireStartParseEvent("Header");
			ctx.fireParseEvent("LittleEndian", !bigEndian);
			ctx.fireParseEvent("Version", "1.0");
			ctx.fireEndParseEvent("Header");
	
			// read the position of the first IFD record...
			long next = FXUtil.getNumericalValue(ftk, IntegerElement.INT_SIZE,
					bigEndian);
	
			// READ THE IFD DIRECTORIES
			while (next != 0x00) {
				ftk.setPosition(next);
				Element tiffIFDRecord = new ImageFileDirectory(bigEndian);
				ctx.fireStartParseEvent("ImageFileDirectory");
				tiffIFDRecord.read(ftk, ctx);
				ctx.fireEndParseEvent("ImageFileDirectory");
	
				// read where the next record is.
				next = FXUtil.getNumericalValue(ftk, IntegerElement.INT_SIZE,
						bigEndian);
			}
	
			ctx.fireEndParseEvent("tiff");
		}
		finally {
			AdapterUtils.close(ftk);
		}

	}

}