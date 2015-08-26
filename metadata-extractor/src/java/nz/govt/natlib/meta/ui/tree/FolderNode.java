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

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import nz.govt.natlib.meta.HarvestSource;
import nz.govt.natlib.meta.HarvestStatus;

/**
 * @author unascribed
 * @version 1.0
 */

public class FolderNode implements BaseNode {

	private Vector children;

	private String name;

	private BaseNode parent;

	public FolderNode(String name) {
		this.children = new Vector();
		this.name = name;
	}

	public int getType() {
		return parent.getType();
	}

	public TreeNode getChildAt(int childIndex) {
		return (TreeNode) children.get(childIndex);
	}

	public int getChildCount() {
		return children.size();
	}

	public TreeNode getParent() {
		return parent;
	}

	public void addNotify(BaseNode parent) {
		this.parent = parent;
	}

	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public boolean isLeaf() {
		return false;
	}

	public Enumeration children() {
		return children.elements();
	}

	public HarvestSource[] getChildren() {
		HarvestSource[] c = new HarvestSource[children.size()];
		children.toArray(c);
		return c;
	}

	public File getFile() {
		return null;
	}

	public void setStatus(HarvestStatus status, String message) {
		for (int i = 0; i < getChildCount(); i++) {
			((BaseNode) getChildAt(i)).setStatus(status, message);
		}
	}

	public void addNode(BaseNode node) {
		this.children.add(node);
		node.addNotify(this);
		fireNodeChanged(this);
	}

	public void removeNode(BaseNode node) {
		this.children.remove(node);
		node.addNotify(null);
		fireNodeChanged(this);
	}

	public void clear() {
		this.children.clear();
		fireNodeChanged(this);
	}

	public void fireNodeChanged(BaseNode node) {
		if (parent != null) {
			parent.fireNodeChanged(node);
		}
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return this.name;
	}

	public boolean hasError() {
		Iterator i = children.iterator();
		while (i.hasNext()) {
			if (((BaseNode) i.next()).hasError()) {
				return true;
			}
		}
		return false;
	}
}