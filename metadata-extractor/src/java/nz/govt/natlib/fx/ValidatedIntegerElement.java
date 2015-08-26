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
 * Validates an integer read from the datasource. Once read the element can take
 * several actions: 1. notify the parser of the true/false condition 2. notify
 * the parser with arbitrary true/false values 3. notify the parser of the true
 * condition (either default value or arbitrary value) and throw and exception
 * on the false condition
 * </p>
 * 
 * @author Nic Evans
 * @version 1.0
 */

public class ValidatedIntegerElement extends Element {

	private long expected = 0;

	private Object valueFalse;

	private Object valueTrue;

	private boolean throwsException = false;

	// integer attributes
	private int size;

	private boolean bigEndian;

	/**
	 * Constructs a ValidatedIntegerElement that trhows an exception in an
	 * 'invalid' situation and passes the valueTrue object to the parser in a
	 * 'valid' situation
	 * 
	 * @param byteSize
	 * @param bigEndian
	 * @param expectedValue
	 * @param valueTrue
	 */
	public ValidatedIntegerElement(int byteSize, boolean bigEndian,
			long expectedValue, Object valueTrue) {
		this(byteSize, bigEndian, expectedValue);
		this.valueTrue = valueTrue;
		this.throwsException = true;
	}

	/**
	 * Constructs a ValidatedIntegerElement that passes true or false to the
	 * parser on validation
	 * 
	 * @param byteSize
	 * @param bigEndian
	 * @param expectedValue
	 */
	public ValidatedIntegerElement(int byteSize, boolean bigEndian,
			long expectedValue) {
		this(byteSize, bigEndian, expectedValue, new Boolean(true),
				new Boolean(false));
	}

	/**
	 * Constructs a ValidatedIntegerElement that passes arbitrary values to the
	 * parser on validation
	 * 
	 * @param byteSize
	 * @param bigEndian
	 * @param expectedValue
	 * @param valueTrue
	 * @param valueFalse
	 */
	public ValidatedIntegerElement(int byteSize, boolean bigEndian,
			long expectedValue, Object valueTrue, Object valueFalse) {
		this.size = byteSize;
		this.bigEndian = bigEndian;
		this.expected = expectedValue;
		this.valueTrue = valueTrue;
		this.valueFalse = valueFalse;
		this.throwsException = false;
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
		long value = FXUtil.getNumericalValue(data, size, bigEndian);

		if ((value != expected) && (throwsException)) {
			throw new RuntimeException("Expected value: " + expected
					+ " not found while parsing");
		} else {
			Object result = (value == expected ? valueTrue : valueFalse);
			fireParseEvent(ctx, result);
		}
	}
}