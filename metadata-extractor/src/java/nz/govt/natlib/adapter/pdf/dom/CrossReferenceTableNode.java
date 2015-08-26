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
 * Created on 28/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nz.govt.natlib.adapter.pdf.dom;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author nevans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CrossReferenceTableNode extends SimpleNode {

	private ArrayList objectrefs = new ArrayList();

	// state as we add new values...
	private int section = 0;

	private int sectionCount = 0;

	private int objId = 0;

	private boolean readSect = true; // the first thing to do...

	private boolean readCount = false; // the first thing to do...

	private CrosReferenceNode currentRef = null;

	/**
	 */
	public CrossReferenceTableNode() {
		super(XREF);
	}

	// process the values according to the rules for this type of container
	public void acceptValue(Object value) {
		if (readSect == true) {
			// read the object number start...
			objId = ((IntegerNode) value).getIntValue();
			section++;
			readSect = false;
			readCount = true;
		} else if (readCount) {
			// read the count...
			sectionCount = ((IntegerNode) value).getIntValue() * 3; // how many
																	// values in
																	// this
																	// section
																	// (grouped
																	// into 3
																	// objs per
																	// ref)
			// System.out.println("Section Count :"+sectionCount);
			readCount = false;
		} else {
			if (sectionCount % 3 == 0) {
				currentRef = new CrosReferenceNode();
				currentRef.setId(objId++);
				objectrefs.add(currentRef);
				currentRef.setParent(this);
				int offset = ((IntegerNode) value).getIntValue();
				currentRef.setOffset(offset);
			}
			if (sectionCount % 3 == 2) {
				int version = ((IntegerNode) value).getIntValue();
				currentRef.setVersion(version);
			}
			if (sectionCount % 3 == 1) {
				String current = ((StringNode) value).getStringValue();
				currentRef.setCurrent(current.equalsIgnoreCase("n"));
			}

			sectionCount--;
			if (sectionCount == 0) {
				readSect = true;
				readCount = false;
				section++;
			}
		}
	}

	public String toString() {
		return "Cross Reference Table - " + objectrefs;
	}

	public int size() {
		return objectrefs.size();
	}

	public CrosReferenceNode get(int i) {
		return (CrosReferenceNode) objectrefs.get(i);
	}

	/**
	 * 
	 * @param id
	 * @param version
	 * @return -1 if not found...
	 */
	public CrosReferenceNode getObjectRef(int id, int version) {
		Iterator it = objectrefs.iterator();
		CrosReferenceNode found = null;
		while (it.hasNext()) {
			CrosReferenceNode ref = (CrosReferenceNode) it.next();
			if (ref.getId() == id && ref.getVersion() == version) {
				found = ref;
				break;
			}
		}

		return found;
	}

	/**
	 * 
	 * @param id
	 * @return -1 if not found...
	 */
	public CrosReferenceNode getObjectRef(int id) {
		Iterator it = objectrefs.iterator();
		CrosReferenceNode found = null;
		while (it.hasNext()) {
			CrosReferenceNode ref = (CrosReferenceNode) it.next();
			if (ref.getId() == id) {
				if ((found == null) || (found.getVersion() < ref.getVersion())) {
					found = ref;
				}
			}
		}

		return found;
	}

}
