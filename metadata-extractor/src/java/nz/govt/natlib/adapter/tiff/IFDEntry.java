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

package nz.govt.natlib.adapter.tiff;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */

public class IFDEntry {

	private int tag;

	private int length;

	private int type;

	private String name;

	private Object value;

	IFDEntry(int tag, int length, int type, String name, Object value) {
		this.tag = tag;
		this.length = length;
		this.type = type;
		this.name = name;
		this.value = value;
	}

	public Object getName() {
		return this.name;
	}

	public Object getValue() {
		return this.value;
	}

	public int getTag() {
		return this.tag;
	}

	public int getType() {
		return this.type;
	}

	public int getLength() {
		return this.length;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("[name=" + name + ", ");
		buf.append("tag=" + tag + ", ");
		buf.append("length=" + length + ", ");
		buf.append("type=" + type + ", ");
		buf.append("value=" + value + "]");
		return buf.toString();
	}
}