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
 * Reads a set length of data from the stream. This has the effect of relocating
 * the file pointer as well as setting any variables.
 * 
 * @author Nic Evans
 * @version 1.0
 */
public class PositionalElement extends Element {
	private int length = 0;

	/**
	 * fixed length only
	 * 
	 * @param length
	 */
	public PositionalElement(int length) {
		this.length = length;
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
	 */
	public void read(DataSource data, ParserContext ctx) throws IOException {
		byte[] buf = data.getData(length);
		fireParseEvent(ctx, buf);
	}

}
