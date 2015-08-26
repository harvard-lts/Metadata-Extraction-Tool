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
 * Created on 7/06/2004
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
public class BooleanElement extends Element {

	private int length = IntegerElement.BYTE_SIZE;

	public BooleanElement() {
		this(IntegerElement.BYTE_SIZE);
	}

	public BooleanElement(int length) {
		this.length = length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nz.govt.natlib.fx.Element#read(nz.govt.natlib.fx.DataSource,
	 *      nz.govt.natlib.fx.ParserContext)
	 */
	public void read(DataSource data, ParserContext ctx) throws IOException {
		byte[] buf = data.getData(length);
		long value = FXUtil.getNumericalValue(buf, false);
		boolean result = (value != 0);
		ctx.fireParseEvent(result + "");
	}

}
