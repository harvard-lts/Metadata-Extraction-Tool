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

import java.io.IOException;

/**
 * 
 * @author Nic Evans
 * @version 1.0
 */

public interface DataSource {

	/**
	 * Do not assume this has no effect on the position - if you need to
	 * preserve the location within the datasource then you must first save it
	 * and restore when finished.
	 * 
	 * @param pos
	 *            the position to read data from
	 * @param length
	 *            the amount of data to read (if available)
	 * @return byte[] containing the data requested
	 * @throws IOException
	 */
	public byte[] getData(long pos, int length) throws IOException;

	/**
	 * Returns data from the datasource. Reads from the current file pointer.
	 * Note do not assume the file pointer is moved 'n' bytes.
	 * 
	 * @param length
	 *            the amount of data to read (if available)
	 * @return byte[] containing the data requested
	 * @throws IOException
	 */
	public byte[] getData(int length) throws IOException;

	/**
	 * Sets the position of the file pointer.
	 * 
	 * @param pos
	 *            the new position
	 * @throws IOException
	 */
	public void setPosition(long pos) throws IOException;

	/**
	 * Gets the position of the filepointer.
	 * 
	 * @return the current file pointer position
	 * @throws IOException
	 */
	public long getPosition() throws IOException;

	/**
	 * Closes the datasource
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;
}