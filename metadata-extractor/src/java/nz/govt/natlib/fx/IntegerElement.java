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

package nz.govt.natlib.fx;

import java.io.IOException;

/**
 * Can read an 'n' integers from a datasource. Any number of similar length
 * integers and formats (hex, octal, binary etc...) can be read.
 * 
 * @author Nic Evans
 * @version 1.0
 */
public class IntegerElement extends Element {

	public static final int BYTE_SIZE = 1;

	public static final int SHORT_SIZE = 2;

	public static final int INT_SIZE = 4;

	public static final int LONG_SIZE = 8;

	public static final int DOUBLE_SIZE = 8;

	public static final int HEX_FORMAT = 1;

	public static final int DECIMAL_FORMAT = 2;

	public static final int BINARY_FORMAT = 3;

	public static final int OCTAL_FORMAT = 4;

	protected int bytesize = BYTE_SIZE;

	protected int elementCount = 1;

	protected boolean bigEndian;

	protected int outputFomrmat = HEX_FORMAT;

	/**
	 * Constructor for an element that can read 1 integer of arbitary length
	 * 
	 * @param byteSize
	 * @param bigEndian
	 * @param outputFormat
	 */
	public IntegerElement(int byteSize, boolean bigEndian, int outputFormat) {
		this(byteSize, 1, bigEndian, outputFormat);
	}

	/**
	 * Constructor for an element that can read 'n' integers of arbitary length
	 * 
	 * @param byteSize
	 * @param bigEndian
	 * @param outputFomrmat
	 */
	public IntegerElement(int byteSize, int elementCount, boolean bigEndian,
			int outputFomrmat) {
		this.bytesize = byteSize;
		this.elementCount = elementCount;
		this.bigEndian = bigEndian;
		this.outputFomrmat = outputFomrmat;
	}

	/**
	 * The main method used by parsers top execute the functionality of the
	 * Element.
	 * 
	 * @param data
	 *            the source of the data to be parsed by this element
	 * @param ctx
	 *            the context of the parser, within which this element is being
	 *            executed. Elements should output all significant parse events
	 *            into this context.
	 * @throws IOException
	 * @throws Runtime
	 *             Exception if the match is not made.
	 */
	public void read(DataSource data, ParserContext ctx) throws IOException {
		byte[] buf = data.getData(bytesize * elementCount);
		Object result = null;

		if (buf.length == 0) {
			// i.e. nothing read...
			result = null;
		} else {
			Object[] ob = new Object[elementCount];
			// get each element in the array...
			for (int i = 0; i < elementCount; i++) {
				long val = getNumericalValue(buf, i);
				ob[i] = resolve(val);
			}

			if (elementCount == 1) {
				result = ob[0];
			} else {
				result = ob;
			}
		}

		ctx.fireParseEvent(result);
	}

	/**
	 * Returns the numerical value of a bunch of bytes.
	 * 
	 * @param in
	 * @param offset
	 * @return The numerical value of the bytes.
	 */
	public long getNumericalValue(byte[] in, int offset) {
		long val = 0;
		if (bigEndian) {
			val = FXUtil.getBigEndian(in, offset * bytesize, bytesize);
		} else {
			val = FXUtil.getLittleEndian(in, offset * bytesize, bytesize);
		}
		return val;
	}

	/**
	 * Resolves the value into an output format.
	 * 
	 * @param val
	 * @return
	 */
	private Object resolve(long val) {
		Object result = null;
		switch (outputFomrmat) {
		case HEX_FORMAT:
			result = "0x" + Long.toHexString(val);
			break;
		case OCTAL_FORMAT:
			result = Long.toOctalString(val);
			break;
		case BINARY_FORMAT:
			result = Long.toBinaryString(val);
			break;
		case DECIMAL_FORMAT:
			switch (bytesize) {
			case BYTE_SIZE:
				result = new Short((short) val);
				break;
			case SHORT_SIZE:
				result = new Integer((int) val);
				break;
			case INT_SIZE:
				result = new Integer((int) val);
				break;
			case LONG_SIZE:
				result = new Long(val);
				break;
			}
			break;
		}

		return result;
	}

}