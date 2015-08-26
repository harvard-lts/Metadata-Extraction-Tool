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
package nz.govt.natlib.meta;

import java.io.File;

/**
 * A very basic HarvestSource that caters for handling individual files.
 * @author bbeaumont
 */
public class FileHarvestSource implements HarvestSource {
	
	private HarvestSource[] children = new HarvestSource[1];

	/** The file to be harvested */
	private File file;

	/**
	 * Construct a FileHarvestSource.
	 * @param file The file to harvest metadata from.
	 */
	public FileHarvestSource(File file) {
		children[0] = new FileHarvestSource(file, false);
	}
	
	private FileHarvestSource(File file, boolean recurse) {
		this.file = file;
		this.children = null;
	}
	
	public HarvestSource[] getChildren() {
		return children;
	}

	public File getFile() {
		return file;
	}

	public String getName() {
		return file.getName();
	}

	public int getType() {
		return HarvestSource.SIMPLE;
	}

	public void setStatus(HarvestStatus status, String message) {
	}

}
