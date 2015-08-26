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

package nz.govt.natlib.adapter.word.OLE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import nz.govt.natlib.adapter.AdapterUtils;
import nz.govt.natlib.adapter.word.FIB;
import nz.govt.natlib.adapter.word.LanguageMap;
import nz.govt.natlib.adapter.word.WordUtils;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.FileDataSource;
import nz.govt.natlib.fx.IntegerElement;
import nz.govt.natlib.fx.ParserContext;

import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */

public class WordOLEAdapter {
	public static final long OLE_TYPE_SIGNATURE = 0xE11AB1A1E011CFD0L;

	// File position offset to the Document Properties
	private static final int Offset_To_DOP = 0x192;

	private static final LanguageMap langMap = OLELanguageMap.getLanguageMap();

	public boolean acceptsFile(File file, boolean ignoreFileExtension) {
		String name = file.getName().toLowerCase();
		if (ignoreFileExtension || (WordUtils.isDocFile(file) == true)) {
			DataSource ftk = null; 
			try {
				ftk = new FileDataSource(file);
				long signature = FXUtil.getNumericalValue(ftk,
						IntegerElement.LONG_SIZE, false);
				if (signature == OLE_TYPE_SIGNATURE) {
					return true;
				}

			} catch (java.io.IOException ex) {
				throw new RuntimeException("WordOLEAdpator:acceptsFile: "
						+ ex.getMessage());
			}
			finally {
				AdapterUtils.close(ftk);
			}
		}

		return false;

	}

	public void process(File file, ParserContext ctx) throws IOException {
		FileInputStream fin = null;
		try {
			POIFSReader r = new POIFSReader();
			r.registerListener(new MainStreamReader(ctx), "WordDocument");
			r.registerListener(new SummaryReader(ctx),	"\005SummaryInformation");
			r.registerListener(new DocumentSummaryReader(),	"\005DocumentSummaryInformation");
			r.registerListener(new TableStreamReader(ctx), "1Table");
			fin = new FileInputStream(file);
			r.read(fin);
		} 
		catch (Exception ex) {
			throw new RuntimeException(ex);
		} 
		finally {
			AdapterUtils.close(fin);
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
				si = (SummaryInformation) PropertySetFactory.create(event.getStream());
			} catch (Exception ex) {
				throw new RuntimeException("Property set stream \""	+ event.getPath() + event.getName() + "\": " + ex);
			}
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			dateFormatter.applyPattern("yyyy-MM-dd HH:mm:ss");
			into.fireStartParseEvent("summary");
			into.fireParseEvent("template", si.getTemplate());
			into.fireParseEvent("title", si.getTitle());
			into.fireParseEvent("subject", si.getSubject());
			into.fireParseEvent("keywords", si.getKeywords());
			into.fireParseEvent("comments", si.getComments());
			into.fireParseEvent("lastReviewedBy", si.getLastAuthor());
			into.fireParseEvent("author", si.getAuthor());
			into.fireEndParseEvent("summary");

			into.fireStartParseEvent("properties");
			into.fireParseEvent("created", dateFormatter.format(si.getCreateDateTime()));
			if (si.getLastSaveDateTime() != null) {
				into.fireParseEvent("revision", dateFormatter.format(si.getLastSaveDateTime()));
			} else {
				into.fireParseEvent("revision", "");
			}
			if (si.getLastPrinted() != null) {
				into.fireParseEvent("printed", dateFormatter.format(si.getLastPrinted()));
			} else {
				into.fireParseEvent("printed", "");
			}
			into.fireParseEvent("revisioncount", si.getRevNumber());
			// into.simpleRecord("timeediting", si.getEditTime().getTime());
			into.fireParseEvent("pages", si.getPageCount());
			into.fireParseEvent("words", si.getWordCount());
			into.fireParseEvent("characters", si.getCharCount());
			into.fireParseEvent("application", si.getApplicationName());
			into.fireEndParseEvent("properties");

			maskDone = true;
		}
	}

	boolean maskDone = false;

	class TableStreamReader implements POIFSReaderListener {

		ParserContext into;

		public TableStreamReader(ParserContext into) {
			this.into = into;
		}

		public void processPOIFSReaderEvent(POIFSReaderEvent event) {
			if (maskDone)
				return;

			DataSource ftk = null;
			try {
				long dopPos = into.getIntAttribute("Word.FIB.dopOffset");
				ftk = new DocumentDataSource(event.getStream());
				into.fireStartParseEvent("Properties");
				WordUtils.getDOPProperties(ftk, (int) dopPos, into);
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
				maskDone = true;
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
				WordUtils.getHeader(ftk, into);
				into.fireEndParseEvent("header");

				FIB oFib = new FIB(langMap, Offset_To_DOP);
				into.fireStartParseEvent("FIB");
				oFib.read(ftk, into);
				into.fireEndParseEvent("FIB");

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
				si = (DocumentSummaryInformation) PropertySetFactory.create(event.getStream());
			} catch (Exception ex) {
				throw new RuntimeException("Property set stream \""
						+ event.getPath() + event.getName() + "\": " + ex);
			}
		}
	}
}
