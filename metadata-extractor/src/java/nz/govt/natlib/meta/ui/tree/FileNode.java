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

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.TreeNode;

import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.meta.HarvestSource;
import nz.govt.natlib.meta.HarvestStatus;
import nz.govt.natlib.meta.MetaUtil;

/**
 * @author unascribed
 * @version 1.0
 */

public class FileNode implements BaseNode {

	private BaseNode parent = null;

	private File file = null;

	private HarvestStatus status;

	private String statusMessage;

	// special items
	private Icon icon;

	private String description;

	private FileSystemView fileView = FileSystemView.getFileSystemView();

	public int getType() {
		return parent.getType();
	}

	public FileNode(File file) {
		this.file = file;
		this.status = HarvestStatus.BLANK;

		// get the rest...
		Icon systemIcon = (Icon) FXUtil.invoke(fileView, "getSystemIcon", file,
				File.class);
		String systemDesc = (String) FXUtil.invoke(fileView,
				"getSystemTypeDescription", file, File.class);
		systemIcon = systemIcon != null ? systemIcon : UIManager
				.getIcon("FileView.fileIcon");
		setIcon(systemIcon);
		setDescription(systemDesc);
	}

	public String getName() {
		return file.getName();
	}

	public void addNotify(BaseNode parent) {
		this.parent = parent;
	}

	public TreeNode getChildAt(int childIndex) {
		throw new java.lang.UnsupportedOperationException(
				"Method getChildAt() not valid for FileNode.");
	}

	public int getChildCount() {
		return 0;
	}

	public TreeNode getParent() {
		return parent;
	}

	public int getIndex(TreeNode node) {
		return -1;
	}

	public boolean getAllowsChildren() {
		return false;
	}

	public boolean isLeaf() {
		return true;
	}

	public Enumeration children() {
		return null;
	}

	public HarvestSource[] getChildren() {
		return null;
	}

	public File getFile() {
		return this.file;
	}

	public void fireNodeChanged(BaseNode node) {
		parent.fireNodeChanged(this);
	}

	public HarvestStatus getStatus() {
		return this.status;
	}

	public String getStatusMessage() {
		return this.statusMessage;
	}

	public void setStatus(HarvestStatus status, String message) {
		this.status = status;
		this.statusMessage = message;
		if (message != null) {
			this.statusMessage = MetaUtil
					.formatText(message, 30, false, "<br>");
		}
		fireNodeChanged(this);
	}

	public boolean hasError() {
		return status == HarvestStatus.ERROR;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

}