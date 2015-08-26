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
 * Reads a fixed length string.
 * 
 * @author unascribed
 * @version 1.0
 */
public class FixedLengthStringElement extends Element {

	private int length = -1; // variable length

	private boolean clean = false;

	public FixedLengthStringElement(int length) {
		this(length, false);
	}

	public FixedLengthStringElement(int length, boolean clean) {
		this.length = length;
		this.clean = clean;
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
		// the length is fixed...
		byte[] buf = data.getData(length);
		String result = new String(buf);

		if (clean) {
			result = cleanString(result);
		}

		ctx.fireParseEvent(result);
	}

	private String cleanString(String elementValue) {
		String result = elementValue;
		if (elementValue instanceof String) {
			String r = "";
			for (int c = 0; c < ((String) result).length(); c++) {
				char cc = ((String) result).charAt(c);
				if ((cc >= ' ') && (cc <= '}')) {
					r += cc;
				}
			}
			result = r;
		}
		return result;
	}

}