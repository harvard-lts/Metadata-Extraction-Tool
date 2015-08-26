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
 * This is not really an Element at all, in that it doesn't read anything from
 * the datasource. But it an useful element nonetheless. It fires an event with
 * a value as though it was reading from the stream. If you place a constant
 * element into a compound object you can make sure that constants that you want
 * to be part of the eventual output are included.
 * 
 * @author Nic Evans
 * @version 1.0
 */

public class ConstantElement extends Element {

	// an expression representing the constant to be 'injected' into the context
	// private Expression constant;
	private String constant;

	/**
	 * Constructor for the Constant
	 * 
	 * @param constant
	 *            the string representing the constant. If you placed the value
	 *            (1+2)*3 as a string the constant value added would be 9 at
	 *            read time. If you placed the value (a+b)*c as the string
	 *            constant you would get a correct answer, provided the context
	 *            knew what the values of a,b & c were. see Expression for how
	 *            it might know this.
	 */
	public ConstantElement(String constant) {
		this.constant = constant;
	}

	// /**
	// * Constructor for the Constant
	// * @param constant the Expression representing the constant. see
	// Expression
	// * details.
	// */
	// public ConstantElement(Expression constant) {
	// this.constant=constant;
	// }

	/**
	 * Evaluates the expression and fires an event containing the value of the
	 * expression to the context.
	 * 
	 * @param data
	 * @param ctx
	 * @throws IOException
	 */
	public void read(DataSource data, ParserContext ctx)
			throws java.io.IOException {
		// Object value = constant.getValue(ctx);
		fireParseEvent(ctx, constant);
	}
}