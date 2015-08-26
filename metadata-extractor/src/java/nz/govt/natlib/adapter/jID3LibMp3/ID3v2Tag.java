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
 * Created on 8/06/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nz.govt.natlib.adapter.jID3LibMp3;

import java.util.HashMap;

/**
 * @author nevans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ID3v2Tag {
	private String name = null;

	private String value = null;

	private String[] attributes = null;

	private String[] values = null;

	public HashMap getMap() {
		if (attributes == null)
			return null;

		HashMap map = new HashMap();
		for (int i = 0; i < attributes.length; i++) {
			map.put(attributes[i], values[i]);
		}
		return map;
	}

	/**
	 * @return The name of the tag.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The value of the tag.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param string
	 */
	public void setValue(String string) {
		value = string;
	}

	/**
	 * @param strings
	 */
	public void setAttributes(String[] strings) {
		attributes = strings;
	}

	/**
	 * @param strings
	 */
	public void setValues(String[] strings) {
		values = strings;
	}

}
