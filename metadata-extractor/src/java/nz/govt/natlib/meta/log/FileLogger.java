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

package nz.govt.natlib.meta.log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nz.govt.natlib.fx.XMLParserListener;
import nz.govt.natlib.meta.config.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * FileLoggers log in XML.
 * 
 * @author unascribed
 * @version 1.0
 */

public class FileLogger implements Log {

	private static final String MSG_TAG = "LogMessage";

	private static final String LOG_TAG = "LOG";

	private static final String ID_TAG = "ID";

	private static final String DATE_TAG = "Date";

	private static final String SOURCE_TAG = "Source";

	private static final String LEVEL_TAG = "Level";

	private static final String MESSAGE_TAG = "Message";

	private static final String COMMENT_TAG = "Comment";

	private OutputStream out;

	private XMLFormatter formatter;

	private static DateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MMM-d H:mm:ss.SS");

	public FileLogger(String file) throws IOException {
		out = new BufferedOutputStream(new FileOutputStream(file));
		formatter = new XMLFormatter(out, Config.getInstance().getXMLBaseURL()
				+ "/logfile.dtd");
		writeHeader();
	}

	public FileLogger(File file) throws IOException {
		this(file.getPath());
	}

	private void writeHeader() throws IOException {
		formatter.initDoc();
		formatter.writeTagOpen("LOG");
	}

	private void writeFooter() throws IOException {
		formatter.writeTagClose(LOG_TAG);
	}

	public void logMessage(LogMessage message) throws IOException {
		// log it...
		if (out != null) {
			formatter.writeTagOpen(MSG_TAG);
			formatter.writeTag(ID_TAG, message.getId() + "");
			formatter.writeTag(LEVEL_TAG, message.getLevel().getLevel() + "");
			formatter.writeTag(DATE_TAG, dateFormatter
					.format(message.getDate())
					+ "");
			Object st = message.getSource();
			formatter.writeTag(SOURCE_TAG, (st == null ? "" : st + ""));
			st = message.getMessage();
			formatter.writeTag(MESSAGE_TAG, (st == null ? "" : st + ""));
			st = message.getComment();
			formatter.writeTag(COMMENT_TAG, (st == null ? "" : st + ""));
			formatter.writeTagClose(MSG_TAG);
		} else {
			throw new RuntimeException("Log is closed");
		}
	}

	public void suspendEvents(boolean suspend) {
		// no can do...
	}

	public void close() throws IOException {
		writeFooter();
		out.flush();
		out.close();
		out = null;
	}

	public static LogMessage[] readMessages(File file) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		ArrayList messages = new ArrayList();
		SAXHandler handler = new FileLogger.SAXHandler(messages);
		parser.parse(file, handler);

		LogMessage[] result = new LogMessage[messages.size()];
		messages.toArray(result);
		return result;
	}

	private static class SAXHandler extends DefaultHandler {

		private ArrayList into;

		// current info
		private long id;

		private LogLevel level;

		private Date date;

		private Object source;

		private String message;

		private String comment;

		// currenttag
		private String tagName = "";

		public SAXHandler(ArrayList into) {
			this.into = into;
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			tagName = qName;
		}

		public void characters(char ch[], int start, int length)
				throws SAXException {
			String read = new String(ch, start, length);

			if (ID_TAG.equalsIgnoreCase(tagName)) {
				id = Long.parseLong(read.trim());
			}
			if (DATE_TAG.equalsIgnoreCase(tagName)) {
				try {
					date = dateFormatter.parse(read.trim());
				} catch (ParseException ex) {
					date = null;
				}
			}
			if (LEVEL_TAG.equalsIgnoreCase(tagName)) {
				int l = Integer.parseInt(read.trim());
				level = new LogLevel("n/a", l);
			}
			if (SOURCE_TAG.equalsIgnoreCase(tagName)) {
				source = read.trim();
			}
			if (MESSAGE_TAG.equalsIgnoreCase(tagName)) {
				message = read.trim();
			}
			if (COMMENT_TAG.equalsIgnoreCase(tagName)) {
				comment = read.trim();
			}

		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// use all the info gathered to reconstruct a logmessage
			if (MSG_TAG.equalsIgnoreCase(qName)) {
				LogMessage msg = new LogMessage(id, level, date, source,
						message, comment);
				into.add(msg);
			}

		}

	}

	private class XMLFormatter extends XMLParserListener {

		private String dtd;

		public XMLFormatter(OutputStream out, String dtd) {
			super(out);
			this.dtd = dtd;
		}

		// public String getIndent(int indent) {
		// return "";
		// }
		//
		// public String getEOL() {
		// return "";
		// }

		public String getDTD() {
			return this.dtd;
		}
	}

}