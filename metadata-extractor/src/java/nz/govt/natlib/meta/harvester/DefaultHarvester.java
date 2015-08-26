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
import java.io.IOException;

import nz.govt.natlib.meta.HarvestSource;
import nz.govt.natlib.meta.Harvester;
import nz.govt.natlib.meta.ProgressListener;
import nz.govt.natlib.meta.PropertySource;
import nz.govt.natlib.meta.config.Configuration;

/**
 * A DefaultHarvester is an ObjectHarvester that should be subclassed to process
 * an individual file - it has extended the standard ObjectHarvester to recurse
 * through the tree of files and process each one in turn. It also provides
 * methods that can be overridden to process folders - should the nesting/path
 * info be required in the eventual output.
 * 
 * @author unascribed
 * @version 1.0
 */
public abstract class DefaultHarvester extends Harvester {

	private void recursivelyHarvest(Configuration config, HarvestSource source,
			PropertySource props, ProgressListener listener) throws IOException {
		
		// Check the stopping flag.
		if (isStopping()) {
			return;
		}

		// go through and mark one to be an error
		HarvestSource[] children = source.getChildren();
		if (children != null) {
			openFolder(config, source);
			for (int i = 0; i < children.length && !isStopping(); i++) {
				recursivelyHarvest(config, children[i], props, listener);
			}
			closeFolder(config, source);
		} else {
			// harvest an induhvidual
			File file = source.getFile();
			fireStartHarvest(listener, source);
			try {
				// do the deed...
				startHarvestFile(config, file, props);
				harvestFile(config, file, props);
				endHarvestFile(config, file, props);
				fireEndHarvest(listener, source, true, null);
			} catch (Exception ex) {
				ex.printStackTrace();
				fireEndHarvest(listener, source, false, ex);
			}
		}
	}

	/**
	 * This method is called when a harvester recurses into a nested structure
	 * in the objects we are harvesting.
	 * @param config The configuration.
	 * @param source The source.
	 * @throws IOException
	 */
	protected void openFolder(Configuration config, HarvestSource source)
			throws IOException {
	}

	/**
	 * This method is called when a harvester recurses out of a nested structure
	 * in the objects we are harvesting.
	 * @param config The configuration.
	 * @param source The source.
	 * @throws IOException
	 */	
	protected void closeFolder(Configuration config, HarvestSource source)
			throws IOException {
	}

	/**
	 * Called when the harvest starts.
	 * @param config The configuration.
	 * @param props  The properties.
	 * @throws IOException
	 */
	protected void startHarvest(Configuration config, PropertySource props)
			throws IOException {
	}

	/**
	 * Called when the harvest ends.
	 * @param config The configuration.
	 * @param props  The properties.
	 * @throws IOException
	 */	
	protected void endHarvest(Configuration config, PropertySource props)
			throws IOException {
	}

	/**
	 * Called when the harvest of a file starts.
	 * @param config The configuration.
	 * @param file   The File we are starting to harvest.
	 * @param props  The properties.
	 * @throws IOException
	 */	
	protected void startHarvestFile(Configuration config, File file,
			PropertySource props) throws IOException {
	}

	/**
	 * Called when the harvest of a file ends.
	 * @param config The configuration.
	 * @param file   The File we have finished harvesting.
	 * @param props  The properties.
	 * @throws IOException
	 */		
	protected void endHarvestFile(Configuration config, File file,
			PropertySource props) throws IOException {
	}

	/**
	 * Called when a harvest is aborted.
	 * @param config The configuration.
	 * @throws IOException
	 */
	protected void abortHarvest(Configuration config) throws IOException {
	}

	/**
	 * Harvest one file.
	 * @param config The configuration
	 * @param file   The file to harvest.
	 * @param props  The properties.
	 * @throws IOException
	 */
	protected abstract void harvestFile(Configuration config, File file,
			PropertySource props) throws IOException;
	
	
	/**
	 * Start the harvest.
	 * @param config The configuration.
	 * @param source The object to harvest.
	 * @param props  The properties.
	 * @param listener The Progress Listener.
	 * @throws IOException
	 */
	public final void harvest(Configuration config, HarvestSource source,
			PropertySource props, ProgressListener listener) throws IOException {
		try {
			startHarvest(config, props);
			recursivelyHarvest(config, source, props, listener);
			endHarvest(config, props);
		} finally {
			if (isStopping()) {
				abortHarvest(config);
			}
		}
	}

}