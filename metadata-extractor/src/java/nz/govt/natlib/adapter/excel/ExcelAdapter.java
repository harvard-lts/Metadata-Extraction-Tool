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

package nz.govt.natlib.adapter.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import nz.govt.natlib.adapter.AdapterUtils;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.adapter.word.OLE.OLEConstants;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.ParserContext;

import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Adapter for Microsoft Excel files. This adapter serves as an example 
 * of using a third party library to extract metadata from a binary
 * file format.
 * 
 * @author unascribed
 * @version 1.0
 */

public class ExcelAdapter extends DataAdapter {

	private static final int Offset_To_DOP = 0x192;

	private static final OLELanguageMap langMap = OLELanguageMap
			.getLanguageMap();

	/**
	 * Determine if this Adapter can extract metadata from the file
	 * provided. This adapter checks both the extension and looks
	 * for an OLE header. 
	 *
	 * @param file The file to check.
	 * @return true if this adapter supports the filetype.
	 */
	public boolean acceptsFile(File file) {
    	// Convert the filename to lower case to help with the comparison.
		String name = file.getName().toLowerCase();

		// If it is an .xls file, then we are probably an Excel file,
		// but check that it also complies with the OLE header requirement
		// as well. This could be extended with additional checks.
		if (ignoreFileExtension || name.endsWith(".xls")) {
			return checkFileHeader(file, OLEConstants.OLE_HEADER);
		}
		return false;
	}

	public String getVersion() {
		return "1.0";
	}

	public String getOutputType() {
		return "excel.dtd";
	}

	public String getInputType() {
		return "application/vnd.ms-excel";
	}

	public String getName() {
		return "Microsoft Excel Adapter";
	}

	public String getDescription() {
		return "Handles Excel Spreadsheets from Excel 2.0 through to XP";
	}

	/**
	 * Extract the metadata out of the file.
	 *
	 * @param file The file to extract metadata from.
	 * @param ctx The context for raise metadata event to.
	 */
	public void adapt(File file, ParserContext ctx) throws IOException {
    	// Start the MSExcel tag.
		ctx.fireStartParseEvent("MSExcel");
		
		// Output the standard information (see DefaultAdapter).
		writeFileInfo(file, ctx);
		
		// Fire a version event.
		ctx.fireParseEvent("Version", "MSExcel");
		POIFSFileSystem fs = null;
		FileInputStream fin = null;
		try {
            // Initialise the POI library.
			POIFSReader r = new POIFSReader();
			
			// Register some listeners.
			r.registerListener(new MainStreamReader(ctx), "Workbook");
			r.registerListener(new SummaryReader(ctx), "\005SummaryInformation");
			r.registerListener(new DocumentSummaryReader(),	"\005DocumentSummaryInformation");

			// Read the file.
			fin = new FileInputStream(file);
			r.read(fin);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			AdapterUtils.close(fin);
			fs = null;
		}
		
		// Close the MSExcel tag.
		ctx.fireEndParseEvent("MSExcel");
	}

	public void readDirectory(POIFSFileSystem fs, DirectoryEntry dir)
			throws Exception {
		if (dir.getEntryCount() == 0) {
			return;
		}

		for (Iterator iter = dir.getEntries(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			if (entry instanceof DirectoryEntry) {
				// .. recurse into this directory
				readDirectory(fs, (DirectoryEntry) entry);
			} else if (entry instanceof DocumentEntry) {
				// entry is a document, which you can read
				DocumentEntry doc = (DocumentEntry) entry;
				readDocument(fs, doc);
			} else {
				// currently, either an Entry is a DirectoryEntry or a
				// DocumentEntry,
				// but in the future, there may be other entry subinterfaces.
				// The
				// internal data structure certainly allows for a lot more entry
				// types.
			}
		}
	}

	public void readDocument(POIFSFileSystem fs, DocumentEntry doc)
			throws Exception {
		// load file system
		DocumentInputStream stream = new DocumentInputStream(doc);

		if (stream.available() > 256) {
			return;
		}

		// process data from stream
		byte[] content = new byte[stream.available()];
		stream.read(content);
		stream.close();

		for (int i = 0; i < content.length; i++) {
			int c = content[i];
			if (c < 0) {
				c = 0x100 + c;
			}
		}

	}

	
	/**
	 * This class is responsible for listening to events from POI,
	 * and then sending the appropriate metadata events to the 
	 * ParserContext.
	 */
	class SummaryReader implements POIFSReaderListener {
		ParserContext into;

		SummaryReader(ParserContext into) {
			this.into = into;
		}

		public void processPOIFSReaderEvent(POIFSReaderEvent event) {
    		// Read the event into a SummaryInformation object.
			SummaryInformation si = null;
			try {
				si = (SummaryInformation) PropertySetFactory.create(event
						.getStream());
			} catch (Exception ex) {
				throw new RuntimeException("Property set stream \""
						+ event.getPath() + event.getName() + "\": " + ex);
			}
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			dateFormatter.applyPattern("yyyy-MM-dd HH:mm:ss");
			
			// Start a summary section.			
			into.fireStartParseEvent("summary");
			
			// Fire all our events to the ParserContext.
			into.fireParseEvent("title", si.getTitle());
			into.fireParseEvent("subject", si.getSubject());
			into.fireParseEvent("keywords", si.getKeywords());
			into.fireParseEvent("comments", si.getComments());
			into.fireParseEvent("lastReviewedBy", si.getLastAuthor());
			into.fireParseEvent("author", si.getAuthor());
			
			// End the summary section.
			into.fireEndParseEvent("summary");

			// Start a properties section.
			into.fireStartParseEvent("properties");
			
			// Fire the properties metadata events.
			if(si.getCreateDateTime() != null) {
				into.fireParseEvent("created", dateFormatter.format(si
					.getCreateDateTime()));
			}
			else {
				into.fireParseEvent("created","");
			}
			if(si.getLastSaveDateTime() != null) {
				into.fireParseEvent("revision", dateFormatter.format(si
						.getLastSaveDateTime()));
			}
			else {
				into.fireParseEvent("revision","");
			}
			
			if (si.getLastPrinted() != null) {
				into.fireParseEvent("printed", dateFormatter.format(si
						.getLastPrinted()));
			} else {
				into.fireParseEvent("printed", "");
			}

			into.fireParseEvent("os", si.getOSVersion());
			into.fireParseEvent("application", si.getApplicationName());
			
			// End the properties section.
			into.fireEndParseEvent("properties");

		}
	}


	class TableStreamReader implements POIFSReaderListener {

		ParserContext into;

		public TableStreamReader(ParserContext into) {
			this.into = into;
		}

		public void processPOIFSReaderEvent(POIFSReaderEvent event) {

			DataSource ftk = null;
			try {
				long dopPos = into.getIntAttribute("MSExcel.FIB.dopOffset");
				ftk = new DocumentDataSource(event.getStream());
				into.fireStartParseEvent("Properties");
				OLEUtils.getDOPProperties(ftk, (int) dopPos, into);
				into.fireEndParseEvent("Properties");
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (ftk != null) {
					try {
						ftk.close();
					} catch (Exception ex) {
						;
					}
				}
			}
		}
	}

	/**
	 * Read elements out of the OLE Header element.
	 */
	class MainStreamReader implements POIFSReaderListener {
		ParserContext into;

		public MainStreamReader(ParserContext into) {
			this.into = into;
		}

		public void processPOIFSReaderEvent(POIFSReaderEvent event) {
			DataSource ftk = null;
			try {
				ftk = new DocumentDataSource(event.getStream());

				into.fireStartParseEvent("header");
				OLEUtils.getHeader(ftk, into);
				into.fireEndParseEvent("header");

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (ftk != null) {
					try {
						ftk.close();
					} catch (Exception ex) {
						;
					}
				}
			}
		}
	}

	class DocumentSummaryReader implements POIFSReaderListener {
		DocumentSummaryReader() {
		}

		public void processPOIFSReaderEvent(POIFSReaderEvent event) {
			DocumentSummaryInformation si = null;
			try {
				si = (DocumentSummaryInformation) PropertySetFactory
						.create(event.getStream());
			} catch (Exception ex) {
				throw new RuntimeException("Property set stream \""
						+ event.getPath() + event.getName() + "\": " + ex);
			}
		}
	}
}
