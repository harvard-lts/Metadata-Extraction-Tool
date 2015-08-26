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

package nz.govt.natlib.meta.ui.tree;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * @author unascribed
 * @version 1.0
 */

public class Property {

	private boolean editable = true;

	private String name;

	private int type;

	private ArrayList values;

	private boolean visible = true;

	public static final int STRING = 0;

	public static final int INTEGER = 1;

	public static final int ENUMERATION = 2;

	public static final int BOOLEAN = 3;

	public Property(String name, int type) {
		this.name = name;
		this.type = type;
		this.values = new ArrayList();
	}

	public boolean isEditable() {
		return editable;
	}

	public boolean isVisible() {
		return visible;
	}

	public String getLabel() {
		return getName();
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public void addAllowedValue(Object value) {
		this.values.add(value);
	}

	public void removeAllowedValue(Object value) {
		this.values.remove(value);
	}

	public Iterator getAllowedValues() {
		return this.values.iterator();
	}

	public boolean isValid(Object obj) {
		boolean valid = false;

		if (type == INTEGER) {
			String numberValue = obj.toString();
			valid = true;
			for (int i = 0; i < numberValue.length(); i++) {
				if (!Character.isDigit(numberValue.charAt(i))) {
					valid = false;
				}
			}
		}

		if (type == STRING) {
			String numberValue = obj.toString();
			// run through each allowed value and check validity (regex)
			Iterator it = getAllowedValues();
			while (it.hasNext()) {
				String val = (String) it.next();
				// I'd like to use RegEX here but want to maintain JDK1.2
				// compatibility...
				if (val.equals("*")) {
					valid = true;
					break;
				}
				if (obj.equals(val)) {
					valid = true;
					break;
				}
			}
		}

		if (type == ENUMERATION) {
			Iterator it = getAllowedValues();
			while (it.hasNext()) {
				Object val = it.next();
				if (obj.equals(val)) {
					valid = true;
					break;
				}
			}
		}

		return valid;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}