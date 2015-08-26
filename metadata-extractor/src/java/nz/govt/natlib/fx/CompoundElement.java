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
import java.util.HashMap;

/**
 * A type of element which is entirely made up of other elements. When called
 * upon to read a datasource this element calls upon each of it's 'children' to
 * read a part of the datasource. A whole file structure could be parsed using
 * 'trees' of compound elements.
 * 
 * @author Nic Evans
 * @version 1.0
 */
public class CompoundElement extends Element {

	private HashMap elements;

	private String[] names; // shows the order of the names...

	/**
	 * Constructor of a compound element. the array of names and the array of
	 * elements must be the same length and the positions identical. for
	 * instance name[5] is the name for element[5]
	 * 
	 * @param names
	 *            the names of the elements
	 * @param elements
	 *            the elements themselves.
	 */
	public CompoundElement(String[] names, Element[] elements) {
		if (names.length != elements.length) {
			throw new RuntimeException(
					"Element names do not match element count");
		}

		this.elements = new HashMap(names.length, 100);
		for (int i = 0; i < names.length; i++) {
			this.elements.put(names[i], elements[i]);
		}
		this.names = names;
	}

	/**
	 * Reads all the 'children' elements in turn - sending events to the
	 * context.
	 * 
	 * @param data
	 *            the datasource to read from
	 * @param ctx
	 *            the parser context, this fires events to the parser listener.
	 * @throws IOException
	 */
	public void read(DataSource data, ParserContext ctx) throws IOException {
		for (int i = 0; i < names.length; i++) {
			String elementName = names[i];
			Element element = (Element) elements.get(elementName);
			boolean internal = element.isInternal();
			ctx.fireStartParseEvent(elementName, internal, null);
			element.read(data, ctx);
			ctx.fireEndParseEvent(elementName, internal);
		}
	}
}