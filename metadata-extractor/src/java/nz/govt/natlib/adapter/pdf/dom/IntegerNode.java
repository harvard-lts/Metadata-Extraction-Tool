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
public class IntegerNode extends NumericalNode {

	private Integer value;

	public IntegerNode(Integer value) {
		super(INTEGER);
		this.value = value;
	}

	public IntegerNode(int value) {
		this(new Integer(value));
	}

	public void acceptValue(Object value) {
		if (value instanceof Integer) {
			this.value = (Integer) value;
		} else {
			throw new PDFParseException(
					"Cannot pass a non Numerical value to an IntegerNode");
		}
	}

	protected Number getNumberValue() {
		return value;
	}

	/**
	 * starts at zero
	 * 
	 * @param bitPos which bit to get.
	 * @return true if the specified bit is 1.
	 */
	public boolean getBit(int bitPos) {
		return ((getIntValue() >> bitPos) & 0x01) == 0x01;
	}

	/**
	 * 
	 * @return the 4 bytes of the integer... low order first
	 */
	public byte[] getBytes() {
		byte[] res = new byte[4];
		int v = getIntValue();
		for (int i = 0; i < 4; i++) {
			res[i] = (byte) (v & 0xFF);
			v = (v >> 8);
		}

		return res;
	}

}
