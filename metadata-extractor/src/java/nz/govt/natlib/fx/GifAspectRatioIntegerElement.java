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
 * Created on 25/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nz.govt.natlib.fx;

import java.io.IOException;

/**
 * @author nevans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GifAspectRatioIntegerElement extends IntegerElement {

	/**
	 * Constructor for an element that can read 1 integer of arbitary length
	 * 
	 * @param byteSize
	 * @param bigEndian
	 */
	public GifAspectRatioIntegerElement(int byteSize, boolean bigEndian) {
		super(byteSize, 1, bigEndian, IntegerElement.DECIMAL_FORMAT);
	}

	/**
	 * The form of the expression is value+1 where value will be resolved during
	 * the read...
	 * 
	 * @see nz.govt.natlib.fx.Element#read(nz.govt.natlib.fx.DataSource,
	 *      nz.govt.natlib.fx.ParserContext)
	 */
	public void read(DataSource data, ParserContext ctx) throws IOException {
		// reads the integer but then does some maths on it...
		byte[] buf = data.getData(bytesize);
		long value = getNumericalValue(buf, 0);

		Object val = null;
		// now sort out the expression...
		if (value == 0) {
			val = new String("\"n/a\"");
		} else {
			val = new Double((value + 15) / 64);
		}

		ctx.fireParseEvent(val);
	}
}
