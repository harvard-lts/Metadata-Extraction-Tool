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

import javax.swing.tree.TreeNode;

import nz.govt.natlib.meta.HarvestSource;

/**
 * @author unascribed
 * @version 1.0
 */

public interface BaseNode extends TreeNode, HarvestSource {

	public void addNotify(BaseNode parent);

	public void fireNodeChanged(BaseNode node);

	public boolean hasError();

}