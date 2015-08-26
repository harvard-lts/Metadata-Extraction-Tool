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
 * Reads an integer that represents an offset to another part of the datasource
 * the control is then passed to another element to continue the reading at the
 * new point. when the element has finished reading the file position is
 * restored to the point at which it was after reading the offset
 * 
 * @author Nic Evans
 * @version 1.0
 */

public class OffsetElement extends Element {

	private boolean fixed;

	private long offset;

	private int size;

	private boolean bigEndian;

	private Element element;

	/**
	 * Constructs an OffsetElement that has a fixed offset.
	 * 
	 * @param offset
	 * @param element
	 */
	public OffsetElement(long offset, Element element) {
		this.offset = offset;
		this.element = element;
		this.fixed = true;
	}

	/**
	 * Constructs a OffsetElement that reads an integer of a set size and endian
	 * to determine the offset
	 * 
	 * @param size
	 * @param bigEndian
	 * @param element
	 */
	public OffsetElement(int size, boolean bigEndian, Element element) {
		this.size = size;
		this.bigEndian = bigEndian;
		this.element = element;
		fixed = false;
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
		// read the position of the data in the main Datasource...
		long posOffset = 0;
		if (fixed) {
			posOffset = offset;
		} else {
			posOffset = FXUtil.getNumericalValue(data, size, bigEndian);
		}

		long pos = data.getPosition();
		data.setPosition(posOffset);
		element.read(data, ctx);
		data.setPosition(pos);
	}

}