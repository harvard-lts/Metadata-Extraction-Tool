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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * An Adapter to extract the information from the XML and DOCTYPE declarations
 * in an XML file.
 * 
 * @author Nic Evans
 * @version 1.0
 */
public class XMLAdapter extends DataAdapter {
	
	
	private static final Pattern XML_DECL2 = Pattern.compile("^<\\?xml version=(\"|')(\\d\\.\\d)\\1( encoding=(\"|')([A-Za-z].[A-Za-z0-9-_]*)\\4)?( standalone=('|\")(yes|no)\\7)?");
	private static final String SYSTEM_LITERAL = "(?:\"[^\"]*\")|(?:'[^']*')";
	private static final String PUB_LITERAL = "\"[\\x20\\x0D\\x0Aa-zA-Z0-9\\-'\\(\\)\\+,\\.\\/:=\\?;\\!\\*#@$_%]*\"|'[\\x20\\x0D\\x0Aa-zA-Z0-9\\-\\(\\)\\+,\\.\\/:=\\?;\\!\\*#@$_%]*'";
	private static final Pattern DOCTYPE = Pattern.compile("<!DOCTYPE ([A-Za-z_:].[A-Za-z0-9_\\-\\.:]*)( (?:SYSTEM ("+SYSTEM_LITERAL+")|PUBLIC ("+PUB_LITERAL+") ("+SYSTEM_LITERAL+")))?");

	
	/** Maximum number of lines to read from the file */
	public static final int MAX_LINES_TO_READ = 10;

	
	/**
	 * Check if the file is an XML file by checking for the XML declaration at
	 * the beginning of the file.
	 * 
	 * @param file The file to check.
	 * @return true if the file has the XML header. 
	 */
	public boolean acceptsFile(File file) {
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			
			// Read up to our maximum number of lines into the buffer.
			StringBuffer contents = new StringBuffer();
			String line = null;
			for(int i=0; (line = reader.readLine()) != null && i<MAX_LINES_TO_READ; i++) {
				contents.append(line);
			}
			
			// Replace whitespace so our regular expressions are simpler.
			String testString = contents.toString().replaceAll("[\\x20\\x09\\x0D\\x0A]+", " ");

			// We can handle this file as long as the XML Declaration is there.
			return XML_DECL2.matcher(testString).find();
		}
		catch(IOException ex) {
			LogManager.getInstance().logMessage(ex);
			return false;
		}		
		finally {
			try {
				reader.close();
			}
			catch(IOException ex) {
				// Nothing we can do if we fail to close the 
				// stream, so log the messasge and continue.
				LogManager.getInstance().logMessage(
						LogMessage.INFO,
						"Failed to close reader in XMLAdapter::acceptsFile");
			}
		}
	}

	
	/**
	 * Extract the XML encoding type, version, adn DOCTYPE from
	 * the XML file.
	 * @param file The file to extract the metadata from.
	 * @param out The ParserContext to fire events to.
	 */
	public void adapt(File file, ParserContext out) throws IOException {
		BufferedReader reader = null;
		
		try {
			// Open the BMP section of the metadata file. Part of the
			// bmp.dtd defintion is that the native format starts with an
			// opening <BMP> tag.
			out.fireStartParseEvent("XML");

			// Extract the basic file metadata. This includes things like
			// last modified date and so forth.
			writeFileInfo(file, out);			
			
			out.fireStartParseEvent("INFORMATION");
			
			reader = new BufferedReader(new FileReader(file));
			
			// Read the first MAX_LINES_TO_READ lines into a buffer.
			StringBuffer contents = new StringBuffer();
			String line = null;
			for(int i=0; (line = reader.readLine()) != null && i<MAX_LINES_TO_READ; i++) {
				contents.append(line);
			}
			
			// Replace all whitespace with a single space.
			String testString = contents.toString().replaceAll("[\\x20\\x09\\x0D\\x0A]+", " ");
			
			// Find the XML declaration.
			Matcher m = XML_DECL2.matcher(testString);
			if(m.find()) {
				out.fireParseEvent("VERSION", m.group(2));
				out.fireParseEvent("ENCODING", (m.group(5) == null ? "unspecified" : m.group(5)));
				out.fireParseEvent("STANDALONE", (m.group(8) == null ? "unspecified" : m.group(8)));
			}
			
			// Find the DOCTYPE declaration.
			m = DOCTYPE.matcher(testString);
			if(m.find()) {
				out.fireParseEvent("MAINTAG", m.group(1));
				
				if(m.group(2) != null) {
					String systemIdentifier = m.group(3) != null ? m.group(3) : m.group(5);
					String publicIdentifier = m.group(3) != null ? null : m.group(4);
					
					out.fireParseEvent("SYSTEM", stripQuotes(systemIdentifier));
					
					if(publicIdentifier != null) {
						out.fireParseEvent("PUBLIC", stripQuotes(publicIdentifier));
					}
				}
			}
			
			out.fireEndParseEvent("INFORMATION");
			out.fireEndParseEvent("XML");
		}
			
		catch(IOException ex) {
			LogManager.getInstance().logMessage(ex);
		}		
		finally {
			try {
				reader.close();
			}
			catch(IOException ex) {
				// Nothing we can do if we fail to close the 
				// stream, so log the messasge and continue.
				LogManager.getInstance().logMessage(
						LogMessage.INFO,
						"Failed to close reader in XMLAdapter::acceptsFile");
			}
		}
		
	}
	
	/**
	 * Remove the quotes from the string. This is used because the regular
	 * expression does not strip the quotes itself.
	 * @param inStr The string to strip the quotes off.
	 * @return The same string, without the leading/trailing quotes.
	 */
	private String stripQuotes(String inStr) {
    	
    	if(inStr.startsWith("\"") || inStr.startsWith("'")) {
        	inStr = inStr.substring(1);
    	}
    	if(inStr.endsWith("\"") || inStr.endsWith("'")) {
        	inStr = inStr.substring(0, inStr.length() - 1);
    	}
    	return inStr;
	}

	public String getDescription() {
		return "Collects the information from the XML and DOCTYPE declarations";
	}

	public String getInputType() {
		return "application/xml";
	}

	public String getName() {
		return "XML Adapter";
	}

	public String getOutputType() {
		return "xml.dtd";
	}

	public String getVersion() {
		return "2.0";
	}

}