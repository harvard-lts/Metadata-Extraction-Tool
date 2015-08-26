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
 * Created on 27/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nz.govt.natlib.adapter.pdf.dom;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author nevans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DictionaryNode extends SimpleNode {

	private HashMap map = new HashMap();

	// preflush buffer...
	private ArrayList buf = new ArrayList();

	// some state...
	private int element = 0;

	public PDFNode key = null;

	public PDFNode value = null;

	public DictionaryNode() {
		super(DICTIONARY);
	}

	public String toString() {
		return "Dictionary" + map;
	}

	// process the values according to the rules for this type of container
	public void acceptValue(Object value) {
		buf.add(value);
	}

	public PDFNode get(PDFNode search) {
		return (PDFNode) map.get(search);
	}

	public int size() {
		return map.size();
	}

	public PDFNode getKey(int i) {
		return (PDFNode) map.keySet().toArray()[i];
	}

	public PDFNode get(int i) {
		return (PDFNode) map.values().toArray()[i];
	}

	public PDFNode get(String search) {
		PDFNode found = null;

		// it will search all upper/lower and all text...
		String s = search.trim().toLowerCase();
		Iterator keys = map.keySet().iterator();
		while (keys.hasNext()) {
			PDFNode key = (PDFNode) keys.next();
			if (key.equals(s)) {
				found = (PDFNode) map.get(key);
				break;
			}
		}

		return found;
	}

	public String getString(String name) {
		String result = null;
		PDFNode node = get(name);
		if ((node != null) && (node instanceof TextNode)) {
			result = ((TextNode) node).getStringValue();
		}
		return result;
	}

	public Date getDate(String name) {
		Date result = null;
		PDFNode node = get(name);
		if ((node != null) && (node instanceof StringNode)) {
			result = ((StringNode) node).getDateValue();
		}
		return result;
	}

	public void flush() {
		boolean name = false;
		PDFNode val = null;
		for (int i = buf.size() - 1; i >= 0; i--) {
			PDFNode node = (PDFNode) buf.get(i);

			if (!name) {
				if ((node instanceof StringNode)
						&& (((StringNode) node).getStringValue()
								.equalsIgnoreCase("R"))) {
					// it's a reference! so the last two were frauds...
					PDFNode version = (PDFNode) buf.get(i - 1);
					PDFNode id = (PDFNode) buf.get(i - 2);
					ReferenceNode refNode = new ReferenceNode();
					refNode.acceptValue(id);
					refNode.acceptValue(version);
					val = refNode;
					i -= 2; // skip the next two they're bogus...
				} else {
					val = node;
				}
			} else {
				// name
				map.put(node, val);
				node.setParent(this);
				val.setParent(this);
			}

			name = !name;
		}
		buf.clear();
	}
}
