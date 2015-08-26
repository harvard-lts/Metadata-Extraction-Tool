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

package nz.govt.natlib.adapter.powerpoint;

import java.io.IOException;

import nz.govt.natlib.fx.DataSource;

import org.apache.poi.poifs.filesystem.DocumentInputStream;

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
public class DocumentDataSource implements DataSource {
	DocumentInputStream input;

	private int current_offset = 0;

	public DocumentDataSource(DocumentInputStream input) {
		this.input = input;
		// Mark the start of the file
		input.mark(0);
	}

	public byte[] getData(long pos, int len) throws IOException {
		throw new java.lang.RuntimeException("Not Supported for this class");
	}

	public byte[] getData(int len) throws IOException {
		// determine the amount that is possible to read (if len is too much)
		int actualAmount = Math.min(input.available(), len);

		byte[] b = new byte[actualAmount];
		int i = input.read(b);
		return b;
	}

	public void setPosition(long i) throws IOException {
		input.reset();
		input.skip((int) i);
	}

	public long getPosition() throws IOException {
		throw new java.lang.RuntimeException("Not Supported for this class");
	}

	public void close() throws IOException {
		input.close();
		input = null;
	}
}