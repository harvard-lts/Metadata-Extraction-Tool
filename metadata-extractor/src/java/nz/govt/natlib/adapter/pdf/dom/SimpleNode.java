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

/**
 * @author nevans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class SimpleNode implements PDFNode {

	private int type = UNKNOWN;

	private PDFNode parent;

	public SimpleNode(int type) {
		this.type = type;
	}

	public void setParent(PDFNode parent) {
		this.parent = parent;
	}

	public ObjectNode getContainingObject() {
		PDFNode parent = getParent();
		if (parent == null) {
			return null; // the search is over...
		} else {
			if (parent instanceof ObjectNode) {
				return (ObjectNode) parent;
			} else {
				return parent.getContainingObject();
			}
		}
	}

	public PDFNode getParent() {
		return this.parent;
	}

	public int getType() {
		return type;
	}

	public void flush() {
		// normally don't need to flush...
	}

}
