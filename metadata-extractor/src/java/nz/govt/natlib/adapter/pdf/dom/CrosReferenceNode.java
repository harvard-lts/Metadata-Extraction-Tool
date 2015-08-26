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
 * Created on 1/06/2004
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
public class CrosReferenceNode extends SimpleNode {

	private int offset;

	private int id;

	private int version;

	private boolean current;

	/**
	 */
	public CrosReferenceNode() {
		super(XREFREF);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nz.govt.natlib.adapter.pdf.dom.PDFNode#acceptValue(java.lang.Object)
	 */
	public void acceptValue(Object value) {
	}

	/**
	 * @return true if the node is current.
	 */
	public boolean isCurrent() {
		return current;
	}

	/**
	 * @return the ID of the node.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the offset of the node.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @return the version.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param b
	 */
	public void setCurrent(boolean b) {
		current = b;
	}

	/**
	 * @param i
	 */
	public void setId(int i) {
		id = i;
	}

	/**
	 * @param i
	 */
	public void setOffset(int i) {
		offset = i;
	}

	/**
	 * @param i
	 */
	public void setVersion(int i) {
		version = i;
	}

	public String toString() {
		return "[Ref " + id + ", start=" + offset + ", version=" + version
				+ ", current=" + current + "]";
	}

}
