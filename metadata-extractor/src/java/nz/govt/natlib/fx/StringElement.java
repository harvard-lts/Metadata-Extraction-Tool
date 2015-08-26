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

/**
 * Reads a sring with a predetermined terminator (i.e. null = default).
 * 
 * NOTE: should really be two separate classes
 * 
 * @author unascribed
 * @version 1.0
 */
public class StringElement extends Element {

	private byte terminator = 0x00;

	public StringElement() {
		this((byte) 0x00);
	}

	public StringElement(byte terminator) {
		this.terminator = terminator;
	}

	/**
	 * The main method used by parsers top execute the functionality of the
	 * Element.
	 * 
	 * @param data
	 *            the source of the data to be parsed by this element
	 * @param ctx
	 *            the context of the parser, within which this element is being
	 *            executed. Elements should output all significant parse events
	 *            into this context.
	 * @throws IOException
	 * @throws Runtime
	 *             Exception if the match is not made.
	 */
	public void read(DataSource data, ParserContext ctx) throws IOException {
		StringBuffer result = new StringBuffer();

		// use the terminator approach
		while (true) {
			char d = (char) data.getData(1)[0];
			if (d == 0x00)
				break;
			result.append(d);
		}

		ctx.fireParseEvent(result.toString());
	}

}