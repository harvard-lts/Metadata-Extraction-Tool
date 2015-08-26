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
public abstract class TextNode extends SimpleNode {

	protected StringBuffer value = new StringBuffer();

	TextNode(int type) {
		super(type);
		this.value = new StringBuffer();
	}

	TextNode(int type, String value) {
		super(type);
		this.value = new StringBuffer(value);
	}

	public void append(String st) {
		this.value.append(st);
	}

	public void append(char c) {
		this.value.append(c);
	}

	public void setValue(String st) {
		this.value.delete(0, this.value.length());
		append(st);
	}

	public void acceptValue(Object value) {
		setValue(value + "");
	}

	public StringBuffer getValue() {
		return value;
	}

	public String getStringValue() {
		return value + "";
	}

	public String toString() {
		return getStringValue();
	}

	public boolean equals(Object object) {
		return ("" + object).equalsIgnoreCase(value + "");
	}

	public void flush() {
		this.value = PDFUtil.decodeNormalString(this.value);
	}

	public char[] getChars() {
		return getStringValue().toCharArray();
	}

	public byte[] getISOBytes() {
		return PDFUtil.getISOBytes(getStringValue());
	}
}
