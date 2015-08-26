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
 * Can read a 8 byte rational number where the first 4 bytes are the numerator
 * and the last 4 bytes are the demoninator. This alows a rational number to be
 * formed the only known use of this element is in the TIFF binary file format.
 * 
 * @author unascribed
 * @version 1.0
 */
public class RationalElement extends Element {

	private boolean bigEndian = false;

	public RationalElement(boolean bigEndian) {
		this.bigEndian = bigEndian;
	}

	public RationalElement() {
		this(false);
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
	 */
	public void read(DataSource data, ParserContext ctx) throws IOException {
		long numerator = FXUtil.getNumericalValue(data,
				IntegerElement.INT_SIZE, bigEndian);
		long demoninator = FXUtil.getNumericalValue(data,
				IntegerElement.INT_SIZE, bigEndian);
		Object out = null;
		out = new Double((double) numerator / (double) demoninator);
		fireParseEvent(ctx, out);
	}

}
