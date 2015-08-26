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
public class NumberNode extends NumericalNode {

	private Double value;

	public NumberNode(double value) {
		this(new Double(value));
	}

	public NumberNode(Double value) {
		super(NUMBER);
		this.value = value;
	}

	public void acceptValue(Object value) {
		if (value instanceof Number) {
			this.value = new Double(((Number) value).doubleValue());
		} else {
			throw new PDFParseException(
					"Cannot pass a non Numerical value to a NumberNode");
		}
	}

	public Double getValue() {
		return this.value;
	}

	protected Number getNumberValue() {
		return value;
	}

}
