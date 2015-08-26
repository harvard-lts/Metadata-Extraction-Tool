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

package nz.govt.natlib.fx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * An implementation of a datasource that reads its data from a file
 * </p>
 * 
 * @author Nic
 * @version 1.0
 */
public class FileDataSource implements DataSource {

	private RandomAccessFile in;

	private File file;

	/**
	 * 0pens a file and allows the user to Iterate through it one token at a
	 * time boundaries may occur as tab, comma, positional etc...
	 */
	public FileDataSource(File file) {
		this.file = file;
	}

	private RandomAccessFile getInputStream() throws FileNotFoundException {
		if (in == null) {
			in = new RandomAccessFile(file, "r");
		}
		return in;
	}

	/**
	 * does the file have any more data?
	 */
	/*
	 * public boolean hasMoreData() throws IOException { RandomAccessFile ins =
	 * getInputStream(); return ins.getFilePointer()<ins.length(); }
	 */
	/**
	 * Do not assume this has no effect on the position - if you need to
	 * preserve the location within the datasource then you must first save it
	 * and restore when finished.
	 * 
	 * @param pos
	 *            the position to read data from
	 * @param len
	 *            the amount of data to read (if available)
	 * @return byte[] containing the data requested
	 * @throws IOException
	 */
	public synchronized byte[] getData(long pos, int len) throws IOException {
		long p = getInputStream().getFilePointer(); // preserve the pointer...
		setPosition(pos);
		byte[] data = getData(len);
		setPosition(p);
		return data;
	}

	/**
	 * Returns data from the datasource. Reads from the current file pointer.
	 * you can assume the file pointer is moved 'n' bytes.
	 * 
	 * @param len
	 *            the amount of data to read (if available)
	 * @return byte[] containing the data requested
	 * @throws IOException
	 */
	public byte[] getData(int len) throws IOException {
		RandomAccessFile ins = getInputStream();
		// determine the amount that is possible to read (if len is too much)
		long actualAmount = ins.length() - ins.getFilePointer();
		if (len < actualAmount) {
			actualAmount = len;
		}

		byte[] b = new byte[(int) actualAmount];
		int i = ins.read(b);
		return b;
	}

	/**
	 * Sets the position of the file pointer.
	 * 
	 * @param pos
	 *            the new position
	 * @throws IOException
	 */
	public void setPosition(long pos) throws IOException {
		getInputStream().seek(pos);
	}

	/**
	 * Gets the position of the filepointer.
	 * 
	 * @return the current file pointer position
	 * @throws IOException
	 */
	public long getPosition() throws IOException {
		return getInputStream().getFilePointer();
	}

	/**
	 * Closes the datasource
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		getInputStream().close();
		in = null;
	}
}