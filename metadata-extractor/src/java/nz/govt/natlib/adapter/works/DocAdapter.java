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

package nz.govt.natlib.adapter.works;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import nz.govt.natlib.adapter.AdapterUtils;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.adapter.word.LanguageMap;
import nz.govt.natlib.adapter.word.OLE.OLEConstants;
import nz.govt.natlib.fx.ParserContext;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Adapter for Microsoft Works files.
 * 
 * @author unascribed
 * @version 1.0
 */

public class DocAdapter extends DataAdapter {

	private static final int Offset_To_DOP = 0x192;

	private static final LanguageMap langMap = OLELanguageMap.getLanguageMap();

	public boolean acceptsFile(File file) {
		String name = file.getName().toLowerCase();
		if (ignoreFileExtension || name.endsWith(".wps") /* || name.endsWith(".wks") */
				|| name.endsWith(".wdb")) {
			return checkFileHeader(file, OLEConstants.OLE_HEADER);
		}

		return false;
	}

	public String getVersion() {
		return "1.0";
	}

	public String getOutputType() {
		return "msworks.dtd";
	}

	public String getInputType() {
		return "application/vnd.ms-works";
	}

	public String getName() {
		return "Microsoft Works Adapter";
	}

	public String getDescription() {
		return "Adapts all Microsoft Works files from version 2.0 to 4.0 (doc, db, ssheet)";
	}

	public void adapt(File file, ParserContext ctx) throws IOException {
		ctx.fireStartParseEvent("MSWorks");
		writeFileInfo(file, ctx);
		ctx.fireParseEvent("Version", "Works");
		POIFSFileSystem fs = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file);
			fs = new POIFSFileSystem(fin);
			DirectoryEntry root = fs.getRoot();
			readDirectory(fs, root, ctx);
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			AdapterUtils.close(fin);
			fs = null;
		}
		ctx.fireEndParseEvent("MSWorks");
	}

	public void readDirectory(POIFSFileSystem fs, DirectoryEntry dir,
			ParserContext ctx) throws Exception {
		if (dir.getEntryCount() == 0) {
			return;
		}

		for (Iterator iter = dir.getEntries(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			// System.out.println("found entry: " + entry.getName());
			if (entry instanceof DirectoryEntry) {
				// .. recurse into this directory
				// System.out.println(" > Directory...");
				readDirectory(fs, (DirectoryEntry) entry, ctx);
			} else if (entry instanceof DocumentEntry) {
				// entry is a document, which you can read
				// System.out.println(" > Document");
				DocumentEntry doc = (DocumentEntry) entry;
				ArrayList list = readDocument(fs, doc);

				if (entry.getName().endsWith("CompObj")) {
					writeCompObjProps(list, ctx);
				}
			} else {
				// currently, either an Entry is a DirectoryEntry or a
				// DocumentEntry,
				// but in the future, there may be other entry subinterfaces.
				// The
				// internal data structure certainly allows for a lot more entry
				// types.
				// System.out.println(" > Other");
			}
		}
	}

	public void writeCompObjProps(ArrayList list, ParserContext ctx)
			throws Exception {
		Iterator it = list.iterator();
		int count = 0;
		while (it.hasNext()) {
			String value = "" + it.next();
			if (count == 0) {
				ctx.fireParseEvent("package", value);
			}
			if (count == 1) {
				ctx.fireParseEvent("application", value);
			}
			count++;
		}
	}

	public ArrayList readDocument(POIFSFileSystem fs, DocumentEntry doc)
			throws Exception {
		// load file system
		DocumentInputStream stream = new DocumentInputStream(doc);
		ArrayList words = new ArrayList();

		if (stream.available() > 256) {
			// System.out.println("Too big ");
			return words;
		}

		// process data from stream
		byte[] content = new byte[stream.available()];
		stream.read(content);
		stream.close();

		// System.out.println("Read :"+content.length);

		// Fancy Harvester for finding strings 16/8 bit length or terminated...
		String candidate = "";
		int runLong = 0;
		for (int i = 0; i < content.length; i++) {
			char c = (char) content[i];
			if (c > 32 && c < 127) {
				candidate += c;
				runLong = 0;
			} else {
				runLong++;
			}

			// terminate...
			if (runLong >= 2) {
				// weed out anything less than 2 char
				if (candidate.trim().length() > 2) {
					words.add(candidate);
				}
				runLong = 0;
				candidate = "";
			}
		}

		// for (int i=0;i<content.length;i++) {
		// int c = content[i];
		// if (c<0) {
		// c = 0x100 + c;
		// }
		// System.out.println(i+",
		// "+Integer.toString(c)+"\t"+Integer.toHexString(c)+"\t"+(char)c);
		// }

		return words;
	}

}
