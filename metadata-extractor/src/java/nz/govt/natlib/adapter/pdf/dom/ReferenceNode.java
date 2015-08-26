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

import nz.govt.natlib.adapter.pdf.PDFParseException;

/**
 * @author nevans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReferenceNode extends SimpleNode {

	private int id = -1;

	private int version = -1;

	public ReferenceNode() {
		super(OBJ_REFERENCE);
	}

	public void acceptValue(Object value) {
		if (id == -1) {
			id = ((IntegerNode) value).getIntValue();
		} else if (version == -1) {
			version = ((IntegerNode) value).getIntValue();
		} else {
			throw new PDFParseException("too many objects for a reference");
		}
	}

	public int getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	public String toString() {
		return "Object Ref: id=" + id + ", version=" + version;
	}
}