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

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * @author unascribed
 * @version 1.0
 */
public class FileTree extends JTree {

	private HashMap renderers;

	public FileTree(FileModel model) {
		super(model);
		setRootVisible(false);
		setShowsRootHandles(true);
		renderers = new HashMap();
		TreeRendererSelector renderer = new TreeRendererSelector();
		setCellRenderer(renderer);
		setRenderers();

		ToolTipManager.sharedInstance().registerComponent(this);
	}

	private void setRenderers() {
		addRenderer(FolderNode.class, new FolderRenderer());
		addRenderer(ObjectFolderNode.class, new ObjectFolderRenderer());
		addRenderer(FileNode.class, new FileRenderer());
	}

	public void addRenderer(Class type, TreeCellRenderer renderer) {
		renderers.put(type, renderer);
	}

	/**
	 * Standard functionality for a table - I guess 'tree' missed out!
	 * @author unascribed
	 * @version 1.0
	 */
	private class TreeRendererSelector extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			// decide which renderer to use - or use the default.
			TreeCellRenderer renderer = (TreeCellRenderer) renderers.get(value
					.getClass());
			Component rendererComponent = null;

			if (renderer != null) {
				rendererComponent = renderer.getTreeCellRendererComponent(tree,
						value, selected, expanded, leaf, row, hasFocus);
			} else {
				rendererComponent = super.getTreeCellRendererComponent(tree,
						value, selected, expanded, leaf, row, hasFocus);
			}

			return rendererComponent;
		}
	}

}