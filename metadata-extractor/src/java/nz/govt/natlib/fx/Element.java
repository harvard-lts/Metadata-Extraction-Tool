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

/**
 * Abstract class representing an element. An element is a building block for
 * building your own parsers. Each element is capable of reading a particular
 * block of bytes and extracting metadata out of it.
 * 
 * In general, an Adapter defines a CompoundElement that defines a section
 * of metadata inside a file. The <code>read(DataSource,ParserContext)</code>
 * method on that CompoundElement is called and metadata is extracted out of 
 * that section of the file and pushed to the ParserContext.
 * 
 * @author Nic Evans
 * @version 1.0
 */
import java.io.IOException;

public abstract class Element {

	/** Internal elements do not output values to the metadata file. */
	private boolean internal = false;

	/**
	 * Constructs a normal element.
	 */
	public Element() {
		this(false);
	}

	/**
	 * Constructs an Element.
	 * 
	 * @param internal
	 *            true if this should not output anything to the metadata file.
	 */
	public Element(boolean internal) {
		setInternal(internal);
	}

	/**
	 * Checks to see if the Element is an 'internal' element. I.e. a silent part
	 * of the parser that does not require any output.
	 * 
	 * @return true if the element is internal only
	 */
	public boolean isInternal() {
		return this.internal;
	}

	/**
	 * sets an Element to be internal only
	 * 
	 * @param internal
	 *            boolean sets the value of the internal flag
	 */
	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	/**
	 * The main method used by parsers to execute the functionality of the
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
	public abstract void read(DataSource data, ParserContext ctx)
			throws IOException;

	/**
	 * Internal 'fire' event method that is cognizant of the 'internal' nature
	 * of the Element. Note if you call fireEvent on the context itself you must
	 * code the 'internal' knowledge yourself.
	 * 
	 * This method is generally used to write a value to the XML file. It relies
	 * on the element have been opened first.
	 * 
	 * @param ctx
	 *            The context to fire the event to.
	 * @param value
	 *            The value of the event.
	 */
	protected void fireParseEvent(ParserContext ctx, Object value) {
		ctx.fireParseEvent(value, internal);
	}

	/**
	 * Internal 'fire' event method that is cognizant of the 'internal' nature
	 * of the Element. Note if you call fireEvent on the context itself you must
	 * code the 'internal' knowledge yourself.
	 * 
	 * Using the XMLParserListener, this method writes the opening tag, value
	 * and closing tag.
	 * 
	 * For exampe <code>fireParseEvent(ctx, "compressed", "false");</code>
	 * writes:
	 * 
	 * <code><compressed>false</compressed></code>
	 * 
	 * @param ctx
	 *            The context to send the event to.
	 * @param simpleName
	 *            The name of the event.
	 * @param value
	 *            The value of the event.
	 */
	protected void fireParseEvent(ParserContext ctx, String simpleName,
			Object value) {
		ctx.fireParseEvent(simpleName, value, internal, null);
	}

}