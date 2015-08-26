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

package nz.govt.natlib.adapter.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ParserContext;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A generic adapter that uses XSLT to transform an XML file of a given DTD into
 * another DTD It's not that flash but for version 1.0 it is adequate. <br>
 * Nice to haves:<list>
 * <li>validation of input and output dtds...
 * <li> XML output only enforcement
 * <li> proper use of the data handler for output (not a hacked subclass)
 * </list>
 * 
 * @author Nic Evans
 * @version 1.0
 */
public class XMLAdapter3 extends DataAdapter {

	private String inputDTD;

	public XMLAdapter3() {
	}

	public void setInputDTD(String inputDTD) {
		this.inputDTD = inputDTD;
	}

	public String getOutputType() {
		// what is produced?
		return this.inputDTD; // it just passes through - no 'adapting' other
								// than validation
	}

	public String getVersion() {
		return "0.3a";
	}

	public String getInputType() {
		return this.inputDTD;
	}

	public String getName() {
		return "XML Adapter";
	}

	public String getDescription() {
		return "Adapts all XML files, validates agains a nominated DTD";
	}

	public boolean isSystem() {
		return true;
	}

	public void adapt(File file, ParserContext ctx) throws IOException {
		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(file, new SAXHandler(ctx));
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException("Error Adapting XML file: " + file
					+ " error is: " + t.getMessage());
		}
	}

	/**
	 * checks to see if the input file is an XML file with the proper DTD
	 * 
	 */
	public boolean acceptsFile(File file) {
		// it must be an XML file
		String name = file.getName().toLowerCase();
		if (!name.endsWith(".xml")) {
			return false;
		}

		boolean dtdFound = false;
		boolean docTypeFound = false;
		
		try {
			// make sure the input xml file is in the format of the inputDTD.
			// open the file and parse for the following tag... "<!DOCTYPE"
			String find = "<!DOCTYPE";
			InputStream ds = new BufferedInputStream(new FileInputStream(file),
					1024);
			StringBuffer match = new StringBuffer();
			boolean candidate = false;
			
			while (ds.available() > 0) {
				char c = (char) ds.read();
				if (c == '<') {
					candidate = true;
				}
				if ((candidate == true) && (!docTypeFound)) {
					match.append(c);

					// do we have a partial match of the chars gathered so far?
					if (match.toString().toUpperCase().indexOf(
							find.substring(0, match.length())) >= 0) {
						if (find.length() == match.length()) {
							docTypeFound = true;
						}
					} else {
						match.delete(0, match.length()); // reset?
						candidate = false;
					}
				}
// 				 else if (docTypeFound) {
// 					match.append(c);
// 					if (c == '>') {
// 						// check that the dtd is in this line...
// 						dtdFound = (match.toString().indexOf(inputDTD) >= 0);
// 						// System.out.println("found dtd :"+match+".
// 						// "+inputDTD+"="+dtdFound);
// 						break;
// 					}
// 				}
			}
			ds.close();
		} catch (Exception ex) {
			// drop through and return false...
			return false;
		}

		return docTypeFound;
	}

	private class SAXHandler extends DefaultHandler {

		private ParserContext ctx;

		private SAXHandler(ParserContext ctx) {
			this.ctx = ctx;
		}

		public void startDocument() throws SAXException {
			// ParserContext starts itself...
		}

		public void endDocument() throws SAXException {
			// ParserContext ends itself...
		}

		public void startElement(String name, Attributes attrs)
				throws SAXException {
			HashMap params = null;
			if (attrs != null) {
				params = new HashMap();
				for (int i = 0; i < attrs.getLength(); i++) {
					params.put(attrs.getLocalName(i), attrs.getValue(i));
				}
			}
			ctx.fireStartParseEvent(name, false, params);
		}

		public void endElement(String name) throws SAXException {
			ctx.fireEndParseEvent(name);
		}

		public void characters(char buf[], int offset, int len)
				throws SAXException {
			String s = new String(buf, offset, len);
			ctx.fireParseEvent(s);
		}
	}

}