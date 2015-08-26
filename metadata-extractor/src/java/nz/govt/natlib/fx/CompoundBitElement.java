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
 * Created on 24/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nz.govt.natlib.fx;

import java.io.IOException;
import java.util.HashMap;

/**
 * Similar to compound element, this element works by analysing bits and
 * returning their values as bytes...
 * 
 * @author NEvans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CompoundBitElement extends Element {

	private HashMap elements;

	private String[] names; // shows the order of the names...

	/**
	 * Constructor of a compound element. the array of names and the array of
	 * elements must be the same length and the positions identical. for
	 * instance name[5] is the name for element[5]
	 * 
	 * @param names
	 *            the names of the elements
	 * @param elements
	 *            the elements themselves.
	 */
	public CompoundBitElement(String[] names, BitElement[] elements) {
		this(names, elements, true);
	}

	/**
	 * Constructor of a compound element. the array of names and the array of
	 * elements must be the same length and the positions identical. for
	 * instance name[5] is the name for element[5]
	 * 
	 * @param names
	 *            the names of the elements
	 * @param elements
	 *            the elements themselves.
	 */
	public CompoundBitElement(String[] names, BitElement[] elements,
			boolean internal) {
		if (names.length != elements.length) {
			throw new RuntimeException(
					"Element names do not match element count");
		}

		this.elements = new HashMap(names.length, 100);
		for (int i = 0; i < names.length; i++) {
			this.elements.put(names[i], elements[i]);
		}
		this.names = names;

		// the default for this type is internal
		super.setInternal(internal);
	}

	/*
	 * @see nz.govt.natlib.fx.Element#read(nz.govt.natlib.fx.DataSource,
	 *      nz.govt.natlib.fx.ParserContext)
	 */
	public void read(DataSource ftk, ParserContext ctx) throws IOException {
		// run through the list and get the number of bytes to read...
		int bytes = getByteCount();
		byte[] data = ftk.getData(bytes);
		String bits = BitFieldUtil.getBits(data);

		// reading - I need it to cross byte boundaries and allow really long
		// bytes (more than long)
		int pos = 0;
		int len = 0;
		for (int i = 0; i < names.length; i++) {
			String elementName = names[i];
			BitElement element = (BitElement) elements.get(elementName);
			len = element.getBitCount();
			// process it!
			String subByte = bits.substring(pos, pos + len);
			Object value = element.read(BitFieldUtil.getValue(subByte));

			// System.out.println(elementName+"="+value);
			boolean internal = element.isInternal();
			if (!internal) {
				ctx.fireParseEvent(elementName, value);
			}

			pos += len;
		}
	}

	public int getByteCount() {
		int bits = 0;
		for (int i = 0; i < names.length; i++) {
			String elementName = names[i];
			BitElement element = (BitElement) elements.get(elementName);
			bits += element.getBitCount();
		}
		int bytes = (bits / 8) + ((bits % 8) == 0 ? 0 : 1);
		return bytes;
	}

	public interface BitElement {

		public int getBitCount();

		public Object read(int value);

		public boolean isInternal();

	}

	public static class BitReader implements BitElement {

		int bits = 0;

		boolean isInternal = false;

		public BitReader(int bits) {
			this.bits = bits;
		}

		public int getBitCount() {
			return bits;
		}

		public Object read(int value) {
			return "" + value;
		}

		public boolean isInternal() {
			return isInternal;
		}

		public void setInternal(boolean isInternal) {
			this.isInternal = isInternal;
		}

	}

	public static class BitChomper extends BitReader {

		public BitChomper(int bits) {
			super(bits);
			setInternal(true);
		}

	}

	public static class AddingBitReader extends BitReader {

		private int add = 0;

		public AddingBitReader(int bits, int add) {
			super(bits);
			this.add = add;
		}

		public Object read(int value) {
			return "" + (value + add);
		}

	}

	public static class EnumeratedBitReader extends BitReader {

		String[] valuesRead;

		Object[] valuesReplacements;

		Object def = null;

		public EnumeratedBitReader(int bits, String[] valuesRead,
				Object[] valuesReplacements, Object defValue) {
			super(bits);
			if (valuesRead.length != valuesReplacements.length) {
				throw new RuntimeException(
						"Length of values must equal replacements");
			}
			this.valuesRead = valuesRead;
			this.valuesReplacements = valuesReplacements;
			this.def = defValue;
		}

		public Object read(int value) {
			Object val = super.read(value);
			Object replaceVal = def;

			for (int i = 0; i < valuesRead.length; i++) {
				if (valuesRead[i].equals(val)) {
					replaceVal = valuesReplacements[i];
				}
			}

			return replaceVal;
		}

	}

	public static class BooleanBitReader extends BitReader {

		int trueValue = 0;

		public BooleanBitReader(int bits) {
			this(bits, 1);
		}

		public BooleanBitReader(int bits, int trueValue) {
			super(bits);
			this.trueValue = trueValue;

		}

		public Object read(int value) {
			return "" + (value == trueValue);
		}

	}

}
