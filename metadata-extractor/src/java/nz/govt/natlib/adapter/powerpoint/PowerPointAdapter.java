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

package nz.govt.natlib.adapter.powerpoint;

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
 * Adapter for Microsoft Powerpoint files.
 * 
 * @author unascribed
 * @version 1.0
 */

public class PowerPointAdapter extends DataAdapter {

	private static final int Offset_To_DOP = 0x192;

	private static final OLELanguageMap langMap = OLELanguageMap
			.getLanguageMap();

	public boolean acceptsFile(File file) {
		String name = file.getName().toLowerCase();

		if (ignoreFileExtension || name.endsWith(".ppt")) {
			return checkFileHeader(file, OLEConstants.OLE_HEADER);
		}
		return false;
	}

	public String getVersion() {
		return "1.0";
	}

	public String getOutputType() {
		return "powerpoint.dtd";
	}

	public String getInputType() {
		return "application/vnd.ms-powerpoint";
	}

	public String getName() {
		return "Microsoft Powerpoint Adapter";
	}

	public String getDescription() {
		return "Adapts Powerpoint documents";
	}

	public void adapt(File file, ParserContext ctx) throws IOException {
		ctx.fireStartParseEvent("MSPowerPoint");
		writeFileInfo(file, ctx);
		ctx.fireParseEvent("Version", "MSPowerPoint");
		POIFSFileSystem fs = null;
		FileInputStream fin = null;
		try {
			// fs = new POIFSFileSystem(new FileInputStream(file));
			// DirectoryEntry root = fs.getRoot();
			// readDirectory(fs,root);

			POIFSReader r = new POIFSReader();
			r
					.registerListener(new MainStreamReader(ctx),
							"PowerPoint Document");
			r
					.registerListener(new SummaryReader(ctx),
							"\005SummaryInformation");
			r.registerListener(new DocumentSummaryReader(),
					"\005DocumentSummaryInformation");
			// r.registerListener(new TableStreamReader(ctx), "1Table");
			fin = new FileInputStream(file);
			r.read(fin);
			fin.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			AdapterUtils.close(fin);
			fs = null;
		}
		ctx.fireEndParseEvent("MSPowerPoint");
	}

	public void readDirectory(POIFSFileSystem fs, DirectoryEntry dir)
			throws Exception {
		if (dir.getEntryCount() == 0) {
			return;
		}

		for (Iterator iter = dir.getEntries(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			System.out.println("found entry: " + entry.getName());
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
			System.out.println(i + ", " + Integer.toString(c) + "\t"
					+ Integer.toHexString(c) + "\t" + (char) c);
		}

	}

	class SummaryReader implements POIFSReaderListener {
		ParserContext into;

		SummaryReader(ParserContext into) {
			this.into = into;
		}

		public void processPOIFSReaderEvent(POIFSReaderEvent event) {
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
			into.fireStartParseEvent("summary");
			// into.fireParseEvent("template",si.getTemplate());
			into.fireParseEvent("title", si.getTitle());
			into.fireParseEvent("subject", si.getSubject());
			into.fireParseEvent("keywords", si.getKeywords());
			into.fireParseEvent("comments", si.getComments());
			into.fireParseEvent("lastReviewedBy", si.getLastAuthor());
			into.fireParseEvent("author", si.getAuthor());
			into.fireEndParseEvent("summary");

			into.fireStartParseEvent("properties");

			long notAvailable = -11644473600000l;

			if (si.getCreateDateTime() != null && si.getCreateDateTime().getTime() != notAvailable) {
				into
						.fireParseEvent("created", si.getCreateDateTime()
								.getTime() /* dateFormatter.format(si.getCreateDateTime()) */);
			} else {
				into.fireParseEvent("created", "");
			}
			into.fireParseEvent("revision", dateFormatter.format(si
					.getLastSaveDateTime()));
			if (si.getLastPrinted() != null) {
				into.fireParseEvent("printed", dateFormatter.format(si
						.getLastPrinted()));
			} else {
				into.fireParseEvent("printed", "");
			}
			// into.fireParseEvent("revisioncount", si.getRevNumber());
			// into.fireParseEvent("timeediting", si.getEditTime().getTime());
			// into.fireParseEvent("pages", si.getPageCount());
			// into.fireParseEvent("words", si.getWordCount());
			// into.fireParseEvent("characters", si.getCharCount());
			into.fireParseEvent("os", si.getOSVersion());
			into.fireParseEvent("application", si.getApplicationName());
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

				// FIB oFib = new FIB(langMap,Offset_To_DOP);
				// into.fireStartParseEvent("FIB");
				// oFib.read(ftk,into);
				// into.fireEndParseEvent("FIB");

				// processText(oFib,ftk, into);
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
			// System.out.println("getPresentationFormat " +
			// si.getPresentationFormat());

		}
	}
}
