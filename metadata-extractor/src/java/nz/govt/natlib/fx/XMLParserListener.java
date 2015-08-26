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

package nz.govt.natlib.fx;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author unascribed
 * @version 1.0
 */

public class XMLParserListener implements ParserListener {

	private boolean rootElement = false;

	private boolean docStarted = false;

	private OutputStream out;

	// replacements...
	private static final String[][] replaceText = new String[][] {
			{ "<", "&lt;" }, { ">", "&gt;" }, { "&", "&amp;" }, };

	private int indent = 0;

	private String newLine = getEOL();

	public XMLParserListener(OutputStream out) {
		this.out = out;
	}

	/**
	 * Replaces the non-printable or XML reserved characters with readable characters.
	 * This will replace any non-printable chars including newline and carriage return.
	 *  
	 * @param in input string
	 * @return The replaced string
	 */
	protected String replace(String in) {
		return replace(in, false);
	}
	
	/**
	 * Replaces the non-printable or XML reserved characters with readable characters. 
	 * However, based on the flag flagKeepNewLineUntouched, the newline characters 
	 * will be or will not be replaced.
	 *  
	 * @param in input string
	 * @param flagKeepNewLineUntouched - Pass true if you want to keep the 
	 * new-line / carriage-return unchanged. Or, false if you want to replace them.
	 * @return The replaced string
	 */
	protected String replace(String in, boolean flagKeepNewLineUntouched) {
		StringBuffer buf = new StringBuffer();
		for (int k = 0; k < in.length(); k++) {
			String c = in.substring(k, k + 1);

			if ((c.charAt(0) < 32) || (c.charAt(0) > 126)) {
				/*
				 * Examine the flagKeepNewLineUntouched flag to see if 
				 * newline / carriage-return chars need to be replaced.
				 * Replace only if the the flag is false or the chars are
				 * not '\n' and '\r'
				 */
				if (! flagKeepNewLineUntouched || (c.charAt(0) != '\r' && c.charAt(0) != '\n'))
					c = "";
			} else {
				for (int i = 0; i < replaceText.length; i++) {
					if (replaceText[i][0].charAt(0) == c.charAt(0)) {
						c = replaceText[i][1];
						break;
					}
				}
			}
			buf.append(c);
		}
		return buf.toString();
	}

	protected void write(String st) throws IOException {
		if (st != null)
			out.write(st.getBytes());
	}

	public void writeTag(String tag, Object value) throws IOException {
		writeTagOpen(tag);
		if (value != null) {
			writeTagContents(value);
		}
		writeTagClose(tag);
	}

	public void writeTagContents(Object value) throws IOException {
		// replace any dodgy tag text
		write(getIndent(indent) + replace(value.toString(), true) + newLine);
	}

	protected void writeXMLOpen() throws IOException {
		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + newLine);
	}

	protected void writeStartDocument() throws IOException {
		write("<!-- National Library New Zealand -->" + newLine);
	}

	public void initDoc() throws IOException {
		writeXMLOpen();
		writeStartDocument();
	}

	public void writeTagClose(String tag) throws IOException {
		indent--;
		write(getIndent(indent) + "</" + replace(tag) + ">" + newLine);
	}

	protected void writeExtraTags() throws IOException {
	}

	public String getDTD() {
		return null;
	}

	protected String getIndent(int indent) {
		String r = "";
		for (int i = 0; i < indent; i++, r += "  ")
			;
		return r;
	}

	protected String getEOL() {
		return "\r\n";
	}

	public void writeTagOpen(String tag) throws IOException {
		this.writeTagOpen(tag, null, null);
	}

	public void writeTagOpen(String tag, String[] attrNames, String[] attrValues)
			throws IOException {
		if (!rootElement) {
			String dtd = getDTD();
			if (dtd != null) {
				write("<!DOCTYPE " + replace(tag.toUpperCase()) + " SYSTEM \""
						+ dtd + "\">" + newLine);
			}
		}

		write(getIndent(indent) + "<" + replace(tag));
		if ((attrNames != null) && (attrValues != null)) {
			for (int i = 0; i < attrNames.length; i++) {
				write(" " + attrNames[i] + "=\"" + attrValues[i] + "\"");
			}
		}
		write(">" + newLine);
		indent++;

		// add any other default elements that immediately follow the FIRST
		// tag...
		if (!rootElement) {
			rootElement = true; // avoid the locked loop...
			writeExtraTags();
		}
	}

	public void handleParseEvent(ParserEvent event) {
		try {
			if (!docStarted) {
				docStarted = true;
				initDoc();
			}

			Object value = event.getValue();
			int id = event.getID();
			boolean internal = event.isInternal();

			if (!internal) {
				if (id == ParserEvent.OPEN_EVENT) {
					// check to see if the parameter has attributes.
					String[] values = null;
					String[] names = event.getParameterNames();
					if (names != null) {
						values = new String[names.length];
						for (int i = 0; i < names.length; i++) {
							values[i] = event.getParameter(names[i]) + "";
						}
					}
					writeTagOpen(value.toString().toUpperCase(), names, values);
				}
				if (id == ParserEvent.VALUE_EVENT) {
					writeTagContents(value + "");
				}
				if (id == ParserEvent.CLOSE_EVENT) {
					writeTagClose(value.toString().toUpperCase());
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
