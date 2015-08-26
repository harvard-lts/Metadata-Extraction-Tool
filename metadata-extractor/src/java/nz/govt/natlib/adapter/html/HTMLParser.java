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

/*
 * Created on 21/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nz.govt.natlib.adapter.html;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author nevans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HTMLParser {

	public HTMLParser() {

	}

	public void parse(InputStream in, HTMLHandler handler) throws IOException {
		handler.startDoc();
		boolean tag = false;
		boolean comment = false;
		StringBuffer buildTag = new StringBuffer();
		StringBuffer buildChar = new StringBuffer();
		byte[] buf = new byte[1024];
		char[] lastChars = new char[5];
		while (true) {
			// read and add another block
			int r = in.read(buf);
			if (r == -1) {
				break;
			}

			// process the buffer
			for (int i = 0; i < r; i++) {
				// read a char - roll the last char list...
				char c = (char) buf[i];
				for (int roll = 0; roll < lastChars.length - 1; roll++) {
					lastChars[roll] = lastChars[roll + 1];
				}
				lastChars[lastChars.length - 1] = c;

				// work out if we are in a comment situation...
				if ((lastChars[lastChars.length - 4] == '<')
						&& (lastChars[lastChars.length - 3] == '!')
						&& (lastChars[lastChars.length - 2] == '-')
						&& (lastChars[lastChars.length - 1] == '-')) {
					// drop the chars - we are in a comment
					buildTag.delete(0, buildTag.length());
					buildChar.delete(0, buildChar.length());
					buildChar.append("<!-"); // cause the last '-' is the
												// current char
					comment = true;
					tag = false;
				}

				// start a new tag
				if ((c == '<') && (!comment)) {
					tag = true;
				}

				// use the chars
				if (tag) {
					buildTag.append(c);
				} else {
					buildChar.append(c);
				}

				// end comment
				if ((lastChars[lastChars.length - 3] == '-')
						&& (lastChars[lastChars.length - 2] == '-')
						&& (lastChars[lastChars.length - 1] == '>')) {
					comment(handler, buildChar.toString().toCharArray(), 0,
							buildChar.length());
					buildTag.delete(0, buildTag.length());
					buildChar.delete(0, buildChar.length());
					comment = false;
					tag = false;
				}

				// end tag
				if ((c == '>') && (!comment)) {
					// dump the chars built so far
					if (buildChar.length() > 0) {
						text(handler, buildChar.toString().toCharArray(), 0,
								buildChar.length());
					}

					// is it a start or end tag?
					if (buildTag.length() > 0) {
						processTag(handler, buildTag.toString());
					}

					// reset the tag builder
					tag = false;
					buildTag.delete(0, buildTag.length());
					buildChar.delete(0, buildChar.length());
				}
			}
		}
		handler.endDoc();
	}

	private void processTag(HTMLHandler handler, String tag) {
		if ((tag == null) || (tag.trim().length() == 0)) {
			return;
		}

		// parse the tag itself...
		boolean startWithClose = false;
		boolean endWithClose = false;
		String tagName = "";
		String attributes = "";
		// state machine
		boolean name = true; // are we processing the name?
		boolean attr = false; // are we processing attributes?
		tag = tag.substring(1, tag.length() - 1).trim();

		for (int i = 0; i < tag.length(); i++) {
			char c = tag.charAt(i);

			// the slash char has special meaning - sometimes...
			if (c == '/') {
				if (name) {
					startWithClose = true;
					continue;
				} else if (i == tag.length() - 1) {
					// if it's the last char then it's an end with close
					endWithClose = true;
					continue;
				}
			}

			// if we are in name mode then use the chars if they keep rolling,
			// when the stop we go into attribute mode
			if (name) {
				if (Character.isLetterOrDigit(c) || c == '!') { // the doc type
																// is slightly
																// different
																// (with a !)
					tagName += c;
				} else {
					name = false;
					attr = true; // move on to the attributes if there are
									// any...
					continue;
				}
			}

			if (attr) {
				attributes += c;
			}

		}

		// now send the results through...
		if (tag.toLowerCase().startsWith("!doctype")) {
			processDocType(handler, attributes.trim());
		} else if (startWithClose) {
			endTag(handler, tagName);
		} else {
			HashMap attrMap = processAttributes(attributes.trim());
			startTag(handler, tagName, attrMap);

			if (endWithClose) {
				endTag(handler, tagName);
			}
			// some tags are 'end tags anyway - even if they don't explicitly
			// say
			else if (tagName.toLowerCase().equals("img")
					|| tagName.toLowerCase().equals("meta")
					|| tagName.toLowerCase().equals("link")) {
				endTag(handler, tagName);
			}
			// yet others end implicitly when others start! - leave this little
			// special to the parser event listener...
		}
	}

	// this is a good way to process lists of name="value" strings robustly
	public static HashMap processAttributes(String attributes) {
		HashMap map = new HashMap();
		if ((attributes == null) || (attributes.trim().length() == 0)) {
			return map;
		}
		// it's more robust to do it backwards (i.e. if quotes etc are
		// missed...)
		String name = "";
		String value = "";
		// state machine
		boolean parsingName = false;
		boolean parsingValue = true;
		boolean quote = false;

		for (int i = attributes.length(); i > 0; i--) {
			char c = attributes.charAt(i - 1);

			if (c == '\"') {
				quote = !quote;
			}

			if ((c == '=') && (!quote)) {
				parsingName = true;
				parsingValue = false;
				continue;
			}

			if (parsingName && c == ' ') {
				map.put(name.toLowerCase().trim(), value);
				name = "";
				value = "";
				parsingValue = true;
				parsingName = false;
				continue;
			}

			if (parsingValue) {
				value = c + value;
			}
			if (parsingName) {
				name = c + name;
			}
		}
		// residual value... i.e. the first one!
		map.put(name.toLowerCase().trim(), value);

		return map;
	}

	public static String stripQuotes(String st) {

		// trim good's better now - just use that
		return trimGood(st);
	}

	public static String trimGood(String st) {
		if (st == null)
			return null;
		boolean body = false;
		int lastGood = 0;
		int firstGood = 0;
		for (int i = 0; i < st.length(); i++) {
			char c = st.charAt(i);
			if (Character.isLetterOrDigit(c) && body) {
				lastGood = i;
			}
			if (Character.isLetterOrDigit(c) && !body) {
				firstGood = i;
				body = true;
			}
		}
		return body == false ? "" : st.substring(firstGood, lastGood + 1);
	}

	private void processDocType(HTMLHandler handler, String value) {
		if (value == null)
			return;

		boolean inQuote = false;
		DocType docType = new DocType();
		int tok = 0;
		String token = "";
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);

			if (c == 10 || c == 13) {
				continue;
			}

			if (c == '"') {
				inQuote = !inQuote;
				continue;
			}

			if ((c == ' ') && (!inQuote)) {
				if (token.trim().length() > 0) {
					analyseDocType(tok, token, docType);
				}
				token = "";
				tok++;
				continue;
			}

			token += c;

		}
		if (token.trim().length() > 0) {
			analyseDocType(tok, token, docType);
		}

		// output the analysed docType...
		handler.docType(docType.mainType, docType.subType, docType.version,
				docType.strict, docType.DTD);
	}

	private void analyseDocType(int tokID, String token, DocType docType) {
		if (tokID == 0) {
			docType.mainType = token;
		}
		if (tokID == 1) {
			docType.pub = token;
		}

		if (token.toLowerCase().startsWith("-//w3c")) {
			StringTokenizer tok = new StringTokenizer(token, " ");
			int tokCount = 0;
			while (tok.hasMoreTokens()) {
				String subtok = tok.nextToken();

				if (tokCount == 0) {
					docType.W3C = subtok;
				}
				if (tokCount == 1) {
					// may have a // in which case it shows the strict type...
					if (subtok.indexOf("//") >= 0) {
						docType.subType = subtok.substring(0, subtok
								.indexOf("//"));
						docType.strict = "Strict"
								+ subtok.substring(subtok.indexOf("//"));
					} else {
						docType.subType = subtok;
					}
				}
				if (tokCount == 2) {
					// may have a // in which case it shows the strict type...
					if (subtok.indexOf("//") >= 0) {
						docType.version = subtok.substring(0, subtok
								.indexOf("//"));
						docType.strict = "Strict"
								+ subtok.substring(subtok.indexOf("//"));
					} else {
						docType.version = subtok;
					}
				}
				if (tokCount == 3) {
					docType.strict = subtok;
				}

				tokCount++;
			}
		}

		if ((token.toLowerCase().indexOf(".dtd") >= 0)
				|| (token.toLowerCase().indexOf(".xsd") >= 0)) {
			docType.DTD = token;
		}

	}

	public void startTag(HTMLHandler handler, String tag, HashMap attributes) {
		// System.out.println("Start Tag :"+tag);
		handler.startTag(tag, attributes);
	}

	public void endTag(HTMLHandler handler, String tag) {
		// System.out.println("End Tag :"+tag);
		handler.endTag(tag);
	}

	public void text(HTMLHandler handler, char[] chars, int start, int end) {
		// System.out.println("Chars :"+new String(chars,start,end));
		handler.text(chars, start, end);
	}

	public void comment(HTMLHandler handler, char[] chars, int start, int end) {
		// System.out.println("Comment :"+new String(chars,start,end));
		handler.comment(chars, start, end);
	}

	private class DocType {
		String mainType = "html";

		String subType = "html";

		String DTD;

		String version;

		String strict = "strict";

		// others?
		String W3C;

		String pub = "public";
	}
}
