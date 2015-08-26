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

package nz.govt.natlib.meta.ui.wizard;

import java.awt.Component;

/**
 * @author unascribed
 * @version 1.0
 */
public abstract class WizardItem {

	Wizard parent;

	public WizardItem() {
	}

	public void addNotify(Wizard parent) {
		this.parent = parent;
	}

	public void fireChangeEvent() {
		parent.itemChanged(this);
	}

	public abstract Component getWizardComponent();

	public void cancel() {
	}

	public void next() {
	}

	public void previous() {
	}

	public void finish() {
	}

	public abstract String getName();

	public String getHelp() {
		return "Please choose from the following options";
	}

	public String getDescription() {
		return " ";
	}

	public boolean canFinish() {
		return true;
	}

	public boolean canMoveBack() {
		return true;
	}

	public boolean canMoveForward() {
		return true;
	}

	public boolean showFinish() {
		return true;
	}

	public boolean showMoveBack() {
		return true;
	}

	public boolean showMoveForward() {
		return true;
	}

	public boolean showCancel() {
		return true;
	}

}