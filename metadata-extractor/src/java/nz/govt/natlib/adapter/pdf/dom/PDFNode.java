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
public interface PDFNode {
	// simple nodes...
	public static final int UNKNOWN = 0;

	public static final int STRING = 1;

	public static final int BOOLEAN = 2;

	public static final int INTEGER = 3;

	public static final int NUMBER = 4;

	public static final int NAME = 5;

	public static final int COMMENT = 6;

	public static final int STREAM_BYTES = 7;

	public static final int OBJ_REFERENCE = 8;

	public static final int HEXSTRING = 9;

	public static final int NULL = 10;

	public static final int XREFREF = 11;

	// complex nodes...
	public static final int PDF = 106;

	public static final int OBJECT = 101;

	public static final int ARRAY = 102;

	public static final int DICTIONARY = 103;

	public static final int STREAM = 105;

	public static final int XREF = 106;

	public static final int TRAILER = 107;

	// more complex types, identifiers
	public static final String START_OBJECT = "obj";

	public static final String END_OBJ = "endobj";

	public static final String START_TRAILER = "trailer";

	public static final String START_XREF = "xref";

	public static final String START_STARTXREF = "startxref";

	public static final String START_STREAM = "stream";

	public static final String END_STREAM = "endstream";

	public int getType();

	/**
	 * 
	 * @param value
	 *            the object that has meaning for this node and should to be
	 *            processed
	 */
	public void acceptValue(Object value);

	// tells a node it's finished accepting values for now - so flush the buffer
	// if it was holding one...
	public void flush();

	public abstract PDFNode getParent();

	abstract void setParent(PDFNode parent);

	public abstract ObjectNode getContainingObject();
}
