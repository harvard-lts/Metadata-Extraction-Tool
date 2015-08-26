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
package nz.govt.natlib.samples;

import java.io.ByteArrayOutputStream;

import nz.govt.natlib.fx.ParserEvent;
import nz.govt.natlib.fx.ParserListener;
import nz.govt.natlib.fx.XMLParserListener;

/**
 * A simple ParserListener implementation that can be used to collect the meta
 * data events into an XML string.
 * 
 * @author bbeaumont
 */
public class StringBufferXMLParserListener implements ParserListener {

	/** The output stream for temporary storage */
	private ByteArrayOutputStream bos = null;
	/** The delegate ParserListener */
	private ParserListener delegate = null;
	
	/**
	 * Construct a listener.
	 * @param dtd The name of the DTD for the DOCTYPE.
	 */
	public StringBufferXMLParserListener(String dtd) {
		bos = new ByteArrayOutputStream(2048);
		delegate = new XMLParserListener(bos);
	}
	
	
	/**
	 * Delegate all events to the delegate class.
	 * @see nz.govt.natlib.fx.XMLParserListener#handleParseEvent(nz.govt.natlib.fx.ParserEvent)
	 */
	public void handleParseEvent(ParserEvent event) {
		delegate.handleParseEvent(event);
	}
	
	/**
	 * Get the Metadata as a string.
	 * @return The metadata in an XML string.
	 */
	public String getContents() {
		return bos.toString();
	}

}
