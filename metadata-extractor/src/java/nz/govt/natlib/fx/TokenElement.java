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
 * Reads a datasource until a particular token is found the output is the full
 * string up till the token is found.
 * 
 * @author unascribed
 * @version 1.0
 */
public class TokenElement extends Element {
	private String tokens;

	/**
	 * Constructs a token element. Each character of the tokens string
	 * represents a individual token
	 */
	public TokenElement(String tokens) {
		this.tokens = tokens;
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
		// read until a token is found
		String result = "";
		while (true) {
			char d = (char) data.getData(1)[0];
			if (tokens.indexOf(d) >= 0)
				break;
			result += d;
		}
		fireParseEvent(ctx, result);
	}

}
