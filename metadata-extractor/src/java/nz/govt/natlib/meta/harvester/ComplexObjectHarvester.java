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

package nz.govt.natlib.meta.harvester;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nz.govt.natlib.meta.HarvestSource;
import nz.govt.natlib.meta.Harvester;
import nz.govt.natlib.meta.PropertySource;
import nz.govt.natlib.meta.config.Configuration;

/**
 * This harvests objects in the same way as the simple object harvester except
 * it does not open and close a new object for every file - it groups all files
 * together in a single object, arranged in a folder structure.
 * 
 * @author unascribed
 * @version 1.0
 */

public class ComplexObjectHarvester extends SimpleObjectHarvester {

	public ComplexObjectHarvester(Harvester parentHarvester) {
		super(parentHarvester);
	}

	protected void openFolder(Configuration config, HarvestSource source)
			throws IOException {
		startTag("Folder");
		writeTag("Path", source.getName());
	}

	protected void closeFolder(Configuration config, HarvestSource source)
			throws IOException {
		endTag("Folder");
	}

	protected void startHarvest(Configuration config, PropertySource props)
			throws IOException {
		// open the output file.
		File f = new File(config.getOutputDirectory() + "/"
				+ props.getProperty("name", null) + ".xml");
		out = new FileOutputStream(f);

		startOutputFile(props, out);
	}

	protected void startHarvestFile(Configuration config, File file,
			PropertySource props) throws IOException {
		// the simple harvester opens a new file here - we don't want that in
		// this case
	}

	protected void endHarvestFile(Configuration config, File file,
			PropertySource props) throws IOException {
		// the simple harvester closes a file here - we don't want that in this
		// case
	}

	protected void endHarvest(Configuration config, PropertySource props)
			throws IOException {
		endOutputFile(props, out);
		out.close();
		out = null;
	}

}