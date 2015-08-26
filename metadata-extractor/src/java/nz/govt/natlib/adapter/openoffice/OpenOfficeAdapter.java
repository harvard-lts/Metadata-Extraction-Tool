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

package nz.govt.natlib.adapter.openoffice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ParserContext;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Adapter to parse Open Office Documents.
 * 
 * @author Nic Evans
 * @version 1.0
 */

public class OpenOfficeAdapter extends DataAdapter {

	public static final String OPEN_OFFICE_HEADER = "50 4B 03 04 14 00";

	public boolean acceptsFile(File file) {
		// Check the file header.
		return checkFileHeader(file, OPEN_OFFICE_HEADER);
	}

	public String getVersion() {
		return "1.0";
	}

	public String getOutputType() {
		return "openoffice.dtd";
	}

	public String getInputType() {
		return "application/open-office-1.x";
	}

	public String getName() {
		return "Open Office Adapter";
	}

	public String getDescription() {
		return "Adapts all Open Office file formats. v1.0 to v1.1";
	}

	public void adapt(File oFile, ParserContext ctx) throws IOException {
		// Header and default information
		ctx.fireStartParseEvent("OpenOffice");
		writeFileInfo(oFile, ctx);
		try {
			// the file is zipped! - open a zip stream.
			InputStream in = new FileInputStream(oFile);
			ZipInputStream zin = new ZipInputStream(in);
			boolean eof = false;
			while (!eof) {
				ZipEntry zentry = zin.getNextEntry();
				if (zentry != null) {
					processEntry(zentry, new SkipStream(zin), ctx);
				} else {
					eof = true;
				}
			}
			zin.close();
			in.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			ctx.fireEndParseEvent("OpenOffice");
		}
	}

	private void processEntry(ZipEntry zentry, InputStream in, ParserContext ctx)
			throws Exception {
		// we are interested in:
		// a) content.xml which has content 'type' in it.
		// b) meta.xml which has metadata, properties etc...
		String name = zentry.getName().toLowerCase().trim();
		// System.out.println(name);
		if (name.equals("content.xml")) {
			processContent(zentry, in, ctx);
		}
		if (name.equals("meta.xml")) {
			processMetadata(zentry, in, ctx);
		}
	}

	private void processContent(ZipEntry zentry, InputStream in,
			ParserContext ctx) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		SAXParser parser = factory.newSAXParser();
		parser.parse(in, new ContentHandler(ctx));
	}

	private void processMetadata(ZipEntry zentry, InputStream in,
			ParserContext ctx) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		SAXParser parser = factory.newSAXParser();
		parser.parse(in, new MetadataHandler(ctx));
	}

	private class ContentHandler extends SkipHandler {

		private ParserContext ctx;

		public ContentHandler(ParserContext ctx) {
			this.ctx = ctx;
		}

		public void startElement(String nameSpace, String tagName,
				String fullTag, Attributes attr) {
			if (fullTag.toLowerCase().endsWith("document-content")) {
				for (int i = 0; i < attr.getLength(); i++) {
					ctx.fireParseEvent(attr.getLocalName(i), attr.getValue(i));
				}
			}
		}

		public void startDocument() throws SAXException {
			ctx.fireStartParseEvent("content");
		}

		public void endDocument() throws SAXException {
			ctx.fireEndParseEvent("content");
		}

	}

	private class MetadataHandler extends SkipHandler {

		private Stack stack = new Stack();

		private String currentTag = "";

		private ParserContext ctx;

		private String currentTagString = null;

		private Attributes currentTagAttributes = null;

		private HashMap dateMap = new HashMap();

		private String originalDateFormat = "yyyy-mm-dd'T'HH:MM:ss";

		private String finalDateFormat = "yyyy-mm-dd HH:MM:ss";

		private String originalDurationFormat = "'PT'M'M'ss'S'";

		private String finalDurationFormat = "HH:MM:ss";

		private SimpleDateFormat dateReader = new SimpleDateFormat(
				originalDateFormat);

		private SimpleDateFormat dateFormatter = new SimpleDateFormat(
				finalDateFormat);

		private SimpleDateFormat durationReader = new SimpleDateFormat(
				originalDurationFormat);

		private SimpleDateFormat durationFormatter = new SimpleDateFormat(
				finalDurationFormat);

		public MetadataHandler(ParserContext ctx) {
			this.ctx = ctx;
			dateMap.put("format", finalDateFormat);
		}

		public void characters(char[] chars, int arg1, int arg2) {
			// System.out.println("chars "+arg1+", "+arg2);
			currentTagString = new String(chars, arg1, arg2);
		}

		public void startElement(String nameSpace, String tagName,
				String fullTag, Attributes attr) {
			// gather state
			currentTag = fullTag;
			currentTagString = null; // this'll be the value if there's no
										// chars.
			currentTagAttributes = attr; // this'll be the value if there's
											// no chars.
			stack.push(currentTag);

			// open the special outer tags first
			if (fullTag.toLowerCase().endsWith("keywords")) {
				ctx.fireStartParseEvent("keywords");
			}
		}

		public void endElement(String nameSpace, String tagName, String fullTag) {
			// close the special outer tags if any
			if (fullTag.toLowerCase().endsWith("keywords")) {
				ctx.fireEndParseEvent("keywords");
			}

			if (currentTagString != null) {
				// there are some that have attributes which will mean something
				// different (user defined for example)
				if (fullTag.toLowerCase().endsWith("user-defined")) {
					ctx.fireStartParseEvent(tagName);
					ctx
							.fireParseEvent("name", currentTagAttributes
									.getValue(0));
					ctx.fireParseEvent("value", currentTagString);
					ctx.fireEndParseEvent(tagName);
				} else {
					// even some of these are coded funny - so we'll just sort
					// them out!
					if (fullTag.toLowerCase().endsWith("duration")) {
						try {
							Date date = durationReader.parse(currentTagString);
							ctx.fireParseEvent(tagName, durationFormatter
									.format(date));
						} catch (ParseException formatEx) {
							formatEx.printStackTrace();
							ctx.fireParseEvent(tagName, currentTagString);
						}
					} else if (fullTag.toLowerCase().endsWith("date")) {
						// all dates...
						try {
							Date date = dateReader.parse(currentTagString);
							ctx.fireParseEvent(tagName, dateFormatter
									.format(date), false, dateMap);
						} catch (ParseException formatEx) {
							ctx.fireParseEvent(tagName, currentTagString);
						}
					} else {
						ctx.fireParseEvent(tagName, currentTagString);
					}
				}
			} else if (currentTagAttributes != null) {
				if (fullTag.toLowerCase().endsWith("document-statistic")) {
					// just roll them all out...
					for (int i = 0; i < currentTagAttributes.getLength(); i++) {
						ctx.fireParseEvent(
								currentTagAttributes.getLocalName(i),
								currentTagAttributes.getValue(i));
					}
				}
			}

			// reset, pop etc...
			currentTag = (String) stack.pop();
			currentTagString = null;
			currentTagAttributes = null;
		}

		public void startDocument() throws SAXException {
			ctx.fireStartParseEvent("properties");
		}

		public void endDocument() throws SAXException {
			ctx.fireEndParseEvent("properties");
		}

	}

	private class SkipHandler extends DefaultHandler {
		public InputSource resolveEntity(String arg1, String arg2)
				throws SAXException {
			// resolve the output.dtd entity ourselves
			if (arg2.endsWith("office.dtd")) {
				return new InputSource(new ByteArrayInputStream("".getBytes()));
			} else {
				// resolve normally
				try {
					return super.resolveEntity(arg1, arg2);
				}
				catch(SAXException ex) {
					// Catch and rethrow normal exceptions.
					throw ex;
				}
				catch(Exception ex) {
					// For Java 5, catch the IOException and rethrow as 
					// as SAXExceptin.
					throw new SAXException(ex.getMessage(), ex);
				}
			}
		}
	}

	/*
	 * special stream needed for SAX processing collections of XML documents
	 * zipped together. Otherwise the SAX parser WILL close the stream.
	 * 
	 * this class fixes two bugs in SAX
	 * 
	 * a) BUG: Closes input stream after parsing!
	 */
	private class SkipStream extends InputStream {

		private InputStream stream;

		public SkipStream(InputStream stream) {
			this.stream = stream;
		}

		public int read() throws IOException {
			return stream.read();
		}

		public void close() throws IOException {
			// doesn't actually close it!
		}

	}
}