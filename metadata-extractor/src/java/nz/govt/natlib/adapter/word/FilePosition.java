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

package nz.govt.natlib.adapter.word;

import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.IntegerElement;

/**
 * @author Nic Evans
 * @version 1.0
 */

public class FilePosition {

	public FilePosition() {
	}

	public int getFP(DataSource ftk, int offset) throws java.io.IOException {
		ftk.setPosition(offset);
		return (int) FXUtil.getNumericalValue(ftk, IntegerElement.INT_SIZE,
				false);
	}
}