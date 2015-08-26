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

import java.util.HashMap;

/**
 * @author unascribed
 * @version 1.0
 */
public class ObjectFolderNode extends FolderNode implements PropertyContainer {

	private int type = SIMPLE;

	public static Property nameProp = new Property("Name", Property.STRING);

	public static Property idProp = new Property("ID", Property.INTEGER);

	public static Property groupIdProp = new Property("Group ID",
			Property.INTEGER);

	public static Property partOfGroupProp = new Property("Part of Group",
			Property.BOOLEAN);

	// to do with editing properies on certain nodes...
	private HashMap properties = new HashMap();

	public ObjectFolderNode(String name, int type) {
		super(null);
		this.type = type;

		// hide some...
		partOfGroupProp.setVisible(false);

		// use a property for the name
		nameProp.addAllowedValue("*");
		setPropertyValue(nameProp, name);
		setPropertyValue(idProp, "0"); // needs work
		setPropertyValue(groupIdProp, "0");
		setPropertyValue(partOfGroupProp, Boolean.FALSE);
		init = false;
	}

	public int getType() {
		return this.type;
	}

	public String getName() {
		return getPropertyValue(nameProp).toString();
	}

	public Object getPropertyValue(Property prop) {
		return properties.get(prop);
	}

	// do something fancy for the name of this object when a folder gets
	// added...
	private boolean init = false;

	public void addNode(BaseNode node) {
		super.addNode(node);
		if ((node instanceof FolderNode) && (!init)) {
			setPropertyValue(nameProp, node.getName());
		}
	}

	public void setPropertyValue(Property prop, Object value) {
		properties.put(prop, value);
		fireNodeChanged(this);

		// some properies will affect others...

		if (partOfGroupProp == prop) {
			if (value == Boolean.FALSE) {
				properties.put(groupIdProp, "");
			}
		}

		if (groupIdProp == prop) {
			if ((value == null) || (value.toString().trim().equals(""))) {
				properties.put(partOfGroupProp, Boolean.FALSE); // don't want to
																// cycle round
																// pointlessly
			} else {
				properties.put(partOfGroupProp, Boolean.TRUE); // don't want to
																// cycle round
																// pointlessly
			}
		}

		if (nameProp.equals(prop)) {
			init = true;
		}
	}

	public Property[] getProperties() {
		Property[] props = new Property[] { idProp, nameProp, partOfGroupProp,
				groupIdProp };
		return props;
	}

}