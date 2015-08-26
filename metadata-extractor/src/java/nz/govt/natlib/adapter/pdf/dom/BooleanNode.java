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
public class BooleanNode extends SimpleNode {

	private Boolean value = Boolean.FALSE;

	public BooleanNode(Boolean value) {
		super(BOOLEAN);
		this.value = value;
	}

	public BooleanNode(boolean value) {
		this(value ? Boolean.TRUE : Boolean.FALSE);
	}

	public void acceptValue(Object value) {
		if (value instanceof Boolean) {
			this.value = (Boolean) value;
		} else {
			throw new PDFParseException(
					"Cannot pass a non Boolean value to a BooleanNode");
		}
	}

	public Boolean getValue() {
		return value;
	}

}
