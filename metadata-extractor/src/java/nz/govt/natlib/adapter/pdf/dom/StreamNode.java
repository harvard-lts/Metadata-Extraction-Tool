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
public class StreamNode extends SimpleNode {

	private int offset = -1;

	private int len = -1;

	public StreamNode() {
		super(STREAM);
	}

	public void acceptValue(Object value) {
		if (offset == -1) {
			offset = ((Integer) value).intValue();
		} else {
			len = ((Integer) value).intValue();
		}
	}

	public String toString() {
		return "Stream offset=" + offset + ", len=" + len;
	}

	/**
	 * @return The length of the node.
	 */
	public int getLen() {
		return len;
	}

	/**
	 * @return The offset of the node.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param i
	 */
	public void setLen(int i) {
		len = i;
	}

	/**
	 * @param i
	 */
	public void setOffset(int i) {
		offset = i;
	}

}
