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
public class ObjectNode extends SimpleNode {

	private int id = -1;

	private int version = -1;

	private PDFNode value = null;

	public ObjectNode() {
		super(OBJECT);
	}

	public PDFNode getValue() {
		return value;
	}

	public void acceptValue(Object value) {
		this.value = (PDFNode) value;
		this.value.setParent(this);
	}

	public int getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String toString() {
		return "Object: id=" + id + ", version=" + version + " value=" + value;
	}
}