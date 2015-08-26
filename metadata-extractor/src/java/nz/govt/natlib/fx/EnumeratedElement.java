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
 * Created on 7/06/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nz.govt.natlib.fx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nevans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EnumeratedElement extends Element {

	private Element reader;

	private Object defaultValue;

	private Map map;

	/**
	 * Creates a mapped element
	 * 
	 * @param reader
	 *            the element to read the inital value from
	 * @param mapNames
	 *            the names for the map (i.e. the mapname relates directly to
	 *            the result read from the element)
	 * @param mapValues
	 *            the value output for the read in value
	 * @param defaultValue
	 *            if no match is made this element will be used.
	 */
	public EnumeratedElement(Element reader, String[] mapNames,
			Object[] mapValues, Object defaultValue) {
		this(reader, getMap(mapNames, mapValues), defaultValue);
	}

	/**
	 * Creates a mapped element
	 * 
	 * @param reader
	 * @param map
	 *            the values for the map (i.e. keys relate directly to the value
	 *            read by element)
	 * @param defaultValue
	 *            if no match is made this element will be used.
	 */
	public EnumeratedElement(Element reader, Map map, Object defaultValue) {
		this.reader = reader;
		this.defaultValue = defaultValue;
		this.map = map;
	}

	/**
	 * Turns two arrays into a map.
	 * 
	 * @param names
	 * @param elements
	 * @return
	 */
	private static Map getMap(String[] names, Object[] elements) {
		if (names.length != elements.length) {
			throw new RuntimeException("map names do not match map count");
		}
		HashMap arrayMap = new HashMap(names.length, 100);
		for (int i = 0; i < names.length; i++) {
			arrayMap.put(names[i], elements[i]);
		}
		return arrayMap;
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
		Object result = FXUtil.readElement(data, reader);
		// not just a straight map - because the types will mismatch between
		// strings and numbers
		Object mappedValue = (Object) map.get(result + "");

		if (mappedValue == null) {
			if (defaultValue != null) {
				mappedValue = defaultValue;
			} else {
				throw new RuntimeException("No match for " + result
						+ " in map " + this);
			}
		}

		ctx.fireParseEvent(mappedValue);
	}

}
